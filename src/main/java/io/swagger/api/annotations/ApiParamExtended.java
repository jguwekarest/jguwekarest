package io.swagger.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(value={PARAMETER})
@Retention(value=RUNTIME)

public @interface ApiParamExtended {

        /**
         * Enum constants for parameter types
         */
        public enum ParamType {

                PATH("path"),
                QUERY("query"),
                BODY("body"),
                HEADER("header"),
                FORM("form");

                private final String name;

                ParamType(final String name) {
                        this.name = name;
                }

                @Override
                public String toString() {
                        return name;
                }
        }

        /**
         * Enum constants for parameter date types
         */
        public enum DataType {
                STRING("string"),
                INTEGER("integer"),
                DATE("date"),
                BOOLEAN("boolean"),
                LONG("long"),
                FLOAT("float"),
                DOUBLE("double"),
                BYTE("byte"),
                DATETIME("dateTime");

                private final String name;

                DataType(final String name) {
                        this.name = name;
                }

                @Override
                public String toString() {
                        return name;
                }
        }

        /**
         * The parameter name as it appears in the URL pattern.
         *
         * @return name
         */
        String name() default "";

        /**
         * Defines if parameter is mandatory
         *
         * @return boolean if parameter is required
         */
        boolean required() default false;

        /**
         * Another way to allow multiple values for a "query" parameter. If used, the query parameter may accept comma-separated values.
         *
         * @return true if multiple allowed
         */
        boolean allowMultiple() default false;

        /**
         * The parameter description.
         *
         * @return description as string
         */
        String value();
        String defaultValue();
        String xorn();

        /**
         * The type of the parameter (that is, the location of the parameter in the request).
         *
         * @return parameter type (default is PATH)
         */
        ParamType paramType() default ParamType.PATH;

        /**
         * The data type of the parameter
         * @return the data type (default is STRING)
         */
        DataType dataType() default DataType.STRING;


}
