package io.swagger.api.factories;

import io.swagger.api.ApiService;
import io.swagger.api.impl.ApiImpl;


public class ApiFactory {
    private final static ApiService service = new ApiImpl();
    public static ApiService getApiApi() {
        return service;
    }
}
