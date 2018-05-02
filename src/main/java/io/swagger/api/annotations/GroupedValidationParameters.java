package io.swagger.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/*
@Parameters(value = {
    // validation
    @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
    @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10")) @FormDataParam("validationNum") Double validationNum,
}
)*/

public @interface GroupedValidationParameters {

}
