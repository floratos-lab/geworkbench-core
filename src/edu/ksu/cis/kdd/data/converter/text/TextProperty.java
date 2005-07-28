package edu.ksu.cis.kdd.data.converter.text;

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

import edu.ksu.cis.kdd.data.TableProperty;

import java.util.Hashtable;

/**
 * @author Roby Joehanes
 */
public class TextProperty extends TableProperty {
    protected int wordCount = 0, classCount = 0;
    protected Hashtable wordTable = new Hashtable();
    protected Hashtable classTable = new Hashtable();
    private String[] words = new String[10];
    private String[] classes = new String[10];

    public int addWord(String s) {
        if (s == null) throw new RuntimeException();
        Integer i = (Integer) wordTable.get(s);
        if (i == null) {
            i = new Integer(wordCount);
            wordTable.put(s, i);
            if (wordCount >= words.length) {
                int length = words.length;
                String[] temp = new String[length * 2];
                System.arraycopy(words, 0, temp, 0, length);
                words = temp;
            }
            words[wordCount] = s;
            wordCount++;
        }
        return i.intValue();
    }

    public int addClass(String s) {
        if (s == null) throw new RuntimeException();
        Integer i = (Integer) classTable.get(s);
        if (i == null) {
            i = new Integer(classCount);
            classTable.put(s, i);
            if (classCount >= classes.length) {
                int length = classes.length;
                String[] temp = new String[length * 2];
                System.arraycopy(classes, 0, temp, 0, length);
                classes = temp;
            }
            classes[classCount] = s;
            classCount++;
        }
        return i.intValue();
    }

    public int getClassIndex(String s) {
        return ((Integer) classTable.get(s)).intValue();
    }

    public String getClassIndex(int i) {
        return classes[i];
    }

    public int getWordIndex(String s) {
        return ((Integer) wordTable.get(s)).intValue();
    }

    public String getWordIndex(int i) {
        return words[i];
    }

    /**
     * Returns the classCount.
     *
     * @return int
     */
    public int getClassCount() {
        return classCount;
    }

    /**
     * Returns the wordCount.
     *
     * @return int
     */
    public int getWordCount() {
        return wordCount;
    }

    public Object clone() {
        TextProperty tp = new TextProperty();
        tp.classTable = (Hashtable) classTable.clone();
        tp.wordTable = (Hashtable) wordTable.clone();
        tp.classCount = classCount;
        tp.wordCount = wordCount;
        tp.classes = new String[classes.length];
        System.arraycopy(tp.classes, 0, classes, 0, classes.length);
        tp.words = new String[words.length];
        System.arraycopy(tp.words, 0, words, 0, words.length);
        return tp;
    }
    /*
     * @see edu.ksu.cis.kdd.data.datastructure.TuplesProperty#getClassValues()
     *
    public Set getClassValues() {
HashSet set = new HashSet();
for (int i = 0; i < classCount; i++)
{
set.add(classes[i]);
}
        return set;
    }*/

}
