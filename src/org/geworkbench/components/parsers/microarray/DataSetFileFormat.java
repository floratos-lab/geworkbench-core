package org.geworkbench.components.parsers.microarray;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.components.parsers.FileFormat;
import org.geworkbench.components.parsers.InputFileFormatException;
import java.io.InterruptedIOException;

import java.io.File;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id: DataSetFileFormat.java,v 1.5 2009-10-07 15:38:48 my2248 Exp $
 */

public abstract class DataSetFileFormat extends FileFormat {
  public DataSetFileFormat() {
  }
  abstract public DSDataSet getDataFile(File file) throws InputFileFormatException, InterruptedIOException;
  public DSDataSet getDataFile(File file, String compatibilityLabel) throws InputFileFormatException, InterruptedIOException, UnsupportedOperationException {
      throw new UnsupportedOperationException();
  }
     
  abstract public DSDataSet getDataFile(File[] files) throws InputFileFormatException;
}
