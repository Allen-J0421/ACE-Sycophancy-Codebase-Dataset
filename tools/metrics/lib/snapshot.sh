#!/usr/bin/env bash
# snapshot.sh — materialize a git commit's tree to disk (read-only; never touches main).
# Source config.sh + common.sh first.

# tree_sha <commitish> -> the tree object SHA (content identity used as the dedup cache key)
tree_sha() { git -C "$REPO_ROOT" rev-parse "$1^{tree}" 2>/dev/null; }

# extract_commit <commitish> <destdir> : populate destdir with that commit's file tree.
extract_commit() {
  local commit="$1" dest="$2"
  rm -rf "$dest"; mkdir -p "$dest"
  git -C "$REPO_ROOT" archive "$commit" | tar -x -C "$dest"
}

# java_count <dir>
java_count() { find "$1" -name '*.java' 2>/dev/null | wc -l | tr -d ' '; }
