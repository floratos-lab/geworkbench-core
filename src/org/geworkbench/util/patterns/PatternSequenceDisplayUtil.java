package org.geworkbench.util.patterns;

import java.util.TreeSet;

import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PatternSequenceDisplayUtil{
       private org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence sequence;
       private TreeSet<PatternLocations> treeSet;

       public PatternSequenceDisplayUtil(CSSequence _sequence){
           sequence = _sequence;
           treeSet = new TreeSet<PatternLocations>();
       }

       public void addPattern(PatternLocations pl){
           treeSet.add(pl);
       }
       public boolean hasPattern(){
           return !treeSet.isEmpty();
       }

    public TreeSet getTreeSet() {
        return treeSet;
    }

    public void setTreeSet(TreeSet treeSet) {
        this.treeSet = treeSet;
    }


}
