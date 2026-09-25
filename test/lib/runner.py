#!/usr/bin/env python3
"""Run correctness tests for experiment subjects listed in test/manifest.tsv.

Usage:
  python3 test/lib/runner.py --unit 001_binary_search            # all 10 iters (+baseline)
  python3 test/lib/runner.py --unit 001_binary_search --iter 3
  python3 test/lib/runner.py --all                               # everything
  python3 test/lib/runner.py --unit X --no-baseline              # skip iter 0

Per subject:
  1. `git archive <sha>` into a scratch workdir.
  2. Copy in stubs from test/units/<unit>/stubs/ if present (never overwrite
     subject files unless conf says STUB_OVERRIDE=1).
  3. Compile all subject .java files, minus exclusions, into work/classes.
     Auto-excluded: files importing org.junit/org.testng/org.openjdk.jmh/javafx
     (unavailable here), plus conf EXCLUDE_REGEX matches.
  4. Compile test/units/<unit>/Driver.java (standalone, reflection-only;
     cached per unit) into test/units/<unit>/.driver_classes.
  5. Run: java <JAVA_FLAGS> -cp <driver>:<classes> Driver <workdir> <iter>
     Driver must print a final line `RESULT PASS` or `RESULT FAIL <reason>`.

Statuses: PASS, FAIL, COMPILE_ERROR, DRIVER_ERROR (no RESULT line / driver
missing), TIMEOUT. Results appended to test/results/<unit>.tsv (one row per
subject, overwriting previous rows for the same unit+iter).
"""
import argparse
import csv
import re
import shutil
import subprocess
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent.parent
TEST = REPO / "test"
RESULTS = TEST / "results"
SCRATCH = Path("/tmp/ace_subject_runs")

BAD_IMPORT = re.compile(r"^\s*import\s+(?:static\s+)?(org\.junit|org\.testng|org\.openjdk\.jmh|javafx)\.",
                        re.M)


PUBLIC_TYPE = re.compile(
    r"^public\s+(?:(?:final|abstract|sealed|non-sealed|strictfp)\s+)*"
    r"(?:class|interface|enum|record|@interface)\s+([A-Za-z_$][\w$]*)", re.M)


def fix_public_filename(f):
    """Baselines ship e.g. `public class ActivitySelection` in activity_selection.java,
    which javac rejects on filename grounds alone. Rename such a file to match its
    top-level public type (only if that name is free), so the code is judged on its
    content. Units whose conf EXCLUDEs the original path never reach here."""
    try:
        m = PUBLIC_TYPE.search(f.read_text(errors="replace"))
    except OSError:
        return f
    if not m or f.stem == m.group(1):
        return f
    target = f.with_name(m.group(1) + ".java")
    if target.exists():
        return f
    f.rename(target)
    return target


def read_conf(unit):
    conf = {"EXCLUDE_REGEX": "", "JAVA_FLAGS": "-Djava.awt.headless=true",
            "COMPILE_TIMEOUT": "180", "RUN_TIMEOUT": "120", "STUB_OVERRIDE": "0",
            "AUTO_EXCLUDE_IMPORTS": "1"}
    p = TEST / "units" / unit / "unit.conf"
    if p.exists():
        for line in p.read_text().splitlines():
            line = line.strip()
            if line and not line.startswith("#") and "=" in line:
                k, v = line.split("=", 1)
                conf[k.strip()] = v.strip()
    return conf


def compile_driver(unit):
    udir = TEST / "units" / unit
    drivers = sorted(udir.glob("*.java"))
    if not drivers:
        return None, "no Driver.java"
    out = udir / ".driver_classes"
    newest_src = max(d.stat().st_mtime for d in drivers)
    stamp = out / ".stamp"
    if stamp.exists() and stamp.stat().st_mtime >= newest_src:
        return out, None
    if out.exists():
        shutil.rmtree(out)
    out.mkdir(parents=True)
    r = subprocess.run(["javac", "-d", str(out), *[str(d) for d in drivers]],
                       capture_output=True, text=True, timeout=180)
    if r.returncode != 0:
        return None, "driver compile failed:\n" + r.stderr[-3000:]
    stamp.touch()
    return out, None


