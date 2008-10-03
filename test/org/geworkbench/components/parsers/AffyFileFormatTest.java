package org.geworkbench.components.parsers;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import junit.framework.TestCase;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.parsers.resources.AffyResource;
import org.geworkbench.bison.parsers.resources.Resource;

/**
 * 
 * @author yc2480
 * @version $Id: AffyFileFormatTest.java,v 1.1 2008-10-03 16:48:20 chiangy Exp $
 */
public class AffyFileFormatTest extends TestCase {

	AffyFileFormat affyFileFormat;

	/**
	 * 
	 * @param name
	 */
	public AffyFileFormatTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		affyFileFormat = new AffyFileFormat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 
	 */
	public final void testCheckFormat() {
		// test empty file
		assertEquals(
				"empty file should return false",
				false,
				affyFileFormat
						.checkFormat(new File(
								"test/org/geworkbench/components/parsers/testFile1.txt")));
		// test a correct simple file with header and data
		assertEquals(
				"correct file should return true",
				true,
				affyFileFormat
						.checkFormat(new File(
								"test/org/geworkbench/components/parsers/testFile2.txt")));
		// test an unmatched columns
		assertEquals(
				"unmatched columns should return false",
				false,
				affyFileFormat
						.checkFormat(new File(
								"test/org/geworkbench/components/parsers/testFile3.txt")));
		// test NaN
		// FIXME - add this back in after adding check in code
		// assertEquals("If values not numbers, should return false", false,
		// affyFileFormat.checkFormat(new
		// File("test/org/geworkbench/components/parsers/testFile4.txt")));
		// test a correct simple file with real number
		assertEquals(
				"correct file should return true",
				true,
				affyFileFormat
						.checkFormat(new File(
								"test/org/geworkbench/components/parsers/testFile5.txt")));
		// test a correct simple file with scientific numbers
		assertEquals(
				"correct file should return true",
				true,
				affyFileFormat
						.checkFormat(new File(
								"test/org/geworkbench/components/parsers/testFile6.txt")));
		// test a correct simple file with comments
		assertEquals(
				"correct file should return true",
				true,
				affyFileFormat
						.checkFormat(new File(
								"test/org/geworkbench/components/parsers/testFile7.txt")));
		// TODO: should also check if certain columns exist,
		// ex: String[] columnNames = {"Probe Set Name", "Stat Pairs", "Stat
		// Pairs Used", "Signal", "Detection", "Detection p-value", "Stat Common
		// Pairs", "Signal Log Ratio", "Signal Log Ratio Low", "Signal Log Ratio
		// High", "Change", "Change p-value", "Positive", "Negative", "Pairs",
		// "Pairs Used", "Pairs InAvg", "Log Avg", "Pos/Neg", "Avg Diff", "Abs
		// Call", "Inc", "Dec", "Inc Ratio", "Dec Ratio", "Pos Change", "Neg
		// Change", "Inc/Dec", "DPos-DNeg Ratio", "Log Avg Ratio Change", "Diff
		// Call", "Avg Diff Change", "B=A", "Fold Change", "Sort Score"};
		// because if none of them exist, it's probably not a valid format.
	}

	/**
	 * 
	 */
	public final void testGetResource() {
		// testing for error situation, this section will generate error message
		// in console.
		// FIXME: we should change the code generating error message to using
		// log4j
		// otherwise, it dump garbage to console as for now.
		Resource result1 = affyFileFormat.getResource(new File(""));
		assertEquals(AffyResource.class, result1.getClass());
		AffyResource resource1 = (AffyResource) result1;
		assertNull(resource1.getReader());
		assertNull(resource1.getInputFile());
		// testing if it runs fine.
		Resource result2 = affyFileFormat.getResource(new File(
				"test/org/geworkbench/components/parsers/testFile5.txt"));
		assertEquals(AffyResource.class, result2.getClass());
		AffyResource resource2 = (AffyResource) result2;
		assertNotNull(resource2.getReader());
		assertNotNull(resource2.getInputFile());
	}

	/**
	 * 
	 */
	public final void testGetMArraySetFileCSExprMicroarraySet() {
		// moved to GUI Test
	}

	/**
	 * 
	 */
	public final void testGetFileExtensions() {
		String[] affyExtensionsOrg = { "affy", "txt", "TXT" };
		String[] affyExtensionsTest = affyFileFormat.getFileExtensions();
		assertEquals(affyExtensionsOrg.length, affyExtensionsTest.length);
		for (int i = 0; i < affyExtensionsTest.length; i++) {
			assertEquals(affyExtensionsOrg[i], affyExtensionsTest[i]);
		}
	}

	/**
	 * 
	 */
	public final void testGetFileFilter() {
		String[] affyExtensionsOrg = { "affy", "txt", "TXT" };
		FileFilter result = affyFileFormat.getFileFilter();
		for (int i = 0; i < affyExtensionsOrg.length; i++) {
			String ext = affyExtensionsOrg[i];
			assertTrue(result.accept(new File("test." + ext)));
		}
		assertFalse(result.accept(new File("test.asdf")));
	}

	/**
	 * 
	 */
	public final void testGetDataFileFile() {
		// moved to GUI Test
	}

	/**
	 * testing calling getDataFile() with compatibilityLabel
	 */
	public final void testGetDataFileFileString() {
		boolean invalidFormat = false;
		try {
			// please note that calling this way won't have GUI interaction
			DSDataSet result = affyFileFormat.getDataFile(new File(
					"test/org/geworkbench/components/parsers/testFile8.txt"),
					"HG_U95Av2_annot.csv");
			assertEquals("HG_U95Av2_annot.csv", result.getCompatibilityLabel());
			// for now that string doesn't matter
			result = affyFileFormat.getDataFile(new File(
					"test/org/geworkbench/components/parsers/testFile8.txt"),
					"asdf");
			assertEquals("asdf", result.getCompatibilityLabel());
		} catch (InputFileFormatException e) {
			invalidFormat = true;
		}
		assertEquals("correct file should return false", false, invalidFormat);
	}

	/**
	 * 
	 */
	public final void testGetDataFileFileArray() {
		File[] files = new File[3];
		files[0] = new File(
				"test/org/geworkbench/components/parsers/testFile8.txt");
		files[1] = new File(
				"test/org/geworkbench/components/parsers/testFile8.txt");
		files[2] = new File(
				"test/org/geworkbench/components/parsers/testFile8.txt");
		// FIXME - add this back in after implemented the method
		// assertNotNull("getDataFile() should return a DSDataSet instead of
		// Null.",affyFileFormat.getDataFile(files));
	}

	/**
	 * 
	 */
	public final void testAffyFileFormat() {
		FileFilter result = affyFileFormat.getFileFilter();
		assertNotNull(result);
		assertEquals(String.class, result.getDescription().getClass());
		assertEquals("Affymetrix MAS5/GCOS files", result.getDescription());
	}

	/**
	 * 
	 */
	public final void testGetMArraySetFile() {
		// moved to GUI Test
	}

}
