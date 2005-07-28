package edu.ksu.cis.bnj.bbn.inference.cutset;

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

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.BBNValue;
import salvo.jesus.graph.GraphImpl;
import salvo.jesus.graph.Vertex;
import salvo.jesus.graph.VertexImpl;

import java.util.*;

class CreateSplittingGraph {
    // the graph that came from the viewer
    private GraphImpl graph;    // an undirected version of the graph
    private BBNGraph bayesNet;
    private VertexImpl vertices[];
    private LinkedList cutsetNodeList;
    private Hashtable cutsetNodeTable;
    private Hashtable nodeTable;
    int index;

    public CreateSplittingGraph(BBNGraph g) {
        bayesNet = g;
        graph = new GraphImpl();
        vertices = new VertexImpl[g.getVerticesCount() * 2];

        cutsetNodeList = new LinkedList();
        cutsetNodeTable = new Hashtable();
        nodeTable = new Hashtable();
        //System.out.println(" " + bn.size());
        index = 0;
    }

    public LinkedList go() {
        String outName;
        LinkedList cutsetOutNodes = new LinkedList();
        convertBnToGraph();
        printVertex();
        removeVertices2(false);
        //System.out.println("dfdfd");
        //printVertex();
        while (graph.getVerticesCount() > 0) {
            outName = removeByRatio();
            if (outName != null) {
                cutsetOutNodes.add(outName);
                //              System.out.println("Outname " + outName);
            }
            removeVertices2(true);
        }
        getCutsetNodes(cutsetOutNodes);
        return cutsetNodeList;
    }

    private Vertex vertexAdd(String lab, double weight) {
        //System.out.println(" " + index);
        vertices[index] = new VertexImpl();
        vertices[index].setObject(new Double(weight));
        //System.out.println(lab);
        vertices[index].setLabel(lab);
        try {
            graph.add(vertices[index]);
        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
        }


        index++;
        return vertices[index - 1];
    }

    private void printVertex() {
        /*VertexImpl v;
        System.out.println("Printing Vertices");
        Iterator vi = graph.getVerticesIterator();
        while(vi.hasNext())
        {
            v = (VertexImpl)vi.next();
            System.out.println(v.getLabel());
        }*/
        //System.out.println("" + graph.toString());
    }

    private Vertex getVertex(String lab) {
        VertexImpl v;
        Iterator vi = graph.getVerticesIterator();
        while (vi.hasNext()) {
            v = (VertexImpl) vi.next();
            //System.out.println(v.getLabel());
            //System.out.println(lab);
            if (lab.equals(v.getLabel()))
                return v;
        }
        return null;
    }

    public int getArity(BBNNode currNode) {
        int arity = 0;
        BBNValue value = currNode.getValues();
        if (value instanceof BBNDiscreteValue) {
            BBNDiscreteValue dval = (BBNDiscreteValue) value;
            arity = dval.getArity();
        }
        return arity;
    }

    public void convertBnToGraph() {
        BBNNode child, parent, tempchild;  // this is temporary inorder to accomodate the bn.getNodeAt
        int numparents, numchildren;
        String nodein, nodeout, parentname, childname;
        int numberofstates;
        Vertex v1, v2, v3;
        List order = bayesNet.topologicalSort();
        for (Iterator i = order.iterator(); i.hasNext();) {
            child = (BBNNode) i.next(); //////////////this is the problem causing me to use node instead of BBNNode
            nodein = child.getLabel() + "in";
            nodeout = child.getLabel() + "out";
            v1 = getVertex(nodein);
            if (v1 == null) {
                v1 = vertexAdd(nodein, 999999999.9);   //// some high value corresponding to infinity
            }

            v2 = getVertex(nodeout);
            if (v2 == null) {
                numberofstates = getArity(child);
                v2 = vertexAdd(nodeout, numberofstates);   //// some high value corresponding to infinity
            }

            try {
                //System.out.println("Edge added" + v1.getLabel() + v2.getLabel());
                graph.addEdge(v1, v2);
            } catch (Exception e) {
                System.err.println("Exception caught: " + e.getMessage());
            }
            List l = child.getParents();
            numparents = l.size();
            parentname = null;
            //System.out.println(v1.getLabel() + " "+ numparents );
            for (Iterator parentIterator = l.iterator(); parentIterator.hasNext();) {
                parent = (BBNNode) parentIterator.next();

                parentname = parent.getLabel() + "out";
                v3 = getVertex(parentname);
                if (v3 == null) {
                    numberofstates = getArity(parent);
                    v3 = vertexAdd(parentname, numberofstates);
                }
                try {
                    List adjVertices = graph.getAdjacentVertices(v1);
                    //System.out.println( v1.getLabel() + v3.getLabel() + graph.isConnected(v1,v3) + adjVertices.contains(v3));
                    if (adjVertices.contains(v3) == false) {
                        //System.out.println("Edge added" + v1.getLabel() + v3.getLabel());
                        graph.addEdge(v1, v3);
                    }
                } catch (Exception e) {
                    System.err.println("Exception caught: " + e.getMessage());
                }
            }
            l = child.getChildren();
            numchildren = l.size();
            for (Iterator childIterator = l.iterator(); childIterator.hasNext();) {
                tempchild = (BBNNode) childIterator.next();
                childname = tempchild.getLabel() + "in";
                v3 = getVertex(childname);
                if (v3 == null) {
                    v3 = vertexAdd(childname, 999999999.9);
                }
                try {
                    List adjVertices = graph.getAdjacentVertices(v2);

                    //System.out.println( v2.getLabel() + v3.getLabel() + graph.isConnected(v2,v3) + adjVertices.contains(v3));
                    if (adjVertices.contains(v3) == false) {
                        //System.out.println("Edge added" + v2.getLabel() + v3.getLabel());
                        graph.addEdge(v2, v3);
                    }
                } catch (Exception e) {
                    System.err.println("Exception caught: " + e.getMessage());
                }
            }
            v1 = null;
            v2 = null;
            v3 = null;

        }

    }

