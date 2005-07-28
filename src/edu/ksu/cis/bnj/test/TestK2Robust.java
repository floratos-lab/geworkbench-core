/*
 * Created on Feb 20, 2003
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
package edu.ksu.cis.bnj.test;

import edu.ksu.cis.bnj.bbn.learning.analysis.Robustness;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.kdd.data.Table;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Testing K2 Functionality with Robustness
 *
 * @author Roby Joehanes
 */
public class TestK2Robust {

    public static void main(String[] args) {

        if (args == null || args.length < 4) {
            System.out.println("Usage: TestK2Robust filename.xml #bootstrap #subsample result.txt [order]");
            System.out.println("E.g.: TestK2Robust filename.xml 200 76 result.txt 1,2,3");
            System.out.println("Order is optional. Order may not have spaces -- only commas are allowed.");
            return;
        }

        try {
            int bootstrapSample = Integer.parseInt(args[1]);
            int subsample = Integer.parseInt(args[2]);

            Table tuples = Table.load(args[0]);
            K2 k2 = new K2(tuples);
            k2.setCalculateCPT(false);

            if (args.length > 4) {
                LinkedList order = new LinkedList();
                HashSet seenBefore = new HashSet();
                StringTokenizer tok = new StringTokenizer(args[4], ",");
                while (tok.hasMoreTokens()) {
                    String token = tok.nextToken();
                    Integer i = new Integer(Integer.parseInt(token) - 1);
                    if (seenBefore.contains(i)) throw new RuntimeException("Duplicate order #" + i);
                    seenBefore.add(i);
                    order.add(tuples.getAttribute(i.intValue()).getName());
                }
                k2.setOrdering(order);
            }


            Robustness robust = new Robustness(k2, bootstrapSample, subsample);
            robust.dump(new FileOutputStream(args[3]));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
