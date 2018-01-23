package authsystem;

import io.swagger.api.authorization.AuthorizationService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizationServiceTest {
    private String user     = "guest";
    private String password = "guest";

    @Test(description = "A&A login at openam service")
    public void login() throws Exception {
        Assert.assertTrue(AuthorizationService.login(user, password) != null);
    }

    @Test(description = "A&A login and validate token")
    public void validate() throws Exception {
        String token = AuthorizationService.login(user, password);
        Assert.assertTrue(AuthorizationService.validate(token));
    }

    @Test(description = "A&A logout and token devaluation")
    public void logout() throws Exception {
        String token = AuthorizationService.login(user, password);
        Assert.assertTrue(AuthorizationService.logout(token));
        Assert.assertFalse(AuthorizationService.logout(token));
    }

}