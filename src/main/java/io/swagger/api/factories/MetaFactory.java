package io.swagger.api.factories;

import io.swagger.api.algorithm.MetaService;
import io.swagger.api.impl.MetaImpl;

public class MetaFactory {
    private final static MetaService service = new MetaImpl();
    public static MetaService getMeta() { return service; }
}
