package io.swagger.api;

import weka.core.Instances;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;

public class WekaUtils {
    public static Instances instancesFromString(String arff) throws IOException {
        StringReader reader = new StringReader(arff);
        Instances insts = new Instances(reader);
        if (insts.classIndex() == -1)
            insts.setClassIndex(insts.numAttributes() - 1);
        return insts;
    }


    public static Boolean saveWekaModel(Object v, String filename) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new FileOutputStream(filename));
            oos.writeObject(v);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
