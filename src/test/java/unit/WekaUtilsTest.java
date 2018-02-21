package unit;

import io.swagger.api.StringUtil;
import io.swagger.api.WekaOptionHelper;
import io.swagger.api.WekaUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import weka.core.Instances;

import java.math.BigDecimal;

public class WekaUtilsTest {
    @Test(description = "Create instances from an arff string")
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
        Assert.assertEquals(4, instances.classIndex(), "Class index error.");
        Assert.assertEquals(4, instances.size());
    }

    @Test
    public void saveWekaModel() throws Exception {
    }

    @Test(description = "Test param string helper for WEKA params")
    public void getParamString() throws Exception {
        Assert.assertEquals(" -R 100 ", WekaOptionHelper.getParamString(100, "R", 2));
        Assert.assertEquals(" -H 2 ", WekaOptionHelper.getParamString(null, "H", 2));
        Assert.assertEquals(" -X ", WekaOptionHelper.getParamString(null, "X", null));
    }

    @Test(description = "Test WekaOptionHelper for WEKA params")
    public void getOptions() throws Exception {

        String[] options = WekaOptionHelper.getJ48Options(0, new BigDecimal(0.25), 2, 3,1, 1,
                1, 1, 0);
        Assert.assertEquals("-M 2 -R -N 3 -Q 1 -U", StringUtil.join(options," "), "get J48 options");

        options = WekaOptionHelper.getKNNOptions(0, 1, 0, "0",0, "LinearNNSearch");
        Assert.assertEquals("-W 0 -K 1 -A weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"",StringUtil.join(options," "), "get KNN options");

        options = WekaOptionHelper.getBayesNetOptions("SimpleEstimator", new BigDecimal(0.5), 0, "local.K2","-P 1 -S BAYES");
        Assert.assertEquals("-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",StringUtil.join(options," "), "get BayesNet options");

        options = WekaOptionHelper.getLibSVMOptions(0, 0.0F, 1F, 3,new BigDecimal("0.001"), new BigDecimal(0),
                2,new BigDecimal("0.1"),false,new BigDecimal("0.5") , false, true,"1");
        Assert.assertEquals("-S 0 -R 0.0 -C 1.0 -D 3 -E 0.001 -G 0 -K 2 -P 0.1 -N 0.5 -W 1",StringUtil.join(options," "), "get Lib SVM options");

        options = WekaOptionHelper.getM5RuleOptions(1, 1, 4.0, 1);
        Assert.assertEquals("-N -U -M 4.0 -R",StringUtil.join(options," "), "get M5Rule options");

        options = WekaOptionHelper.getAdaOptions(102, 10, 0, 101);
        Assert.assertEquals("-P 101 -I 10 -batch-size 102",StringUtil.join(options," "), "get Ada Boost options");

        options = WekaOptionHelper.getBaggingOptions(75, 100, 10);
        Assert.assertEquals("-batch-size 100 -P 75 -I 10",StringUtil.join(options," "), "get Bagging options");


    }

}