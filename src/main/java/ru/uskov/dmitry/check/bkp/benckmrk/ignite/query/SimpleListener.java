package ru.uskov.dmitry.check.bkp.benckmrk.ignite.query;

import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheKey;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheValue;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;
import java.util.function.Consumer;

public class SimpleListener implements CacheEntryUpdatedListener<CacheKey, CacheValue> {

    private Consumer<CacheEntryEvent<? extends CacheKey, ? extends CacheValue>> consumer;
    private boolean executeConsumer = false;

    public SimpleListener(Consumer<CacheEntryEvent<? extends CacheKey, ? extends CacheValue>> consumer) {
        this.consumer = consumer;
        this.executeConsumer = true;
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends CacheKey, ? extends CacheValue>> iterable) throws CacheEntryListenerException {
        for (CacheEntryEvent<? extends CacheKey, ? extends CacheValue> cacheEntryEvent : iterable) {
            if (executeConsumer) {
                consumer.accept(cacheEntryEvent);
                executeConsumer = false;
            }
            System.out.println("CacheEntryUpdatedListener: " + cacheEntryEvent.getKey() + "/" + cacheEntryEvent.getValue());
        }
    }

}
