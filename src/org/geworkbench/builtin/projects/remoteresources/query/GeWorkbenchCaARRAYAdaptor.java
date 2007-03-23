package org.geworkbench.builtin.projects.remoteresources.query;


import gov.nih.nci.common.search.SearchException;
import gov.nih.nci.common.search.Directable;
import gov.nih.nci.common.search.SearchResult;
import gov.nih.nci.common.search.session.SecureSession;
import gov.nih.nci.common.search.session.SecureSessionFactory;
import gov.nih.nci.mageom.search.Description.OntologyEntrySearchCriteria;
import gov.nih.nci.mageom.search.SearchCriteriaFactory;
import gov.nih.nci.mageom.search.EnhancedSearchCriteriaFactory;
import gov.nih.nci.mageom.search.Experiment.enhanced.ExperimentSearchCriteria;
import gov.nih.nci.mageom.search.Experiment.enhanced.NameCriterion;
import gov.nih.nci.mageom.domain.Description.OntologyEntry;
import gov.nih.nci.mageom.domain.Experiment.Experiment;

import javax.swing.*;
import java.util.*;
import java.io.IOException;

import org.geworkbench.engine.properties.PropertiesManager;

/**
 * Util class for connect with caArray RMI sever.
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Mar 1, 2007
 * Time: 12:21:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeWorkbenchCaARRAYAdaptor {

    public final static String CAARRAY_USERNAME = "username";
    public final static String PASSWORD = "password";
    public final static String SERVERLOCATION = "serverlocation";

    private String piName;
    private String chipTypeName;
    private String tissueTypeName;
    private String organName;
    private String username;
    String password;
    private String _serverLocation;

    public GeWorkbenchCaARRAYAdaptor() throws IOException {
        try {
            username = PropertiesManager.getInstance().getProperty(getClass(), CAARRAY_USERNAME, "Default Value");
            _serverLocation = PropertiesManager.getInstance().getProperty(getClass(), SERVERLOCATION, "Default Value");
            String newPassword = PropertiesManager.getInstance().getProperty(getClass(), PASSWORD, "Default Value");
            if (newPassword == null) {
                password = "";
            } else {
                password = newPassword;
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Report Error.
     *
     * @param message
     * @return
     */
    public static boolean fail(String message) {
        JOptionPane.showMessageDialog(null, message);
        return false;
    }

    /**
     * Get the predefined values for some catagories. The values will be used for the content table.
     *
     * @param catagory
     * @return
     * @throws SearchException
     */
    public Set testGetOntologyEntriesByCategory(String catagory) throws SearchException {
        SecureSession sess = null;
        try {
            sess = SecureSessionFactory.defaultSecureSession();
            ((Directable) sess).direct(_serverLocation + "SecureSessionManager");
            sess.start(username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error starting session." + "Got " + ex.getClass().getName() + ": " + ex.getMessage());
        }

        OntologyEntrySearchCriteria oesc = null;
        try {
            String className = OntologyEntrySearchCriteria.class.getName();
            oesc = (OntologyEntrySearchCriteria) SearchCriteriaFactory.new_DESCRIPTION_ONTOLOGYENTRY_SC();
            ((Directable) oesc).direct(_serverLocation + "SearchCriteriaHandler");
            oesc.setSessionId(sess.getSessionId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error getting experiment SC." + "Got " + ex.getClass().getName() + ": " + ex.getMessage());
        }
        oesc.setCategory(catagory);
        SearchResult result = oesc.search();
        OntologyEntry[] oeResults = (OntologyEntry[]) result.getResultSet();
        Set distinctCategory = new TreeSet();
        for (int i = 0; i < oeResults.length; i++) {
            OntologyEntry entry = oeResults[i];
            distinctCategory.add(entry.getValue().toLowerCase().trim());
        }
        return distinctCategory;

    }

    /**
     * Now caArray only supports two match types for pull down menu. ChipType will be added later.
     *
     * @param key
     * @return
     */
    public String matchCatagory(String key) {
        if (key.equalsIgnoreCase(CaARRAYQueryPanel.TISSUETYPE)) {
            return "OrganismPart";
        } else {
            return "Organism";
        }
    }

    public void testFindDistinctCategoryFromOntologyEntry() throws SearchException {
        SecureSession sess = null;

        sess = SecureSessionFactory.defaultSecureSession();
        ((Directable) sess).direct(_serverLocation + "SecureSessionManager");
        sess.start(username, password);
        OntologyEntrySearchCriteria oesc = null;
        try {
            oesc = (OntologyEntrySearchCriteria) SearchCriteriaFactory.new_DESCRIPTION_ONTOLOGYENTRY_SC();
            ((Directable) oesc).direct(_serverLocation + "SearchCriteriaHandler");
            oesc.setSessionId(sess.getSessionId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error getting experiment SC." + "Got " + ex.getClass().getName() + ": " + ex.getMessage());
        }
        SearchResult result = oesc.search();
        OntologyEntry[] oeResults = (OntologyEntry[]) result.getResultSet();
        Set distinctCategory = new HashSet();
        for (int i = 0; i < oeResults.length; i++) {
            OntologyEntry entry = oeResults[i];
            distinctCategory.add(entry.getCategory());
            System.out.println(entry.toString() + " " + entry.getCategory().toString());
        }
        Vector array = new Vector(distinctCategory);
        Collections.sort(array);

    }


    public void testFiltering() throws Exception {
        SecureSession sess = SecureSessionFactory.defaultSecureSession();

        ((Directable) sess).direct(_serverLocation + "SecureSessionManager");
        sess.start(username, password);
        String sessID = sess.getSessionId();
        ExperimentSearchCriteria esc =
                EnhancedSearchCriteriaFactory.new_EXPERIMENT_SC();
        esc.setSessionId(sessID);
        SearchResult results = esc.search();
        /*return all, no filtering*/
        Experiment[] allResults = (Experiment[]) results.getResultSet();
        esc.setTissueType("prostate and ovary");
        results = esc.search();
        Experiment[] result = (Experiment[]) results.getResultSet();

    }

    public Experiment[]  getExperiments(boolean useFilteringParameters) throws Exception {
        SecureSession sess = SecureSessionFactory.defaultSecureSession();
        ((Directable) sess).direct(_serverLocation + "SecureSessionManager");
        sess.start(username, password);
        String sessID = sess.getSessionId();
        ExperimentSearchCriteria esc =
                EnhancedSearchCriteriaFactory.new_EXPERIMENT_SC();
        esc.setSessionId(sessID);

        if (useFilteringParameters) {

            if (tissueTypeName != null && tissueTypeName.length() > 0)
                esc.setTissueType(tissueTypeName);
            if (chipTypeName != null && chipTypeName.length() > 0)
                esc.setChipPlatformType(chipTypeName);
            if (piName != null && piName.length() > 0) {
                esc.setInvestigatorName(new NameCriterion(piName, piName));
            }
            if (organName != null && organName.length() > 0) {
                esc.setOrganism(organName);
            }
            SearchResult results = esc.search();
            Experiment[] result = (Experiment[]) results.getResultSet();
            return result;

        } else {
            SearchResult results = esc.search();
            /*return all, no filtering*/
            Experiment[] allResults = (Experiment[]) results.getResultSet();
            //  System.out.println(allResults.length + " experiments exist without filtering. ");
            return allResults;
        }

    }

    public String getPiName() {
        return piName;
    }

    public void setPiName(String piName) {
        this.piName = piName;
    }

    public String getChipTypeName() {
        return chipTypeName;
    }

    public void setChipTypeName(String chipTypeName) {
        this.chipTypeName = chipTypeName;
    }

    public String getTissueTypeName() {
        return tissueTypeName;
    }

    public void setTissueTypeName(String tissueTypeName) {
        this.tissueTypeName = tissueTypeName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }
}
