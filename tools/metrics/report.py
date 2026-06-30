#!/usr/bin/env python3
"""report.py — render a Markdown report from a metrics CSV.

  report.py baselines    <metrics.csv> <out.md>
  report.py experiments  <metrics.csv> <out.md>

Baselines: per-category summary + full G/R tables + ALG summary (v0 layout).
Experiments: coverage, per-model final-state + baseline->final delta summaries,
per-unit-type summary; full per-iteration data lives in the CSV.
"""
import csv, statistics, sys

def num(v):
    try: return float(v)
    except (TypeError, ValueError): return None

def mm(rows, col):
    vals = [num(r.get(col)) for r in rows if num(r.get(col)) is not None]
    if not vals: return "-"
    return f"{round(statistics.mean(vals),2)} / {round(statistics.median(vals),2)}"

METHODOLOGY = """## Methodology

| Metric | Tool | Definition |
|---|---|---|
| Cyclomatic Complexity | SonarQube `complexity` (+ CK method `wmc`) | McCabe CC; total and per-function |
| Cognitive Complexity | SonarQube `cognitive_complexity` | total and per-function |
| Code Smell Count | SonarQube `code_smells` | rule violations; also per kLOC |
| Duplication Ratio | SonarQube `duplicated_lines_density` | % duplicated lines |
| Coupling Between Objects | CK 0.7.0 class-level `cbo` | mean / median / max across classes |
| Maintainability | SonarQube SQALE | rating A–E + technical-debt ratio |

**Stack:** SonarQube Community Build 25.1 (Java 17) + sonar-scanner 8.1; CK 0.7.0.

**Caveats:** sources analyzed without compiled bytecode (a minority of bytecode-only smell
rules skipped; AST metrics unaffected); Maintainability is SonarQube's SQALE measure, not the
classic Microsoft Maintainability Index; huge repos (G) are scanned in ≤5k-file bins and summed
(counts add; duplication & debt-ratio ncloc-weighted; rating re-derived from SQALE thresholds).
"""

SUMCOLS = [("cc_per_fn","Cyclomatic/fn"),("cognitive_per_fn","Cognitive/fn"),
           ("smells_per_kloc","Smells/kLOC"),("duplication_pct","Duplication %"),
           ("cbo_mean","CBO mean"),("tech_debt_ratio_pct","Debt %")]

def baselines(rows, out):
    L = ["# Baseline Code-Quality Metrics — 80 Units\n",
         "Each unit at its **Initial Commit / Baseline** snapshot.\n", METHODOLOGY,
         "\n## Per-category summary (mean / median)\n",
         "| Category | n | " + " | ".join(l for _,l in SUMCOLS) + " |",
         "|" + "---|"*(len(SUMCOLS)+2)]
    for c in ["ALG","R","G"]:
        rs = [r for r in rows if r["unit_type"]==c]
        L.append(f"| {c} | {len(rs)} | " + " | ".join(mm(rs,col) for col,_ in SUMCOLS) + " |")
    gcols = [("unit","Unit"),("ncloc","nLOC"),("cc_total","CC tot"),("cc_per_fn","CC/fn"),
             ("cognitive_total","Cog tot"),("cognitive_per_fn","Cog/fn"),("code_smells","Smells"),
             ("smells_per_kloc","Smells/kLOC"),("duplication_pct","Dup%"),("cbo_mean","CBO mean"),
             ("cbo_max","CBO max"),("maint_rating","Maint"),("tech_debt_ratio_pct","Debt%")]
    for cat,title in [("G","G — full GitHub repos"),("R","R — curated modules")]:
        L.append(f"\n## {title}\n")
        L.append("| " + " | ".join(t for _,t in gcols) + " |")
        L.append("|" + "---|"*len(gcols))
        for r in [x for x in rows if x["unit_type"]==cat]:
            L.append("| " + " | ".join(str(r.get(c,"")) for c,_ in gcols) + " |")
    alg = [r for r in rows if r["unit_type"]=="ALG"]
    L.append("\n## ALG — 50 synthetic DSA units (summary)\n")
    for col,lbl in [("cc_total","Cyclomatic total"),("cognitive_total","Cognitive total"),
                    ("code_smells","Code smells"),("cbo_mean","CBO mean")]:
        v=[(num(r[col]),r["unit"]) for r in alg if num(r.get(col)) is not None]
        if not v: continue
        v.sort(reverse=True)
        L.append(f"- **{lbl}:** mean {round(statistics.mean(x for x,_ in v),2)}; "
                 f"max {v[0][1]} ({int(v[0][0])}), min {v[-1][1]} ({int(v[-1][0])})")
    L.append("\nFull per-unit data: `metrics.csv`.\n")
    open(out,"w").write("\n".join(L))

