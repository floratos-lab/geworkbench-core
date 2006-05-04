package org.geworkbench.util.sequences;

/**
 * <p>Title: Bioworks</p>
 * <p>Class: Genome</p>
 * <p>Description: Genome data for genome assembly. </p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Genome {

    public static int num = 4;
    public static String[] fullName = {"human (GoldenPath)", "mouse (GoldenPath)", "rat (GoldenPath)", "chimpanzee (GoldenPath)"};

    public static String[] shortName = {"hg18", "mm7", "rn3", "panTro1"};

    public static int[] numConventionalChromosomes = {22, // hg18
                                                      19, // mm7
                                                      20, // rn3
                                                      23 // panTro1
    };

    public static int[] numNonConventionalChromosomes = {2, // hg16
                                                         2, // mm7
                                                         2, // rn3
                                                         2 // panTro1
    };

    public static String[][] nonConventionalChromNames = {{"chrX", "chrY"}, {// hg18
        "chrX", "chrY"}, {// mm7
            "chrX", "chrY"}, {// rn3
                "chrX", "chrY"} // panTro1
    };

    public static int[][] chromSizes = {  {247249719, 242951149, 199501827, 191273063, 180857866, 170899992, 158821424, 146274826, 140273252, 135374737, 134452384, 132349534, 114142980, 106368585, 100338915, 88827254, 78774742, 76117153, 63811651, 62435964, 46944323, 49691432, 154913754, 57772954} // hg18
                                        , {194923535, 182548267, 159849039, 155175443, 153054177, 149646834, 141766352, 127874053, 123828236, 130066766, 122091587, 117814103, 116696528, 119226840, 103647385, 98481019, 93276925, 90918714, 61223509, 164906252, 15523453} // mm7
                                        , {268121971, 258222147, 170969371, 187371129, 173106704, 147642806, 143082968, 129061546, 113649943, 110733352, 87800381, 111348958, 46649226, 109774626, 90224819, 97307196, 87338544, 59223525, 55296979, 160775580, 1} // rn34
                                        , {229575298, 203813066, 209662276, 188378868, 175429504, 161576975, 149542033, 138322177, 136640551, 135301796, 123086034, 117159028, 134309081, 97804244, 106954593, 101535987, 73346066, 83875239, 82489036, 61571712, 65473740, 47338174, 50034486, 160174553, 50597644} // panTro1
    };

    public static int getGenomeNum(String nm) {
        int i;

        for (i = 0; i < num; i++) {
            if (nm.equalsIgnoreCase(shortName[i])) {
                return i;
            }
        }
        return -1;
    }

    public static int getChrNum(String genome_name) {
        int n = 0, i = 0;

        i = getGenomeNum(genome_name);
        if (i == -1)
            return 0;
        n = numConventionalChromosomes[i] + numNonConventionalChromosomes[i];
        return n;
    }

    public static String getChrName(String genome_name, int n) {
        int m, g;
        String ret = null;

        m = getChrNum(genome_name);
        g = getGenomeNum(genome_name);
        if (n < numConventionalChromosomes[g]) {
            // It's a conventional chromosome
            ret = new String("chr" + (n + 1));
        } else {
            if (n >= numConventionalChromosomes[g] && n <= numConventionalChromosomes[g] + numNonConventionalChromosomes[g]) {
                ret = new String(nonConventionalChromNames[g][n - numConventionalChromosomes[g]]);
            }
        }
        return ret;
    }
}
