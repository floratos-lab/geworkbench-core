package org.geworkbench.bison.util;

import java.util.ArrayList;

/**
 * @author John Watkinson
 */
public class StringUtils {

    public static String[] splitRemovingEmptyStrings(String s, String regex) {
        String[] tokens = s.split(regex);
        ArrayList<String> list = new ArrayList<String>();
        for (String token : tokens) {
            if (token.length() > 0) {
                list.add(token);
            }
        }
        return list.toArray(new String[0]);
    }

}
