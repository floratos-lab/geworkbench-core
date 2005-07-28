/*
 * Created on Oct 19, 2003
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

import java.util.StringTokenizer;

/**
 * Various string utility methods
 *
 * @author Roby Joehanes
 */
public class StringUtil {

    private StringUtil() {
    } // make it non-instantiable

    /**
     * Convert the escape codes in a string accordingly. Note: Octal escapes are not handled.
     * Use the unicode instead.
     *
     * @param str String to mangle
     * @return The mangled string
     */
    public static String unescapeString(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, "\\"); // $NON-NLS-1$
        StringBuffer buf = new StringBuffer();
        boolean hasEscape = str.startsWith("\\"); // $NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (hasEscape) {
                char escapeCode = token.charAt(0);
                switch (escapeCode) {
                    case 'n':
                        token = "\n" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case 'r':
                        token = "\r" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case 't':
                        token = "\t" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case '"':
                        token = "\"" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case '\'':
                        token = "\'" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case '\\':
                        token = "\\" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case 'b':
                        token = "\b" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case 'f':
                        token = "\f" + token.substring(1);
                        break;  // $NON-NLS-1$
                    case 'u':  // Unicode escapes
                        if (token.length() < 5) throw new RuntimeException("Unicode syntax error: " + token);
                        try {
                            char code = (char) Integer.parseInt(token.substring(1, 5), 16); // Unicode is always in hex
                            token = String.valueOf(code) + token.substring(5);
                        } catch (Exception e) {
                            if (Settings.isDebug()) e.printStackTrace();
                            throw new RuntimeException("Unicode syntax error: " + token);
                        }
                        break;
                }
            } else
                hasEscape = true;
            buf.append(token);
        }
        return buf.toString();
    }

    /**
     * Escape the string s to oblige to the XML rules (e.g.: "'" becomes &apos;,
     * "&" becomes &amp;, and so on).
     *
     * @param s The raw string
     * @return String The formatted string
     */
    public static String mangleXMLString(String s) {
        StringBuffer buf = new StringBuffer();
        int max = s.length();
        for (int i = 0; i < max; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\'':
                    buf.append("&apos;");
                    break; //$NON-NLS-1$
                case '&':
                    buf.append("&amp;");
                    break; //$NON-NLS-1$
                case '<':
                    buf.append("&lt;");
                    break; //$NON-NLS-1$
                case '>':
                    buf.append("&gt;");
                    break; //$NON-NLS-1$
                case '\"':
                    buf.append("&quot;");
                    break; //$NON-NLS-1$
                default:
                    buf.append(c);
            }
        }
        return buf.toString();
    }
}
