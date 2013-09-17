package org.geworkbench.util.pathwaydecoder;

import java.util.BitSet;
import java.util.Comparator;

/**
 *
 * @author not attributable
 * @version $Id$
 */

public class RankSorter {
    final private static int active = 1;
    final private static int filter = 2;
    final private static int plotted = 5;
    public double x;
    public double y;
    public int ix;
    public int iy;
    public int id;
    private BitSet bits = new BitSet(8);

    public void setPlotted() {
        bits.set(plotted);
    }

    public boolean isPlotted() {
        return bits.get(plotted);
    }

    public void setActive(boolean status) {
        bits.set(active, status);
    }

    public boolean isActive() {
        return bits.get(active);
    }

    public boolean isFiltered() {
        return bits.get(filter);
    }

    public static final Comparator<RankSorter> SORT_X = new Comparator<RankSorter>() {
        public int compare(RankSorter rs1, RankSorter rs2) {
            if (rs1.x < rs2.x)
                return -1;
            if (rs1.x > rs2.x)
                return 1;
            if (rs1.id < rs2.id)
                return -1;
            if (rs1.id > rs2.id)
                return 1;
            return 0;
        }
    };
    public static final Comparator<RankSorter> SORT_Y = new Comparator<RankSorter>() {

        public int compare(RankSorter rs1, RankSorter rs2) {
        	/**
        	 * TODO
        	 * if ranks are the same return random result
        	 * This is needed for ranksorting to introduce some randomness for equal values
        	 */
            if (rs1.y < rs2.y)
                return -1;
            if (rs1.y > rs2.y)
                return 1;
            if (rs1.id < rs2.id)
                return -1;
            if (rs1.id > rs2.id)
                return 1;
            return 0;
        }
    };

}
