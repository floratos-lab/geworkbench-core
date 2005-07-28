/**
 * <p>Title: caWorkbench</p>
 *
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 *
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 *
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 * This code was adapted from the Weks project: weka.core.Utils
 */


package org.geworkbench.bison.util;

import java.io.File;

public final class OptionUtils {
    /**
     * Checks if the given array contains the flag "-Char". Stops
     * searching at the first marker "--". If the flag is found,
     * it is replaced with the empty string.
     *
     * @param flag    the character indicating the flag.
     * @param strings the array of strings containing all the options.
     * @return true if the flag was found
     * @throws Exception if an illegal option was found
     */

    public static boolean getFlag(char flag, String[] options) throws Exception {

        if (options == null) {
            return false;
        }
        for (int i = 0; i < options.length; i++) {
            if ((options[i].length() > 1) && (options[i].charAt(0) == '-')) {
                try {
                    Double dummy = Double.valueOf(options[i]);
                } catch (NumberFormatException e) {
                    if (options[i].length() > 2) {
                        throw new Exception("Illegal option: " + options[i]);
                    }
                    if (options[i].charAt(1) == flag) {
                        options[i] = "";
                        return true;
                    }
                    if (options[i].charAt(1) == '-') {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets an option indicated by a flag "-Char" from the given array
     * of strings. Stops searching at the first marker "--". Replaces
     * flag and option with empty strings.
     *
     * @param flag    the character indicating the option.
     * @param options the array of strings containing all the options.
     * @return the indicated option or an empty string
     * @throws Exception if the option indicated by the flag can't be found
     */
    public static /*@non_null@*/ String getOption(char flag, String[] options) throws Exception {

        String newString;

        if (options == null)
            return "";
        for (int i = 0; i < options.length; i++) {
            if ((options[i].length() > 0) && (options[i].charAt(0) == '-')) {

                // Check if it is a negative number
                try {
                    Double dummy = Double.valueOf(options[i]);
                } catch (NumberFormatException e) {
                    if (options[i].length() != 2) {
                        throw new Exception("Illegal option: " + options[i]);
                    }
                    if (options[i].charAt(1) == flag) {
                        if (i + 1 == options.length) {
                            throw new Exception("No value given for -" + flag + " option.");
                        }
                        options[i] = "";
                        newString = new String(options[i + 1]);
                        options[i + 1] = "";
                        return newString;
                    }
                    if (options[i].charAt(1) == '-') {
                        return "";
                    }
                }
            }
        }
        return "";
    }

    public static int getIntOption(char flag, String[] options) throws Exception {
        String strOption = getOption(flag, options);
        return Integer.parseInt(strOption);
    }

    public static float getFloatOption(char flag, String[] options) throws Exception {
        String strOption = getOption(flag, options);
        return Float.parseFloat(strOption);
    }

    public static double getDoubleOption(char flag, String[] options) throws Exception {
        String strOption = getOption(flag, options);
        return Double.parseDouble(strOption);
    }

    public static File getFileOption(char flag, String[] options) throws Exception {
        String strOption = getOption(flag, options);
        return new File(strOption);
    }

    public static boolean getBooleanOption(char flag, String[] options) throws Exception {
        String strOption = getOption(flag, options);
        return Boolean.parseBoolean(strOption);
    }


    /**
     * Returns the secondary set of options (if any) contained in
     * the supplied options array. The secondary set is defined to
     * be any options after the first "--". These options are removed from
     * the original options array.
     *
     * @param options the input array of options
     * @return the array of secondary options
     */
    public static String[] partitionOptions(String[] options) {

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals("--")) {
                options[i++] = "";
                String[] result = new String[options.length - i];
                for (int j = i; j < options.length; j++) {
                    result[j - i] = options[j];
                    options[j] = "";
                }
                return result;
            }
        }
        return new String[0];
    }


    /**
     * Joins all the options in an option array into a single string,
     * as might be used on the command line.
     *
     * @param optionArray the array of options
     * @return the string containing all options.
     */
    public static String joinOptions(String[] optionArray) {

        String optionString = "";
        for (int i = 0; i < optionArray.length; i++) {
            if (optionArray[i].equals("")) {
                continue;
            }
            if (optionArray[i].indexOf(' ') != -1) {
                optionString += '"' + optionArray[i] + '"';
            } else {
                optionString += optionArray[i];
            }
            optionString += " ";
        }
        return optionString.trim();
    }
}

