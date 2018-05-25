package unit;

import io.swagger.api.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

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
    public void testSerialize() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);

        Object obj = new Object();
        obj = "class weka.classifiers.functions.GaussianProcesses\n" +
            "Gaussian Processes\n" +
            "\n" +
            "Kernel used:\n" +
            "  Linear Kernel: K(x,y) = <x,y>\n" +
            "\n" +
            "All values shown based on: Normalize training data\n" +
            "\n" +
            "Average Target Value : 0.4851421188630491\n" +
            "Inverted Covariance Matrix:\n" +
            "    Lowest Value = -0.07579825336723331\n" +
            "    Highest Value = 0.9991157622596833\n" +
            "Inverted Covariance Matrix * Target-value Vector:\n" +
            "    Lowest Value = -0.4782845987029812\n" +
            "    Highest Value = 0.46648681733797204";
        os.writeObject(obj);
        System.out.println( out.toByteArray().toString());
    }

}