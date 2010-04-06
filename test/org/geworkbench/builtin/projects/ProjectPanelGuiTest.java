/*
 * The geworkbench_gforge project
 * 
 * Copyright (c) 2006 Columbia University
 * 
 */
package org.geworkbench.builtin.projects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.skin.Skin;

/**
 * Test cases for ProjectPanel. Tests the functionality of saving and loading
 * workspaces.
 * 
 * @author keshav
 * 
 */
public class ProjectPanelGuiTest extends TestCase {
    private Log log = LogFactory.getLog(this.getClass());

    ProjectPanel projectPanel = null;

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        projectPanel = new ProjectPanel();

    }

    /**
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        projectPanel = null;
    }

    /**
     *
     *
     */
    public void testSerialize() {
        log.debug("1. CURRENT OPERATION");

        /* project selection */
        GeawConfigObject.setGuiWindow(new Skin());
        ProjectSelection projectSelection = new ProjectSelection(projectPanel);

        /* set the root (parent node) to the new project */
        ProjectNode projectNode = new ProjectNode("A New Test Project");

        /* create the child node DataSetNode */
        File file = new File("data/web100.exp");
        DSDataSet dataSet = new CSDataSet();
        dataSet.setFile(file);
        ProjectTreeNode dataSetNode = new DataSetNode(dataSet);

        /* add child nodes */
        projectNode.add(dataSetNode);
        /* add parent to project */
        projectPanel.addToProject(projectNode, true);

    }

    /**
     *
     *
     */
    public void testSerializeWithoutProjectTreeNodeAndBison() {
        log.debug("2. WITHOUT PROJECTTREENODE AND BISON");

        /* project selection */
        GeawConfigObject.setGuiWindow(new Skin());
        ProjectSelection projectSelection = new ProjectSelection(projectPanel);

        /* set the root (parent node) to the new project */
        ProjectNode projectNode = new ProjectNode("A New Test Project");

        /* create the child node DataSetNode */
        File file = new File("data/web100.exp");
        MutableTreeNode childNode = new DefaultMutableTreeNode(file);

        /* add child nodes */
        projectNode.add(childNode);
        /* add parent to project */
        projectPanel.addToProject(projectNode, true);

    }

    /**
     *
     *
     */
    public void testSerializeWithoutProjectTreeNode() {
        log.debug("4. WITHOUT PROJECTTREENODE");
        /* project selection */
        GeawConfigObject.setGuiWindow(new Skin());
        ProjectSelection projectSelection = new ProjectSelection(projectPanel);

        /* set the root (parent node) to the new project */
        ProjectNode projectNode = new ProjectNode("A New Test Project");

        /* create the child node DataSetNode */

        DSDataSet dataSet = new CSDataSet();
        File file = new File("data/web100.exp");
        dataSet.setFile(file);
        MutableTreeNode childNode = new DefaultMutableTreeNode(file);

        /* add child nodes */
        projectNode.add(childNode);
        /* add parent to project */
        projectPanel.addToProject(projectNode, true);

    }

    /**
     *
     *
     */
    public void testSerializeWithDefaultMutableTreeNodeAsProjectTreeNode() {
        log
                .debug("5. USING DEFAULTMUTABLETREENODE AS PROJECTTREENODE (handles the class cast exception	");
        /* project selection */
        GeawConfigObject.setGuiWindow(new Skin());
        ProjectSelection projectSelection = new ProjectSelection(projectPanel);

        /* set the root (parent node) to the new project */
        ProjectNode projectNode = new ProjectNode("A New Test Project");

        /* create the child node DataSetNode */

        DSDataSet dataSet = new CSDataSet();
        File file = new File("data/web100.exp");
        dataSet.setFile(file);

        ProjectTreeNode childNode = new ProjectTreeNode(file);

        /* add child nodes */
        projectNode.add(childNode);
        /* add parent to project */
        projectPanel.addToProject(projectNode, true);

    }
}
