#!/usr/bin/env python3
"""plot_trajectories.py — metric-vs-turn trajectory plots from the experiments CSV.

  plot_trajectories.py <metrics.csv> <out_dir>

Renders one PNG per code-quality metric. Each figure has two subplots — left for
the ALG suite, right for the R suite — showing the metric aggregated (mean across
that suite's units) over refactoring iteration T, one line per model.

Style mirrors sycophancy-ACE/plot_lines.py (serif, per-model color/marker, in-ticks).
"""
import os, sys, tempfile
from pathlib import Path

_CACHE = Path(tempfile.gettempdir()) / "metrics-plot-cache"
(_CACHE / "mpl").mkdir(parents=True, exist_ok=True)
os.environ.setdefault("MPLCONFIGDIR", str(_CACHE / "mpl"))

import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker

plt.rcParams.update({
    "font.family": "serif", "font.size": 11, "axes.linewidth": 0.8,
    "xtick.direction": "in", "ytick.direction": "in",
    "xtick.major.size": 4, "ytick.major.size": 4,
    "legend.frameon": True, "legend.framealpha": 0.9, "legend.edgecolor": "0.8",
    "figure.dpi": 150,
})

# Per-model style, matching sycophancy-ACE/style_config.py (haiku omitted: out of scope).
COLORS = {"gpt-5.5":"#2ca02c","gpt-5.4":"#1f77b4","gpt-5.4-mini":"#17becf",
          "claude-opus-4-8":"#9467bd","claude-sonnet-4-6":"#ff7f0e"}
MARKERS = {"gpt-5.5":"^","gpt-5.4":"o","gpt-5.4-mini":"v",
           "claude-opus-4-8":"P","claude-sonnet-4-6":"s"}
LABELS = {"gpt-5.5":"GPT-5.5","gpt-5.4":"GPT-5.4","gpt-5.4-mini":"GPT-5.4-mini",
          "claude-opus-4-8":"Claude Opus 4.8","claude-sonnet-4-6":"Claude Sonnet 4.6"}
MODEL_ORDER = ["gpt-5.5","gpt-5.4","gpt-5.4-mini","claude-opus-4-8","claude-sonnet-4-6"]

# (column, axis label, filename slug)
METRICS = [
    ("cc_per_fn",           "Cyclomatic / function",  "cyclomatic_per_fn"),
    ("cognitive_per_fn",    "Cognitive / function",   "cognitive_per_fn"),
    ("smells_per_kloc",     "Smells / kLOC",          "smells_per_kloc"),
    ("duplication_pct",     "Duplication %",          "duplication_pct"),
    ("cbo_mean",            "CBO mean",               "cbo_mean"),
    ("tech_debt_ratio_pct", "Technical Debt %",       "tech_debt_pct"),
]
SUITES = [("ALG", "Algorithms suite"), ("R", "R (real-world) suite")]


def suite_models(df):
    present = [m for m in MODEL_ORDER if m in set(df["model"])]
    return present + sorted(set(df["model"]) - set(present))


def render(df, col, ylabel, out_path):
    fig, axes = plt.subplots(1, 2, figsize=(12, 3.8), sharex=False)
    for ax, (utype, stitle) in zip(axes, SUITES):
        sub = df[df["unit_type"] == utype]
        # mean metric across that suite's units, per model per iteration
        agg = (sub.groupby(["model", "iteration"])[col]
                  .mean().reset_index())
        for model in suite_models(sub):
            g = agg[agg["model"] == model].sort_values("iteration")
            if g.empty:
                continue
            ax.plot(g["iteration"], g[col], marker=MARKERS.get(model, "o"),
                    markersize=5, linewidth=1.2, color=COLORS.get(model, "#333"),
                    label=LABELS.get(model, model))
        ax.set_title(stitle)
        ax.set_xlabel("Refactoring Iteration (turn T)")
        ax.set_ylabel(ylabel)
        ax.xaxis.set_major_locator(ticker.MaxNLocator(integer=True))
        ax.grid(axis="y", linestyle="--", linewidth=0.5, alpha=0.5)
    axes[0].legend(loc="best", fontsize=7, title="Model", title_fontsize=8,
                   framealpha=0.9, markerscale=0.8, handlelength=1.6,
                   labelspacing=0.3, borderpad=0.4)
    fig.suptitle(f"{ylabel} over refactoring turns", y=1.02, fontsize=12)
    fig.tight_layout()
    out_path.parent.mkdir(parents=True, exist_ok=True)
    fig.savefig(out_path, bbox_inches="tight")
    plt.close(fig)
    print(f"wrote {out_path}")


def main():
    csv_path, out_dir = sys.argv[1], Path(sys.argv[2])
    df = pd.read_csv(csv_path)
    df["iteration"] = pd.to_numeric(df["iteration"], errors="coerce")
    for col, ylabel, slug in METRICS:
        df[col] = pd.to_numeric(df[col], errors="coerce")
        render(df.dropna(subset=["iteration", col]), col, ylabel,
               out_dir / f"trajectory_{slug}.png")


if __name__ == "__main__":
    main()
