package jgu.weka.api.factories;

import jgu.weka.api.algorithm.LazyService;
import jgu.weka.api.impl.LazyImpl;

public class LazyFactory {
    private final static LazyService service = new LazyImpl();

    public static LazyService getLazy() {
        return service;
    }
}

