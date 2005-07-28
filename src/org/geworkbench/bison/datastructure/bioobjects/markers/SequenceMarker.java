package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import java.io.Serializable;

public class SequenceMarker extends CSGeneMarker implements Serializable {
    public SequenceMarker() {
    }

    /**
     * parseLabel
     *
     * @param s String
     */
    public void parseLabel(String s) {
        String[] tokens = s.split("[|><>]");

        if (tokens.length > 2) {
            if (tokens[1].equalsIgnoreCase("Affy")) {
                setDescription("Affy:" + tokens[2]);
                setLabel(tokens[2]);
            } else {

                int last = tokens.length - 1;
                if (tokens[1].equalsIgnoreCase("pir")) {
                    setDescription("PIR: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[2]);
                } else if (tokens[1].equalsIgnoreCase("gp")) {
                    setDescription("GP: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[2]);
                } else if (tokens[1].equalsIgnoreCase("sp")) {
                    setDescription("SP: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[2]);
                } else if (tokens[1].equalsIgnoreCase("gi")) {
                    setDescription("GI: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[2]);
                } else if (tokens[1].equalsIgnoreCase("gb")) {
                    setDescription("GB: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[2]);
                } else {
                    setDescription(s);
                    setLabel(s);
                }
            }
        } else if (tokens.length == 2) {
            setDescription(tokens[1]);
            setLabel(tokens[1]);
        } else {
            setDescription(s);
            setLabel(s);
        }
    }

    public String toString() {
        if (label == null) {
            return label;
        }
        return label;
    }

    public DSGeneMarker deepCopy() {
        return null;
    }

}
