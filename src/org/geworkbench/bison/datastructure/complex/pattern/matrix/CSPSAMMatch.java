package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.pattern.CSPatternMatch;

/**
 * @author manjunath at genomecenter dot columbia dot edu
 */
public class CSPSAMMatch<T, R> extends CSPatternMatch<T, R> implements DSBioObject {
    
    String label = "";
    
    /** Creates a new instance of CSPSAMMatch */
    public CSPSAMMatch(T object) {
        super(object);
    }

    public void addNameValuePair(String name, Object value) {
    }

    public Object[] getValuesForName(String name) {
        throw new UnsupportedOperationException("method not implemented");
    }

    public void forceUniqueValue(String name) {
    }

    public void allowMultipleValues(String name) {
    }

    public boolean isUniqueValue(String name) {
        throw new UnsupportedOperationException("method not implemented");
    }

    public void clearName(String name) {
    }

    public void addDescription(String description) {
    }

    public String[] getDescriptions() {
        throw new UnsupportedOperationException("method not implemented");
    }

    public void removeDescription(String description) {
    }

    public String getID() {
        throw new UnsupportedOperationException("method not implemented");
    }

    public void setID(String id) {
    }

    public int getSerial() {
        throw new UnsupportedOperationException("method not implemented");
    }

    public void setSerial(int serial) {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean enabled() {
        throw new UnsupportedOperationException("method not implemented");
    }

    public void enable(boolean status) {
    }   
    
    public String toString() {
        return getLabel();
    }
}