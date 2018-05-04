package io.swagger.api.impl;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.util.Random;

public class Validation {
    public static String crossValidation(Instances instances, Classifier algorithm) {
        return crossValidation(instances, algorithm,10);
    }

    public static String leaveOneOutValidation(Instances instances, Classifier algorithm) {
        Integer folds = instances.numInstances();
        return crossValidation(instances, algorithm, folds);
    }

    public static String crossValidation(Instances instances, Classifier algorithm, Integer folds) {
        String eval_out = "";
        try {
            Evaluation eval = new Evaluation(instances);
            eval.crossValidateModel(algorithm, instances, folds, new Random(1));
            try {
                eval_out = eval.toSummaryString("\n=== Crossvalidation Results ===\n", false);
            }catch(Exception e){
                System.out.println("Weka Validation error: " + e.getMessage() + "\n");
            }
            try {
                eval_out += "\n" + eval.toClassDetailsString() + "\n";
            }catch(Exception e){
                System.out.println("Weka Validation error: " + e.getMessage() + "\n");
            }
            try {
                eval_out += "\n" + eval.toMatrixString() + "\n";
            }catch(Exception e){
                System.out.println("Weka Validation error: " + e.getMessage() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Weka Validation error: " + e.getMessage() + "\n");
            return "";
        }
        return eval_out;
    }
}
