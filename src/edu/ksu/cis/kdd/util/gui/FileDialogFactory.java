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

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Creation date: (7/8/01 11:46:01 PM)
 *
 * @author : Roby Joehanes
 */
public class FileDialogFactory {

    /**
     * Create a file chooser that accepts all
     */
    public static JFileChooser create(String desc) {
        LinkedList ll = new LinkedList();
        ll.add("*.*"); //$NON-NLS-1$
        return create(ll, desc);
    }

    public static JFileChooser create(List extensionList, String desc) {
        JFileChooser fc = new JFileChooser();
        CustomFileFilter ff = new CustomFileFilter();

        if (extensionList != null) {
            ff.setFileList(extensionList);
            if (desc != null) ff.setDescription(desc);
            fc.addChoosableFileFilter(ff);
            fc.setFileFilter(ff);
        }
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        return fc;
    }

    public static File display(JFileChooser fc, JFrame parent, String text) {
        if (fc == null) return null;
        File chosen = null;

        do {
            int response = fc.showDialog(parent, text);
            if (response == JFileChooser.APPROVE_OPTION) {
                chosen = fc.getSelectedFile();
                if (chosen.isDirectory()) {
                    fc.setCurrentDirectory(chosen);
                    fc.rescanCurrentDirectory();
                } else //if (chosen.exists())
                {
                    return chosen;
                }
            } else
                return null;
        } while (true);

    }

    public static File[] displayMulti(JFileChooser fc, JFrame parent, String text) {
        if (fc == null) return null;
        fc.setMultiSelectionEnabled(true);
        File[] chosen = null;

        do {
            int response = fc.showDialog(parent, text);
            if (response == JFileChooser.APPROVE_OPTION) {
                chosen = fc.getSelectedFiles();
                if (chosen == null) continue;
                if (chosen.length == 1 && chosen[0].isDirectory()) {
                    fc.setCurrentDirectory(chosen[0]);
                    fc.rescanCurrentDirectory();
                } else //if (chosen.exists())
                {
                    LinkedList ll = new LinkedList();
                    int max = chosen.length;
                    for (int i = 0; i < max; i++) {
                        if (chosen[i].isFile()) ll.add(chosen[i]);
                    }
                    return (File[]) ll.toArray(new File[0]);
                }
            } else
                return null;
        } while (true);

    }

    public static File displayDir(JFileChooser fc, JFrame parent, String text) {
        if (fc == null) return null;
        fc.setMultiSelectionEnabled(false);
        File chosen = null;

        do {
            int response = fc.showDialog(parent, text);
            if (response == JFileChooser.APPROVE_OPTION) {
                chosen = fc.getSelectedFile();
                if (chosen.isDirectory()) {
                    return chosen;
                }
            } else
                return null;
        } while (true);

    }

}
