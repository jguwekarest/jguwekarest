package unit;

import io.swagger.api.WekaUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import weka.core.Instances;

public class WekaUtilsTest {
    @Test
    public void instancesFromString() throws Exception {
        String arff = "@relation weather-weka.filters.unsupervised.instance.RemoveRange-R5-last\n" +
                "@attribute outlook {sunny,overcast,rainy}\n" +
                "@attribute temperature numeric\n" +
                "@attribute humidity numeric\n" +
                "@attribute windy {TRUE,FALSE}\n" +
                "@attribute play {yes,no}\n" +
                "@data\n" +
                "sunny,87,87,TRUE,yes\n" +
                "sunny,75,90,FALSE,no\n" +
                "overcast,81,76,FALSE,yes\n" +
                "rainy,59,96,FALSE,yes";
        Instances instances = WekaUtils.instancesFromString(arff, true);
        Assert.assertEquals(instances.getClass(), Instances.class);
        Assert.assertEquals(5, instances.numAttributes());
        Assert.assertEquals(4, instances.classIndex(),"Class index error.");
        Assert.assertEquals(4, instances.size());
    }

    @Test
    public void saveWekaModel() throws Exception {
    }

    @Test
    public void getParamString() throws Exception {
        Assert.assertEquals(" -R 100 ", WekaUtils.getParamString(100, "R", 2));
        Assert.assertEquals(" -H 2 ", WekaUtils.getParamString(null, "H", 2));
        Assert.assertEquals(" -X ", WekaUtils.getParamString(null, "X", null));
    }

}