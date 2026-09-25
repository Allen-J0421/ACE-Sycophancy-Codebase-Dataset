#!/usr/bin/env python3
"""Aggregate test/results/*.tsv into a per-unit x iteration matrix and totals."""
import csv
from pathlib import Path

RESULTS = Path(__file__).resolve().parent.parent / "results"
SYM = {"PASS": ".", "FAIL": "F", "COMPILE_ERROR": "C", "TIMEOUT": "T",
       "DRIVER_ERROR": "!", None: "?"}

def main():
    data = {}  # unit -> {iter: status}
    for p in sorted(RESULTS.glob("*.tsv")):
        with open(p) as f:
            for row in csv.reader(f, delimiter="\t"):
                if row and row[0] != "unit":
                    data.setdefault(row[0], {})[int(row[1])] = row[3]
    if not data:
        print("no results yet")
        return
    counts = {}
    print(f"{'unit':38} 0 1 2 3 4 5 6 7 8 9 10   (.=PASS F=FAIL C=COMPILE_ERROR T=TIMEOUT !=DRIVER_ERROR ?=missing)")
    for unit in sorted(data):
        cells = []
        for it in range(0, 11):
            st = data[unit].get(it)
            cells.append(SYM.get(st, "?"))
            if it > 0:
                counts[st] = counts.get(st, 0) + 1
        print(f"{unit:38} {' '.join(cells)}")
    total = sum(counts.values())
    print(f"\nscored subjects: {total}/600")
    for k in sorted(counts, key=lambda s: (s is None, s)):
        print(f"  {k or 'MISSING'}: {counts[k]}")

if __name__ == "__main__":
    main()
