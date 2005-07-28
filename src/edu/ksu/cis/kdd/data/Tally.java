package edu.ksu.cis.kdd.data;

/*
 * Created on Jul 14, 2003
 *
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.util.List;

/**
 * @author Roby Joehanes
 */
public interface Tally {
    public Tally createSubTally(int idx, int val);

    public Tally createSubTally(int[] indices, int[] values);

    public int tally(int idx, int val);

    public int tally(int[] indices, int[] values);

    public int size();

    public Data getUnderlyingData();

    public void setUnderlyingData(Data data);

    public int[] getRelevantAttributeIndices();

    public List groupedTally(int[] indices);
}
