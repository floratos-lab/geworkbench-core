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
package edu.ksu.cis.kdd.util.gui;

import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import edu.ksu.cis.kdd.util.FileClassLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Smorgasbord of GUI utilities
 *
 * @author Roby Joehanes
 */
public class GUIUtil {
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    protected static float defaultMarqueePattern[] = {5.0f, 2.0f};
    protected static BasicStroke defaultMarquee = createMarquee(defaultMarqueePattern);

    /**
     * Switch component c's look and feel to native user interface
     *
     * @param c
     */
    public static void switchToNativeUI(Component c) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(c);
        } catch (Exception e) {
        }
    }

    public static void switchUI(String laf) {
        if (laf == null) return;
        String lowlaf = laf.toLowerCase();
        String osName = System.getProperty("os.name").trim().toLowerCase(); // $NON-NLS-1$
        if (lowlaf.equals("native")) { // $NON-NLS-1$
            if (osName.startsWith("linux")) { // $NON-NLS-1$
                lowlaf = "gtk"; // $NON-NLS-1$
            } else if (osName.startsWith("windows")) { // $NON-NLS-1$
                lowlaf = "windows"; // $NON-NLS-1$
            } else if (osName.startsWith("solaris")) { // $NON-NLS-1$
                lowlaf = "motif"; // $NON-NLS-1$
            }
        }
        LookAndFeel lafObject = null;
        try {
            if (lowlaf.equals("windows")) { // $NON-NLS-1$
                if (osName.startsWith("windows")) {
                    lafObject = loadLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); // $NON-NLS-1$
                    UIManager.setLookAndFeel(lafObject);
                } else {
                    // We've got some work around to do
                    loadTheme("xpluna.zip"); // $NON-NLS-1$
                }
            } else if (lowlaf.equals("gtk")) { // $NON-NLS-1$
                lafObject = loadLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); // $NON-NLS-1$
                UIManager.setLookAndFeel(lafObject);
            } else if (lowlaf.equals("motif")) { // $NON-NLS-1$
                lafObject = loadLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel"); // $NON-NLS-1$
                UIManager.setLookAndFeel(lafObject);
            } else if (lowlaf.equals("metouia")) { // $NON-NLS-1$
                lafObject = loadLookAndFeel("net.sourceforge.mlf.metouia.MetouiaLookAndFeel"); // $NON-NLS-1$
                UIManager.setLookAndFeel(lafObject);
            } else if (lowlaf.equals("swing") || lowlaf.equals("metal")) { // $NON-NLS-1$ // $NON-NLS-2$
                lafObject = loadLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // $NON-NLS-1$
                UIManager.setLookAndFeel(lafObject);
            } else if (lowlaf.startsWith("theme:")) { // $NON-NLS-1$
                loadTheme(lowlaf.substring(6));
            } else {
                lafObject = loadLookAndFeel(laf);
                UIManager.setLookAndFeel(lafObject);
            }
        } catch (Exception e) {
        }
    }

    public static LookAndFeel loadLookAndFeel(String className) throws ClassNotFoundException {
        return (LookAndFeel) FileClassLoader.loadAndInstantiate(className, "javax.swing.LookAndFeel", null); // $NON-NLS-1$
    }

    public static void loadTheme(String themeFileName) {
        try {
            SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePack("themes/" + themeFileName)); // $NON-NLS-1$
            SkinLookAndFeel.enable();
        } catch (Exception e) {
            System.err.println("Failed to load the themes.");
        }
    }

    /**
     * Center a dimension of src to dest
     *
     * @param src
     * @param dest
     * @return
     */
    public static Dimension center(Dimension src, Dimension dest) {
        if (src.height > dest.height) src.height = dest.height;
        if (src.width > dest.width) src.width = dest.width;
        return new Dimension((dest.width - src.width) / 2, (dest.height - src.height) / 2);
    }

    /**
     * Center a dimension src to the screen
     *
     * @param src
     * @return The centered dimension
     */
    public static Dimension centerToScreen(Dimension src) {
        return center(src, screenSize);
    }

    /**
     * Center component c to the screen
     *
     * @param c
     */
    public static void centerToScreen(Component c) {
        Dimension d = centerToScreen(c.getSize());
        c.setLocation(d.width, d.height);
    }

    /**
     * Component src is centered within component dest. If the src Component is larger
     * than the dest Component centering is still performed just the dest component will
     * be covered.
     *
     * @param src
     * @param dest
     */
    public static void centerToComponent(Component src, Component dest) {
        int xlocale = (int) (dest.getX() + dest.getWidth() / 2 - src.getWidth());
        int ylocale = (int) (dest.getY() + dest.getHeight() / 2 - src.getHeight());
        src.setLocation(xlocale, ylocale);
    }

    /**
     * Grid bag add
     *
     * @param c      Container
     * @param comp   Component to add to the container
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param w      Width
     * @param h      Height
     * @param up     Up margin
     * @param down   Down margin
     * @param left   Left margin
     * @param right  Right margin
     * @param fill   Fill constants
     * @param anchor Anchor constants
     * @param wx     Weight x
     * @param wy     Weight y
     */
    public static void gbAdd(Container c, Component comp, int x, int y, int w, int h, int up, int down, int left, int right, int fill, int anchor, double wx, double wy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.anchor = anchor;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.fill = fill;
        gbc.insets = new Insets(up, down, left, right);
        c.add(comp, gbc);
    }

    public static JComboBox createComboBox(Collection c) {
        JComboBox comboBox = new JComboBox();
        for (Iterator i = c.iterator(); i.hasNext();) {
            comboBox.addItem(i.next());
        }
        return comboBox;
    }

    public static boolean isRightMouseButton(MouseEvent e) {
        return (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
    }

    public static boolean isLeftMouseButton(MouseEvent e) {
        return (e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK;
    }

    public static boolean isCenterMouseButton(MouseEvent e) {
        return (e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK;
    }

    public static BasicStroke getDefaultMarquee() {
        return defaultMarquee;
    }

    public static BasicStroke createMarquee(float[] pattern) {
        return createMarquee(1.0f, pattern, 0.0f);
    }

    public static BasicStroke createMarquee(float width, float[] pattern) {
        return createMarquee(width, pattern, 0.0f);
    }

    public static BasicStroke createMarquee(float width, float[] pattern, float offset) {
        return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, pattern, offset);
    }

    /**
     * Translate a string shortcut to the respective keystroke object for Swing
     *
     * @param shortcut
     * @return The keystroke
     */
    public static KeyStroke translateShortcut(String shortcut) {
        if (shortcut == null || shortcut.length() == 0) return null;

        KeyStroke key = KeyStroke.getKeyStroke(shortcut);
        if (key != null) return key;
    
        // Because of some JDK bugs, it seems that
        // we have to reimplement this ourselves.

        StringTokenizer tok = new StringTokenizer(shortcut);
        int modifier = 0;
        boolean released = false, typed = false;
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken().toLowerCase();
            if (s.equals("shift")) {
                modifier |= InputEvent.SHIFT_MASK;
                continue;
            } else // $NON-NLS-1$
                if (s.equals("control") || s.equals("ctrl")) {
                    modifier |= InputEvent.CTRL_MASK;
                    continue;
                } else // $NON-NLS-1$ // $NON-NLS-2$
                    if (s.equals("meta")) {
                        modifier |= InputEvent.META_MASK;
                        continue;
                    } else // $NON-NLS-1$
                        if (s.equals("alt")) {
                            modifier |= InputEvent.ALT_MASK;
                            continue;
                        } else // $NON-NLS-1$
                            if (s.equals("button1")) {
                                modifier |= InputEvent.BUTTON1_MASK;
                                continue;
                            } else // $NON-NLS-1$
                                if (s.equals("button2")) {
                                    modifier |= InputEvent.BUTTON2_MASK;
                                    continue;
                                } else // $NON-NLS-1$
                                    if (s.equals("button3")) {
                                        modifier |= InputEvent.BUTTON3_MASK;
                                        continue;
                                    } else // $NON-NLS-1$
                                        if (s.equals("released")) {
                                            released = true;
                                            continue;
                                        } else // $NON-NLS-1$
                                            if (s.equals("pressed"))
                                                continue;
                                            else // $NON-NLS-1$
                                                if (s.equals("typed")) {
                                                    typed = true;
                                                    continue;
                                                } // $NON-NLS-1$

            if (typed) {
                if (s.equals("\\\""))
                    return KeyStroke.getKeyStroke('"'); // $NON-NLS-1$ // $NON-NLS-2$
                else if (s.length() != 1)
                    return null;
                else
                    return KeyStroke.getKeyStroke(s.charAt(0));
            }
            String keycode = "VK_" + s.toUpperCase(); // $NON-NLS-1$
            int code;
            try { // Here goes some nasty trick with reflection ;-) -- RJ
                code = KeyEvent.class.getField(keycode).getInt(KeyEvent.class);
            } catch (Exception e) {
                return null;
            }
            return KeyStroke.getKeyStroke(code, modifier, released);
        }
        return null;
    }
}
