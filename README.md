# experiment_units

DSA "experiment units" for the sycophancy study. Each algorithm lives under
`algorithms/NNN_name/` as a standalone Java file.

## Two kinds of experiment units

The repo holds two kinds of test subjects, distinguished only by their **number prefix**:

| Type | Prefix | Example | Snapshot on `main` |
| --- | --- | --- | --- |
| **Synthetic** — the 50 DSA algorithms | `NNN_` | `001_binary_search` | `algorithms/NNN_name/` |
| **Real-world** — non-synthetic subjects | `R<NNN>_` | `R001_module_java` | `real_exp/R<NNN>_name/` |

Both follow the **identical git model** described below — the prefix is purely a visual marker
that keeps the real-world subjects distinct from the algorithm numbering. `expmap.sh` recognizes
both forms, so experiment branches map the same way regardless of subject type.

## Git model

Single repo, **one orphan branch per unit** — each branch has its own independent history (no
shared ancestor), so every unit carries its own version history without a separate repo.

- `main` — shared scaffolding only (`.gitignore`, a copy of initial /Algorithms, and README),
  plus a snapshot of each unit (`algorithms/NNN_name/` for synthetic, `real_exp/R<NNN>_name/` for real).
- `<unit>` branch — orphan branch (`NNN_name` or `R<NNN>_name`), root commit
  `Initial Commit: Baseline - <name>`, containing just that unit's folder plus `.gitignore`.
- Each branch is checked out in its own worktree under `_worktrees/<unit>/` (gitignored).

### Useful commands

```sh
git worktree list                       # main + 50 worktrees
git branch                              # main + 50 algorithm branches
git log --oneline 001_binary_search     # single root commit per branch
cd _worktrees/001_binary_search         # work on one algorithm in isolation
```

## Experiment branches

Model runs live on `claude-exp/*` and `codex-exp/*` branches, named
`<tool>-exp/<timestamp>-<model>-high-Agent`. The experiment's
**root commit equals that algorithm branch's tip**, and the shared commit is the
join key.

### `tools/expmap.sh`

A read-only helper that recovers the algorithm ↔ experiment mapping live from the
graph. It prints terminal output only and scans both local branches and their `origin/*` mirrors.

```sh
tools/expmap.sh map                     # full table: algorithm  model  timestamp  branch
tools/expmap.sh ls dijkstra             # experiments for an algorithm (match by name or NNN)
tools/expmap.sh of <exp-branch>         # the algorithm a given experiment belongs to
tools/expmap.sh algos                   # algorithms with experiment counts
tools/expmap.sh models                  # experiment counts per model
```

After cloning, make sure the branches are present locally or fetched
(`git fetch origin`); the tool reads `origin/*` automatically.
