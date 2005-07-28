/*
 * Created on Apr 5, 2003
 *
 */
package edu.ksu.cis.bnj.test;

import java.util.*;

/**
 * @author Roby Joehanes
 */
public class TestUniqueInst {

    private static int[] values;

    private static void getUniqueInstantiation(LinkedList nodes, Hashtable curInst) {
        Integer node = (Integer) nodes.removeFirst();
        int nodeInt = node.intValue();
        for (int i = 0; i < values[nodeInt]; i++) {
            curInst.put(node, new Integer(i));
            if (nodes.size() == 0) {
                System.out.println(curInst.toString());
            } else {
                getUniqueInstantiation(nodes, curInst);
            }
        }

        nodes.addFirst(node);
    }

    private static Set getUniqueInstantiation(LinkedList nodes, Hashtable curInst, HashSet set) {
        Integer node = (Integer) nodes.removeFirst();
        int nodeInt = node.intValue();
        for (int i = 0; i < values[nodeInt]; i++) {
            curInst.put(node, new Integer(i));
            if (nodes.size() == 0) {
                set.add(curInst.clone());
            } else {
                getUniqueInstantiation(nodes, curInst);
            }
        }

        nodes.addFirst(node);
        return set;
    }

    public static void main(String[] args) {
        values = new int[3];
        values[0] = 2;
        values[1] = 3;
        values[2] = 2;

        LinkedList list = new LinkedList();
        list.add(new Integer(0));
        list.add(new Integer(1));
        list.add(new Integer(2));

        //getUniqueInstantiation(list, new Hashtable());
        Set set = getUniqueInstantiation(list, new Hashtable(), new HashSet());
        for (Iterator i = set.iterator(); i.hasNext();) {
            Hashtable tbl = (Hashtable) i.next();
            System.out.println(tbl.toString());
        }
    }
}
