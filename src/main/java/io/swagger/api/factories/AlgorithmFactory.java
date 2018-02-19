package io.swagger.api.factories;

import io.swagger.api.impl.AlgorithmImpl;
import io.swagger.api.AlgorithmService;

public class AlgorithmFactory {
    private final static AlgorithmService service = new AlgorithmImpl();
    public static AlgorithmService getAlgorithm() {
        return service;
    }
}
