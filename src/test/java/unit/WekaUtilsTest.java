package unit;

import io.swagger.api.StringUtil;
import io.swagger.api.WekaOptionHelper;
import io.swagger.api.WekaUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import weka.core.Instances;

import java.math.BigDecimal;
import java.util.HashMap;

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
        HashMap<String, Object> params = new HashMap<>();

        params.put("binarySplits", "0");
        params.put("confidenceFactor", "0.25");
        params.put("minNumObj", "2");
        params.put("numFolds", "5");
        params.put("reducedErrorPruning", "1");
        params.put("seed", "1");
        params.put("subtreeRaising", "1");
        params.put("unpruned", "1");
        params.put("useLaplace", "0");

        String[] options = WekaOptionHelper.getJ48Options(params);
        Assert.assertEquals("-M 2 -R -N 5 -Q 1 -U", StringUtil.join(options," "), "get J48 options");

        options = WekaOptionHelper.getKNNOptions(0, 1, 0, "0",0, "LinearNNSearch");
        Assert.assertEquals("-W 0 -K 1 -A weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"",StringUtil.join(options," "), "get KNN options");

        params = new HashMap<>();
        params.put("estimator", "SimpleEstimator");
        params.put("estimatorParams", new BigDecimal(0.5));
        params.put("useADTree", 1);
        params.put("searchAlgorithm",  "local.K2");
        params.put("searchParams", "-P 1 -S BAYES");

        //options = WekaOptionHelper.getBayesNetOptions("SimpleEstimator", new BigDecimal(0.5), 0, "local.K2","-P 1 -S BAYES");
        options = WekaOptionHelper.getBayesNetOptions(params);
        Assert.assertEquals("-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",StringUtil.join(options," "), "get BayesNet options");

        options = WekaOptionHelper.getLibSVMOptions(0, 0.0F, 1F, 3,new BigDecimal("0.001"), new BigDecimal(0),
                2,new BigDecimal("0.1"),false,new BigDecimal("0.5") , false, true,"1");
        Assert.assertEquals("-S 0 -R 0.0 -C 1.0 -D 3 -E 0.001 -G 0 -K 2 -P 0.1 -N 0.5 -W 1",StringUtil.join(options," "), "get Lib SVM options");

        options = WekaOptionHelper.getM5RuleOptions(1, 1, 4.0, 1);
        Assert.assertEquals("-N -U -M 4.0 -R",StringUtil.join(options," "), "get M5Rule options");

        params = new HashMap<>();
        params.put("batchSize", 102);
        params.put("numIterations", 10);
        params.put("useResampling",0);
        params.put("weightThreshold",101);
        options = WekaOptionHelper.getAdaBoostOptions(params);
        Assert.assertEquals("-P 101 -I 10 -batch-size 102",StringUtil.join(options," "), "get Ada Boost options");

        params = new HashMap<>();
        params.put("batchSize", 100);
        params.put("bagSizePercent",75);
        params.put("numItergations", 10);

        options = WekaOptionHelper.getBaggingOptions(params);
        Assert.assertEquals("-batch-size 100 -P 75 -I 10",StringUtil.join(options," "), "get Bagging options");


    }

}