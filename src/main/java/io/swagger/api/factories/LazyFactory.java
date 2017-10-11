package io.swagger.api.factories;

import io.swagger.api.algorithm.LazyService;
import io.swagger.api.impl.LazyImpl;

public class LazyFactory {
    private final static LazyService service = new LazyImpl();

    public static LazyService getLazy() {
        return service;
    }
}

