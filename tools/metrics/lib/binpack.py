#!/usr/bin/env python3
"""Bin-pack a large source tree into groups of <=CAP Java files for SonarQube.

Recursively finds leaf directories whose Java-file count is <= CAP (no parent/child
overlap), then greedily bin-packs them into bins of <= CAP files each. Prints one bin
per line: "<index>\t<comma-separated source dirs>". Used by sonar.sh for huge repos
(e.g. elasticsearch) that exceed the single-scan memory budget on an 8 GB box.
"""
import os, sys, subprocess

def jcount(d):
    out = subprocess.run(["bash", "-c", f'find "{d}" -name "*.java" | wc -l'],
                         capture_output=True, text=True)
    return int(out.stdout.strip() or 0)

def leaves(root, cap):
    """Yield (count, dir) leaf chunks each <= cap (or indivisible)."""
    c = jcount(root)
    if c == 0:
        return
    if c <= cap:
        yield (c, root); return
    subs = [os.path.join(root, d) for d in sorted(os.listdir(root))
            if os.path.isdir(os.path.join(root, d))]
    had = False
    for s in subs:
        if jcount(s) > 0:
            had = True
            yield from leaves(s, cap)
    if not had:
        yield (c, root)   # indivisible (loose files), emit as-is

def main():
    root, cap = sys.argv[1], int(sys.argv[2])
    chunks = sorted(leaves(root, cap), reverse=True)
    bins = []  # [total, [dirs]]
    for c, p in chunks:
        for b in bins:
            if b[0] + c <= cap:
                b[0] += c; b[1].append(p); break
        else:
            bins.append([c, [p]])
    for i, b in enumerate(bins):
        print(f"{i}\t{','.join(b[1])}")

if __name__ == "__main__":
    main()
