package org.geworkbench.bison.datastructure.chipchip.io;

import gov.nih.nci.caBIO.bean.Gene;
import gov.nih.nci.caBIO.bean.GeneSearchCriteria;
import gov.nih.nci.caBIO.bean.SearchResult;
import gov.nih.nci.caBIO.search.*;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.chipchip.CSChipchip;
import org.geworkbench.bison.datastructure.chipchip.CSChipchipSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class ChipchipReader {
    public ChipchipReader() {
    }

    void doIt() {
        File testFile = new File("c:/chipchip/2764SuppRawDataSet.txt");
        File unigeneFile = new File("c:/chipchip/Unigenes.txt");
        readFromFile(testFile);
        //        read(testFile, unigeneFile);
    }

    public CSChipchipSet readFromFile(File file) {

        List<String> geneNames = FileUtil.readVector(file, 1, 2);
        System.out.println("creating unigene hash map");
        long startTime = System.currentTimeMillis();
        HashMap<String, String> geneUnigeneMap = createGeneUnigeneHashMap(geneNames);
        long endTime = System.currentTimeMillis();
        System.out.println("Total time " + (endTime - startTime) / 1000);

        return null;
    }

    public CSChipchipSet read(File file, File unigeneFile) {
        CSChipchipSet chipchipSet = new CSChipchipSet();
        HashMap<String, String> unigeneMap = FileUtil.readHashMap(unigeneFile);
        int markerCtr = 0;
        for (String key : unigeneMap.keySet()) {
            if (unigeneMap.get(key) != null) {
                DSGeneMarker marker = new CSGeneMarker();
                marker.setSerial(markerCtr);
                marker.setLabel(key);
                marker.getUnigene().setUnigeneId(Integer.parseInt(unigeneMap.get(key)));
                if (!chipchipSet.contains(marker)) {
                    chipchipSet.addGenericMarker(marker);
                    markerCtr++;
                }
            }
        }

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String[] arrLine = reader.readLine().split("\t");
            for (int i = 3; i < arrLine.length; i += 2) {
                CSChipchip chipchip = new CSChipchip(new String(arrLine[i]), chipchipSet.getMarkerCount());
                chipchipSet.addChipchip(chipchip);
            }

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                arrLine = new String(line).split("\t");
                String geneName = arrLine[1];
                String unigene = unigeneMap.get(geneName);
                if (unigene != null && unigene.length() > 0) {
                    DSGeneMarker testMarker = new CSGeneMarker(geneName);
                    testMarker.getUnigene().setUnigeneId(Integer.parseInt(unigene));
                    DSGeneMarker refMarker = chipchipSet.getMarker(testMarker);
                    int chipCtr = 0;
                    for (int i = 3; i < arrLine.length; i += 2) {
                        CSChipchip chipchip = chipchipSet.getChipchip(chipCtr);
                        float pValue = Float.parseFloat(arrLine[i]);
                        float ratio = Float.parseFloat(arrLine[i + 1]);
                        DSMutableMarkerValue markerValue = new CSExpressionMarkerValue(ratio);
                        markerValue.setConfidence(pValue);
                        chipchip.setMarkerValue(markerValue, refMarker.getSerial());
                        chipCtr++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chipchipSet;
    }

    HashMap<String, String> createGeneUnigeneHashMaptmp(List<String> geneNames) {
        try {
            geneNames = geneNames.subList(1200, 2000);
            for (String name : geneNames) {
                Gene myGene = new Gene();
                GeneSearchCriteria criteria = new GeneSearchCriteria();
                criteria.setSymbol(name);
                SearchResult result = myGene.search(criteria);
                Gene[] genes = (Gene[]) result.getResultSet();
                if (genes != null && genes.length > 0) {
                    System.out.println(genes[0].getSymbol() + "\t" + genes[0].getClusterId());
                } else {
                    System.out.println(name);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    HashMap<String, String> createGeneUnigeneHashMap(List<String> geneNames) {
        try {
            //            geneNames = geneNames.subList(3500, 4700);
            geneNames = geneNames.subList(4700, geneNames.size());
            //            for (Object elem : geneNames) {
            //                System.out.println(elem);
            //            }

            GeneSearchCriteria gsc = new GeneSearchCriteria();
            gsc.putCriteria("name", geneNames);

            Vector geneAtts = new Vector();
            geneAtts.add("name");
            geneAtts.add("clusterId");
            geneAtts.add("locusLinkId");
            SelectionNode tree = new SelectionNodeImpl("Gene", gsc, geneAtts);

            SearchCriteriaMapping[] myMappings = new SearchCriteriaMapping[geneNames.size()];
            for (ListIterator i = geneNames.listIterator(); i.hasNext();) {
                GeneSearchCriteria sc = new GeneSearchCriteria();
                String myTag = (String) i.next();
                sc.putCriteria("name", myTag);
                SearchCriteriaMapping scm = new SearchCriteriaMapping(myTag, sc);
                myMappings[i.previousIndex()] = scm;
            }
            GridSearchCriteria gridCriteria = new GridSearchCriteria(tree, myMappings);
            ObjectGrid myObjGrid = new ObjectGridImpl();
            GridSearchResultMapping[] results = myObjGrid.search(gridCriteria);

            for (int i = 0; i < results.length; i++) {
                GridSearchResultMapping resultMapping = results[i];
                String attVal = (String) resultMapping.getClientData();
                GridRow[] rows = resultMapping.getResult();
                if (rows.length > 1) {
                    GridCell nameCell = rows[0].getCell("Gene.name");
                    GridCell myCell = rows[0].getCell("Gene.clusterId");
                    GridCell llCell = rows[0].getCell("Gene.locusLinkId");
                    //                System.out.println("for criteria = " + attVal + "\t" +
                    //                                   nameCell.getObject() +
                    //                                   " the gene name is: " + myCell.getObject());
                    System.out.println(nameCell.getObject() + "\t" + myCell.getObject() + "\t" + llCell.getObject());
                } else {
                    System.out.println(attVal + "\t");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        new ChipchipReader().doIt();
    }
}
