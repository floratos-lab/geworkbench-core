package org.geworkbench.util.sequences;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.geworkbench.bison.datastructure.complex.pattern.PatternResult;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.util.patterns.PatternSorter;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: The model holds the current displayed patterns in the table.
 * The model gets its pattern from a PatternSource class.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id$
 */

public class PatternTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 9144326847214513997L;
	/**
     * Column definition
     */
    public static final int PTMSupport = 0;
    public static final int PTMSeqNo = 1;
    public static final int PTMTokNo = 2;
    public static final int PTMZScore = 3;
    public static final int PTMPattern = 4;
    private static final String[] headerName = {"Hits", "Sequences Hit", "# of Tokens", "ZScore", "Motif"};

    // TODO this is only an artifact to implement sorting. It may not be necessary.
    private List<DSMatchedSeqPattern> pattern = new ArrayList<DSMatchedSeqPattern>();

    /**
     * Used to format the pValue/zScore field.
     */
    static final private DecimalFormat fmt = new DecimalFormat("0.#E0##");

    private final int rowCount;

    public PatternTableModel(PatternResult patternResult) {
    	// convert to a List. this may not be necessary except to implement sorting
        rowCount = patternResult.getPatternNo();

        for (int i = 0; i < rowCount; ++i) {
            pattern.add( patternResult.getPattern(i) );
        }
	}

    /**
     * Sort the patterns in the model on field
     */
	public void sort(int field) {
		PatternSorter sorter = new PatternSorter();
		sorter.setMode(field);
		Collections.sort(pattern, sorter);
	}

    /**
     * Mask the patterns of this model
     *
     * @param indeces to mask.
     * @param mask    operation
     */
    public void mask(int[] index, boolean maskOperation) {
        // TODO nothing implemented yet
    }

    /**
     * See  javax.swing.table.TableModel
     *
     * @return number of columns in the model
     */
    public int getColumnCount() {
        return headerName.length;
    }

    /**
     * See  javax.swing.table.TableModel
     *
     * @return number of columns in the model
     */
    public synchronized int getRowCount() {
        return rowCount;
    }

    /**
     * See  javax.swing.table.TableModel
     *
     * @param columnIndex the index of the column
     * @return the name of the column
     */
    public String getColumnName(int columnIndex) {
        return headerName[columnIndex];
    }

    public synchronized Object getValueAt(int row, int col) {
        DSMatchedSeqPattern pattern = null;
        Object cell = null;
        pattern = getPattern(row);
        if (pattern != null) {
            switch (col) {
                case PTMSupport:
                    cell = new Integer(pattern.getSupport());
                    break;
                case PTMSeqNo:
                    cell = new Integer(pattern.getUniqueSupport());
                    break;
                case PTMTokNo:
                    cell = new Integer(pattern.getLength());
                    break;
                case PTMZScore:
                    cell = fmt.format(pattern.getPValue());
                    break;
                case PTMPattern:
                    cell = pattern.getASCII();
                    break;
            }
        } else {
            //the pattern is not in the model, yet.
            cell = (col == PTMSupport) ? "loading" : "...";
        }
        return cell;
    }

    /**
     * Get the pattern at the index row. This method will block until
     * the pattern is retrieved from the underline source. See setPatternSource.
     *
     * @param row
     * @return
     */
    public synchronized DSMatchedSeqPattern getPattern(int row) {
        //get the pattern from a Source if needed.
        if ((row < 0) || (row > rowCount - 1)) {
            throw new IndexOutOfBoundsException("[row=" + row + ", rowCount=" + rowCount + "]");
        }

        return pattern.get(row);
    }

}

