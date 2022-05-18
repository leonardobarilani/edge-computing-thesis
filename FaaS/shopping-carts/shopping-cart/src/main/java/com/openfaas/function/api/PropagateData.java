package com.openfaas.function.api;

import java.util.List;

public class PropagateData {
    public String value;
    public String function;

    public PropagateData(String value, String function) {
        this.value = value;
        this.function = function;
    }
}
