package com.ocularminds.eduzie;

import java.util.ArrayList;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

/**
 * @author Jejelowo B. Festus
 * @param <K>
 * @param <T>
 */
// @todo SearchMemoryCache -  fix missing dependencies.
public class SearchMemoryCache<K, T> {

    private long timeToLive;
    private final LRUMap searchCacheMap;

    protected class SearchCacheObject {

        public final long created = System.currentTimeMillis();
        public long lastAccessed = System.currentTimeMillis();
        public T value;

        protected SearchCacheObject(T value) {
            this.value = value;
        }
    }

    public SearchMemoryCache(int maxItems) {
        searchCacheMap = new LRUMap(maxItems);
    }

    public void put(K key, T value) {
        synchronized (searchCacheMap) {
            searchCacheMap.put(key, new SearchCacheObject(value));
        }
    }

    @SuppressWarnings("unchecked")
    public T get(K key) {
        synchronized (searchCacheMap) {
            SearchCacheObject c = (SearchCacheObject) searchCacheMap.get(key);

            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (searchCacheMap) {
            searchCacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (searchCacheMap) {
            return searchCacheMap.size();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanup() {

        ArrayList<K> deleteKey;
        synchronized (searchCacheMap) {

            MapIterator itr = searchCacheMap.mapIterator();
            deleteKey = new ArrayList<>((searchCacheMap.size() / 2) + 1);
            K key;
            SearchCacheObject c = null;

            while (itr.hasNext()) {

                key = (K) itr.next();
                c = (SearchCacheObject) itr.getValue();
                deleteKey.add(key);
            }
        }

        for(K key : deleteKey) {
            synchronized (searchCacheMap) {
                searchCacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}