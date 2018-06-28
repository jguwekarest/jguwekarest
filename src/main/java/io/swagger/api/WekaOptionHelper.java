package io.swagger.api;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper Class to translate webparam names into WEKA library params.
 */
public class WekaOptionHelper {

    private static final Logger LOG = Logger.getLogger(WekaOptionHelper.class.getName());
    private static final String QUOTE = "\"";
    public static String[] getClassifierOptions(String classifier, HashMap params){
        String[] options = null;
        switch (classifier) {
            case "BayesNet":
                options = getBayesNetOptions(params);
                break;
            case "DecisionStump":
                // has no options
                break;
            case "GaussianProcesses":
                options = getGaussianProcessesOptions(params);
                break;
            case "IBk":
                options = getIBkOptions(params);
                break;
            case "J48":
                options = getJ48Options(params);
                break;
            case "NaiveBayes":
                options = getNaiveBayesOptions(params);
                break;
            case "LinearRegression":
                options = getLROptions(params);
                break;
            case "LibSVM":
                options = getLibSVMOptions(params);
                break;
            case "Logistic":
                options = getLogisticOptions(params);
                break;
            case "M5P":
                options = getM5POptions(params);
                break;
            case "M5Rule":
                options = getM5RuleOptions(params);
                break;
            case "MultilayerPerceptron":
                options = getMultilayerPerceptronOptions(params);
                break;
            case "RandomForest":
                options = getRandomForestOptions(params);
                break;
            case "SMO":
                options = getSMOOptions(params);
                break;
            case "SMOreg":
                options = getSMOregOptions(params);
                break;
            case "ZeroR":
                //ZeroR has no options
                break;
            case "AdaBoost":
                options = getAdaBoostOptions(params);
                break;
            case "Bagging":
                options = getBaggingOptions(params);
                break;
        }
        return options;
    }

    /**
     * Generate option string for J48
     * @param params HashMap: binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace
     * @return options array
     */
    public static String[] getJ48Options(HashMap params) {
        String parameters = "";
        if (params.get("binarySplits") != null && params.get("binarySplits").toString().equals("1")) {
            parameters += " -B ";
        }

        parameters += getParamString(params.get("minNumObj"), "M", 2);

        if (params.get("reducedErrorPruning") != null && params.get("reducedErrorPruning").toString().equals("1")) {
            if (params.get("numFolds") != null) {
                parameters += " -R -N " + params.get("numFolds");
            } else {
                parameters += " -R -N 3 ";
            }
        }

        parameters += getParamString(params.get("seed"), "Q", 2);

        if (params.get("unpruned") != null && params.get("unpruned").toString().equals("1")) {
            parameters += " -U ";
        } else {
            if (params.get("subtreeRaising") != null && params.get("subtreeRaising").toString().equals("0")) {
                parameters += " -S ";
            }
            if (params.get("confidenceFactor") != null) {
                parameters += " -C " + params.get("confidenceFactor");
            } else {
                parameters += " -C 0.25 ";
            }
        }

        if (params.get("useLaplace") != null && params.get("useLaplace").toString().equals("1")) {
            parameters += " -A ";
        }

        LOG.log(Level.INFO,"parameterstring for weka: weka.classifiers.trees.J48 " + parameters);
        
        return splitOptions(parameters);
    }

    /**
     * Generate option string for IBk
     * @param params HashMap: windowSize, IBk, crossValidate, distanceWeighting, meanSquared, nearestNeighbourSearchAlgorithm
     * @return options array
     */
    public static String[] getIBkOptions(HashMap params) {
        String parameters = "";
        parameters += getParamString(params.get("windowSize"), "W", 0);
        parameters += getParamString(params.get("KNN"), "K", 1);
        parameters += ((params.get("crossValidate") != null && !params.get("crossValidate").toString().equals("0")) ? " -X " : "");

        if (params.get("distanceWeighting") != null) {
            if (params.get("distanceWeighting").equals("F") || params.get("distanceWeighting").equals("I")) {
                parameters += " -" + params.get("distanceWeighting") + " ";
            }
        }

        if (params.get("meanSquared") != null && !params.get("meanSquared").toString().equals("0")) parameters += " -E ";


        //if (nearestNeighbourSearchAlgorithm != null && !nearestNeighbourSearchAlgorithm.isEmpty()) {
            //use LinearNNSearch fixed
            parameters += " -A ";
            parameters += "\"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";
        //}

        LOG.log(Level.INFO,"parameterstring for weka: IBk " + parameters.replaceAll("( )+", " "));

        return splitOptions(parameters);
    }

