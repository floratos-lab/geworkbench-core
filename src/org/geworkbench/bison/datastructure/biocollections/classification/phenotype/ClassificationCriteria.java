package org.geworkbench.bison.datastructure.biocollections.classification.phenotype;

import java.io.Serializable;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 *          <p/>
 *          The fundamental goal of defining a set of phenotypic criteria is to divide the microarrays
 *          into a set of distinct classes for data analysis. This is used principally in the context of
 *          <i>supervised</i> and <i>unsupervised learning</i> algorithms, where a classifier capable of assigning
 *          microarrays to two or more classes is seeked.<p>
 *          Currently the ClassificationCriteria class, supports only two-class supervised learning but it
 *          could be easily extended to support an arbitrary number of classes.
 *          The two predefined classes are called <b>Case</b> and <b>Control</b> (for obvious mnemonic reasons).
 *          Additionally, there are two support classes, called <b>Test</b> and <b>Ignore</b>, that address the
 *          following needs.<p>
 *          How cases, controls, test, and ignore are treated in the context of the individual algorithms
 *          is left to the algorithms. The Phenotype package simply supports determining which one of these
 *          classes a microarray belongs to. Note that belonging to more than one class is not allowed.
 *          Also note that, by default, microarrays that have not been specifically assigned to a class
 *          are considered members of the Control class.<p>
 *          Typically, the following semantics should be used
 *          <table border="1" width="80%">
 *          <tr>
 *          <td width="10%"> Case:</td>
 *          <td width="90%"> In a SUPERVISED LEARNING context, microarray in this group should be used to generate
 *          a classifier that discriminates them from those in the control set.
 *          In an UNSUPERVISED LEARNING context, microarrays in this group should be analyzed to find
 *          some internal organization criteria of the class.</td>
 *          </tr>
 *          <tr>
 *          <td width="10%">Controls:</td>
 *          <td width="90%">In a SUPERVISED LEARNING context, microarray in this group should be used to generate
 *          a classifier that discriminates them from those in the case set.
 *          In an UNSUPERVISED LEARNING context, microarrays in this group should be ignored </td>
 *          </tr>
 *          <tr>
 *          <td width="10%"> Test:</td>
 *          <td width="90%"> Micrarrays in this set should be used to test the discriminative power of the classifiers
 *          obtained in either a SUPERVISED or UNSUPERVISED LEARNING context. Note, by definition, that
 *          one should never train a classifier on a set which is then used to test its quality.</td>
 *          </tr>
 *          <tr>
 *          <td width="10%">Ignore:</td>
 *          <td width="90%"> Microarrays in this class should be simply ignored by algorithms and analysises</td>
 *          </tr>
 *          </table><p>
 *          <p/>
 *          Several constants are defined to iterate over the microarrays assigned to a class.
 *          To do this, the following code is used:<p>
 *          JMicroarraySet microarraySet;<p>
 *          ...<p>
 *          Iterator it = microarraySet.iterator(SelXXX);<p>
 *          while(it.hasNext()) {<p>
 *          IMicroarray microArray = (IMicroarray)it.next();<p>
 *          ...<p>
 *          }<p>
 *          <p/>
 *          where, microarraySet is a JMicroarraySet object that has been properly initialized,
 *          and SelXXX is any one of the values ({@link ClassificationCriteria#selAll}, {@link ClassificationCriteria#selControl},
 *          {@link ClassificationCriteria#selCase}, {@link ClassificationCriteria#selIgnore}, {@link ClassificationCriteria#selTest})
 *          <p/>
 *          Additionally, the class of a specific microarray can be determined as follows:
 *          <p/>
 *          int classId = microarraySet.getPhenotype().getPhenoClassId(microArray);<p>
 *          <p/>
 *          where microarray implements the IMicroarray interface.<p>
 *          <p/>
 *          Each microarray is characterized by a set of values for some predefined properties.
 *          For instance, an NCI60 cell line microarray could be characterized by the following properties:<p>
 *          "Cancer Panel", "p53 mutation", "GI50 for compound XXX", etc.<p>
 *          A particular microarray, then, would be characterized by the values that each one of
 *          these properties would have, as in:<p>
 *          <p/>
 *          <table border="1" width="37%">
 *          <tr>
 *          <td width="46%">
 *          <p align="center"><b>Property</b></td>
 *          <td width="54%">
 *          <p align="center"><b>Value</b></td>
 *          </tr>
 *          <tr>
 *          <td width="46%">&nbsp;Cancer Panel</td>
 *          <td width="54%">&nbsp;Melanoma</td>
 *          </tr>
 *          <tr>
 *          <td width="46%">&nbsp;p53 mutation</td>
 *          <td width="54%">&nbsp;No</td>
 *          </tr>
 *          <tr>
 *          <td width="46%">
 *          <p align="left">&nbsp;GI50 for Taxol</td>
 *          <td width="54%">&nbsp;2.34</td>
 *          </tr>
 *          </table><p>
 *          Currently, a Phenotypic criteria is set by first selecting a particular property (e.g., Cancer panel)
 *          and then by assigning its specific values (and the corresponding microarrays) to a specific class.
 *          <p/>
 *          For instance, suppose that we were interested in differentiating breast and ovarian cancer from
 *          CNS cancer.  Then, we would select the "Cancer Panel" property and assign "Breast Cancer" and "Ovarian
 *          Cancer" to the Case class, "CNS Cancer" to the Control class, and all other values to the Ignore class
 *          <p/>
 *          The PhenotypePanel also allows a user to create new properties and assign values to microarrays
 *          on the fly, using the GUI. This would allow for instance the ability to create a new property, say "Inhibited by
 *          taxol" and assign the value YES to all the microarrays that have a GI50 lower than a predefined value and NO to
 *          the rest.
 *          <p/>
 *          In the future, two improvements will be made:
 *          <ol>
 *          <li> arbitrary criteria will be allowed for class selection.<p>
 *          E.g. "Case := ('GI 50 for Taxol' >= 2.12)" or<p>
 *          "Case := (('Cancer Panel' == 'Breast Cancer') OR ('Cancer Panel' == 'Ovarian Cancer'))<p></li>
 *          <li> Criteria spanning multiple properties will be allowed.<p>
 *          E.g., "Case := (('Cancer Panel' == 'Breast Cancer') AND ('GI 50 for Taxol' >= 2.12)) <p></li></ol>
 */

public class ClassificationCriteria implements Serializable {


    /**
     * The following variables contain visual icons to represent the four classes
     */

    //    public final static ImageIcon redPinIcon   = new ImageIcon(ClassificationCriteria.class.getResource("redpin.gif"));
    //    public final static ImageIcon whitePinIcon = new ImageIcon(ClassificationCriteria.class.getResource("whitepin.gif"));
    //    public final static ImageIcon eraserIcon   = new ImageIcon(ClassificationCriteria.class.getResource("eraser.gif"));
    //    public final static ImageIcon markerIcon   = new ImageIcon(ClassificationCriteria.class.getResource("marker.gif"));
    /**
     * use microarraySet.iterator(SelAll) to iterate over all microrrays in microarraySet
     */
    //    public final static int selAll          = -98;
    /**
     * use microarraySet.iterator(SelControl) to iterate over all microrrays of the Control class in microarraySet
     */
    //    public final static int selControl      = 0;
    /**
     * use microarraySet.iterator(SelCase) to iterate over all microrrays of the Case class in microarraySet
     */
    //    public final static int selCase         = 1;
    /**
     * use microarraySet.iterator(SelIgnore) to iterate over all microrrays of the Ignore class in microarraySet
     */
    //    public final static int selIgnore       = -99;
    /**
     * use microarraySet.iterator(SelTest) to iterate over all microrrays of the Test class in microarraySet
     */
    //    public final static int selTest         = -100;

    /**
     * The name of the currently defined criteria
     */
    //    private String  name         = null;
    /**
     * The following maps hold all the Values (for the selected Property) that are assigned to the
     * corresponding class. Note that controlSet does not need to be defined as it contains all the
     * remaining values by default.<p>
     * E.g. in the previous example:<p>
     * <b>caseSet</b>    would contain 'Breast Cancer' and 'Ovarian Cancer'<p>
     * <b>ignoreSet</b>  would contain all the other values of the 'Cancer Panel' property except for 'CNS Cancer'<p>
     * <b>testSet</b>    would be empty<p>
     */
    //    private HashMap caseSet         = new HashMap();
    //    private HashMap ignoreSet       = new HashMap();
    //    private HashMap testSet         = new HashMap();
    /**
     * The following boolean variable is used to differentiate a SUPERVISED LEARNING context from an UNSUPERVISED one
     * in the former, specific class assignments are made to Cases and Controls. In the latter, All microarrays, except
     * those assigned to the Ignore class will be considered as members of the Case class
     */
    //    private boolean unsupervised = false;
    /**
     * The number of microarrays respectively in the Case, Control, Test, and Ignore classes.
     */
    //    protected int caseNo      = 0;
    //    protected int controlNo   = 0;
    //    protected int testNo      = 0;
    //    protected int ignoreNo    = 0;

    /**
     * Constructor
     * @param name The name of the Phenotypic Criteria (E.g., "Breast and Ovarian Cancer vs. CNS Cancer")
     */
    //    public ClassificationCriteria(String name) {
    //        this.name = name;
    //    }
    /**
     * Use to get the name of the current Phenotypic Criteria
     * @return the name of the current Phenotypic Criteria
     */
    //    public String getName() {
    //        return name;
    //    }

    /**
     * Used to determine the class that a particular property value is assigned to
     * @param phValue A string with the property value
     * @return The class as one of selCase, selControl, selIgnore, or selTest
     */
    //    public int getPhenoClassId(DSAnnotValue value) {
    //        if (unsupervised() || (value == null)) {
    //            return selCase;
    //        } else {
    //            if (caseSet.get(value) != null) {
    //                return selCase;
    //            } else if (value.equals("-99") || ignoreSet.get(value) != null) {
    //                return selIgnore;
    //            } else if (value.equals("-100") || testSet.get(value) != null) {
    //                return selTest;
    //            }
    //        }
    //        return selControl;
    //    }
    /**
     * Intrease the number of microarrays in the corresponding class
     * @param phClassId the class (one of selCase, selControl, selIgnore, or selTest)
     */
    //    public void addCount(int phClassId) {
    //        switch (phClassId) {
    //            case selCase:
    //                caseNo++;
    //                break;
    //            case selControl:
    //                controlNo++;
    //                break;
    //            case selTest:
    //                testNo++;
    //                break;
    //            case selIgnore:
    //            default:
    //                ignoreNo++;
    //                break;
    //        }
    //    }
    /**
     * returns the number of microarrays in the selected class
     * @param phClassId the class (one of selCase, selControl, selIgnore, or selTest)
     * @return the number of microarrays in class "phClassId"
     */
    //    public int getCount(int phClassId) {
    //        int count = 0;
    //        switch (phClassId) {
    //            case selCase:
    //                count = caseNo;
    //                break;
    //            case selControl:
    //                count = controlNo;
    //                break;
    //            case selTest:
    //                count = testNo;
    //                break;
    //            case selIgnore:
    //            default:
    //                count = ignoreNo;
    //                break;
    //        }
    //        return count;
    //    }
    /**
     * Resets all the class counts to 0
     */
    //    public void resetCounts() {
    //        caseNo    = 0;
    //        controlNo = 0;
    //        testNo    = 0;
    //        ignoreNo  = 0;
    //    }
    /**
     * Used to assign a phenotypic property value (e.g., Breast Cancer) to a specific class
     * @param propertyValue the property value to be assigned
     * @param classId the id of the class
     * @return null if already present in the class
     */
    //    public Object assignPropertyValue(String propertyValue, int classId) {
    //        Object added = null;
    //        switch (classId) {
    //            case selIgnore:
    //                testSet.remove(propertyValue);
    //                caseSet.remove(propertyValue);
    //                added = ignoreSet.put(propertyValue, "");
    //                break;
    //            case selTest:
    //                caseSet.remove(propertyValue);
    //                ignoreSet.remove(propertyValue);
    //                added = testSet.put(propertyValue, "");
    //                break;
    //            case selCase:
    //                testSet.remove(propertyValue);
    //                ignoreSet.remove(propertyValue);
    //                if(!unsupervised()) {
    //                    added = caseSet.put(propertyValue, "");
    //                }
    //                break;
    //            case selControl:
    //            case selAll:
    //                caseSet.remove(propertyValue);
    //                testSet.remove(propertyValue);
    //                ignoreSet.remove(propertyValue);
    //        }
    //        return added;
    //    }
    /**
     * Used to assign a phenotypic property value (e.g., Breast Cancer) to a specific class
     * @param propertyValue the property value to be assigned
     * @param classId the id of the class
     * @return null if not present in the class
     */
    //    public Object removePropertyValue(String propertyValue, int classId) {
    //        Object added = null;
    //        switch (classId) {
    //            case selIgnore:
    //                added = ignoreSet.remove(propertyValue);
    //                break;
    //            case selTest:
    //                added = testSet.remove(propertyValue);
    //                break;
    //            case selCase:
    //                added = caseSet.remove(propertyValue);
    //                break;
    //            case selControl:
    //            case selAll:
    //        }
    //        return added;
    //    }
    /**
     * Checks if the current class id is a valid one
     * @param phClassId
     * @return true if one of the valid class Ids
     */
    //    static public boolean isClassValid(int phClassId) {
    //        switch (phClassId) {
    //            case selIgnore:
    //            case selTest:
    //            case selCase:
    //            case selControl:
    //            case selAll:
    //                return true;
    //            default:
    //                return false;
    //        }
    //    }
    /**
     * Returns the label associated with the corresponding class id
     * @param phClassId the class id
     * @return the label of the corresponding class (E.g., Case)
     */
    //    static public String getPhenoClassLabel(int phClassId) {
    //        String description = null;
    //        switch (phClassId) {
    //            case selCase:
    //                description = "Case";
    //                break;
    //            case selControl:
    //                description = "Control";
    //                break;
    //            case selTest:
    //                description = "Test";
    //                break;
    //            case selIgnore:
    //            default:
    //                description = "Ignore";
    //                break;
    //        }
    //        return description;
    //    }
    /**
     * Each class has an associated color that is used whenever a microarray of that class wants to be
     * represented in a color graph. This method returns the default color for each classId.
     * @param phClassId the class Id
     * @return the default color for the class Id
     */
    //    static public Color getSelectionColor(int phClassId) {
    //        Color color = null;
    //        switch (phClassId) {
    //            case selCase:
    //                color = Color.blue;
    //                break;
    //            case selControl:
    //                color = Color.yellow;
    //                break;
    //            case selTest:
    //                color = Color.darkGray;
    //                break;
    //            case selIgnore:
    //            default:
    //                color = Color.white;
    //                break;
    //        }
    //        return color;
    //    }
    /**
     * Each class has an associated icon that is used whenever a microarray of that class wants to be
     * represented in an iconic fashion. This method returns the default icon for each classId.
     * @param phClassId the class id
     * @return the default icon for that class id
     */
    //    static public ImageIcon getSelectionIcon(int phClassId) {
    //        ImageIcon icon = null;
    //        switch (phClassId) {
    //            case selCase:
    //                icon = redPinIcon;
    //                break;
    //            case selControl:
    //                icon = whitePinIcon;
    //                break;
    //            case selTest:
    //                icon = markerIcon;
    //                break;
    //            case selIgnore:
    //            default:
    //                icon = eraserIcon;
    //                break;
    //        }
    //        return icon;
    //    }
    /**
     * returns whether this criteria is "classless", i.e., unsupervised.
     * @return true if unsupervised
     */
    //    public boolean unsupervised() {
    //        return unsupervised;
    //    }
    /**
     * sets  whether this criteria is "classless", i.e., unsupervised.
     * @param unsupervised true if unsupervised
     */
    //    public void unsupervised(boolean unsupervised) {
    //        this.unsupervised = unsupervised;
    //    }
}
