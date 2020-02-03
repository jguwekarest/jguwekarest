package org.kramerlab.wekarestapi.factories;

import org.kramerlab.wekarestapi.AlgorithmService;
import org.kramerlab.wekarestapi.impl.AlgorithmImpl;

public class AlgorithmFactory {
    private final static AlgorithmService service = new AlgorithmImpl();
    public static AlgorithmService getAlgorithm() {
        return service;
    }
}
