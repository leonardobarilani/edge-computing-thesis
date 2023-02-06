package com.openfaas.function.api;

import java.util.List;

public interface IEdgeDB {

    void close();

    String get(String key);

    void set(String key, String value);

    List<String> getList(String key);

    void addToList(String key, String value);

    void propagate(String value, String levelToPropagateTo, String function);

    void setTTL(long seconds);

    void delete(String key);
}
