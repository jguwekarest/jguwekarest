package jgu.weka.api.factories;

import jgu.weka.api.algorithm.BayesService;
import jgu.weka.api.impl.BayesImpl;

public class BayesFactory {
    private final static BayesService service = new BayesImpl();

    public static BayesService getBayes() {
        return service;
    }

}


