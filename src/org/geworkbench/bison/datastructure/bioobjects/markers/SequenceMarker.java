package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.util.Util;

import java.io.Serializable;

public class SequenceMarker extends CSGeneMarker implements Serializable {
	private static final long serialVersionUID = -575408154089692683L;

	public SequenceMarker() {
    }

    /**
     * Parse label.
     *
     * @param s String
     */
    public void parseLabel(String s) {
        // Ignore leading '>' and any other '<', '>' or characters enclosed by '</...>'.
        s = Util.filter(s, "(</.*?>)|[<>]");
        String[] tokens = s.split("[|]");

        if (tokens.length > 2) {
            if (tokens[0].equalsIgnoreCase("Affy")) {
                setDescription("Affy:" + tokens[1]);
                setLabel(tokens[0]);
            } else {
                int last = tokens.length - 1;
				setDescription(tokens[0] + ": " + tokens[last - 1]
						+ tokens[last]);
				if (tokens[0].equalsIgnoreCase("pir")
						|| tokens[0].equalsIgnoreCase("gp")
						|| tokens[0].equalsIgnoreCase("sp")
						| tokens[0].equalsIgnoreCase("gi")
						|| tokens[0].equalsIgnoreCase("gb")) {
					setLabel(tokens[0] + "|" + tokens[1]);
				} else {
					setLabel(tokens[0]);
				}
            }
        } else if (tokens.length == 2) {
            setDescription(tokens[0] + "|" + tokens[1]);
            setLabel(tokens[0] + "|" + tokens[1]);
        } else {
            setDescription(s);
            setLabel(s);
        }
    }

    public String toString() {
        return label;
    }

    public DSGeneMarker deepCopy() {
        throw new UnsupportedOperationException("deepCopy not implemented for SequenceMarker");
    }

}
