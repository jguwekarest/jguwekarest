package io.swagger.api;

import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.IOException;
import java.io.StringReader;

public class WekaUtils {

    /**
     * Get Instances out of an arff string
     * @param arff String arff data
     * @param setClass Boolean set a class
     * @return Instances
     * @throws IOException error
     */
    public static Instances instancesFromString(String arff, Boolean setClass) throws IOException {
        StringReader reader = new StringReader(arff);
        Instances insts = new Instances(reader);
        if (insts.classIndex() == -1 && setClass) insts.setClassIndex(insts.numAttributes() - 1);
        return insts;
    }

    /**
     * Write a model to filesystem
     * @param model WEKA model
     * @param filename to save to
     * @return Boolean success
     */
    public static Boolean saveWekaModel(Object model, String filename) {
        try {
            SerializationHelper.write(filename, model);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
