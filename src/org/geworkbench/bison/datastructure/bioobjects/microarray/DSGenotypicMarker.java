package org.geworkbench.bison.datastructure.bioobjects.microarray;

public interface DSGenotypicMarker extends DSMutableMarkerValue {
    /**
     * Gets either of the two alleles
     *
     * @param id int either of the two dimensions
     * @return int allele as int
     */
    public int getAllele(int id);

    /**
     * Sets the primary allele
     *
     * @param allele int
     */
    public void setAllele(int allele);

    /**
     * Sets genotype
     *
     * @param allele_1 int
     * @param allele_2 int
     */
    public void setGenotype(int allele_1, int allele_2);
}
