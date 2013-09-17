package org.geworkbench.util.pathwaydecoder.mutualinformation;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

public class NetBoostDataSet extends CSAncillaryDataSet<DSBioObject> {
	
	private static final long serialVersionUID = -6835973287728524201L;
    private NetBoostData data;
    private String filename;

    public NetBoostDataSet(DSDataSet<DSBioObject> parent, String label, NetBoostData data, String filename) {
        super(parent, label);
        this.data = data;
        this.filename = filename;
    }

	public NetBoostData getData() {
        return data;
    }

    public void setData(NetBoostData data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
