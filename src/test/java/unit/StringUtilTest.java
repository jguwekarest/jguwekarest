package unit;

import io.swagger.api.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringUtilTest {
    @Test
    public void containsIgnoreCase() throws Exception {
        //function not in use
    }

    @Test(description = "Function join for ruby like String join")
    public void join() throws Exception {
        String[] testStr = {"sub", "test", "subdir"};
        String proofStr = "sub/test/subdir";
        Assert.assertEquals(proofStr, StringUtil.join(testStr, "/"));
    }

    @Test()
    public void checkTrailingSlash() throws Exception {
        String testStr = "http://test.tt/subdir/";
        String proofStr = "http://test.tt/subdir";
        Assert.assertEquals(testStr, StringUtil.checkTrailingSlash(proofStr));
    }

    @Test
    public void removeTrailingSlash() throws Exception {
        String testStr = "http://test.tt/subdir/";
        String proofStr = "http://test.tt/subdir";
        Assert.assertEquals(proofStr, StringUtil.removeTrailingSlash(testStr));
    }

    @Test
    public void isNumeric() throws Exception {
        Assert.assertTrue(StringUtil.isNumeric("1.234"));
        Assert.assertTrue(StringUtil.isNumeric("123"));
        Assert.assertTrue(StringUtil.isNumeric("-1e8"));
        Assert.assertTrue(StringUtil.isNumeric("0.0002"));
        Assert.assertFalse(StringUtil.isNumeric("GGxyzcdef"));
    }

    @Test
    public void isUri() throws Exception {
        Assert.assertTrue(StringUtil.isUri("http://www.google.de"));
        Assert.assertTrue(StringUtil.isUri("https://www.google.de/subdir"));
        Assert.assertTrue(StringUtil.isUri("https://www.google.de/subdir?bla=23&blubb=test"));
        Assert.assertFalse(StringUtil.isUri("tralala://www....google.de//subdir?bla=23&blubb=test"));
    }

    @Test
    public void switching() throws Exception {
        String val = "text/uri-list";
        switch (val){
            case "":
                System.out.println("empty String out");
            case "text/uri-list":
                System.out.println("text/uri-list out");
            default:
                System.out.println("default out");
        }
        Assert.assertTrue(StringUtil.isUri("http://www.google.de"));
        Assert.assertTrue(StringUtil.isUri("https://www.google.de/subdir"));
        Assert.assertTrue(StringUtil.isUri("https://www.google.de/subdir?bla=23&blubb=test"));
        Assert.assertFalse(StringUtil.isUri("tralala://www....google.de//subdir?bla=23&blubb=test"));
    }


}