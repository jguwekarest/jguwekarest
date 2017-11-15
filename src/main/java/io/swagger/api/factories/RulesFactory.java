package io.swagger.api.factories;

import io.swagger.api.algorithm.RulesService;
import io.swagger.api.impl.RulesImpl;

public class RulesFactory {
    private final static RulesService service = new RulesImpl();

    public static RulesService getRules() {
        return service;
    }
}

