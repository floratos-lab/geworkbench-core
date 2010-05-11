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
 * @version $Id$
 */
public class PatternSequenceDisplayUtil{
       private TreeSet<PatternLocations> treeSet;

       public PatternSequenceDisplayUtil(CSSequence _sequence){ // FIXME argument is ignored
           treeSet = new TreeSet<PatternLocations>();
       }

       public void addPattern(PatternLocations pl){
           treeSet.add(pl);
       }
       public void mergePatternSequenceDisplayUtil(PatternSequenceDisplayUtil newPu){
           if(newPu==null){
               return;
           }
           if(treeSet==null){
               treeSet = new TreeSet<PatternLocations>();
           }
           TreeSet<PatternLocations> newTreeSet = newPu.getTreeSet();
           for(PatternLocations pl: newTreeSet){
               treeSet.add(pl);
           }
       }
       public boolean hasPattern(){
           return !treeSet.isEmpty();
       }

    public TreeSet<PatternLocations> getTreeSet() {
        return treeSet;
    }

    public void setTreeSet(TreeSet<PatternLocations> treeSet) {
        this.treeSet = treeSet;
    }


}