    /**
     * Generate option string for BayesNet
     * @param params HashMap: estimator, estimatorParams, useADTree, searchAlgorithm, searchParams
     * @return options array
     */
    public static String[] getBayesNetOptions(HashMap params){
        String parameters = "";
        if (params.get("useADTree") != null && !params.get("useADTree").toString().equals("0")) { parameters += " -D ";}

        // Set the parameter for the searchAlgo
        parameters += " -Q ";
        parameters += "weka.classifiers.bayes.net.search." + params.get("searchAlgorithm");
        System.out.println("searchAlgorithm is: " + params.get("searchAlgorithm"));
        // Set the search parameters
        if (params.get("searchParams") != null) {
            parameters += " -- ";
            parameters += params.get("searchParams");
        }
        // Set estimator
        if (params.get("estimator") != null) {
            parameters += " -E ";
            parameters += "weka.classifiers.bayes.net.estimate." + params.get("estimator");
        }
        // Set the parameters for the estimator
        if (params.get("estimatorParams") != null) {
            parameters += " -- ";
            parameters += " -A ";
            parameters += params.get("estimatorParams");
        }
        LOG.log(Level.INFO,"parameterstring for weka: " + parameters);

        return splitOptions(parameters);
    }

    public static String[] getNaiveBayesOptions(HashMap params){
        String parameters = "";

        parameters += WekaOptionHelper.getParamString(params.get("batchSize") ,"batch-size", 100);

        if (params.get("useKernelEstimator") != null && params.get("useKernelEstimator").toString().equals("1")) {
            parameters += " -K ";
        } else if (params.get("useSupervisedDiscretization") != null && params.get("useSupervisedDiscretization").toString().equals("1")) {
            parameters += " -D ";
        }

        return splitOptions(parameters);
    }

    /**
     * Generate option string for Gaussian Processes
     * @param params HashMap
     * @return options array
     */
    public static String[] getGaussianProcessesOptions(HashMap params){
        String parameters = "";
        parameters += getParamString(params.get("batchSize"), "batch-size", "100");
        parameters += getParamString(QUOTE + params.get("kernel")+ QUOTE, "K","\"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\"");
        parameters += getParamString(params.get("filterType"), "N","0");
        parameters += getParamString(QUOTE + params.get("noise") + QUOTE, "L","1.0");
        parameters += " -S 1 "; //seed = 1
        return splitOptions(parameters);
    }


    /**
     * Generate option string for LibSVM
     * @param params HashMap: svmType, coef0, cost, degree, eps, gamma, kernelType, loss, normalize, nu, probabilityEstimates, shrinking, weights
     * @return options array
     */
    public static String[] getLibSVMOptions(HashMap params){
        String parameters = "";
        parameters += getParamString(params.get("svmType"), "S", 0);
        parameters += getParamString(params.get("coef0"), "R", 0);
        parameters += getParamString(params.get("cost"), "C", "1.0");
        parameters += getParamString(params.get("degree"), "D", 3);
        parameters += getParamString(params.get("eps"), "E", "0.001");
        parameters += getParamString(params.get("gamma"), "G", "0.0");
        parameters += getParamString(params.get("kernelType"), "K", 0);
        parameters += getParamString(params.get("loss"), "P", "0.1");

        if(params.get("normalize") != null && params.get("normalize").toString().equals("true")) parameters += " -Z ";

        parameters += getParamString(params.get("nu"), "N", "0.5");

        if (params.get("probabilityEstimates") != null && params.get("probabilityEstimates").toString().equals("true")) parameters +=  " -B ";

        if (params.get("shrinking") != null && !params.get("shrinking").toString().equals("true")) parameters +=  " -H ";

        if(params.get("weights") != null && !Objects.equals(params.get("weights").toString(), "")) parameters +=  " -W \"" + params.get("weights") + "\"";

        LOG.log(Level.INFO,"parameterstring for weka: weka.classifiers.function.LibSVM " + parameters);

        return splitOptions(parameters);
    }

    /**
     * Generate option string for Logistic
     * @param params HashMap
     * @return options array
     */
    public static String[] getLogisticOptions(HashMap params){
        String parameters = "";
        if (params.get("useConjugateGradientDescent") != null && (params.get("useConjugateGradientDescent").toString().equals("1")||params.get("useConjugateGradientDescent").toString().equals("true"))) { parameters += " -C ";}
        if (params.get("ridge") != null ) parameters += " -R " + params.get("ridge") + " "; //1.0E-8
        parameters += WekaOptionHelper.getParamString(params.get("maxIts"), "M", "-1");
        parameters += " -num-decimal-places 4 "; //set default of 4
        LOG.log(Level.INFO,"parameterstring for weka: weka.classifiers.functions.Logistic " + parameters);
        return splitOptions(parameters);
    }


