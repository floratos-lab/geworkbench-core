package org.geworkbench.util.pathwaydecoder.bind;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author Ta-tsen Soong
 * @version 1.0
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class myio {
    public myio() {
    }

    public static boolean isDouble(String d) {
        boolean bMIinteraction = false;
        try {
            // is it a number???
            double val = Double.parseDouble(d);
            bMIinteraction = true; // it's a temporary one -> meaning this is an MI edge
        } catch (NumberFormatException nx) {
            bMIinteraction = false;
        }
        return bMIinteraction;
    }

    public static String decimal(double num) {
        DecimalFormat df1 = new DecimalFormat("###.0000");
        return df1.format(num);
    }

    public static String decimal(double num, String format) {
        DecimalFormat df1 = new DecimalFormat(format);
        return df1.format(num);
    }

    public static String vector2tring(Vector v, String seperator) {
        if (v.size() == 0) return "";
        StringBuffer output = new StringBuffer();
        int vsize = v.size();
        for (int i = 0; i < vsize - 1; i++) {
            output.append(v.get(i) + seperator);
        }
        output.append(v.get(vsize - 1));
        return output.toString();
    }

    public static String array2string(int[] sl, String seperator) {
        if (sl.length == 0) return "";
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < sl.length - 1; i++) {
            output.append(sl[i] + seperator);
        }
        output.append(sl[sl.length - 1]);
        return output.toString();
    }

    public static String array2string(ArrayList sl, String seperator) {
        if (sl.size() == 0) return "";
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < sl.size() - 1; i++) {
            output.append(sl.get(i) + seperator);
        }
        output.append(sl.get(sl.size() - 1));
        return output.toString();
    }

    public static String array2string(String[] sl, String seperator) {
        if (sl.length == 0) return "";
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < sl.length - 1; i++) {
            output.append(sl[i] + seperator);
        }
        output.append(sl[sl.length - 1]);
        return output.toString();
    }

    public static ArrayList obj2arraylist(Object[] obj) {
        ArrayList al = new ArrayList();
        if (obj == null) return al;
        for (int i = 0; i < obj.length; i++) {
            al.add(obj[i].toString());
        }
        return al;
    }
}
