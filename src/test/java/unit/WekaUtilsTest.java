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

    @Test(description = "Test WekaOptionHelper for WEKA Clusterer params")
    public void getBooleanParam() throws Exception {
        Assert.assertEquals(" -X ", WekaOptionHelper.getBooleanParam("true","X","true"));
        Assert.assertEquals("", WekaOptionHelper.getBooleanParam("true","X","false"));
        Assert.assertEquals(" -X ", WekaOptionHelper.getBooleanParam("false","X","false"));
        Assert.assertEquals(" -H ", WekaOptionHelper.getBooleanParam(1, "H", 1));
        Assert.assertEquals("", WekaOptionHelper.getBooleanParam(1, "S", 0));
    }

    @Test(description = "Test WekaOptionHelper for WEKA params")
    public void getOptions() throws Exception {

        HashMap<String, Object> params = new HashMap<>();
        // J48
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
        Assert.assertEquals("-M 2 -R -N 5 -Q 1 -U", StringUtil.join(options, " "), "get J48 options");

        // KNN
        params = new HashMap<>();
        params.put("windowSize", 0);
        params.put("KNN", 1);
        params.put("crossValidate", 0);
        params.put("distanceWeighting", 0);
        params.put("meanSquared", 0);
        params.put("nearestNeighbourSearchAlgorithm", "LinearNNSearch");
        //Integer windowSize, Integer KNN, Integer crossValidate, String distanceWeighting, Integer meanSquared,
        // String nearestNeighbourSearchAlgorithm
        // 0, 1, 0, "0",0, "LinearNNSearch"
        options = WekaOptionHelper.getIBkOptions(params);
        Assert.assertEquals("-W 0 -K 1 -A weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"", StringUtil.join(options, " "), "get KNN options");


        // Linear Regression
        params = new HashMap<>();
        params.put("attributeSelectionMethod", 0);
        params.put("eliminateColinearAttributes", 1);
        params.put("ridge", 1.0E-8);

        options = WekaOptionHelper.getLROptions(params);
        Assert.assertEquals("-S 0 -R 1.0E-8 -num-decimal-places 4", StringUtil.join(options, " "), "get Bagging options");

        // BayesNet
        params = new HashMap<>();
        params.put("estimator", "SimpleEstimator");
        params.put("estimatorParams", new BigDecimal(0.5));
        params.put("useADTree", 1);
        params.put("searchAlgorithm", "local.K2");
        params.put("searchParams", "-P 1 -S BAYES");

        options = WekaOptionHelper.getBayesNetOptions(params);
        Assert.assertEquals("-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5", StringUtil.join(options, " "), "get BayesNet options");

        // NaiveBayes
        params = new HashMap<>();
        params.put("batchSize", 101);
        params.put("useKernelEstimator", 1);
        params.put("useSupervisedDiscretization", 0);

        options = WekaOptionHelper.getNaiveBayesOptions(params);
        Assert.assertEquals("-batch-size 101 -K", StringUtil.join(options, " "), "get BayesNet options");

        params = new HashMap<>();
        params.put("batchSize", 102);
        params.put("useKernelEstimator", 0);
        params.put("useSupervisedDiscretization", 1);

        options = WekaOptionHelper.getNaiveBayesOptions(params);
        Assert.assertEquals("-batch-size 102 -D", StringUtil.join(options, " "), "get BayesNet options");


        // LibSVM
        params = new HashMap<>();
        params.put("svmType", 0);
        params.put("coef0", 0.0f);
        params.put("cost", 1f);
        params.put("degree", 3);
        params.put("eps", 0.001d);
        params.put("gamma", 0.1d);
        params.put("kernelType", 2);
        params.put("loss", 0.1d);
        params.put("normalize", false);
        params.put("nu", 0.5d);
        params.put("probabilityEstimates", false);
        params.put("shrinking", true);
        params.put("weights", "1");

        options = WekaOptionHelper.getLibSVMOptions(params);
        Assert.assertEquals("-S 0 -R 0.0 -C 1.0 -D 3 -E 0.001 -G 0.1 -K 2 -P 0.1 -N 0.5 -W 1", StringUtil.join(options, " "), "get Lib SVM options");

        // M5Rules
        params = new HashMap<>();
        params.put("unpruned", 1);
        params.put("useUnsmoothed", 1);
        params.put("minNumInstances", 4.0d);
        params.put("buildRegressionTree", 1);
        //Integer unpruned, Integer useUnsmoothed, Double minNumInstances, Integer buildRegressionTree
        options = WekaOptionHelper.getM5RuleOptions(params);
        Assert.assertEquals("-N -U -M 4.0 -R", StringUtil.join(options, " "), "get M5Rule options");

        // MultilayerPerceptron
        params = new HashMap<>();
        params.put("momentum", "0.2");
        params.put("nominalToBinaryFilter", "false");
        params.put("hiddenLayers", "a");
        params.put("validationThreshold", "22");
        params.put("normalizeAttributes", "false");
        params.put("batchSize", "110");
        params.put("decay", "true");
        params.put("validationSetSize", "0");
        params.put("trainingTime", "520");
        params.put("normalizeNumericClass", "true");
        params.put("learningRate", "0.3");
        params.put("reset", "true");
        options = WekaOptionHelper.getMultilayerPerceptronOptions(params);
        Assert.assertEquals("-M 0.2 -B -H a -E 22 -I -batch-size 110 -D -V 0 -N 520 -L 0.3", StringUtil.join(options, " "), "get MultilayerPerceptron options");


        // AdaBoost M1
        params = new HashMap<>();
        params.put("batchSize", 102);
        params.put("numIterations", 10);
        params.put("useResampling", 0);
        params.put("weightThreshold", 101);
        options = WekaOptionHelper.getAdaBoostOptions(params);
        Assert.assertEquals("-P 101 -I 10 -batch-size 102", StringUtil.join(options, " "), "get Ada Boost options");

        // Bagging
        params = new HashMap<>();
        params.put("batchSize", 100);
        params.put("bagSizePercent", 75);
        params.put("numItergations", 10);

        options = WekaOptionHelper.getBaggingOptions(params);
        Assert.assertEquals("-batch-size 100 -P 75 -I 10", StringUtil.join(options, " "), "get Bagging options");


    }

    @Test(description = "Test WekaOptionHelper for WEKA Clusterer params")
    public void getClustererOptions() throws Exception {

        HashMap<String, Object> params = new HashMap<>();
        // SimpleKMeans
        params.put("canopyMaxNumCanopiesToHoldInMemory", "101");
        params.put("canopyMinimumCanopyDensity", "2.1");
        params.put("canopyPeriodicPruningRate", "10001");
        params.put("canopyT1", "-1.24");
        params.put("canopyT2", "-1.01");
        params.put("displayStdDevs", "true");
        params.put("distanceFunction", "weka.core.ManhattanDistance -R 1,2,3,4");
        params.put("dontReplaceMissingValues", "true");
        params.put("fastDistanceCalc", true);
        params.put("initializationMethod", "1");
        params.put("maxIterations", "500");
        params.put("numClusters", "3");
        params.put("numExecutionSlots", "1");
        params.put("preserveInstancesOrder", "true");
        params.put("reduceNumberOfDistanceCalcsViaCanopies", "true");
        params.put("seed", "11");

        String[] options = WekaOptionHelper.getSimpleKMeansOptions(params);
        Assert.assertEquals("-max-candidates 101 -min-density 2.1 -periodic-pruning 10001 -t1 -1.24 -t2 -1.01 -V -A weka.core.ManhattanDistance -R 1,2,3,4 -M -fast -init 1 -I 500 -N 3 -num-slots 1 -O -C -S 11", StringUtil.join(options, " "), "get SimpleKMeans options");

        // Hierarchical
        params = new HashMap<>();
        params.put("printNewick", "true");
        params.put("numClusters", 3);
        params.put("linkType", "MEAN");
        params.put("distanceIsBranchLength", "true");
        params.put("distanceFunction", "weka.core.ManhattanDistance -R first-last");
        options = WekaOptionHelper.getHierarchicalClustererOptions(params);
        Assert.assertEquals("-P -N 3 -L MEAN -B -A weka.core.ManhattanDistance -R first-last", StringUtil.join(options, " "), "get Hierachical Clusterer options");

    }
}