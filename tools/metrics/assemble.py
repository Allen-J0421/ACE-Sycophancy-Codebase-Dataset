#!/usr/bin/env python3
"""assemble.py — join identity rows (rows.jsonl) with the tree-SHA metric cache into
one CSV (one row per branch x iteration). Missing/failed caches yield empty metric cells.

  assemble.py <rows.jsonl> <cache_dir> <out.csv>
"""
import csv, json, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from aggregate import METRIC_COLS

ID_COLS = ["unit","unit_type","tool","model","timestamp","branch","iteration",
           "commit_sha","tree_sha"]

def main():
    rows_path, cache_dir, out_csv = sys.argv[1], sys.argv[2], sys.argv[3]
    cache = {}
    rows = []
    for line in open(rows_path):
        line = line.strip()
        if not line: continue
        r = json.loads(line)
        tsha = r["tree_sha"]
        if tsha not in cache:
            p = os.path.join(cache_dir, tsha + ".json")
            cache[tsha] = json.load(open(p)) if os.path.exists(p) else {}
        m = cache[tsha]
        rows.append({**r, **{c: m.get(c) for c in METRIC_COLS}})
    # stable sort: unit, tool, model, timestamp, iteration
    rows.sort(key=lambda r: (r["unit"], r["tool"], r["model"], r["timestamp"], int(r["iteration"])))
    with open(out_csv, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=ID_COLS + METRIC_COLS)
        w.writeheader()
        for r in rows: w.writerow(r)
    print(f"wrote {out_csv}: {len(rows)} rows, {len(cache)} unique trees")

if __name__ == "__main__":
    main()
