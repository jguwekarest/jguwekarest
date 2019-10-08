package org.kramerlab.wekarestapi.factories;

import org.kramerlab.wekarestapi.ApiService;
import org.kramerlab.wekarestapi.impl.ApiImpl;


public class ApiFactory {
    private final static ApiService service = new ApiImpl();
    public static ApiService getApiApi() {
        return service;
    }
}
