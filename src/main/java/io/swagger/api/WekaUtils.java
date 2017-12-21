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
     * @throws IOException
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

   /**
    * Option-string helper method for WEKA options from optionname, value and defaultValue.
    * <ul>
    *     <li>sets a value to a given optionname</li>
    *     <li>sets a defaultValue to a given optionname when value is null</li>
    *     <li>sets an option when both values are null</li>
    * </ul>
    * <pre>{@code
    * WekaUtils.getParamString(100, "R", 2) => " -R 100 "
    * WekaUtils.getParamString(null, "H", 2) => " -H 2 "
    * WekaUtils.getParamString(null, "X", null) => " -X "
    * }</pre>
    * @param value        value of the option
    * @param option       optionname to set
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

}
