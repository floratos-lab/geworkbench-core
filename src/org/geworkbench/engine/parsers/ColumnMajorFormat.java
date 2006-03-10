/*
 * ColumnMajorFormat.java
 *
 * Created on March 7, 2006, 11:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.geworkbench.engine.parsers;

import COM.rsa.jsafe.v;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.bison.util.Range;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

/**
 * @author manjunath at genomecenter dot columbia dot edu
 */

public class ColumnMajorFormat extends DataSetFileFormat {
    
    public ColumnMajorFormat() {
        formatName = "Stanford Expression Format";
        maFilter = new ColumnMajorFileFilter();
        Arrays.sort(maExtensions);
    }
    
    String[] maExtensions = {"txt", "xls"};
    org.geworkbench.engine.parsers.ExpressionResource resource = new ExpressionResource();
    ColumnMajorFileFilter maFilter = null;
    int microarrayNo = 0;
    int phenotypeNo = 0;
    int markerNo = 0;
    
    public Resource getResource(File file) {
        try {
            resource.setReader(new BufferedReader(new FileReader(file)));
            resource.setInputFileName(file.getName());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        return resource;
    }
    
    public String[] getFileExtensions() {
        return maExtensions;
    }
    
    public boolean checkFormat(File file) {
        return true;
    }
    
    public DSDataSet getDataFile(File file) {
        return (DSDataSet) getMArraySet(file);
    }
    
    public DSMicroarraySet getMArraySet(File file) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        try {
            read(maSet, file);
        } catch (Exception e) {
        }
        if (maSet.loadingCancelled)
            return null;
        return maSet;
    }
    
    private void read(DSMicroarraySet<DSMicroarray> maSet, File file) {
        maSet.setLabel(file.getName());
        maSet.setAbsPath(file.getAbsolutePath());
        try{
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            parseSheet(hssfSheet, maSet);
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
    }
    
    public FileFilter getFileFilter() {
        return maFilter;
    }
    
    /**
     * getDataFile
     *
     * @param files File[]
     * @return DataSet
     */
    public DSDataSet getDataFile(File[] files) {
        return null;
    }
    
    class ColumnMajorFileFilter extends FileFilter {
        
        public String getDescription() {
            return "Column Major Format";
        }
        
        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < maExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(maExtensions[i])) {
                return true;
                }
            return returnVal;
        }
    }
    
    void parseSheet(HSSFSheet sheet, DSMicroarraySet<DSMicroarray> maSet) {
        phenotypeNo = 0;
        microarrayNo = sheet.getLastRowNum() - sheet.getFirstRowNum() - 1;
        markerNo = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
        maSet.initialize(microarrayNo, markerNo);
        HSSFRow row = sheet.getRow(0);
        int cellCount = 0;
        for (int i = 0; i < markerNo; i++){
            String marker = row.getCell((short)i).getStringCellValue();
//            String marker = ((HSSFCell)iter.next()).getStringCellValue();
            DSGeneMarker m = maSet.getMarkers().get(cellCount);
            m.setLabel(marker);
            m.setGeneName(marker);
            m.setSerial(cellCount++);
        }
        for (int i = 0; i < microarrayNo; i++){
            row = sheet.getRow(i + 1);
            CSMicroarray array = (CSMicroarray)maSet.get(i);
            array.setLabel("Array: " + i);
            array.setName("Array: " + i);
            array.setSerial(i);
            //maSet.set(i, array);
            cellCount = 0;
            for (Iterator iter = row.cellIterator(); iter.hasNext();){
                double value = ((HSSFCell)iter.next()).getNumericCellValue();
                DSMutableMarkerValue marker = (DSMutableMarkerValue) array.getMarkerValue(cellCount);
                Range range = ((DSRangeMarker) maSet.getMarkers().get(cellCount)).getRange();
                marker.setValue(value);
                marker.setConfidence(1);
                range.max = Math.max(range.max, value);
                range.min = Math.min(range.min, value);
                range.norm.add(value);                
                cellCount++;
            }
        }
    }
}