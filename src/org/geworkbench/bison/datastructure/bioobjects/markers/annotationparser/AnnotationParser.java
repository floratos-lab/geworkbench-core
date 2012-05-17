package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;
 
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject; 
import org.geworkbench.util.Util;

/**
 *
 * Description:This Class is for retrieving probe annotation information from
 * default annotation files provided by Affymetrix.
 *
 * @author Xuegong Wang
 * @author manjunath at genomecenter dot columbia dot edu
 * @version $Id$
 */

public class AnnotationParser implements Serializable {
	private static final long serialVersionUID = -117234619759135916L;

	static Log log = LogFactory.getLog(AnnotationParser.class);	
 
	public static final String GENE_ONTOLOGY_BIOLOGICAL_PROCESS = "Gene Ontology Biological Process";

	public static final String GENE_ONTOLOGY_CELLULAR_COMPONENT = "Gene Ontology Cellular Component";

	public static final String GENE_ONTOLOGY_MOLECULAR_FUNCTION = "Gene Ontology Molecular Function";

	public static final String GENE_SYMBOL = "Gene Symbol";

	public static final String MAIN_DELIMITER = "\\s*///\\s*";

	// field names
	public static final String DESCRIPTION = "Gene Title"; // (full name)

	// please stop using this confusing alias, use GENE_SYMBOL instead
	public static final String ABREV = GENE_SYMBOL; // title(short name)

	// FIXME this is misleading name. only used by CNKB
	public static final String GOTERM = GENE_ONTOLOGY_BIOLOGICAL_PROCESS; // Goterms

	public static final String UNIGENE = "UniGene ID"; // Unigene

	public static final String LOCUSLINK = "Entrez Gene"; // LocusLink

	public static final String SWISSPROT = "SwissProt"; // swissprot

	public static final String REFSEQ = "RefSeq Transcript ID"; // RefSeq
	
	public static final String PLEASE_SELECT  = "Please Select";
	
	public static final String AFFYMETRIX_3_EXPRESSION  = "Affymetrix 3' Expression";
	
	public static final String AFFY_GENE_EXON_10_ST = "Affymetrix Gene/Exon 1.0 ST";
	
	
	private static List<AffyAnnotationParser> annotationParsers;
	private static DSMicroarraySet currentDataSet = null;
	private static WeakHashMap<DSMicroarraySet, String> datasetToChipTypes = new WeakHashMap<DSMicroarraySet, String>();
	private static WeakHashMap<DSMicroarraySet, Map<String, AnnotationFields>> datasetToAnnotation = new WeakHashMap<DSMicroarraySet, Map<String, AnnotationFields>>();

	private static boolean isCanceled = false;
	private static String selectedFileType = null;
	
	
	static  
	{		 
			annotationParsers = new ArrayList<AffyAnnotationParser>();
			annotationParsers.add(new Affy3ExpressionAnnotationParser());
			annotationParsers.add(new AffyGeneExonStAnnotationParser());
		 
	}
	
	
	/* The reason that we need APSerializable is that the status fields are designed as static. */
	public static APSerializable getSerializable() {
		return new APSerializable(currentDataSet, datasetToChipTypes,
				datasetToAnnotation);
	}

	public static void setFromSerializable(APSerializable aps) {
		currentDataSet = aps.currentDataSet;
		for(DSMicroarraySet dataset : aps.datasetToChipTypes.keySet()) {
			String s = aps.datasetToChipTypes.get(dataset);
			datasetToChipTypes.put(dataset, s);
		}
		for(DSMicroarraySet dataset : aps.datasetToAnnotation.keySet()) {
			Map<String, AnnotationFields> m = aps.datasetToAnnotation.get(dataset);
			datasetToAnnotation.put(dataset, m);
		}
	}

	public static void setCurrentDataSet(DSDataSet<?> currentDataSet) {
		if(!(currentDataSet instanceof DSMicroarraySet)) {
			AnnotationParser.currentDataSet = null;
		} else {
			AnnotationParser.currentDataSet = (DSMicroarraySet)currentDataSet;
		}
	}

	// this method is only used to get the annotation info to be reused for merged dataset,
	// which may be re-implemented in a better design.
	// so please do not use this method unless you have a very clear reason
	public static String getChipType(DSMicroarraySet dataset) {
		return datasetToChipTypes.get(dataset);
	}

