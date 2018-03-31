package ru.uskov.dmitry.check.bkp.benckmrk.ignite.cache;

public class CacheValue {

    private String value;

    public CacheValue(String value ) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "CacheValue{" +
               "value='" + value + '\'' +
               '}';
    }
}
