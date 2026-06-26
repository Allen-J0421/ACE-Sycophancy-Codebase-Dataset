#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAIN_SRC_DIR="$ROOT_DIR/src/main/java"
TEST_SRC_DIR="$ROOT_DIR/src/test/java"
BUILD_DIR="$ROOT_DIR/target"
MAIN_CLASSES_DIR="$BUILD_DIR/classes"
TEST_CLASSES_DIR="$BUILD_DIR/test-classes"
MAIN_SOURCES_FILE="$BUILD_DIR/main-sources.txt"
TEST_SOURCES_FILE="$BUILD_DIR/test-sources.txt"
CONFIG_SRC_DIR="$ROOT_DIR/config"
CONFIG_DST_DIR="$MAIN_CLASSES_DIR/config"

rm -rf "$BUILD_DIR"
mkdir -p "$MAIN_CLASSES_DIR" "$TEST_CLASSES_DIR"

find "$MAIN_SRC_DIR" -name '*.java' | sort > "$MAIN_SOURCES_FILE"
if [ -s "$MAIN_SOURCES_FILE" ]; then
    javac -encoding UTF-8 -d "$MAIN_CLASSES_DIR" @"$MAIN_SOURCES_FILE"
fi

if [ -d "$CONFIG_SRC_DIR" ]; then
    mkdir -p "$CONFIG_DST_DIR"
    cp "$CONFIG_SRC_DIR"/*.properties "$CONFIG_DST_DIR"/
fi

if [ -d "$TEST_SRC_DIR" ]; then
    find "$TEST_SRC_DIR" -name '*.java' | sort > "$TEST_SOURCES_FILE"
    if [ -s "$TEST_SOURCES_FILE" ]; then
        javac -encoding UTF-8 -cp "$MAIN_CLASSES_DIR" -d "$TEST_CLASSES_DIR" @"$TEST_SOURCES_FILE"
    fi
fi
