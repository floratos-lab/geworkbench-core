/*
 * Created on Mar 17, 2003
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
package edu.ksu.cis.kdd.ga;

/**
 * Basically a constraint to reject or accept a newly created chromosome c.
 * After GA operations (such as crossover or mutation), the newly generated
 * chromosome may be invalid. If it can be invalid, then it's the implementor
 * of Constraint interface who does the job.
 *
 * @author Roby Joehanes
 */
public interface Constraint {
    public boolean isAccepted(Chromosome c);
}
