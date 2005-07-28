package edu.ksu.cis.bnj.bbn.prm;

/***********THIS CLASS IS NOT USED ANY MORE***************
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

import edu.ksu.cis.bnj.bbn.BBNNode;

/**
 * PRMNode
 *
 * @author
 */
public class PRMNode extends BBNNode {
    protected String className;

    /**
     * @return String
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the className.
     *
     * @param className The className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    public void setType(int type) {
        super.setType(type);
    }

}
