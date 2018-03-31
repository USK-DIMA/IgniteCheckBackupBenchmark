package ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.util.UUID;

public class CacheKey {

    public static final String DEFAULT_AFFINITY_KEY = "cons";

    private static String affinityKeyValue = DEFAULT_AFFINITY_KEY;

    private String key;

    @AffinityKeyMapped
    private String affinityKey;

    public CacheKey(String key) {
        this.key = key;
        this.affinityKey = UUID.randomUUID().toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAffinityKey() {
        return affinityKey;
    }

    public void setAffinityKey(String affinityKey) {
        this.affinityKey = affinityKey;
    }

    public static void setAffinityKeyValue(String affinityKeyValue) {
        CacheKey.affinityKeyValue = affinityKeyValue;
    }

    public static String getAffinityKeyValue() {
        return affinityKeyValue;
    }

    @Override
    public String toString() {
        return "CacheKey{" +
               "key='" + key + '\'' +
               ", affinityKey='" + affinityKey + '\'' +
               '}';
    }
}
