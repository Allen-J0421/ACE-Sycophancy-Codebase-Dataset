#!/usr/bin/env python3
"""Generate test/manifest.tsv: one row per test subject.

A test subject is (unit, iteration 1..10) for every claude-opus-4-8 experiment
branch belonging to the 50 algorithm units and R001-R010. The state tested for
iteration k is the LAST commit on the branch whose assigned iteration number is
<= k (branches commit sparsely: an iteration with no code change has no commit).

Iteration assignment rule, walking first-parent history from the root:
  - commit subject matching  r'iteration (\d+)'  -> that number
  - any other commit (e.g. "Refactor ...", "Document ...") -> the previous
    assigned number (an addendum to that iteration), or 1 if it is the first
    commit after the baseline.

Output columns (TSV): unit  iter  sha  branch  commit_subject
Also emits iteration 0 (the baseline root commit) per unit, used only for
driver sanity checks - run_all skips it for scoring.
"""
import re
import subprocess
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
MODEL = "claude-opus-4-8"

# root-commit message name -> canonical unit id
def canon(unit_name: str):
    m = re.match(r"module_java(\d*)$", unit_name)
    if m:
        n = int(m.group(1) or "1")
        if 1 <= n <= 10:
            return f"R{n:03d}_module_java"
        return None  # R011+ not in scope
    algos = [p.name for p in (REPO / "algorithms").iterdir() if p.is_dir()]
    for a in algos:
        if a.split("_", 1)[1] == unit_name:
            return a
    return None  # G-units and anything else: out of scope


def git(*args):
    return subprocess.run(["git", "-C", str(REPO), *args], check=True,
                          capture_output=True, text=True).stdout


def main():
    branches = [b.strip() for b in git("branch", "--format=%(refname:short)").splitlines()
                if MODEL in b]
    rows = []
    seen_units = {}
    for br in sorted(branches):
        root = git("rev-list", "--max-parents=0", "--first-parent", br).split()[0]
        root_subj = git("log", "-1", "--format=%s", root).strip()
        name = root_subj.replace("Initial Commit: Baseline - ", "")
        unit = canon(name)
        if unit is None:
            continue
        if unit in seen_units:
            sys.exit(f"ERROR: two {MODEL} branches for {unit}: {seen_units[unit]} and {br}")
        seen_units[unit] = br
        commits = [c for c in git("rev-list", "--reverse", "--first-parent", br).split()
                   if c != root]
        assigned = []  # (iter_no, sha, subject)
        prev = 0
        for sha in commits:
            subj = git("log", "-1", "--format=%s", sha).strip()
            m = re.search(r"iteration (\d+)", subj)
            it = int(m.group(1)) if m else max(prev, 1)
            prev = it
            assigned.append((it, sha, subj))
        rows.append((unit, 0, root, br, root_subj))
        for k in range(1, 11):
            cand = [(i, s, sub) for (i, s, sub) in assigned if i <= k]
            if not cand:
                # no commit yet at iteration k: state == baseline
                rows.append((unit, k, root, br, "(baseline; no commit yet)"))
            else:
                i, s, sub = cand[-1]
                rows.append((unit, k, s, br, sub))

    rows.sort(key=lambda r: (r[0], r[1]))
    out = REPO / "test" / "manifest.tsv"
    with open(out, "w") as f:
        f.write("unit\titer\tsha\tbranch\tcommit_subject\n")
        for r in rows:
            f.write("\t".join(str(x) for x in r) + "\n")
    n_units = len(seen_units)
    n_subjects = sum(1 for r in rows if r[1] > 0)
    print(f"wrote {out}: {n_units} units, {n_subjects} scored subjects "
          f"(+{n_units} baseline rows)")
    missing = 60 - n_units
    if missing:
        print(f"WARNING: expected 60 units, found {n_units}")


if __name__ == "__main__":
    main()