    /**
     * Generate option string for Linear Regression
     * @param params HashMap: attributeSelectionMethod, eliminateColinearAttributes, ridge
     * @return options array
     */
    public static String[] getLROptions(HashMap params) {
        String parameters = "";

        parameters += ((params.get("attributeSelectionMethod") != null) ? (" -S " + params.get("attributeSelectionMethod") + " ") : (" -S 1 ") );
        if (params.get("eliminateColinearAttributes") != null && params.get("eliminateColinearAttributes").toString().equals("0")) parameters += " -C ";
        if (params.get("ridge") != null ) parameters += " -R " + params.get("ridge") + " ";
        parameters += " -num-decimal-places 4 "; //set default of 4

        LOG.log(Level.INFO,"parameterstring for weka: weka.classifiers.functions.LinearRegression " + parameters);

        return splitOptions(parameters);
    }

    /**
     * Generate option string for M5P
     * @param params HashMap
     * @return options array
     */
    public static String[] getM5POptions(HashMap params) {
        String parameters = "";
        if (params.get("unpruned") != null && params.get("unpruned").toString().equals("1")) { parameters += " -N ";}

        if (params.get("useUnsmoothed") != null && params.get("useUnsmoothed").toString().equals("1")) { parameters += " -U ";}
        // Set minNumInstances
        parameters += WekaOptionHelper.getParamString(params.get("minNumInstances"), "M", "4.0");
        // set buildRegressionTree
        if (params.get("buildRegressionTree") != null && params.get("buildRegressionTree").toString().equals("1")) { parameters += " -R ";}

        LOG.log(Level.INFO,"parameterstring for weka: M5Rules " + parameters);

        return splitOptions(parameters);
    }

    /**
     * Generate option string for M5Rule
     * @param params HashMap: unpruned, useUnsmoothed, minNumInstances, buildRegressionTree
     * @return options array
     */
    public static String[] getM5RuleOptions(HashMap params) {
        String parameters = "";
        // set unpruned
        if (params.get("unpruned") != null && params.get("unpruned").toString().equals("1")) { parameters += " -N ";}
        // set use unsmoothed
        if (params.get("useUnsmoothed") != null && params.get("useUnsmoothed").toString().equals("1")) { parameters += " -U ";}
        // Set minNumInstances
        parameters += WekaOptionHelper.getParamString(params.get("minNumInstances"), "M", "4.0");
        // set buildRegressionTree
        if (params.get("buildRegressionTree") != null && params.get("buildRegressionTree").toString().equals("1")) { parameters += " -R ";}

        LOG.log(Level.INFO,"parameterstring for weka: M5Rules " + parameters);

        return splitOptions(parameters);
    }


    /**
     * Generate option string for MultilayerPerceptron
     * @param params HashMap
     * @return options array
     */
    public static String[] getMultilayerPerceptronOptions(HashMap params){
        String parameters = "";

        parameters += getParamString(params.get("momentum"),"M","0.2");
        parameters += getBooleanParam(params.get("nominalToBinaryFilter"), "B", "false");
        parameters += getParamString(params.get("hiddenLayers"), "H", "a");
        parameters += getParamString(params.get("validationThreshold"),"E", "20");
        parameters += getBooleanParam(params.get("normalizeAttributes"),"I","false");
        parameters += getParamString(params.get("batchSize"),"batch-size","100");
        parameters += getBooleanParam(params.get("decay"),"D","true");
        parameters += getParamString(params.get("validationSetSize"),"V", "0");
        parameters += getParamString(params.get("trainingTime"),"N","500");
        parameters += getBooleanParam(params.get("normalizeNumericClass"),"C", "false");
        parameters += getParamString(params.get("learningRate"),"L","0.3" );
        parameters += getBooleanParam(params.get("reset"), "R","false");

        return splitOptions(parameters);
    }

