#!/usr/bin/env sh
set -eu

rm -rf /tmp/bucket_sort_build
mkdir -p /tmp/bucket_sort_build

javac -d /tmp/bucket_sort_build $(find src/main/java -name '*.java') $(find src/test/java -name '*.java')
java -cp /tmp/bucket_sort_build bucketsort.BucketSortTest
java -cp /tmp/bucket_sort_build bucketsort.Main
