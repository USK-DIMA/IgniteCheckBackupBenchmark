package ru.uskov.dmitry.check.bkp.benckmrk;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.openjdk.jmh.annotations.*;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.AddValueExecutor;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheKey;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache.CacheValue;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.query.SimpleFilter;
import ru.uskov.dmitry.check.bkp.benckmrk.ignite.query.SimpleListener;

import javax.cache.event.CacheEntryEvent;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Fork(0)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 20)
public class IgniteCheckIsBackupBenchmark {

    private static Ignite ignite;

    private static IgniteCache<CacheKey, CacheValue> cache;
    private static CacheKey key;
    private static Method entryMethod;
    private static Method isBackupMethod;


    private static CacheEntryEvent<? extends CacheKey, ? extends CacheValue> event;

    static {
        initReflectionMethods();
        startIgnite();
        key = AddValueExecutor.execute(cache, 1);
    }

    @Benchmark
    public Affinity getAffinity() {
        return ignite.affinity(cache.getName());
    }

    @Benchmark
    public boolean isPrimaryAffinity1() {
        Affinity<Object> affinity = ignite.affinity(cache.getName());
        return affinity.isPrimary(ignite.cluster().localNode(), key);
    }

    @Benchmark
    public boolean isPrimaryAffinity2() {
        Affinity<Object> affinity = ignite.affinity(cache.getName());
        ClusterNode primary = affinity.mapKeyToNode(event.getKey());
        return primary.id().equals(ignite.cluster().localNode());
    }

    @Benchmark
    public boolean isBackupReflection() {
        try {
            Object entry = entryMethod.invoke(event);
            return (boolean) isBackupMethod.invoke(entry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void startIgnite() {
        Ignition.start();
        ignite = Ignition.ignite();
        String cacheName = "A-Cache";
        CacheConfiguration<CacheKey, CacheValue> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setName(cacheName);
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheConfiguration.setBackups(1);

        cache = ignite.getOrCreateCache(cacheConfiguration);

        ContinuousQuery<CacheKey, CacheValue> query = new ContinuousQuery<>();

        query.setRemoteFilterFactory(new SimpleFilter());
        SimpleListener simpleListener = new SimpleListener(c -> event = c);
        query.setLocalListener(simpleListener);
        cache.query(query);
    }

    private static void initReflectionMethods() {
        try{
            Class<?> cacheContinuousQueryEvent = Class.forName("org.apache.ignite.internal.processors.cache.query.continuous.CacheContinuousQueryEvent");
            entryMethod = cacheContinuousQueryEvent.getDeclaredMethod("entry");
            Class<?> cacheContinuousQueryEntry = Class.forName("org.apache.ignite.internal.processors.cache.query.continuous.CacheContinuousQueryEntry");
            isBackupMethod = cacheContinuousQueryEntry.getDeclaredMethod("isBackup");
            entryMethod.setAccessible(true);
            isBackupMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
