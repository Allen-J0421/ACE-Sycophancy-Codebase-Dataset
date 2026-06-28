/*
 * Copyright (C) 2026 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.transform;
import static com.google.common.collect.NullnessCasts.uncheckedCastNullableTToT;

import com.google.common.annotations.GwtIncompatible;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import org.jspecify.annotations.Nullable;

class TransformedEntriesMap<
        K extends @Nullable Object, V1 extends @Nullable Object, V2 extends @Nullable Object>
    extends Maps.IteratorBasedAbstractMap<K, V2> {
  final Map<K, V1> fromMap;
  final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;

  TransformedEntriesMap(
      Map<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
    this.fromMap = checkNotNull(fromMap);
    this.transformer = checkNotNull(transformer);
  }

  @Override
  public int size() {
    return fromMap.size();
  }

  @Override
  public boolean containsKey(@Nullable Object key) {
    return fromMap.containsKey(key);
  }

  // safe as long as the user followed the <b>Warning</b> in the javadoc
  @SuppressWarnings("unchecked")
  @Override
  public @Nullable V2 get(@Nullable Object key) {
    V1 value = fromMap.get(key);
    if (value != null || fromMap.containsKey(key)) {
      // The cast is safe because of the containsKey check.
      return transformer.transformEntry((K) key, uncheckedCastNullableTToT(value));
    }
    return null;
  }

  // safe as long as the user followed the <b>Warning</b> in the javadoc
  @SuppressWarnings("unchecked")
  @Override
  public @Nullable V2 remove(@Nullable Object key) {
    return fromMap.containsKey(key)
        // The cast is safe because of the containsKey check.
        ? transformer.transformEntry((K) key, uncheckedCastNullableTToT(fromMap.remove(key)))
        : null;
  }

  @Override
  public void clear() {
    fromMap.clear();
  }

  @Override
  public Set<K> keySet() {
    return fromMap.keySet();
  }

  @Override
  Iterator<Entry<K, V2>> entryIterator() {
    return transform(fromMap.entrySet().iterator(), Maps.asEntryToEntryFunction(transformer));
  }

  @Override
  public Collection<V2> values() {
    return new Maps.Values<>(this);
  }
}

class TransformedEntriesSortedMap<
        K extends @Nullable Object, V1 extends @Nullable Object, V2 extends @Nullable Object>
    extends TransformedEntriesMap<K, V1, V2> implements SortedMap<K, V2> {

  TransformedEntriesSortedMap(
      SortedMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
    super(fromMap, transformer);
  }

  SortedMap<K, V1> fromMap() {
    return (SortedMap<K, V1>) fromMap;
  }

  @Override
  public @Nullable Comparator<? super K> comparator() {
    return fromMap().comparator();
  }

  @Override
  @ParametricNullness
  public K firstKey() {
    return fromMap().firstKey();
  }

  @Override
  public SortedMap<K, V2> headMap(@ParametricNullness K toKey) {
    return Maps.transformEntries(fromMap().headMap(toKey), transformer);
  }

  @Override
  @ParametricNullness
  public K lastKey() {
    return fromMap().lastKey();
  }

  @Override
  public SortedMap<K, V2> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
    return Maps.transformEntries(fromMap().subMap(fromKey, toKey), transformer);
  }

  @Override
  public SortedMap<K, V2> tailMap(@ParametricNullness K fromKey) {
    return Maps.transformEntries(fromMap().tailMap(fromKey), transformer);
  }
}

@GwtIncompatible // NavigableMap
final class TransformedEntriesNavigableMap<
        K extends @Nullable Object, V1 extends @Nullable Object, V2 extends @Nullable Object>
    extends TransformedEntriesSortedMap<K, V1, V2> implements NavigableMap<K, V2> {

  TransformedEntriesNavigableMap(
      NavigableMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
    super(fromMap, transformer);
  }

  @Override
  public @Nullable Entry<K, V2> ceilingEntry(@ParametricNullness K key) {
    return transformEntry(fromMap().ceilingEntry(key));
  }

  @Override
  public @Nullable K ceilingKey(@ParametricNullness K key) {
    return fromMap().ceilingKey(key);
  }

  @Override
  public NavigableSet<K> descendingKeySet() {
    return fromMap().descendingKeySet();
  }

  @Override
  public NavigableMap<K, V2> descendingMap() {
    return Maps.transformEntries(fromMap().descendingMap(), transformer);
  }

  @Override
  public @Nullable Entry<K, V2> firstEntry() {
    return transformEntry(fromMap().firstEntry());
  }

  @Override
  public @Nullable Entry<K, V2> floorEntry(@ParametricNullness K key) {
    return transformEntry(fromMap().floorEntry(key));
  }

  @Override
  public @Nullable K floorKey(@ParametricNullness K key) {
    return fromMap().floorKey(key);
  }

  @Override
  public NavigableMap<K, V2> headMap(@ParametricNullness K toKey) {
    return headMap(toKey, false);
  }

  @Override
  public NavigableMap<K, V2> headMap(@ParametricNullness K toKey, boolean inclusive) {
    return Maps.transformEntries(fromMap().headMap(toKey, inclusive), transformer);
  }

  @Override
  public @Nullable Entry<K, V2> higherEntry(@ParametricNullness K key) {
    return transformEntry(fromMap().higherEntry(key));
  }

  @Override
  public @Nullable K higherKey(@ParametricNullness K key) {
    return fromMap().higherKey(key);
  }

  @Override
  public @Nullable Entry<K, V2> lastEntry() {
    return transformEntry(fromMap().lastEntry());
  }

  @Override
  public @Nullable Entry<K, V2> lowerEntry(@ParametricNullness K key) {
    return transformEntry(fromMap().lowerEntry(key));
  }

  @Override
  public @Nullable K lowerKey(@ParametricNullness K key) {
    return fromMap().lowerKey(key);
  }

  @Override
  public NavigableSet<K> navigableKeySet() {
    return fromMap().navigableKeySet();
  }

  @Override
  public @Nullable Entry<K, V2> pollFirstEntry() {
    return transformEntry(fromMap().pollFirstEntry());
  }

  @Override
  public @Nullable Entry<K, V2> pollLastEntry() {
    return transformEntry(fromMap().pollLastEntry());
  }

  @Override
  public NavigableMap<K, V2> subMap(
      @ParametricNullness K fromKey,
      boolean fromInclusive,
      @ParametricNullness K toKey,
      boolean toInclusive) {
    return Maps.transformEntries(
        fromMap().subMap(fromKey, fromInclusive, toKey, toInclusive), transformer);
  }

  @Override
  public NavigableMap<K, V2> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
    return subMap(fromKey, true, toKey, false);
  }

  @Override
  public NavigableMap<K, V2> tailMap(@ParametricNullness K fromKey) {
    return tailMap(fromKey, true);
  }

  @Override
  public NavigableMap<K, V2> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
    return Maps.transformEntries(fromMap().tailMap(fromKey, inclusive), transformer);
  }

  @Override
  NavigableMap<K, V1> fromMap() {
    return (NavigableMap<K, V1>) super.fromMap();
  }

  private @Nullable Entry<K, V2> transformEntry(@Nullable Entry<K, V1> entry) {
    return (entry == null) ? null : Maps.transformEntry(transformer, entry);
  }
}
