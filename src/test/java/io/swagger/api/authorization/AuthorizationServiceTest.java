package io.swagger.api.authorization;

import org.junit.Assert;
import org.junit.Test;

public class AuthorizationServiceTest {
    String user     = "guest";
    String password = "guest";

    @Test
    public void login() throws Exception {
        Assert.assertTrue(AuthorizationService.login(user, password) instanceof String);
    }

    @Test
    public void validate() throws Exception {
        String token = AuthorizationService.login(user, password);
        Assert.assertTrue(AuthorizationService.validate(token));
    }

    @Test
    public void logout() throws Exception {
        String token = AuthorizationService.login(user, password);
        Assert.assertTrue(AuthorizationService.logout(token));
        Assert.assertFalse(AuthorizationService.logout(token));
    }

}