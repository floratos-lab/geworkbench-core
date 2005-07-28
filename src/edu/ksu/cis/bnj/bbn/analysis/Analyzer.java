package edu.ksu.cis.bnj.bbn.analysis;

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
 * 
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * An abstract class for Analyzer
 *
 * @author Roby Joehanes
 */
public abstract class Analyzer {
    public abstract void dump(OutputStream o);

    public void dump(String file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(new File(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dump(out);
    }

    public void dump() {
        dump(System.out);
    }
}