	public static void setChipType(DSDataSet<? extends DSBioObject> dataset, String chiptype) {
		if(!(dataset instanceof DSMicroarraySet)) return;
		
		if(chiptype==null) return;
		
		DSMicroarraySet dset = (DSMicroarraySet)dataset;
		for(DSMicroarraySet d: datasetToChipTypes.keySet()) {
			if(chiptype.equals(datasetToChipTypes.get(d))) { // existing annotation
				datasetToAnnotation.put(dset, datasetToAnnotation.get(d));
				break;
			}
		}
		datasetToChipTypes.put(dset, chiptype);
		currentDataSet = dset;
	}

	/* this is used to handle annotation file when the real dataset is chosen after annotation. */
	private static CSMicroarraySet dummyMicroarraySet = new CSMicroarraySet();
	public static String getLastAnnotationFileName () {
		return dummyMicroarraySet.getAnnotationFileName();
	}

	/* if the annotation file is given, this method is called directly without GUI involved */
	public static void loadAnnotationFile(
			DSMicroarraySet dataset, File annotationData) {
		
		 
		if (!annotationData.exists()) { // data file is found
			log.error("Annotation file " + annotationData + " does not exist.");
			return;
		}

		if (dataset == null) {
			dummyMicroarraySet.setAnnotationFileName(annotationData
					.getAbsolutePath());
		} else {
			dataset.setAnnotationFileName(annotationData.getAbsolutePath());
		}
		
		String chipType = annotationData.getName();
		
		for(DSMicroarraySet d: datasetToChipTypes.keySet()) {
			if(chipType.equals(datasetToChipTypes.get(d))) { // existing annotation
				datasetToAnnotation.put(dataset, datasetToAnnotation.get(d));
				datasetToChipTypes.put(dataset, chipType);
				return;
			}
		}
		
		
		
		JDialog loadDialog = new JDialog();		 
		loadDialog.setTitle("Select Annotation File Type");
		AnnotationFileTypePanel loadPanel = new AnnotationFileTypePanel(loadDialog);

		loadDialog.add(loadPanel);
		loadDialog.setModal(true);
		loadDialog.pack();
		Util.centerWindow(loadDialog);
		loadDialog.setVisible(true);

		if (isCanceled)
			return;	

		AffyAnnotationParser parser = getAnnotationParsers();
		Map<String, AnnotationFields> markerAnnotation  = parser.parse(annotationData, false);
		if(markerAnnotation!=null) {
			datasetToAnnotation.put(dataset, markerAnnotation);
			datasetToChipTypes.put(dataset, chipType);
		
		}
		
		currentDataSet = dataset;
	}

	/**
	 * This method returns required annotation field for a given affymatrix marker ID .
	 *
	 * @param affyid
	 *            affyID as string
	 * @param fieldID
	 *
	 */
	// this method depends on currentDataSet, which is dangerous and causes unnecessary dependency. try to avoid.
	// please use the next version that does not depend on currentDataSet whenever possible
	static public String[] getInfo(String affyID, String fieldID) {
		try {
			String field = "";

			AnnotationFields fields = datasetToAnnotation.get(currentDataSet).get(affyID);
			// individual field to be process separately to eventually get rid of the large map
			if(fieldID.equals(GENE_SYMBOL)) { // same as ABREV
				field = fields.getGeneSymbol();
			} else if(fieldID.equals(LOCUSLINK)) {
				field = fields.getLocusLink();
			} else if(fieldID.equals(DESCRIPTION)) {
				field = fields.getDescription();
			} else if(fieldID.equals(GENE_ONTOLOGY_MOLECULAR_FUNCTION)) {
				field = fields.getMolecularFunction();
			} else if(fieldID.equals(GENE_ONTOLOGY_CELLULAR_COMPONENT)) {
				field = fields.getCellularComponent();
			} else if(fieldID.equals(GENE_ONTOLOGY_BIOLOGICAL_PROCESS)) {
				field = fields.getBiologicalProcess();
			} else if(fieldID.equals(UNIGENE)) {
				field = fields.getUniGene();
			} else if(fieldID.equals(REFSEQ)) {
				field = fields.getRefSeq();
			} else if(fieldID.equals(SWISSPROT)) {
				field = fields.getSwissProt();
			} else {
				log.error("trying to retreive unsupported field "+fieldID+" from marker annotation. null is returned.");
				return null;
			}
			return field.split(MAIN_DELIMITER);
		} catch (Exception e) {
			if (affyID != null) {
				log
						.debug("Error getting info for affyId (" + affyID
								+ "):" + e);
			}
			return null;
		}
	}

