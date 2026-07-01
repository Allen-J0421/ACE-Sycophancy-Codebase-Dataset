#!/usr/bin/env python3
"""plot_grid.py — one ICSE-style 2x6 grid of metric trajectories.

  plot_grid.py <metrics.csv> <out_dir>

Rows = suite (Algorithms / OOP Homeworks); columns = the 6 code-quality metrics.
Each cell: metric (mean across that suite's units) vs refactoring turn T, one line
per model. Mini-plot style: small cells, x-axis ticks only {0, 10}, thick lines.
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
    "font.family": "serif", "font.size": 10, "axes.linewidth": 0.9,
    "xtick.direction": "in", "ytick.direction": "in",
    "xtick.major.size": 3, "ytick.major.size": 3,
    "figure.dpi": 200,
})

COLORS = {"gpt-5.5":"#2ca02c","gpt-5.4":"#1f77b4","gpt-5.4-mini":"#17becf",
          "claude-opus-4-8":"#9467bd","claude-sonnet-4-6":"#ff7f0e"}
MARKERS = {"gpt-5.5":"^","gpt-5.4":"o","gpt-5.4-mini":"v",
           "claude-opus-4-8":"P","claude-sonnet-4-6":"s"}
LABELS = {"gpt-5.5":"GPT-5.5","gpt-5.4":"GPT-5.4","gpt-5.4-mini":"GPT-5.4-mini",
          "claude-opus-4-8":"Claude Opus 4.8","claude-sonnet-4-6":"Claude Sonnet 4.6"}
MODEL_ORDER = ["gpt-5.5","gpt-5.4","gpt-5.4-mini","claude-opus-4-8","claude-sonnet-4-6"]

# (column, short header)
METRICS = [
    ("cc_per_fn",           "Cyclomatic/fn"),
    ("cognitive_per_fn",    "Cognitive/fn"),
    ("smells_per_kloc",     "Smells/kLOC"),
    ("duplication_pct",     "Duplication %"),
    ("cbo_mean",            "CBO"),
    ("tech_debt_ratio_pct", "Debt %"),
]
SUITES = [("ALG", "Algorithms"), ("R", "OOP Homeworks")]

TITLE_SIZE = 14   # column headers == row labels
TICK_SIZE = 13


def fmt_tick(v, _=None):
    a = abs(v)
    if a == 0: return "0"
    if a < 1:  return f"{v:.2f}"
    if a < 10: return f"{v:.1f}"
    return f"{v:.0f}"


def main():
    csv_path, out_dir = sys.argv[1], Path(sys.argv[2])
    df = pd.read_csv(csv_path)
    df["iteration"] = pd.to_numeric(df["iteration"], errors="coerce")
    for col, _ in METRICS:
        df[col] = pd.to_numeric(df[col], errors="coerce")

    present = [m for m in MODEL_ORDER if m in set(df["model"])]
    fig, axes = plt.subplots(2, 6, figsize=(15, 4.0))
    handles = None
    for row, (utype, rlabel) in enumerate(SUITES):
        sub = df[df["unit_type"] == utype]
        for col, (mcol, header) in enumerate(METRICS):
            ax = axes[row][col]
            agg = sub.dropna(subset=["iteration", mcol]).groupby(
                ["model", "iteration"])[mcol].mean().reset_index()
            ys = []
            for model in present:
                g = agg[agg["model"] == model].sort_values("iteration")
                if g.empty:
                    continue
                ys += list(g[mcol].to_numpy())
                ax.plot(g["iteration"], g[mcol], marker=MARKERS[model],
                        markersize=5, markeredgewidth=0, linewidth=3.4,
                        color=COLORS[model], label=LABELS[model])
            ax.set_xlim(-0.4, 10.4)
            ax.set_xticks([0, 10])
            # y-axis: only the bottom & top tick (data range), per subplot
            if ys:
                lo, hi = min(ys), max(ys)
                pad = (hi - lo) * 0.07 or 0.05
                ax.set_ylim(lo - pad, hi + pad)
                ax.set_yticks([lo, hi])
                ax.yaxis.set_major_formatter(ticker.FuncFormatter(fmt_tick))
            ax.tick_params(labelsize=TICK_SIZE)
            # regular grid mesh via minor gridlines; keep only the 2 labelled ticks
            ax.xaxis.set_minor_locator(ticker.AutoMinorLocator(5))   # verticals at 2,4,6,8
            ax.yaxis.set_minor_locator(ticker.AutoMinorLocator(4))   # 3 lines between lo/hi
            ax.tick_params(which="minor", length=0)
            ax.grid(True, which="both", linestyle="--", linewidth=0.5, alpha=0.4)
            if row == 0:
                ax.set_title(header, fontsize=TITLE_SIZE, fontweight="bold", pad=4)
                ax.set_xticklabels([])
            if col == 0:   # row label placed outside the tick labels
                ax.set_ylabel(rlabel, fontsize=TITLE_SIZE - 2.5, fontweight="bold", labelpad=6)
            if handles is None:
                handles, _ = ax.get_legend_handles_labels()

    # very tight layout; thin bottom strip for the shared legend
    fig.tight_layout(pad=0.3, w_pad=0.6, h_pad=0.5, rect=[0.0, 0.10, 0.999, 0.999])
    y_bottom = axes[1][0].get_position().y0
    fig.legend(handles, [LABELS[m] for m in present], loc="upper center",
               ncol=len(present), prop=dict(family="serif", size=11),
               frameon=True, framealpha=0.9,
               edgecolor="0.8", handlelength=1.8, columnspacing=1.6,
               bbox_to_anchor=(0.51, y_bottom - 0.05))

    out_dir.mkdir(parents=True, exist_ok=True)
    for ext in ("png", "pdf"):
        p = out_dir / f"grid_all_metrics.{ext}"
        fig.savefig(p, bbox_inches="tight")
        print(f"wrote {p}")
    plt.close(fig)


if __name__ == "__main__":
    main()
