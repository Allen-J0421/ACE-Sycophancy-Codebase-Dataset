# experiment_units

DSA "experiment units" for the sycophancy study. Each algorithm lives under
`algorithms/NNN_name/` as a standalone Java file.

## Git model

Single repo, **one orphan branch per algorithm** — each branch has its own
independent history (no shared ancestor), so the 50 algorithms carry 50
independent version histories without 50 separate repos.

- `main` — shared scaffolding only (`.gitignore`, this README). Does not track `algorithms/`.
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
