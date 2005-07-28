package edu.ksu.cis.kdd.util.gui;

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

/* Custom file filter: By: Roby Joehanes */
/**
 * <p>A powerful file filter for Java. Accepts UNIX style wildcards.
 * <p>Usage :<BR>
 * The method <tt>setFileList(List l)</tt> is to set a list of wildcard
 * strings for a template. Then you can use it in file dialogs and
 * file listing statements.
 * <p>It accepts * and ? as the usual wildcards. The question marks ('?')
 * requires exactly one character (not zero or one as it is in DOS). The
 * star ('*') denotes zero or more character.
 * <p>The inner working is when the list is passed, the strings in the
 * list are tokenized in <tt>parseRE(String)</tt> method and placed into
 * the hashtable <tt>RETable</tt>. Then, whenever
 * <tt>accept(File, String)</tt> is called by file listing statements,
 * it calls <tt>isMatched(String, String)</tt> for each string token in
 * the table.
 * <p>The method <tt>includeDir(boolean)</tt> determines whether we should
 * filter directories too or not. If it is set to <tt>true</tt>, then
 * the directories will <b>NOT</b> be filtered. It is filtered otherwise.
 * <p>An example on how using it:
 <pre>	CustomFileFilter ff = new CustomFileFilter();

 LinkedList l = new LinkedList();
 l.addLast("*.jpg");
 l.addLast("*.png");
 l.addLast("*.gif");
 l.addLast("*.bmp");
 ff.setFileList(l);
 </pre>
 <p>Now, suppose we have a method called <tt>dirLister</tt> as follows:

 <pre>	void dirLister(File fn, FileFilter filter)
 {
 File[] files = fn.listFiles(filter);
 for (int i=0; i < files.length; i++)
 {
 if (files[i].isDirectory())
 {
 if (recurseDir) dirLister(files[i], filter);
 } else
 {
 analyzeFile(files[i]); // Or do anything with it
 }
 }
 }
 </pre>
 <p>In the main method, we simply call:<br>
 <tt>dirLister(new File("."),ff);</tt>

 <p>Alternatively, we can put the file filter as a field and
 modify the <tt>dirLister</tt> method into accepting only <tt>File fn</tt>.

 *   @author Roby Joehanes
 **/

import javax.swing.*;
import java.io.File;
import java.util.*;

public class CustomFileFilter extends javax.swing.filechooser.FileFilter {
    private boolean dirtoo = true;
    private Hashtable RETable = new Hashtable();
    private String description = ""; //$NON-NLS-1$

    /**
     * CustomFileFilter constructor comment.
     */
    public CustomFileFilter() {
        super();
    }

    public CustomFileFilter(List l, String desc) {
        super();
        setFileList(l);
        description = desc;
    }

    public boolean accept(File file) {
        if (dirtoo) {
            try {
                if (file.isDirectory())
                    return true;
            } catch (Exception e) {
                return false;
            }
        }
        for (Enumeration e = RETable.keys(); e.hasMoreElements();) {
            String s = (String) e.nextElement();
            if (isMatched(s, file.getName()))
                return true;
        }
        return false;
    }