    public void removeVertices(boolean changeweight) {
        Set s1, s0;
        VertexImpl vx;
        Double weight;
        boolean noderemovedflag = true;
        s0 = graph.getVertices(0);
        s1 = graph.getVertices(1);
        while (noderemovedflag == true) {
            Iterator vi = graph.getVerticesIterator();
            while (vi.hasNext()) {
                vx = (VertexImpl) vi.next();
                if (s0.contains(vx) || s1.contains(vx)) {
                    if (changeweight == true) {
                        weight = (Double) vx.getObject();
                        recalculateWeights(vx, weight.doubleValue() / graph.getDegree(vx));
                    }

                    try {
                        //System.out.println(vx.getLabel()+ "removed");
                        graph.remove(vx);
                    } catch (Exception e) {
                        System.err.println("Exception caught: " + e.getMessage());
                    }
                    noderemovedflag = true;
                    s0 = graph.getVertices(0);
                    s1 = graph.getVertices(1);
                    break;
                }
                noderemovedflag = false;
            }
        }
    }

    public void removeVertices2(boolean changeweight) {
        Set s1, s0;
        VertexImpl vx;
        Double weight;
        boolean noderemovedflag = true;
        s0 = graph.getVertices(0);
        s1 = graph.getVertices(1);
        Iterator i;
        while (!(s0.isEmpty() && s1.isEmpty())) {
            i = s0.iterator();
            while (i.hasNext()) {
                vx = (VertexImpl) i.next();
                if (changeweight == true) {
                    weight = (Double) vx.getObject();
                    recalculateWeights(vx, weight.doubleValue() / graph.getDegree(vx));
                }

                try {
                    //System.out.println(vx.getLabel()+ "removed");
                    //graph.removeEdges(vx);
                    graph.remove(vx);
                    vx = null;
                } catch (Exception e) {
                    System.err.println("Exception caught: " + e.getMessage());
                }
            }
            i = s1.iterator();
            while (i.hasNext()) {
                vx = (VertexImpl) i.next();
                if (changeweight == true) {
                    weight = (Double) vx.getObject();
                    recalculateWeights(vx, weight.doubleValue() / graph.getDegree(vx));
                }

                try {
                    //System.out.println(vx.getLabel()+ "removed");
                    //graph.removeEdges(vx);
                    graph.remove(vx);
                    vx = null;
                } catch (Exception e) {
                    System.err.println("Exception caught: " + e.getMessage());
                }
            }
            s0 = graph.getVertices(0);
            s1 = graph.getVertices(1);

        }

    }

    public String removeByRatio() {
        VertexImpl v;
        VertexImpl nodewithminratio = null;
        // some initial arbitrary weight which is not possible
        double minratio = -8888.8;
        double tempratio = 0.0;
        Double tempweight;
        Iterator vi = graph.getVerticesIterator();
        while (vi.hasNext()) {
            //System.out.println("in remove by ratio");
            v = (VertexImpl) vi.next();
            //tempstr = v.getLabel();
            tempweight = (Double) v.getObject();
            tempratio = tempweight.doubleValue() / graph.getDegree(v);
            if (nodewithminratio == null) {
                nodewithminratio = v;
                minratio = tempratio;
            } else {
                if (minratio > tempratio) {
                    nodewithminratio = v;
                    minratio = tempratio;
                }
            }

        }
        //System.out.println(nodewithminratio.getLabel() + " " + minratio);
        //cutsetOutNodes.add(nodewithminratio.getLabel());
        if (nodewithminratio != null) {
            String label = nodewithminratio.getLabel();
            recalculateWeights(nodewithminratio, minratio);
            try {
                graph.remove(nodewithminratio);
            } catch (Exception e) {
                System.err.println("Exception caught: " + e.getMessage());
            }
            return label;
        } else
            return null;

    }


    public void recalculateWeights(VertexImpl v, double weight) {
        List l = graph.getAdjacentVertices(v);
        Double tempweight;
        for (int i = 0; i < l.size(); i++) {
            v = (VertexImpl) l.get(i);
            tempweight = (Double) v.getObject();
            v.setObject(new Double(tempweight.doubleValue() - weight));
        }
    }

    public Hashtable getNodetable() {
        return nodeTable;
    }

    public Hashtable getCutsetTable() {
        return cutsetNodeTable;
    }

    public void getCutsetNodes(LinkedList cutsetOutNodes) {

        Iterator vertexIterator = bayesNet.getVerticesIterator();
        //System.out.println("-------------------------");
        while (vertexIterator.hasNext()) {
            BBNNode vertex = (BBNNode) vertexIterator.next();
            //System.out.println("came here");
            nodeTable.put(vertex.getLabel(), vertex);
            String vertexOutName = vertex.getLabel() + "out";
            if (cutsetOutNodes.contains(vertexOutName)) {
                cutsetNodeList.add(vertex);
                System.out.println(vertex.getLabel());
                cutsetNodeTable.put(vertex.getLabel(), vertex);
            }
        }
        System.out.println("----------Cutset Nodes---------------");
        System.out.println(cutsetNodeTable.toString());
        //System.out.println("-------------------------");
        //System.out.println(nodeTable.toString());
    }
    /*public void recalculateWeights(Vertex v,double w)
    {
    }*/


}
