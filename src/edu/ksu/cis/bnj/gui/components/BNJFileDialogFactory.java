package edu.ksu.cis.bnj.gui.components;

/*
 * Created on 30 Jul 2003
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

import edu.ksu.cis.bnj.i18n.Messages;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.DialogFactory;
import edu.ksu.cis.kdd.util.gui.FileDialogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * A marshall class for a melange of standard BNJ file choosers
 *
 * @author Roby Joehanes
 */
public class BNJFileDialogFactory {
    protected File currentDir = null;
    protected Container owner = null;
    protected JFileChooser netFileChooser = null;
    protected JFileChooser dataFileChooser = null;
    protected JFileChooser evidenceFileChooser = null;
    protected JFileChooser outputFileChooser = null;
    protected JFileChooser rmsePlotFileChooser = null;

    public BNJFileDialogFactory(Container o) {
        owner = o;
        init();
    }

    protected void init() {
        List netExtensionList = Settings.getNetExtensionList();
        netFileChooser = FileDialogFactory.create(netExtensionList, Messages.getString("Dialog.BayesNetFiles")); // $NON-NLS-1$

        List dataExtensionList = Settings.getDataExtensionList();
        dataFileChooser = FileDialogFactory.create(dataExtensionList, Messages.getString("Dialog.DataFiles")); // $NON-NLS-1$

        LinkedList ll = new LinkedList();
        ll.add("*.xeb"); // $NON-NLS-1$
        evidenceFileChooser = FileDialogFactory.create(ll, Messages.getString("Dialog.EvidenceFiles")); // $NON-NLS-1$

        ll = new LinkedList();
        ll.add("*.*"); // $NON-NLS-1$
        outputFileChooser = FileDialogFactory.create(ll, Messages.getString("Dialog.OutputFiles")); // $NON-NLS-1$

        rmsePlotFileChooser = FileDialogFactory.create(ll, Messages.getString("Dialog.RMSEPlotFiles")); // $NON-NLS-1$
    }

    public File openNetFiles() {
        netFileChooser.setDialogTitle(Messages.getString("Dialog.OpenBayesNetFiles")); // $NON-NLS-1$
        return openFileDialog(netFileChooser);
    }

    public File openDataFiles() {
        dataFileChooser.setDialogTitle(Messages.getString("Dialog.OpenDataFiles")); // $NON-NLS-1$
        return openFileDialog(dataFileChooser);
    }

    public File openEvidenceFiles() {
        evidenceFileChooser.setDialogTitle(Messages.getString("Dialog.OpenEvidenceFiles")); // $NON-NLS-1$
        return openFileDialog(evidenceFileChooser);
    }

    public File openOutputFiles() {
        outputFileChooser.setDialogTitle(Messages.getString("Dialog.OpenOutputFiles")); // $NON-NLS-1$
        return openFileDialog(outputFileChooser);
    }

    public File openRMSEPlotFiles() {
        rmsePlotFileChooser.setDialogTitle(Messages.getString("Dialog.OpenRMSEPlotFiles")); // $NON-NLS-1$
        return openFileDialog(rmsePlotFileChooser);
    }

    public File saveNetFiles() {
        netFileChooser.setDialogTitle(Messages.getString("Dialog.SaveBayesNetFiles")); // $NON-NLS-1$
        return saveFileDialog(netFileChooser);
    }

    public File saveDataFiles() {
        dataFileChooser.setDialogTitle(Messages.getString("Dialog.SaveDataFiles")); // $NON-NLS-1$
        return saveFileDialog(dataFileChooser);
    }

    public File saveEvidenceFiles() {
        evidenceFileChooser.setDialogTitle(Messages.getString("Dialog.SaveEvidenceFiles")); // $NON-NLS-1$
        return saveFileDialog(evidenceFileChooser);
    }

    public File saveOutputFiles() {
        outputFileChooser.setDialogTitle(Messages.getString("Dialog.SaveOutputFiles")); // $NON-NLS-1$
        return saveFileDialog(outputFileChooser);
    }

    public File saveRMSEPlotFiles() {
        rmsePlotFileChooser.setDialogTitle(Messages.getString("Dialog.SaveRMSEPlotFiles")); // $NON-NLS-1$
        return saveFileDialog(rmsePlotFileChooser);
    }

    protected File openFileDialog(JFileChooser fc) {
        fc.setCurrentDirectory(getCurrentDir());

        File chosen = FileDialogFactory.display(fc, null, Messages.getString("Common.Open")); // $NON-NLS-1$
        if (chosen == null) return null;
        if (!chosen.exists()) {
            DialogFactory.getOKDialog(owner, DialogFactory.ERROR, Messages.getString("Error.FileNotExist"), // $NON-NLS-1$
                    Messages.getString("Error.CantOpenFile")); // $NON-NLS-1$
            return null;
        }

        // Update directory information
        File parent = chosen.getParentFile();
        if (parent.isDirectory()) currentDir = parent;
        return chosen;
    }

    protected File saveFileDialog(JFileChooser fc) {
        fc.setCurrentDirectory(getCurrentDir());

        File chosen = FileDialogFactory.display(fc, null, Messages.getString("Common.Save")); // $NON-NLS-1$
        if (chosen == null) return null;
        // Update directory information
        File parent = chosen.getParentFile();
        if (parent.isDirectory()) currentDir = parent;
        return chosen;
    }

    private File getCurrentDir() {
        File currentDir = this.currentDir;

        if (currentDir != null && currentDir.exists()) return currentDir;
        currentDir = new File(System.getProperty("user.dir"));  //$NON-NLS-1$
        return currentDir;
    }
}