	// this method is similar to the previous one except that it takes dataset instead
	// of using currentDataSet
	static public String[] getInfo(DSMicroarraySet dataset,
			String affyID, String fieldID) {
		String field = null;

		AnnotationFields fields = datasetToAnnotation.get(dataset).get(
				affyID);
		// individual field to be process separately to eventually get rid of
		// the large map
		if (fieldID.equals(GENE_SYMBOL)) { // same as ABREV
			field = fields.getGeneSymbol();
		} else if (fieldID.equals(LOCUSLINK)) {
			field = fields.getLocusLink();
		} else if (fieldID.equals(DESCRIPTION)) {
			field = fields.getDescription();
		} else if (fieldID.equals(GENE_ONTOLOGY_MOLECULAR_FUNCTION)) {
			field = fields.getMolecularFunction();
		} else if (fieldID.equals(GENE_ONTOLOGY_CELLULAR_COMPONENT)) {
			field = fields.getCellularComponent();
		} else if (fieldID.equals(GENE_ONTOLOGY_BIOLOGICAL_PROCESS)) {
			field = fields.getBiologicalProcess();
		} else if (fieldID.equals(UNIGENE)) {
			field = fields.getUniGene();
		} else if (fieldID.equals(REFSEQ)) {
			field = fields.getRefSeq();
		} else if (fieldID.equals(SWISSPROT)) {
			field = fields.getSwissProt();
		} else {
			log.error("trying to retreive unsupported field " + fieldID
					+ " from marker annotation. null is returned.");
			return null;
		}
		return field.split(MAIN_DELIMITER);
	}

	public static void cleanUpAnnotatioAfterUnload(DSDataSet<? extends DSBioObject> dataset) {
		// using weak reference making this manual management unnecessary
	}
	
	

	
	private static AffyAnnotationParser getAnnotationParsers()
	{
		AffyAnnotationParser affyAnnotationParser = null;
		for(AffyAnnotationParser parser: annotationParsers)
		{
			if (parser.getAnnotationFileType().equalsIgnoreCase(selectedFileType))
			{
				affyAnnotationParser = parser;
				break;
			}
		}
		return affyAnnotationParser;
		
	}
	
	private static class AnnotationFileTypePanel extends JPanel {
		 
		/**
		 * 
		 */
		private static final long serialVersionUID = -6651285046254401480L;
		
		final JDialog parent;
	 
		private JComboBox annotationFileTypeJcb;
	 
		 
		public AnnotationFileTypePanel(JDialog parent) {

			setLayout(new BorderLayout());
			this.parent = parent;
		 
			init();

		}

		private void init() {

			JPanel panel1 = new JPanel();
			 
			JPanel panel3 = new JPanel(new GridLayout(0, 3));
			JLabel label1 = new JLabel("Annotation file type:   ");

			annotationFileTypeJcb = new JComboBox();
			annotationFileTypeJcb.addItem(PLEASE_SELECT);
			annotationFileTypeJcb.addItem(AnnotationParser.AFFYMETRIX_3_EXPRESSION);
			annotationFileTypeJcb.addItem(AnnotationParser.AFFY_GENE_EXON_10_ST);
		 
			annotationFileTypeJcb.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					 
				}
			});		
			

			JButton continueButton = new JButton("Continue");
			JButton cancelButton = new JButton("Cancel");

			parent.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent e) {
					isCanceled = true;
					selectedFileType = null; 
				}
			});
			
			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {				 
					selectedFileType = annotationFileTypeJcb.getSelectedItem().toString();
					if (selectedFileType.trim().equals(PLEASE_SELECT))
						return;
					
					parent.dispose();					
					isCanceled = false;
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.dispose();
					selectedFileType = null;
					isCanceled = true;
				}
			}); 

			panel1.add(label1);
			panel1.add(annotationFileTypeJcb);
			 

			panel3.add(continueButton);
			panel3.add(new JLabel("  "));
			panel3.add(cancelButton);

			this.add(panel1, BorderLayout.NORTH);
			 
			this.add(panel3, BorderLayout.SOUTH);

		}
 
	 
	}

	
	

}
