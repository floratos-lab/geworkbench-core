package org.geworkbench.bison.datastructure.biocollections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException; 
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrix.NodeType;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker; 
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.parsers.InputFileFormatException;

/**
 * @author John Watkinson
 * @version $Id$
 */
public class AdjacencyMatrixDataSet extends CSAncillaryDataSet<DSMicroarray> {

	private static final long serialVersionUID = 2222442531807486171L;

	public static final String SIF_FORMART = "sif format";
	public static final String ADJ_FORMART = "adj format";
	public static final String GENE_NAME = "gene name";
	public static final String ENTREZ_ID = "entrez id";
	public static final String OTHER = "other";
	public static final String PROBESET_ID = "probeset id";

	static Log log = LogFactory.getLog(AdjacencyMatrixDataSet.class);

	private AdjacencyMatrix matrix;

	private final double threshold;
	private String networkName;

	@SuppressWarnings("unchecked")
	public AdjacencyMatrixDataSet(final AdjacencyMatrix matrix,
			final double threshold, final String name,
			final String networkName,
			final DSMicroarraySet<? extends DSMicroarray> parent) {
		super((DSDataSet<DSMicroarray>) parent, name);
		setID(RandomNumberGenerator.getID());
		this.matrix = matrix;
		this.threshold = threshold;
		this.networkName = networkName;
	}

	public String getExportName(AdjacencyMatrix.Node node) {
		if (node.type == NodeType.MARKER) {
			return node.marker.getLabel();
		}
		else if (node.type == NodeType.GENE_SYMBOL) {
			return node.stringId;
		}
		else if (node.type == NodeType.STRING) {
			return node.stringId;
		} else {
			return "unknown";
		}
	}

