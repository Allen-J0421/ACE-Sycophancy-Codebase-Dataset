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
import static com.google.common.collect.Collections2.safeContains;
import static com.google.common.collect.Collections2.transform;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.j2objc.annotations.WeakOuter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import org.jspecify.annotations.Nullable;

class AsMapView<K extends @Nullable Object, V extends @Nullable Object>
    extends Maps.ViewCachingAbstractMap<K, V> {

  private final Set<K> set;
  final Function<? super K, V> function;

  AsMapView(Set<K> set, Function<? super K, V> function) {
    this.set = checkNotNull(set);
    this.function = checkNotNull(function);
  }

  Set<K> backingSet() {
    return set;
  }

  @Override
  Set<K> createKeySet() {
    return Maps.removeOnlySet(backingSet());
  }

  @Override
  Collection<V> createValues() {
    return transform(set, function);
  }

  @Override
  public int size() {
    return backingSet().size();
  }

  @Override
  public boolean containsKey(@Nullable Object key) {
    return backingSet().contains(key);
  }

  @Override
  public @Nullable V get(@Nullable Object key) {
    if (safeContains(backingSet(), key)) {
      @SuppressWarnings("unchecked") // unsafe, but Javadoc warns about it
      K k = (K) key;
      return function.apply(k);
    }
    return null;
  }

  @Override
  public @Nullable V remove(@Nullable Object key) {
    if (backingSet().remove(key)) {
      @SuppressWarnings("unchecked") // unsafe, but Javadoc warns about it
      K k = (K) key;
      return function.apply(k);
    }
    return null;
  }

  @Override
  public void clear() {
    backingSet().clear();
  }

  @Override
  Set<Entry<K, V>> createEntrySet() {
    @WeakOuter
    final class EntrySetImpl extends Maps.EntrySet<K, V> {
      @Override
      Map<K, V> map() {
        return AsMapView.this;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
        return asMapEntryIterator(backingSet(), function);
      }
    }
    return new EntrySetImpl();
  }

  static <K extends @Nullable Object, V extends @Nullable Object> Iterator<Entry<K, V>>
      asMapEntryIterator(Set<K> set, Function<? super K, V> function) {
    return new TransformedIterator<K, Entry<K, V>>(set.iterator()) {
      @Override
      Entry<K, V> transform(@ParametricNullness K key) {
        return Maps.immutableEntry(key, function.apply(key));
      }
    };
  }
}

final class SortedAsMapView<K extends @Nullable Object, V extends @Nullable Object>
    extends AsMapView<K, V> implements SortedMap<K, V> {

  SortedAsMapView(SortedSet<K> set, Function<? super K, V> function) {
    super(set, function);
  }

  @Override
  SortedSet<K> backingSet() {
    return (SortedSet<K>) super.backingSet();
  }

  @Override
  public @Nullable Comparator<? super K> comparator() {
    return backingSet().comparator();
  }

  @Override
  public Set<K> keySet() {
    return Maps.removeOnlySortedSet(backingSet());
  }

  @Override
  public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
    return Maps.asMap(backingSet().subSet(fromKey, toKey), function);
  }

  @Override
  public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
    return Maps.asMap(backingSet().headSet(toKey), function);
  }

  @Override
  public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
    return Maps.asMap(backingSet().tailSet(fromKey), function);
  }

  @Override
  @ParametricNullness
  public K firstKey() {
    return backingSet().first();
  }

  @Override
  @ParametricNullness
  public K lastKey() {
    return backingSet().last();
  }
}

@GwtIncompatible // NavigableMap
final class NavigableAsMapView<K extends @Nullable Object, V extends @Nullable Object>
    extends AbstractNavigableMap<K, V> {
  /*
   * Using AbstractNavigableMap is simpler than extending SortedAsMapView and rewriting all the
   * NavigableMap methods.
   */

  private final NavigableSet<K> set;
  private final Function<? super K, V> function;

  NavigableAsMapView(NavigableSet<K> set, Function<? super K, V> function) {
    this.set = checkNotNull(set);
    this.function = checkNotNull(function);
  }

  @Override
  public NavigableMap<K, V> subMap(
      @ParametricNullness K fromKey,
      boolean fromInclusive,
      @ParametricNullness K toKey,
      boolean toInclusive) {
    return Maps.asMap(set.subSet(fromKey, fromInclusive, toKey, toInclusive), function);
  }

  @Override
  public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
    return Maps.asMap(set.headSet(toKey, inclusive), function);
  }

  @Override
  public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
    return Maps.asMap(set.tailSet(fromKey, inclusive), function);
  }

  @Override
  public @Nullable Comparator<? super K> comparator() {
    return set.comparator();
  }

  @Override
  public @Nullable V get(@Nullable Object key) {
    if (safeContains(set, key)) {
      @SuppressWarnings("unchecked") // unsafe, but Javadoc warns about it
      K k = (K) key;
      return function.apply(k);
    }
    return null;
  }

  @Override
  public void clear() {
    set.clear();
  }

  @Override
  Iterator<Entry<K, V>> entryIterator() {
    return AsMapView.asMapEntryIterator(set, function);
  }

  @Override
  Iterator<Entry<K, V>> descendingEntryIterator() {
    return descendingMap().entrySet().iterator();
  }

  @Override
  public NavigableSet<K> navigableKeySet() {
    return Maps.removeOnlyNavigableSet(set);
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public NavigableMap<K, V> descendingMap() {
    return Maps.asMap(set.descendingSet(), function);
  }
}
