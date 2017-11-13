package jgu.weka.api.factories;

import jgu.weka.api.algorithm.RulesService;
import jgu.weka.api.impl.RulesImpl;

public class RulesFactory {
    private final static RulesService service = new RulesImpl();

    public static RulesService getRules() {
        return service;
    }
}

