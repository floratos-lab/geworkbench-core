package edu.ksu.cis.kdd.classifier.validator;

/*
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

import edu.ksu.cis.kdd.classifier.ClassifierEngine;
import edu.ksu.cis.kdd.data.Table;

/**
 * @author Roby Joehanes
 */
public abstract class Validator {
    protected ClassifierEngine owner;

    public Validator() {
        setOwner(null);
    }

    public Validator(ClassifierEngine o) {
        setOwner(o);
    }

    public abstract boolean hasNext();

    public abstract void next();

    public abstract Table getTrainData();

    public abstract Table getTestData();

    public abstract void init();

    /**
     * Returns the owner.
     *
     * @return Learner
     */
    public ClassifierEngine getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner The owner to set
     */
    public void setOwner(ClassifierEngine owner) {
        this.owner = owner;
    }

}
