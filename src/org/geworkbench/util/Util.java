package org.geworkbench.util;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Util {
    public Util() {
    }

    public static HashMap readHashMapFromFile(File file) {
        HashMap map = new HashMap();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String key = st.nextToken();
                String value = st.nextToken();
                map.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static HashMap readHashMapFromFiles(File[] files, String commentString) {

        HashMap map = new HashMap();

        for (int fileCtr = 0; fileCtr < files.length; fileCtr++) {
            try {
                map.putAll(readHashMapFromFile(files[fileCtr], commentString));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static HashMap readHashMapFromFiles(String[] fileNames, String commentString) {

        HashMap map = new HashMap();

        for (int fileCtr = 0; fileCtr < fileNames.length; fileCtr++) {
            try {
                File file = new File(fileNames[fileCtr]);
                if (file.exists()) {
                    map.putAll(readHashMapFromFile(file, commentString));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static HashMap readHashMapFromFile(File file, String commentString) {
        HashMap map = new HashMap();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(commentString)) {
                    StringTokenizer st = new StringTokenizer(line, "\t");
                    if (st.countTokens() == 2) {
                        String key = st.nextToken();
                        String value = st.nextToken();
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static String getString(HashMap map, String key) {
        String value = (String) map.get(key);
        return value;
    }

    public static int getInt(HashMap map, String key) {
        Object val = map.get(key);
        if (val != null) {
            int value = Integer.parseInt((String) val);
            return value;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public static double getDouble(HashMap map, String key) {
        Object val = map.get(key);
        if (val != null) {
            double value = Double.parseDouble((String) val);
            return value;
        } else {
            return Double.NaN;
        }
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Util.class.getResource(path);
        return new ImageIcon(imgURL);
    }


    /**
     * Generates a unique name given a desired name and a set of existing names.
     * Appends ' (n)' to the name for n = 1, 2, 3, ... until a unique name is found.
     * @param desiredName
     * @param existingNames
     * @return
     */
    public static String getUniqueName(String desiredName, Set<String> existingNames) {
        String name = desiredName;
        int i = 0;
        while (existingNames.contains(name)) {
            i++;
            name = desiredName + " (" + i + ")";
        }
        return name;
    }

}
