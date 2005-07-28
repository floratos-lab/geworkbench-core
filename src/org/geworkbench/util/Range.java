package org.geworkbench.util;

import org.geworkbench.util.Normal;

import java.io.Serializable;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Range implements Serializable {
    public double min = +999999;
    public double max = -999999;
    public Normal norm = new Normal();

    public Range() {
    }
}
