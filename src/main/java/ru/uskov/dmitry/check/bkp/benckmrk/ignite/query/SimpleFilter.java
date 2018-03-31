package ru.uskov.dmitry.check.bkp.benckmrk.ignite.query;

import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheKey;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheValue;

import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEventFilter;

public class SimpleFilter implements Factory<CacheEntryEventFilter<CacheKey, CacheValue>> {

    @Override
    public CacheEntryEventFilter<CacheKey, CacheValue> create() {
        return event -> true;
    }
}
