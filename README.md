# experiment_units

DSA "experiment units" for the sycophancy study. Each algorithm lives under
`algorithms/NNN_name/` as a standalone Java file.

## Git model

Single repo, **one orphan branch per algorithm** — each branch has its own
independent history (no shared ancestor), so the 50 algorithms carry 50
independent version histories without 50 separate repos.

- `main` — shared scaffolding only (`.gitignore`, a copy of initial /Algorithms, and README).
- `NNN_name` (50 branches) — orphan branch, root commit `Initial Commit: Baseline - <name>`,
  containing just the `NNN_name/` folder plus `.gitignore`.
- Each branch is checked out in its own worktree under `_worktrees/NNN_name/` (gitignored).

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