def run_subject(unit, it, sha, conf, driver_cp):
    work = SCRATCH / unit / f"iter{it:02d}"
    if work.exists():
        shutil.rmtree(work)
    work.mkdir(parents=True)
    # materialize
    ar = subprocess.run(["git", "-C", str(REPO), "archive", sha],
                        capture_output=True, timeout=120)
    if ar.returncode != 0:
        return "DRIVER_ERROR", "git archive failed: " + ar.stderr.decode()[-500:]
    subprocess.run(["tar", "-x", "-C", str(work)], input=ar.stdout, check=True)
    # stubs
    stubs = TEST / "units" / unit / "stubs"
    if stubs.exists():
        for s in stubs.rglob("*"):
            if s.is_file():
                dest = work / s.relative_to(stubs)
                if dest.exists() and conf["STUB_OVERRIDE"] != "1":
                    continue
                dest.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(s, dest)
    # gather sources
    excl = re.compile(conf["EXCLUDE_REGEX"]) if conf["EXCLUDE_REGEX"] else None
    srcs = []
    for f in sorted(work.rglob("*.java")):
        rel = str(f.relative_to(work))
        if excl and excl.search(rel):
            continue
        if conf["AUTO_EXCLUDE_IMPORTS"] == "1":
            try:
                if BAD_IMPORT.search(f.read_text(errors="replace")):
                    continue
            except OSError:
                continue
        srcs.append(str(fix_public_filename(f)))
    if not srcs:
        return "COMPILE_ERROR", "no compilable sources"
    classes = work / "_classes"
    classes.mkdir()
    try:
        r = subprocess.run(["javac", "-nowarn", "-encoding", "utf-8", "-d", str(classes),
                            "-cp", str(classes), *srcs],
                           capture_output=True, text=True,
                           timeout=int(conf["COMPILE_TIMEOUT"]), cwd=work)
    except subprocess.TimeoutExpired:
        return "TIMEOUT", "compile timeout"
    if r.returncode != 0:
        return "COMPILE_ERROR", r.stderr[-2000:]
    # run driver
    flags = conf["JAVA_FLAGS"].split()
    cmd = ["java", *flags, "-cp", f"{driver_cp}:{classes}", "Driver", str(work), str(it)]
    try:
        r = subprocess.run(cmd, capture_output=True, text=True,
                           timeout=int(conf["RUN_TIMEOUT"]), cwd=work)
    except subprocess.TimeoutExpired:
        return "TIMEOUT", "driver run timeout"
    out = (r.stdout or "") + ("\n[stderr]\n" + r.stderr if r.stderr.strip() else "")
    m = None
    for line in (r.stdout or "").splitlines():
        mm = re.match(r"^RESULT (PASS|FAIL)\s*(.*)$", line.strip())
        if mm:
            m = mm
    if not m:
        return "DRIVER_ERROR", out[-2000:]
    if m.group(1) == "PASS":
        return "PASS", m.group(2)[-500:]
    return "FAIL", (m.group(2) + " || " + out[-1200:])[:1500]


def write_results(unit, new_rows):
    RESULTS.mkdir(exist_ok=True)
    path = RESULTS / f"{unit}.tsv"
    existing = {}
    if path.exists():
        with open(path) as f:
            for row in csv.reader(f, delimiter="\t"):
                if row and row[0] != "unit":
                    existing[(row[0], row[1])] = row
    for row in new_rows:
        existing[(row[0], row[1])] = row
    with open(path, "w") as f:
        f.write("unit\titer\tsha\tstatus\tdetail\n")
        for k in sorted(existing, key=lambda x: (x[0], int(x[1]))):
            f.write("\t".join(existing[k]) + "\n")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--unit")
    ap.add_argument("--iter", type=int)
    ap.add_argument("--all", action="store_true")
    ap.add_argument("--no-baseline", action="store_true")
    args = ap.parse_args()

    manifest = []
    with open(TEST / "manifest.tsv") as f:
        for row in csv.reader(f, delimiter="\t"):
            if row and row[0] != "unit":
                manifest.append((row[0], int(row[1]), row[2]))

    units = sorted({u for u, _, _ in manifest})
    if args.unit:
        if args.unit not in units:
            sys.exit(f"unknown unit {args.unit}")
        units = [args.unit]
    elif not args.all:
        sys.exit("pass --unit <id> or --all")

    exit_code = 0
    for unit in units:
        conf = read_conf(unit)
        driver_cp, err = compile_driver(unit)
        rows = []
        for u, it, sha in manifest:
            if u != unit:
                continue
            if args.iter is not None and it != args.iter:
                continue
            if it == 0 and (args.no_baseline or args.iter != 0 and args.iter is not None):
                continue
            if driver_cp is None:
                status, detail = "DRIVER_ERROR", err
            else:
                status, detail = run_subject(unit, it, sha, conf, driver_cp)
            detail = " ".join(detail.split())[:1500]
            rows.append([unit, str(it), sha, status, detail])
            print(f"{unit}\titer{it}\t{status}\t{detail[:120]}")
            if status not in ("PASS",) and it > 0:
                exit_code = 1
        write_results(unit, rows)
    return exit_code


if __name__ == "__main__":
    sys.exit(main())