    /**
     * Generate option string for RandomForest
     * @param params HashMap
     * @return options array
     */
    public static String[] getRandomForestOptions(HashMap params){
        String parameters = "";
        parameters += getBooleanParam(params.get("storeOutOfBagPredictions"), "store-out-of-bag-predictions","true");
        parameters += getParamString(params.get("numExecutionSlots"), "num-slots", "1");
        parameters += getParamString(params.get("bagSizePercent"),"P","100");
        parameters += getParamString(params.get("numDecimalPlaces"), "num-decimal-places","2");
        parameters += getParamString(params.get("batchSize"), "batch-size", "100");
        parameters += getBooleanParam(params.get("printClassifiers"),"print", "true");
        parameters += getParamString(params.get("numIterations"), "I", "100");
        parameters += getBooleanParam(params.get("outputOutOfBagComplexityStatistics"), "output-out-of-bag-complexity-statistics","true");
        parameters += getBooleanParam(params.get("breakTiesRandomly"),"B","true");
        parameters += getParamString(params.get("maxDepth"), "depth","0");
        parameters += getBooleanParam(params.get("computeAttributeImportance"), "attribute-importance","true");
        parameters += getBooleanParam(params.get("calcOutOfBag"), "O", "true");
        parameters += getParamString(params.get("numFeatures"), "K","0");
        parameters += " -S 1 ";
        return splitOptions(parameters);
    }

