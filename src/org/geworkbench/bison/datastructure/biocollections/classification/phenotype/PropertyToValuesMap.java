package org.geworkbench.bison.datastructure.biocollections.classification.phenotype;

import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Title: Plug And Play Framework</p>
 * <p>Description: Architecture for enGenious Plug&Play</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 *          This class implements a map between Pheno Properties and an ArrayList of associated values.
 *          E.g.: A property vector could have two properties
 *          Affected, with values Yes or No
 *          Gender, with values M and F
 */

public class PropertyToValuesMap extends HashMap<DSAnnotLabel, HashSet<DSAnnotValue>> implements Serializable {
    /**
     * Constructor
     */
    public PropertyToValuesMap() {
    }

    /**
     * Contructor from existing PropertyToValuesMap
     *
     * @param pv
     */
    public PropertyToValuesMap(PropertyToValuesMap pv) {
        this.putAll(pv);
    }

    /**
     * Add a value to a phenotypic property (e.g., add M to Gender)
     *
     * @param property the property (e.g. Gender)
     * @param value    the value (e.g., M)
     */
    public void addPropertyValue(DSAnnotLabel property, DSAnnotValue value) {
        HashSet values = (HashSet) this.get(property);
        if (values == null) {
            values = new HashSet();
            values.add(value);
            this.put(property, values);
        } else {
            if (!values.contains(value)) {
                values.add(value);
            }
        }
    }

    /**
     * Remove a value associated with a given property
     *
     * @param property
     * @param value
     */
    public void delPropertyValue(DSAnnotLabel property, DSAnnotValue value) {
        HashSet values = this.get(property);
        if (values != null) {
            values.remove(value);
        }
    }

    /**
     * Remove a property and associated values from the map
     *
     * @param property
     */
    public void delProperty(DSAnnotLabel property) {
        this.remove(property);
    }

    /**
     * Return the array of values associated with a given property
     *
     * @param property
     * @return
     */
    public Set getPropertyValues(DSAnnotLabel property) {
        Set obj = this.get(property);
        return obj;
    }

    /**
     * return a Set of all the PhenoProperties (keys) in the map
     *
     * @return a Set of PhenoProperties
     */
    public Set getPhenoProperties() {
        return this.keySet();
    }
}
