package org.systemsbiology.chem.app;

/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 * 
 * This source code is distributed under the GNU Lesser 
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import netx.jnlp.Launcher;
import netx.jnlp.runtime.JNLPRuntime;
import org.systemsbiology.chem.Model;
import org.systemsbiology.chem.sbml.ModelExporterMarkupLanguage;
import org.systemsbiology.util.DataNotFoundException;
import org.systemsbiology.util.URLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

public class CytoscapeViewer {
    private static final String JNLP_TEMPLATE_FILE_NAME = "CytoscapeViewer.xml";
    private static final String TAG_NAME_APPLICATION_DESC = "application-desc";
    private Component mMainFrame;

    public CytoscapeViewer(Component pMainFrame) {
        mMainFrame = pMainFrame;
    }

    static {
        JNLPRuntime.setSecurityEnabled(false);
        JNLPRuntime.initialize();
    }

    public void viewModelInCytoscape(Model pModel) {
        try {
            ModelExporterMarkupLanguage exporterMarkupLanguage = new ModelExporterMarkupLanguage();
            File tempFile = File.createTempFile("sbml", ".xml");
            String tempFileName = tempFile.getAbsolutePath();
            FileWriter fileWriter = new FileWriter(tempFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            
            // convert the model to SBML level 1, version 1 (eventually we will modify Cytoscape's SBML reader
            // so that it can read SBML level 1, version 2-- then we will be able to export L1V2 to Cytoscape).
            exporterMarkupLanguage.export(pModel, printWriter, ModelExporterMarkupLanguage.Specification.LEVEL1_VERSION1);
            URL jnlpFileResource = this.getClass().getResource(JNLP_TEMPLATE_FILE_NAME);
            if (null == jnlpFileResource) {
                throw new DataNotFoundException("could not find resource file: " + JNLP_TEMPLATE_FILE_NAME);
            }
            InputStream jnlpFileInputStream = jnlpFileResource.openStream();

            File tempJNLPFile = File.createTempFile("cytoscpe", ".jnlp");
            FileWriter tempJNLPFileWriter = new FileWriter(tempJNLPFile);
            PrintWriter tempJNLPPrintWriter = new PrintWriter(tempJNLPFileWriter);
            String tempJNLPFileURLString = URLUtils.createFileURL(tempJNLPFile);
            URL tempJNLPFileURL = new URL(tempJNLPFileURLString);

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(jnlpFileInputStream);

            Element jnlpElement = document.getDocumentElement();
            jnlpElement.setAttribute("href", tempJNLPFileURLString);

            NodeList applicationDescElements = document.getElementsByTagName(TAG_NAME_APPLICATION_DESC);
            Element applicationDescElement = (Element) applicationDescElements.item(0);
            Element argumentElement = document.createElement("argument");
            applicationDescElement.appendChild(argumentElement);
            Text jnlpFileNameTextNode = document.createTextNode(tempFileName);
            argumentElement.appendChild(jnlpFileNameTextNode);
            
            // write out the XML 
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            Properties properties = transformer.getOutputProperties();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(tempJNLPPrintWriter);
            transformer.transform(source, result);
            tempJNLPPrintWriter.flush();

            //=========================================================================
            // This code is used in conjunction with Sun's Java Web Start:
            //            BrowserLauncher.openURL(tempJNLPFileURLString);
            //=========================================================================

            //=========================================================================
            // This code is used in conjunction with the OpenJNLP API (which seems to be
            // incompatible with our JNLP file):
            //            FileCache fileCache = FileCache.defaultCache();
            //            JNLPParser.launchJNLP(fileCache, tempJNLPFileURL, true);
            //=========================================================================

            //=========================================================================
            // This code is used in conjunction with the NetX JNLP API:
            Launcher launcher = new Launcher();
            launcher.launch(tempJNLPFileURL);
            //=========================================================================
        } catch (Exception e) {
            ExceptionDialogOperationCancelled dialog = new ExceptionDialogOperationCancelled(mMainFrame, "View-in-Cytoscape operation failed: " + e.getMessage(), e);
            dialog.show();

        }
    }
}
