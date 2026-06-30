#!/usr/bin/env python3
"""Sum SonarQube bin sub-scans into one measures JSON (same shape as a single scan).

Counts add; duplication density and SQALE debt-ratio are ncloc-weighted; the
maintainability rating is re-derived from SonarQube's debt-ratio thresholds. Reads all
bin_*.json in the given directory; prints the combined component-measures JSON to stdout.
"""
import json, os, sys

SUM_KEYS = ["complexity","cognitive_complexity","code_smells","ncloc","lines",
            "files","functions","classes","duplicated_lines","duplicated_blocks","sqale_index"]

def fnum(v):
    try: return float(v)
    except (TypeError, ValueError): return None

def rating_from_ratio(pct):
    if pct <= 5: return "1.0"
    if pct <= 10: return "2.0"
    if pct <= 20: return "3.0"
    if pct <= 50: return "4.0"
    return "5.0"

def main():
    d = sys.argv[1]
    sums = {k: 0.0 for k in SUM_KEYS}
    wratio = wncloc = 0.0; n = 0
    for fn in sorted(os.listdir(d)):
        if not fn.endswith(".json"): continue
        try: j = json.load(open(os.path.join(d, fn)))
        except Exception: continue
        mv = {m["metric"]: fnum(m.get("value")) for m in j.get("component", {}).get("measures", [])}
        if not mv: continue
        n += 1
        for k in SUM_KEYS:
            if mv.get(k) is not None: sums[k] += mv[k]
        nl = mv.get("ncloc") or 0
        if mv.get("sqale_debt_ratio") is not None:
            wratio += mv["sqale_debt_ratio"] * nl; wncloc += nl
    measures = [{"metric": k, "value": str(int(v))} for k, v in sums.items()]
    dup = (sums["duplicated_lines"]/sums["lines"]*100) if sums["lines"] else 0.0
    ratio = (wratio/wncloc) if wncloc else 0.0
    measures.append({"metric": "duplicated_lines_density", "value": str(round(dup, 1))})
    measures.append({"metric": "sqale_debt_ratio", "value": str(round(ratio, 1))})
    measures.append({"metric": "sqale_rating", "value": rating_from_ratio(ratio)})
    print(json.dumps({"component": {"measures": measures, "_bins": n}}))

if __name__ == "__main__":
    main()