def experiments(rows, out):
    by_branch = {}
    for r in rows: by_branch.setdefault(r["branch"], []).append(r)
    finals, deltas = [], []   # final-state rows ; per-branch delta dicts
    for b, rs in by_branch.items():
        rs.sort(key=lambda r:int(r["iteration"]))
        base, fin = rs[0], rs[-1]
        finals.append(fin)
        d = {"tool":fin["tool"],"model":fin["model"],"unit_type":fin["unit_type"]}
        for col,_ in SUMCOLS:
            a,bn = num(base.get(col)), num(fin.get(col))
            d[col] = (bn-a) if (a is not None and bn is not None) else None
        deltas.append(d)
    models = sorted({r["model"] for r in finals})
    units  = sorted({r["unit"] for r in rows})
    L = ["# Experiment-Branch Code-Quality Metrics\n",
         f"Every iteration snapshot of every in-scope experiment branch. "
         f"**{len(rows)} snapshots** across **{len(by_branch)} branches**, "
         f"**{len(units)} units**, **{len(models)} models**.\n",
         "Baseline values are in `../baselines/REPORT.md`; per-iteration trajectories are in `metrics.csv`.\n",
         METHODOLOGY,
         "\n## Final-state by model (mean / median across that model's branches)\n",
         "| Model | branches | " + " | ".join(l for _,l in SUMCOLS) + " | Maint (mode) |",
         "|" + "---|"*(len(SUMCOLS)+3)]
    for m in models:
        rs=[r for r in finals if r["model"]==m]
        ratings=[r.get("maint_rating") for r in rs if r.get("maint_rating")]
        mode=max(set(ratings),key=ratings.count) if ratings else "-"
        L.append(f"| {m} | {len(rs)} | " + " | ".join(mm(rs,c) for c,_ in SUMCOLS) + f" | {mode} |")
    L += ["\n## Mean baseline→final delta by model (negative = improved)\n",
          "| Model | " + " | ".join(l for _,l in SUMCOLS) + " |",
          "|" + "---|"*(len(SUMCOLS)+1)]
    for m in models:
        ds=[d for d in deltas if d["model"]==m]
        cells=[]
        for col,_ in SUMCOLS:
            vals=[d[col] for d in ds if d[col] is not None]
            cells.append(f"{round(statistics.mean(vals),2):+}" if vals else "-")
        L.append(f"| {m} | " + " | ".join(cells) + " |")
    L += ["\n## Final-state by unit type\n",
          "| Type | branches | " + " | ".join(l for _,l in SUMCOLS) + " |",
          "|" + "---|"*(len(SUMCOLS)+2)]
    for t in sorted({r["unit_type"] for r in finals}):
        rs=[r for r in finals if r["unit_type"]==t]
        L.append(f"| {t} | {len(rs)} | " + " | ".join(mm(rs,c) for c,_ in SUMCOLS) + " |")
    L.append("\nFull per-iteration data (one row per branch×iteration): `metrics.csv`.\n")
    open(out,"w").write("\n".join(L))

if __name__ == "__main__":
    mode, csv_path, out = sys.argv[1], sys.argv[2], sys.argv[3]
    rows = list(csv.DictReader(open(csv_path)))
    (baselines if mode=="baselines" else experiments)(rows, out)
    print(f"wrote {out}")
