package ru.uskov.dmitry.check.bkp.benckmrk.ignite;

import org.apache.ignite.IgniteCache;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheKey;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheValue;

public class AddValueExecutor {

    private static int valueIndex = 0;


    private static synchronized CacheKey createNextValue() {
        return new CacheKey("nodeX-" + valueIndex++);
    }

    public static CacheKey execute(IgniteCache<CacheKey, CacheValue> cache, int count) {
        CacheKey centralCacheKey  = null;
        for (int i = 0; i < count; i++) {
            CacheKey cacheKey = addNewValue(cache);
            if(i == count/2) {
                centralCacheKey = cacheKey;
            }
        }
        return centralCacheKey;
    }

    private static CacheKey addNewValue(IgniteCache<CacheKey, CacheValue> cache) {
        CacheKey key = createNextValue();
        while (cache.get(key) != null) {
            key = createNextValue();
        }
        CacheValue value = new CacheValue(key.getKey());
        CacheKey finalKey = key;
        cache.put(finalKey, value);
        return finalKey;
    }
}
