package io.swagger.api;

public class Exeption {

    public static class AAException extends Exception{

        int code;
        public AAException(int code, String msg) {
            super("AAException Error: " + msg);
            this.code = code;
        }

    }


}
