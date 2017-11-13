package jgu.weka.api;

import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.IOException;
import java.io.StringReader;

public class WekaUtils {
    public static Instances instancesFromString(String arff) throws IOException {
        StringReader reader = new StringReader(arff);
        Instances insts = new Instances(reader);
        if (insts.classIndex() == -1)
            insts.setClassIndex(insts.numAttributes() - 1);
        return insts;
    }

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
