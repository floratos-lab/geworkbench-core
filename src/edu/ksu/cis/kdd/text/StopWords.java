package edu.ksu.cis.kdd.text;

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

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Roby Joehanes
 */
public class StopWords {
    private HashSet set = new HashSet();

    public void add(String s) {
        set.add(s);
    }

    public void remove(String s) {
        set.remove(s);
    }

    public Set getWords() {
        return set;
    }

    public boolean contains(String s) {
        return set.contains(s);
    }

    public String toString() {
        return set.toString();
    }

    public static StopWords load(String suffix) {
        StopWords sw = new StopWords();

        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(sw.getClass().getResource("../../../../../stopwords-" + suffix + ".txt").getFile())); //$NON-NLS-1$ //$NON-NLS-2$
            String s;
            do {
                s = reader.readLine();
                if (s == null) break;
                s = s.trim();
                if (s.equals("") || s.startsWith("//")) continue; //$NON-NLS-1$ //$NON-NLS-2$
                sw.add(s);
            } while (true);
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw;
    }

    public static StopWords load() {
        return load("en"); //$NON-NLS-1$
    }

    public static void main(String[] args) {
        System.out.println(load().toString());
    }
}
