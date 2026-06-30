#!/usr/bin/env python3
"""assemble_baselines.py — rows.jsonl (already metric-merged) -> baselines metrics.csv.
  assemble_baselines.py <rows.jsonl> <out.csv>
"""
import csv, json, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from aggregate import METRIC_COLS

ORDER = {"ALG": 0, "R": 1, "G": 2}

def main():
    rows = [json.loads(l) for l in open(sys.argv[1]) if l.strip()]
    rows.sort(key=lambda r: (ORDER.get(r["unit_type"], 9), r["unit"]))
    cols = ["unit_type", "unit"] + METRIC_COLS
    with open(sys.argv[2], "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=cols)
        w.writeheader()
        for r in rows:
            w.writerow({c: r.get(c) for c in cols})
    print(f"wrote {sys.argv[2]}: {len(rows)} units")

if __name__ == "__main__":
    main()
