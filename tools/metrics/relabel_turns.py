#!/usr/bin/env python3
"""relabel_turns.py — correct the `iteration` (turn) column in the experiments CSV.

  relabel_turns.py <metrics.csv> [sycophancy_output_dir]

The scanner (run_experiments.sh) numbered snapshots by *commit position* over
`git rev-list`, so branches whose agent produced extra/duplicate commits per turn
(mostly claude-sonnet-4-6 on the R suite) were mis-numbered — showing phantom
turns >10. The authoritative per-turn commit for each run lives in the experiment
log CSVs (`logs/<stamp>-<model>-log.csv`, one row per run 1..10 with commit_sha).

This rewrites `iteration` to the true turn:
  - baseline (Initial Commit) -> turn 0
  - a commit that is run N's commit_sha in the log -> turn N
  - any other commit (agent's own intermediate/duplicate commit) -> dropped

289/300 branches are already correct and unchanged. Uses the log commit_sha, NOT a
commit-message heuristic (which would mis-pick when a turn has duplicate markers).
"""
import csv, os, sys
from collections import defaultdict

DEFAULT_OUT = "/Users/allenjiang/Documents/sycophancy-ACE/output"
SUITE_SUBDIR = {"ALG": "Algorithms", "R": "RealWorld", "G": "RealWorld"}


def log_path(out_dir, unit, utype, model, stamp):
    sub = SUITE_SUBDIR.get(utype)
    if not sub:
        return None
    p = f"{out_dir}/{sub}/{unit}-high-Agent/logs/{stamp}-{model}-log.csv"
    return p if os.path.exists(p) else None


def main():
    csv_path = sys.argv[1]
    out_dir = sys.argv[2] if len(sys.argv) > 2 else DEFAULT_OUT
    rows = list(csv.DictReader(open(csv_path)))
    fields = rows[0].keys()
    by_branch = defaultdict(list)
    for r in rows:
        by_branch[r["branch"]].append(r)

    kept, dropped, changed_branches, missing_log = [], 0, [], []
    for b, rs in by_branch.items():
        r0 = rs[0]
        lp = log_path(out_dir, r0["unit"], r0["unit_type"], r0["model"], r0["timestamp"])
        if not lp:
            missing_log.append(b)
            kept.extend(rs)                      # leave untouched if no log found
            continue
        sha2run = {lr["commit_sha"]: int(lr["run"])
                   for lr in csv.DictReader(open(lp))}
        new_rows, seen_turns, changed = [], {}, False
        for r in rs:
            if int(r["iteration"]) == 0:
                turn = 0
            elif r["commit_sha"] in sha2run:
                turn = sha2run[r["commit_sha"]]
            else:
                dropped += 1
                changed = True
                continue
            if str(turn) != r["iteration"]:
                changed = True
            r = dict(r); r["iteration"] = str(turn)
            # if two commits map to the same turn, keep the last one
            seen_turns[turn] = r
        new_rows = [seen_turns[t] for t in sorted(seen_turns)]
        if changed:
            changed_branches.append((b, len(rs), len(new_rows)))
        kept.extend(new_rows)

    # stable sort matching assemble.py: unit, tool, model, timestamp, iteration
    kept.sort(key=lambda r: (r["unit"], r["tool"], r["model"], r["timestamp"], int(r["iteration"])))
    with open(csv_path, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=list(fields))
        w.writeheader()
        w.writerows(kept)

    print(f"rewrote {csv_path}: {len(kept)} rows ({dropped} phantom commits dropped)")
    print(f"branches changed: {len(changed_branches)} / {len(by_branch)}"
          + (f"; missing log: {len(missing_log)}" if missing_log else ""))
    for b, old, new in sorted(changed_branches, key=lambda x: x[1] - x[2], reverse=True):
        print(f"  {b.split('/')[-1]:52s} {old:2d} -> {new:2d} rows")


if __name__ == "__main__":
    main()
