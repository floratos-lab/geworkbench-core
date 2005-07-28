/*
 * Created on Mar 6, 2003
 *
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.ksu.cis.kdd.util;

import java.util.LinkedList;
import java.util.List;

/**
 * <P>Helper class to process parameters
 *
 * @author Roby Joehanes
 */
public class Parameter {

    public static ParameterTable process(String[] args) {
        ParameterTable result = new ParameterTable();
        if (args == null || args.length == 0) return result;

        int max = args.length;
        for (int i = 0; i < max; i++) {
            if (args[i].charAt(0) == '-') {
                int equalIdx = args[i].indexOf(':');
                if (equalIdx == -1) {
                    result.put(args[i], Boolean.TRUE);
                } else {
                    String param = args[i].substring(0, equalIdx).trim();
                    String contents = args[i].substring(equalIdx + 1).trim();
                    result.put(param, contents);
                }
            } else {
                List nodash = (List) result.get(".standard"); // $NON-NLS-1$
                if (nodash == null) nodash = new LinkedList();
                nodash.add(args[i]);
                result.put(".standard", nodash); // $NON-NLS-1$
            }
        }

        return result;
    }

    public static ParameterTable processCurrentParams(String[] args) {
        ParameterTable result = new ParameterTable();
        if (args == null || args.length == 0) return result;

        int max = args.length;
        for (int i = 0; i < max; i++) {
            if (args[i].charAt(0) == '-') {
                int equalIdx = args[i].indexOf(':');
                if (equalIdx == -1) {
                    result.put(args[i], Boolean.TRUE);
                } else {
                    String param = args[i].substring(0, equalIdx).trim();
                    String contents = args[i].substring(equalIdx + 1).trim();
                    result.put(param, contents);
                }
            } else
                break;
        }
        return result;
    }

    public static String[] spliceSubModuleParams(String[] args) {
        if (args == null || args.length == 0) return new String[0];

        int max = args.length;
        boolean found = false;
        LinkedList ll = new LinkedList();

        for (int i = 0; i < max; i++) {
            if (found) {
                ll.add(args[i]);
            } else {
                if (args[i].charAt(0) != '-') found = true;
            }
        }

        return (String[]) ll.toArray(new String[0]);
    }

    public static String getSubModuleName(String[] args) {
        if (args == null || args.length == 0) return null;

        int max = args.length;
        for (int i = 0; i < max; i++) {
            if (args[i].charAt(0) != '-') return args[i];
        }

        return null;
    }
}
