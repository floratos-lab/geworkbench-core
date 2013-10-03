package org.geworkbench.bison.datastructure.biocollections;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrix.Edge;
import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrix.Node;
import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrix.NodeType;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

/* This design is to some extent tentative. See mantis issue #3098, note 13121. */
final public class AracneResult extends AdjacencyMatrixDataSet {

	private static final long serialVersionUID = -1321892354613689799L;

	private final List<String> hubList;

	public AracneResult(AdjacencyMatrix matrix, double threshold, String name,
			String networkName, DSMicroarraySet parent, List<String> hubList) {
		super(matrix, threshold, name, networkName, parent);
		
		/* XXX not really a clean design, but have to do this to fix bug 3569 */
		NodeType nodeType = NodeType.MARKER;
		List<Node> nodes = matrix.getNodes();
		if(nodes!=null && nodes.size()>0) {
			Node node = nodes.get(0);
			nodeType = node.type;
		}
		if(nodeType == NodeType.GENE_SYMBOL) {
			this.hubList = new ArrayList<String>();
			DSItemList<DSGeneMarker> markers = parent.getMarkers();
			for(String probeset : hubList) {
				this.hubList.add( markers.get(probeset).getGeneName() );
			}
		} else {
			this.hubList = hubList;
		}
	}

	@Override
	public void writeToFile(String fileName) {
		File file = new File(fileName);

		try {
			file.createNewFile();
			if (!file.canWrite()) {
				throw new IOException("Cannot write to specified file.");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			List<Edge> allEdges = matrix.getEdges();
			for (String node1 : hubList) {
				List<SecondNode> secondNodeList = getSecondNodes(allEdges,
						node1);
				if (secondNodeList.size() == 0)
					continue;

				writer.write(node1);

				for (SecondNode node : secondNodeList) {
					writer.write("\t" + node.text + "\t" + node.value);
				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			log.error(e);
		}
	}

	static private String node2String(AdjacencyMatrix.Node node)
			throws IOException {
		if (node.type == NodeType.MARKER) {
			return node.marker.getLabel();
		} else if (node.type == NodeType.GENE_SYMBOL) {
			return node.stringId;
		} else {
			throw new IOException("unexpected node type from AracneResult");
		}
	}

	static private class SecondNode {
		String text;
		float value;

		SecondNode(String text, float value) {
			this.text = text;
			this.value = value;
		}
	}

	static private List<SecondNode> getSecondNodes(List<Edge> allEdges,
			String firstNode) throws IOException {
		List<SecondNode> secondNodes = new ArrayList<SecondNode>();
		for (Edge edge : allEdges) {
			String node1 = node2String(edge.node1);
			String node2 = node2String(edge.node2);
			if (firstNode.equals(node1)) {
				secondNodes.add(new SecondNode(node2, edge.info.value));
			} else if (firstNode.equals(node2)) {
				secondNodes.add(new SecondNode(node1, edge.info.value));
			}
		}
		return secondNodes;
	}

}
