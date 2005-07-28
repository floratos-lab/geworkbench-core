package edu.ksu.cis.bnj.test;

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

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.learning.score.DiscrepancyScore;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;

/**
 * Testing K2 Functionality
 *
 * @author Roby Joehanes
 */
public class TestK2 {

    /**
     * Get the toy example out from paper for testing
     *
     * @return Table
     */
    public static Table getExample() {
        Table data = new Table();
        String p = "present", a = "absent";

        for (int i = 1; i <= 3; i++) {
            Attribute attr = new Attribute("x" + i);
            attr.addValue(p);
            attr.addValue(a);
            data.addAttribute(attr);
        }

        Tuple t;
        t = new Tuple();
        t.addValue(p);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 1
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 2
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(p);
        data.addTuple(t); // 3
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 4
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 5
        t = new Tuple();
        t.addValue(a);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 6
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 7
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 8
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 9
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 10

        return data;
    }

    public static void main(String[] args) {

        if (args != null && args.length > 0) {
            try {
                Runtime r = Runtime.getRuntime();
                long origfreemem = r.freeMemory();
                long freemem;
                long origTime = System.currentTimeMillis();
                Table tuples = Table.load(args[0]);
                K2 k2 = new K2(tuples);
                k2.setCandidateScorer(new DiscrepancyScore());
                //k2.setCalculateCPT(false);
                System.gc();
                long afterLoadTime = System.currentTimeMillis();
                freemem = r.freeMemory() - origfreemem;
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));

                BBNGraph g = k2.getGraph();
                //System.exit(0);
                System.out.println("---------------");
                System.out.println("Result of K2:");
                long learnTime = System.currentTimeMillis();
                freemem = r.freeMemory() - origfreemem;
                System.out.println("Memory needed after learning K2 = " + freemem);
                System.out.println("Learning time = " + ((learnTime - afterLoadTime) / 1000.0));
                if (args.length > 1) g.save(args[1]);
                //if (k2 != null) // A dummy statement to prevent K2 being garbage collected
                //System.out.println(g.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Toy example from the paper...");
            BBNGraph g = new K2(getExample()).getGraph();
            System.out.println("---------------");
            System.out.println("Result of K2:");
            System.out.println(g.toString());
        }

    }
}
