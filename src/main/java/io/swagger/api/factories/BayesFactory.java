package io.swagger.api.factories;

import io.swagger.api.BayesService;
import io.swagger.api.impl.BayesImpl;

public class BayesFactory {
    private final static BayesService service = new BayesImpl();

    public static BayesService getBayes() {
        return service;
    }

}


