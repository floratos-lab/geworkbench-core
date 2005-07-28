package edu.ksu.cis.bnj.i18n;

/*
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

import edu.ksu.cis.kdd.util.StringUtil;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Locale;
import java.util.Properties;

public class Messages {

    private static final Messages instance = new Messages();
    private static Properties property;
    private static Properties englishProperty;
    private static Locale currentLocale;

    static {
        loadLocales(Locale.ENGLISH);
        englishProperty = property;
    }

    private Messages() {
    }

    public static void loadLocales(Locale locale) {
        String name;
        currentLocale = locale;
        if (locale == Locale.ENGLISH || locale == Locale.CANADA || locale == Locale.UK || locale == Locale.US) {
            name = "message.properties"; // $NON-NLS-1$
        } else {
            name = "message_" + locale + ".properties"; // $NON-NLS-1$ // $NON-NLS-2$
        }
        String filename = instance.getClass().getResource(name).getFile().replaceAll("%20", " "); // $NON-NLS-1$ // $NON-NLS-2$ 
        property = new Properties();
        Locale.setDefault(locale);
        try {
            LineNumberReader fr = new LineNumberReader(new FileReader(filename));
            String s = null;
            do {
                s = fr.readLine();
                if (s == null) break;
                s = s.trim();
                if (s.length() == 0 || s.startsWith("//")) continue; // $NON-NLS-1$
                int idx = s.indexOf('=');
                String key = s.substring(0, idx).trim();
                String value = StringUtil.unescapeString(s.substring(idx + 1).trim());
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) { // $NON-NLS-1$ // $NON-NLS-2$
                    value = value.substring(1, value.length() - 1);
                }

                property.put(key, value);
            } while (true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String getString(String key) {
        String s = (String) property.get(key);
        if (s == null) return (String) englishProperty.get(key);
        return s;
    }
}