    /**
     * Generate option string for SMO
     * @param params HashMap
     * @return options array
     */
    public static String[] getSMOOptions(HashMap params) {
        String parameters = "";
        parameters += getParamString(params.get("numFolds"), "V","-1");
        parameters += getParamString(params.get("c"), "C", "1.0");
        parameters += getParamString(params.get("batchSize"), "batch-size", "100");
        parameters += getParamString(QUOTE + params.get("kernel") + QUOTE, "K","\"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\"");
        parameters += getParamString(params.get("filterType"), "N","0");
        parameters += getParamString(QUOTE + params.get("calibrator") + QUOTE, "calibrator","\"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"");
        parameters += " -L 0.001 "; //The tolerance parameter (shouldn't be changed)
        return splitOptions(parameters);
    }

    /**
     * Generate option string for SMO
     * @param params HashMap
     * @return options array
     */
    public static String[] getSMOregOptions(HashMap params) {
        String parameters = "";
        parameters += getParamString(params.get("c"), "C", "1.0");
        parameters += getParamString(params.get("batchSize"), "batch-size", "100");
        parameters += getParamString(QUOTE + params.get("kernel") + QUOTE, "K","\"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\"");
        parameters += getParamString(params.get("filterType"), "N","0");
        parameters += getParamString(QUOTE + params.get("regOptimizer") + QUOTE, "I","\"weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1\"");
        return splitOptions(parameters);
    }

    /**
     * Generate option string for Adaboost M1
     * @param params HashMap: batchSize, numIterations, useResampling, weightThreshold
     * @return options array
     */
    public static String[] getAdaBoostOptions(HashMap params) {
        String parameters = "";
        parameters += WekaOptionHelper.getParamString(params.get("weightThreshold"), "P", "100");
        if (params.get("useResampling") != null && params.get("useResampling").toString().equals("1")) { parameters += " -Q ";}

        parameters += WekaOptionHelper.getParamString(params.get("numIterations"),"I", 10);
        parameters += WekaOptionHelper.getParamString(params.get("batchSize") ,"batch-size", 100);
        LOG.log(Level.INFO,"parameterstring for weka: weka.classifiers.meta.AdaBoostM1 " + parameters);

        return splitOptions(parameters);
    }

    /**
     * Generate option string for Bagging
     * @param params HashMap: bagSizePercent, batchSize, numIterations
     * @return options array
     */
    public static String[] getBaggingOptions(HashMap params){
        String parameters = "";

        parameters += WekaOptionHelper.getParamString(params.get("batchSize") ,"batch-size", 100);
        parameters += WekaOptionHelper.getParamString(params.get("bagSizePercent") ,"P", 100);
        parameters += WekaOptionHelper.getParamString(params.get("numIterations"),"I", 10);
        LOG.log(Level.INFO,"parameterstring for weka: weka.classifiers.meta.Bagging " + parameters);
        return splitOptions(parameters);
    }

    /**
     * Option-string helper method for WEKA options build from option, value and defaultValue.
     * <ul>
     *     <li>sets a value to a given option</li>
     *     <li>sets a defaultValue to a given option when value is null</li>
     *     <li>sets an option when both value and defaultValue are null</li>
     * </ul>
     * <pre>{@code
     * WekaUtils.getParamString(100, "R", 2) => " -R 100 "
     * WekaUtils.getParamString(null, "H", 2) => " -H 2 "
     * WekaUtils.getParamString(null, "X", null) => " -X "
     * }</pre>
     * @param value        value of the option
     * @param option       option to set
     * @param defaultValue default value is set when value is null
     * @return String the resulting string
     */
     public static String getParamString(Object value, String option, Object defaultValue ){
         if (value == null && defaultValue == null){
             return " -" + option + " ";
         } else {
             return ((value != null) ? (" -" + option + " " + value + " ") : (" -" + option + " " + defaultValue + " ") );
         }
     }


    /**
     * Option-string helper method for WEKA boolean options
     * <pre>{@code
     * WekaUtils.getBooleanParam("true", "R", true) => " -R "
     * WekaUtils.getBooleanParam("false", "X", false) => " -X "
     * WekaUtils.getBooleanParam(1, "H", 1) => " -H "
     * WekaUtils.getBooleanParam(1, "X", 0) => ""
     * }</pre>
     * @param value          value of the boolean option, e.g.: 1, true, -1
     * @param option         option to set
     * @param trueReference  to be compared with value
     * @return String the resulting string
     */
    public static String getBooleanParam(Object value, String option, Object trueReference ){
        if (value != null || trueReference != null){
            if (value.toString().toLowerCase().equals(trueReference.toString().toLowerCase())) return " -" + option + " ";
        }
        return "";
    }

    /**
     * Split parameter string with weka.core.Utils.splitOptions method
     * @param parameters parameter string to split to String[]
     * @return String[] options for weka library
     */
    public static String[] splitOptions(String parameters) {
        String[] options;
        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return options;
    }


    public static String[] getClustererOptions(String clusterer, HashMap params){
        String[] options = null;
        switch (clusterer) {
            case "EM":
                options = getEMOptions(params);
                break;
            case "HierarchicalClusterer":
                options = getHierarchicalClustererOptions(params);
                break;
            case "SimpleKMeans":
                options = getSimpleKMeansOptions(params);
                break;
        }

        return options;
    }


    public static String[] getEMOptions(HashMap params){
        String parameters = "";
        // numFolds
        parameters += WekaOptionHelper.getParamString(params.get("numFolds"), "X", 10);
        // numKMeansRuns
        parameters += WekaOptionHelper.getParamString(params.get("numKMeansRuns"), "K", 10);
        // maximumNumberOfClusters
        parameters += WekaOptionHelper.getParamString(params.get("maximumNumberOfClusters"),"max", -1);
        // numClusters
        parameters += WekaOptionHelper.getParamString(params.get("numClusters"),"N", -1);
        // maxIterations
        parameters += WekaOptionHelper.getParamString(params.get("maxIterations"),"I", 100);

        return splitOptions(parameters);
    }

    public static String[] getHierarchicalClustererOptions(HashMap params){
        String parameters = "";
        parameters += getBooleanParam(params.get("printNewick"), "P","true");
        parameters += getParamString(params.get("numClusters"),"N", 2);
        parameters += getParamString(params.get("linkType"),"L", "SINGLE");
        parameters += getBooleanParam(params.get("distanceIsBranchLength"), "B","true");
        parameters += getParamString(QUOTE + params.get("distanceFunction") + QUOTE,"A", "\"weka.core.EuclideanDistance -R first-last\"");
        return splitOptions(parameters);
    }

    public static String[] getSimpleKMeansOptions(HashMap params){
        String parameters = "";
        parameters += getParamString(params.get("canopyMaxNumCanopiesToHoldInMemory"),"max-candidates", 100);
        parameters += getParamString(params.get("canopyMinimumCanopyDensity"),"min-density", 2.0);
        parameters += getParamString(params.get("canopyPeriodicPruningRate"),"periodic-pruning", 10000);
        parameters += getParamString(params.get("canopyT1"),"t1", -1.25);
        parameters += getParamString(params.get("canopyT2"),"t2", -1.0);
        parameters += getBooleanParam(params.get("displayStdDevs"), "V","true");
        parameters += getParamString(QUOTE + params.get("distanceFunction") + QUOTE,"A", "\"weka.core.EuclideanDistance -R first-last\"");
        parameters += getBooleanParam(params.get("dontReplaceMissingValues"), "M","true");
        parameters += getBooleanParam(params.get("fastDistanceCalc"), "fast","true");
        parameters += getParamString(params.get("initializationMethod"),"init", 0);
        parameters += getParamString(params.get("maxIterations"),"I", 500);
        parameters += getParamString(params.get("numClusters"),"N", 2);
        parameters += getParamString(params.get("numExecutionSlots"),"num-slots", 1);
        parameters += getBooleanParam(params.get("preserveInstancesOrder"), "O","true");
        parameters += getBooleanParam(params.get("reduceNumberOfDistanceCalcsViaCanopies"), "C","true");
        parameters += getParamString(params.get("seed"), "S", 10);
        return splitOptions(parameters);
    }

}