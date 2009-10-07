package org.geworkbench.components.parsers;

import java.io.File;
import java.io.InterruptedIOException;

import junit.framework.TestCase;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;

/**
 * @author yc2480
 * @version $Id: AffyFileFormatGuiTest.java,v 1.3 2009-10-07 15:38:48 my2248 Exp $
 */
public class AffyFileFormatGuiTest extends TestCase {

	AffyFileFormat affyFileFormat; 
	
	public AffyFileFormatGuiTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		affyFileFormat = new AffyFileFormat();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testGetMArraySetFileCSExprMicroarraySet() throws InterruptedIOException{
		boolean invalidFormat = false;
		try {
			CSExprMicroarraySet result = new CSExprMicroarraySet();
			affyFileFormat.getMArraySet(new File("test/org/geworkbench/components/parsers/testFile8.txt"),result);
			assertEquals(CSExprMicroarraySet.class, result.getClass());
			CSExprMicroarraySet emaSet = (CSExprMicroarraySet)result;
			assertEquals("testFile8.txt should have 4 markers",4,emaSet.getMarkers().size());
			assertEquals("AFFX-MurIL2_at",emaSet.getMarkers().get(0).getLabel());
			assertEquals("AFFX-MurIL2_at",emaSet.getMarkers().get(0).getShortName());
			assertEquals(0,emaSet.getMarkers().get(0).getSerial());
			assertEquals("AFFX-MurFAS_at",emaSet.getMarkers().get(3).getLabel());
			assertEquals("AFFX-MurFAS_at",emaSet.getMarkers().get(3).getShortName());
			assertEquals(3,emaSet.getMarkers().get(3).getSerial());
		} catch (InputFileFormatException e) {
			invalidFormat = true;
		}
		assertEquals("correct file should return false", false, invalidFormat);
	}

	public final void testGetDataFileFile()throws InterruptedIOException {
		boolean invalidFormat = false;
		try {
			DSDataSet result = affyFileFormat.getDataFile(new File("test/org/geworkbench/components/parsers/testFile8.txt"));
			assertEquals(CSExprMicroarraySet.class, result.getClass());
			CSExprMicroarraySet emaSet = (CSExprMicroarraySet)result;
			assertEquals("testFile8.txt should have 4 markers",4,emaSet.getMarkers().size());
			assertEquals("AFFX-MurIL2_at",emaSet.getMarkers().get(0).getLabel());
			assertEquals("AFFX-MurIL2_at",emaSet.getMarkers().get(0).getShortName());
			assertEquals(0,emaSet.getMarkers().get(0).getSerial());
			assertEquals("AFFX-MurFAS_at",emaSet.getMarkers().get(3).getLabel());
			assertEquals("AFFX-MurFAS_at",emaSet.getMarkers().get(3).getShortName());
			assertEquals(3,emaSet.getMarkers().get(3).getSerial());
		} catch (InputFileFormatException e) {
			invalidFormat = true;
		}
		assertEquals("correct file should return false", false, invalidFormat);
	}

	public final void testGetMArraySetFile()throws InterruptedIOException {
		boolean invalidFormat = false;
		try {
			DSDataSet result = affyFileFormat.getMArraySet(new File("test/org/geworkbench/components/parsers/testFile8.txt"));
			assertEquals(CSExprMicroarraySet.class, result.getClass());
			CSExprMicroarraySet emaSet = (CSExprMicroarraySet)result;
			assertEquals("testFile8.txt should have 4 markers",4,emaSet.getMarkers().size());
			assertEquals("AFFX-MurIL2_at",emaSet.getMarkers().get(0).getLabel());
			assertEquals("AFFX-MurIL2_at",emaSet.getMarkers().get(0).getShortName());
			assertEquals(0,emaSet.getMarkers().get(0).getSerial());
			assertEquals("AFFX-MurFAS_at",emaSet.getMarkers().get(3).getLabel());
			assertEquals("AFFX-MurFAS_at",emaSet.getMarkers().get(3).getShortName());
			assertEquals(3,emaSet.getMarkers().get(3).getSerial());
		} catch (InputFileFormatException e) {
			invalidFormat = true;
		}
		assertEquals("correct file should return false", false, invalidFormat);
	}
}