    public boolean accept(File dir, String name) {
        if (dirtoo) {
            try {
                if (new File(dir.getCanonicalPath() + File.separator + name).isDirectory())
                    return true;
            } catch (Exception e) {
                return false;
            }
        }
        for (Enumeration e = RETable.keys(); e.hasMoreElements();) {
            String s = (String) e.nextElement();
            if (isMatched(s, name))
                return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public void includeDir(boolean b) {
        dirtoo = b;
    }

    public boolean isMatched(String RE, String s) {
        List l = (List) RETable.get(RE);
        if (l == null)
            l = parseRE(RE);

        int curpos = 0, requires = 0;
        boolean more = false;
        String token = ""; //$NON-NLS-1$
        for (Iterator i = l.iterator(); i.hasNext();) {
            token = (String) i.next();
            if (token.indexOf("?") != -1) //$NON-NLS-1$
            {
                if (token.indexOf("*") != -1) //$NON-NLS-1$
                {
                    more = true;
                    requires = token.length() - 1;
                } else {
                    more = false;
                    requires = token.length();
                }
            } else {
                if (token.indexOf("*") != -1) //$NON-NLS-1$
                {
                    if (token.length() > 1)
                        throw new RuntimeException("Bug!");
                    more = true;
                } else {
                    int pos = s.indexOf(token);
                    if (pos < (requires + curpos))
                        return false;
                    if ((pos > (requires + curpos)) && !more)
                        return false;
                    curpos = pos + token.length();
                }
            }
        }
        if (token.indexOf("?") != -1) //$NON-NLS-1$
        {
            if (token.indexOf("*") != -1) //$NON-NLS-1$
                return (s.length() - curpos) >= (token.length());
            else
                return (s.length() - curpos - 1) > (token.length());
        } else {
            if (token.indexOf("*") == -1 && s.length() > curpos)
                return false; //$NON-NLS-1$
        }
        return true;
    }

    protected List parseRE(String RE) {
        LinkedList l = new LinkedList();
        int state = 0;
        String s = ""; //$NON-NLS-1$

        for (int i = 0; i < RE.length(); i++) {
            char c = RE.charAt(i);
            if (state == 0) {
                if (c == '*' || c == '?') {
                    if (!s.equals(""))
                        l.addLast(s); //$NON-NLS-1$
                    s = "" + c;
                    state = 1; //$NON-NLS-1$
                } else {
                    s += c;
                }
            } else {
                // Accept * or ?
                if (c != '?' && c != '*') {
                    if (!s.equals(""))
                        l.addLast(s); //$NON-NLS-1$
                    s = "" + c;
                    state = 0; //$NON-NLS-1$
                } else {
                    if (c == '?')
                        s = c + s;
                    if (c == '*' && s.indexOf("*") == -1)
                        s += "*"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        if (!s.equals(""))
            l.addLast(s); //$NON-NLS-1$
        return l;
    }

    public void setDescription(String s) {
        description = s;
    }

    public void setFileList(List l) {
        if (l == null)
            throw new RuntimeException("List is null");
        if (l.isEmpty())
            throw new RuntimeException("List is empty");

        try {
            for (Iterator i = l.iterator(); i.hasNext();) {
                String s = (String) i.next();
                if (s == null)
                    throw new RuntimeException("List contains null");
                if (RETable.get(s) == null)
                    RETable.put(s, parseRE(s));
            }
        } catch (Exception e) {
            throw new RuntimeException("List is not properly set up");
        }
    }

    public static void main(String[] args) {
        // An example on how to use it:
        LinkedList l = new LinkedList();
        l.addLast("*.jpg"); //$NON-NLS-1$
        l.addLast("*.png"); //$NON-NLS-1$
        l.addLast("*.gif"); //$NON-NLS-1$
        l.addLast("*.bmp"); //$NON-NLS-1$
        CustomFileFilter ff = new CustomFileFilter(l, "All image formats known to earth"); //$NON-NLS-1$
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(ff);
        fc.setFileFilter(ff);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        /*	fc.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    JFileChooser src = (JFileChooser) e.getSource();
                    String cmd = e.getActionCommand();
                    File f = src.getSelectedFile();
                    if (cmd.equals("ApproveSelection"))
                    {
                        if (f.isDirectory())
                        {
                            src.setCurrentDirectory(f);
                            src.rescanCurrentDirectory();
                            java.awt.Container parent = src.getParent();
                            String text = src.getApproveButtonText();
                            java.awt.Point loc = src.getLocation();
                            java.awt.Dimension size = src.getSize();

                            src.showDialog(parent, text);
                            src.setLocation(loc);
                            src.setSize(size);
                            System.out.println("HEY!! "+f);
                        }
                    }
                }
            });
        */
        int response = fc.showDialog(null, "Open");

        //System.exit(-1);
        /*
        Now, suppose we have a method called dirLister as follows:

        void dirLister(File fn, FileFilter filter)
        {
            File[] files = fn.listFiles(filter);
            for (int i=0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    if (recurseDir) dirLister(files[i], filter);
                } else
                {
                    analyzeFile(files[i]); // Or do anything with it
                }
            }
        }

        In the main method, we simply call:
        dirLister(new File("."),ff);

        Alternatively, we can put the file filter as a field and
        modify the dirLister method into accepting only File fn.
        */
    }
}
