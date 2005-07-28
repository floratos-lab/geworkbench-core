package org.geworkbench.util;

import org.ensembl.datamodel.Gene;
import org.ensembl.datamodel.Location;
import org.ensembl.datamodel.Sequence;
import org.ensembl.driver.CoreDriver;
import org.ensembl.driver.CoreDriverFactory;
import org.ensembl.driver.GeneAdaptor;
import org.ensembl.driver.SequenceAdaptor;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;

import java.util.List;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class PromoterSequenceFetcher {
    public PromoterSequenceFetcher() {
    }

    public static CSSequence[] getPromoterSequence(DSGeneMarker marker, int upstream, int fromStart) {
        try {
            CoreDriver coreDriver = CoreDriverFactory.createCoreDriver("ensembldb.ensembl.org", 3306, "homo_sapiens_core_30_35c", "anonymous", null);

            //            CoreDriver coreDriver =
            //                    CoreDriverFactory.createCoreDriverUsingDatabasePrefix(
            //                            "ensembldb.ensembl.org",
            //                            3306, "homo_sapiens_core", "anonymous", null);

            GeneAdaptor geneAdaptor = coreDriver.getGeneAdaptor();

            List genes = geneAdaptor.fetchBySynonym(marker.getGeneName());

            CSSequence[] sequences = new CSSequence[genes.size()];
            System.out.println("Query gene name " + marker.getGeneName());

            for (int geneCtr = 0; geneCtr < genes.size(); geneCtr++) {
                Gene gene = (Gene) genes.get(geneCtr);

                Location location = gene.getLocation();
                System.out.println(location.getStrand() + "\t" + location.getStartAsFormattedString() + "\t" + location.getEndAsFormattedString() + "\t" + (location.getEnd() - location.getStart()));
                Location newLocation = null;
                if (location.getStrand() == 1) {
                    newLocation = location.transform(-upstream, -location.getNodeLength() + fromStart);
                } else {
                    newLocation = location.transform(location.getNodeLength() - fromStart, upstream);
                }


                //               if(location.getStrand() == 1){
                //                   newLocation = location.transform(-upstream, -location.getNodeLength() + fromStart);
                //               }else if(location.getStrand() == -1){
                //                   newLocation = location.transform()
                //               }

                //               System.out.println(newLocation.getStart() + "\t" + newLocation.getEnd());

                SequenceAdaptor sequenceAdaptor = coreDriver.getSequenceAdaptor();
                Sequence sequence = sequenceAdaptor.fetch(newLocation);
                String strSequence = sequence.getSequence().getString();

                //               System.out.println(strSequence);

                sequences[geneCtr] = new CSSequence(marker.getGeneName(), strSequence);

            }
            coreDriver.closeAllConnections();
            return sequences;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
