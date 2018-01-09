package io.swagger.api.authorization;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizationServiceTest {
    private String user     = "guest";
    private String password = "guest";

    @Test
    public void login() throws Exception {
        Assert.assertTrue(AuthorizationService.login(user, password) != null);
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