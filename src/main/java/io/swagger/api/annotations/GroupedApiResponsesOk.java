package io.swagger.api.annotations;


import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ApiResponses(value = {
    @ApiResponse(code = 200, message = "OK"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 401, message = "Unauthorized"),
    @ApiResponse(code = 403, message = "Forbidden"),
    @ApiResponse(code = 404, message = "Resource Not Found"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
public @interface GroupedApiResponsesOk {
}
