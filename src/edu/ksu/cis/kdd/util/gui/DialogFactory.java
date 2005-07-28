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
import java.awt.*;

/**
 * Dialog Factory
 *
 * @author Roby Joehanes
 */
public class DialogFactory {
    public static final int WARNING = JOptionPane.WARNING_MESSAGE;
    public static final int INFO = JOptionPane.INFORMATION_MESSAGE;
    public static final int ERROR = JOptionPane.ERROR_MESSAGE;

    public static int getYesNoCancelDialog(Container parent, int opt, String title, String text) {
        Object[] choice = getYesNoCancelText();
        int result = JOptionPane.showOptionDialog(parent, text, title, JOptionPane.YES_NO_CANCEL_OPTION, opt, null, choice, choice[1]);
        return result;
    }

    public static int getYesNoDialog(Container parent, int opt, String title, String text) {
        Object[] choice = getYesNoText();
        int result = JOptionPane.showOptionDialog(parent, text, title, JOptionPane.YES_NO_OPTION, opt, null, choice, choice[1]);
        return result;
    }

    public static void getOKDialog(Container parent, int opt, String title, String text) {
        String oktext = "OK";

        JOptionPane.showOptionDialog(parent, text, title, JOptionPane.OK_OPTION, opt, null, new Object[]{oktext}, oktext);
    }

    public static String getInputDialog(Container parent, String title, String text) {
        return (String) JOptionPane.showInputDialog(parent, text, title, JOptionPane.QUESTION_MESSAGE, null, null, null);
    }

    private static Object[] getYesNoCancelText() {
        return new Object[]{"Yes", "No", "Cancel"};
    }

    private static Object[] getYesNoText() {
        return new Object[]{"Yes", "No"};
    }

    private static Object[] getOKCancelText() {
        return new Object[]{"OK", "Cancel"};
    }

}
