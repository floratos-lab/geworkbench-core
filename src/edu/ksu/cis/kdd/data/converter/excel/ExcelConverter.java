/*
 * Created on 23 Jun 2003
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
package edu.ksu.cis.kdd.data.converter.excel;

import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.data.converter.Converter;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <P>Microsoft<sup>(R)</sup> Excel<sup>(TM)</sup> format.
 * <p/>
 * <P>User has the option of including the range of cells that contains
 * the actual data. If the ranges are not specified, then the data is
 * assumed to begin at cell A1 and ends at the row/column size returned
 * by the API.
 * <p/>
 * <P>The actual data must include the title row as the first row AND
 * assuming that between the title row and the actual data there are no
 * blank rows. Also, it is not allowed to have a merged columns at any
 * time. Otherwise the program will throw errors silently.
 * <p/>
 * <P>If we have PRM (or multiple tables), each table must be written in
 * separate sheet. Also, please delete any superfluous / empty sheet so
 * that the "autodetection" can work.
 * <p/>
 * <P>Currently, no charts or pictures are allowed. And there is no way
 * to recover primary / reference key in here (except if we somehow "cheat"
 * later, but definitely not now).
 * <p/>
 * <P>NOTE: Everything is imported as string for now!!!
 *
 * @author Roby Joehanes
 */
public class ExcelConverter implements Converter {

    /**
     * Range of cells (in each sheet) that contains the actual data.
     * See the assumption above!
     */
    protected ExcelSheetRange[] ranges = null;

    /**
     * @see edu.ksu.cis.kdd.data.converter.Converter#initialize()
     */
    public void initialize() {
    }

    public void setRanges(ExcelSheetRange[] ranges) {
        this.ranges = ranges;
    }

    /**
     * <P>Specify the ranges of cells in each sheet, if it doesn't start at A1
     * as requested (see header). E.g.:<BR>
     * B5:G10,A2:H8,B4:J15
     * <p/>
     * <P>That means that the first sheet's data is at B5:G10 cells, second
     * sheet data is at A2:H8, third sheet is at B4:J15 (just like Excel).
     * <p/>
     * <P>Note: If you specify this from the command line, you cannot insert
     * spaces between the commas.
     *
     * @param str
     */
    public void setRanges(String str) {
        StringTokenizer tok = new StringTokenizer(str, ","); // $NON-NLS-1$
        LinkedList ll = new LinkedList();
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            ll.add(new ExcelSheetRange(token));
        }
        ranges = (ExcelSheetRange[]) ll.toArray(new ExcelSheetRange[0]);
    }

    /**
     * @see edu.ksu.cis.kdd.data.converter.Converter#load(java.io.InputStream)
     */
    public Database load(InputStream stream) {
        Database db = new Database();
        try {
            Workbook wb = Workbook.getWorkbook(stream);
            int numSheets = wb.getNumberOfSheets();
            for (int i = 0; i < numSheets; i++) {
                Sheet sheet = wb.getSheet(i);
                Table table = new Table();
                table.setName(sheet.getName());
                db.addTable(table);
                int numCols = sheet.getColumns();
                int numRows = sheet.getRows();
                int startCol = 0;
                int startRow = 0;
                if (ranges != null && ranges.length > i) {
                    startCol = ranges[i].x1;
                    startRow = ranges[i].y1;
                    numCols = ranges[i].x2;
                    numRows = ranges[i].y2;
                }

                // Get the titles
                for (int col = startCol; col < numCols; col++) {
                    Cell cell = sheet.getCell(col, startRow);
                    Attribute attr = new Attribute(cell.getContents());
                    attr.setType(Attribute.STRING);
                    table.addAttribute(attr);
                }

                for (int row = startRow + 1; row < numRows; row++) {
                    Tuple tuple = new Tuple();
                    for (int col = startCol; col < numCols; col++) {
                        Cell cell = sheet.getCell(col, row);
                        tuple.addValue(cell.getContents());
                    }
                    table.addTuple(tuple);
                }
            }
            wb.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return db;
    }

    /**
     * @see edu.ksu.cis.kdd.data.converter.Converter#save(java.io.OutputStream, edu.ksu.cis.kdd.data.datastructure.Table)
     */
    public void save(OutputStream stream, Database db) {
        try {
            WritableWorkbook wb = Workbook.createWorkbook(stream);
            List tables = db.getTables();
            int sheetNo = 0;
            for (Iterator i = tables.iterator(); i.hasNext(); sheetNo++) {
                Table table = (Table) i.next();
                String tableName = table.getName();
                if (tableName == null || tableName.length() == 0) tableName = "Untitled";
                WritableSheet ws = wb.createSheet(tableName, sheetNo);
                int colNo = 0;
                // Column names first
                for (Iterator j = table.getAttributes().iterator(); j.hasNext(); colNo++) {
                    String name = ((Attribute) j.next()).getName();
                    WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                    WritableCellFormat format = new WritableCellFormat(font);
                    format.setBackground(Colour.ICE_BLUE);
                    format.setBorder(Border.ALL, BorderLineStyle.THIN);
                    format.setAlignment(Alignment.CENTRE);
                    Label label = new Label(colNo, 0, name, format);
                    ws.setColumnView(colNo, name.length() + 2);
                    ws.addCell(label);
                }
                // Dump the data
                int rowNo = 1;
                for (Iterator j = table.getTuples().iterator(); j.hasNext(); rowNo++) {
                    Tuple tuple = (Tuple) j.next();
                    colNo = 0;
                    for (Iterator k = tuple.getValues().iterator(); k.hasNext(); colNo++) {
                        String value = k.next().toString();
                        Label label = new Label(colNo, rowNo, value);
                        ws.addCell(label);
                    }
                }
            }
            wb.write();
            wb.close();
            stream.flush();
            stream.close();
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