	public void writeToFile(String fileName) {
		File file = new File(fileName);

		try {
			file.createNewFile();
			if (!file.canWrite()) {
				JOptionPane.showMessageDialog(null,
						"Cannot write to specified file.");
				return;
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			// if entry key is less than 0, for CNKB component, it means the
			// gene is in currently selected microarray.
			for (AdjacencyMatrix.Node node1 : matrix.getNodes()) {
				writer.write(getExportName(node1) + "\t");

				for (AdjacencyMatrix.Edge edge : matrix.getEdges(node1)) {
					writer.write(getExportName(edge.node2) + "\t"
							+ edge.info.value + "\t");
				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			log.error(e);
		}
	}

	public void readFromFile(String fileName,
			DSMicroarraySet<DSMicroarray> maSet) {
		try {
			matrix = parseAdjacencyMatrix(fileName, maSet);
		} catch (InputFileFormatException e) {
			log.error(e);
		}
	}

	public static AdjacencyMatrix parseAdjacencyMatrix(String fileName,
			DSMicroarraySet<DSMicroarray> maSet)
			throws InputFileFormatException {

		return parseAdjacencyMatrix(fileName, maSet, null, ADJ_FORMART,
				PROBESET_ID, true);

	}

	public static AdjacencyMatrix parseAdjacencyMatrix(String fileName,
			DSMicroarraySet<DSMicroarray> maSet,
			Map<String, String> interactionTypeSifMap, String format,
			String selectedRepresentedBy, boolean isRestrict)
			throws InputFileFormatException {
		int connectionsInstantiated = 0;
		int connectionsIgnored = 0;

		BufferedReader br = null;
		AdjacencyMatrix matrix = new AdjacencyMatrix(fileName, maSet,
				interactionTypeSifMap);
		 
		try {

			// readMappings(new File(name));
			br = new BufferedReader(new FileReader(fileName));
			try {
				// String line = br.readLine();
				String line;
				int ctr = 0;
				String interactionType = null;
				float mi = 0.8f;

				// while (br.ready()) {
				while ((line = br.readLine()) != null) {
					if (line.trim().equals(""))
						continue;
					if (ctr++ % 100 == 0) {
						log.debug("Reading line " + ctr);
					}
					// skip comments
					if (line.startsWith(">"))
						continue;
					if (line.length() > 0 && line.charAt(0) != '-') {
						StringTokenizer tr = new StringTokenizer(line, "\t: :");

						// String geneAccess = new String(tr.nextToken());
						String strGeneId1 = new String(tr.nextToken());
						DSGeneMarker m = null;
						if (selectedRepresentedBy.equals(PROBESET_ID)
								|| selectedRepresentedBy.equals(GENE_NAME) || selectedRepresentedBy.equals(ENTREZ_ID))
							m = maSet.getMarkers().get(strGeneId1);
						 
						AdjacencyMatrix.Node node = null;
						
						if (m == null && isRestrict) { // we don't have this gene in our
							// MicroarraySet
                             
							// we skip it
							continue;
						}else if (m == null && !isRestrict)
						{
							if (selectedRepresentedBy.equals(GENE_NAME))
							   node = new AdjacencyMatrix.Node(NodeType.GENE_SYMBOL, strGeneId1);
							else 
							   node = new AdjacencyMatrix.Node(NodeType.STRING, strGeneId1);
						}
						else 
						{
						    if (selectedRepresentedBy.equals(PROBESET_ID))							
							   node = new AdjacencyMatrix.Node(m);
						    else 
						       node = new AdjacencyMatrix.Node(NodeType.GENE_SYMBOL, m.getGeneName());
						}

						if (format.equals(SIF_FORMART) && tr.hasMoreTokens())
							interactionType = tr.nextToken().toLowerCase();
						while (tr.hasMoreTokens()) {
							String strGeneId2 = new String(tr.nextToken());
							
							DSGeneMarker m2 = null;
							if (selectedRepresentedBy.equals(PROBESET_ID)
									|| selectedRepresentedBy.equals(GENE_NAME) || selectedRepresentedBy.equals(ENTREZ_ID))
								m2 = maSet.getMarkers().get(strGeneId2);							 

							AdjacencyMatrix.Node node2 = null;
							
							if (m2 == null && isRestrict) { // we don't have this gene in our
								// MicroarraySet
	                             
								// we skip it
								continue;
							}else if (m2 == null && !isRestrict)
							{
								
								if (selectedRepresentedBy.equals(GENE_NAME))
									   node2 = new AdjacencyMatrix.Node(NodeType.GENE_SYMBOL, strGeneId2);
									else 
										node2 = new AdjacencyMatrix.Node(NodeType.STRING, strGeneId2);
								
							}
							else
							{ 
								 if (selectedRepresentedBy.equals(PROBESET_ID))	
								      node2 = new AdjacencyMatrix.Node(m2);
								 else 
								      node2 = new AdjacencyMatrix.Node(NodeType.GENE_SYMBOL, m2.getGeneName());
							}
							
							if (format.equals(ADJ_FORMART) ) {	
								if (!tr.hasMoreTokens())
								    throw new InputFileFormatException("invalid format around " + strGeneId2);
								String strMi = new String(tr.nextToken());							 
								mi = Float.parseFloat(strMi);
							}
							
							//if (m != m2) {
								connectionsInstantiated++;
								matrix.add(node,
										node2, mi, interactionType);
								// this.addInteractionType2(geneId1, geneId2,
								// mi);
							//} else {
							//	connectionsIgnored++;
							//}
						}

					}
					// line = br.readLine();
				}
				log
						.debug("Connections instantiated "
								+ connectionsInstantiated);
				log.debug("Connections ignored " + connectionsIgnored);
				log.debug("Total processed "
						+ (connectionsInstantiated + connectionsIgnored));
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				throw new InputFileFormatException(ex.getMessage());
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new InputFileFormatException(ex.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new InputFileFormatException(e.getMessage());
			}
		} catch (FileNotFoundException ex3) {
			ex3.printStackTrace();
			throw new InputFileFormatException(ex3.getMessage());
		}

		return matrix;
	}

	public AdjacencyMatrix getMatrix() {
		return matrix;
	}

	public double getThreshold() {
		return threshold;
	}

	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
}
