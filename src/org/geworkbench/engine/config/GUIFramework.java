package org.geworkbench.engine.config;

import javax.swing.*;
import java.awt.*;

/**
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Base class for the top-level window of an application.
 */
public abstract class GUIFramework extends JFrame {

    public static final String TOOL_AREA = "Toolbar";

    public static final String VISUAL_AREA = "VisualArea";

    public static final String SELECTION_AREA = "SelectionArea";

    public static final String COMMAND_AREA = "CommandArea";

    public static final String PROJECT_AREA = "ProjectArea";

    /**
     * Adds the <code>visualPlugin</code> to the container identified by the name
     * <code>areaName</code>.
     *
     * @param areaName
     * @param visualPlugin
     */
    public abstract void addToContainer(String areaName, Component visualPlugin);

    /**
     * Adds the <code>visualPlugin</code> to the container identified by the name
     * <code>areaName</code>. The visual plugin will be displayed using the name
     * <code>pluginName</code>
     *
     * @param areaName
     * @param visualPlugin
     * @param pluginName
     */
    public abstract void addToContainer(String areaName, Component visualPlugin, String pluginName);

    /**
     * Removes the designated <code>visualPlugin</code> from the GUI.
     *
     * @param visualPlugin
     */
    public abstract void remove(Component visualPlugin);

    /**
     * get the infomation about the visual area the visualPlugin was plugged into
     *
     * @param visualPlugin
     */
    public abstract String getVisualArea(Component visualPlugin);
}
