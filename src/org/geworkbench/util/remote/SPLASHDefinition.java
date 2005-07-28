package org.geworkbench.util.remote;

import java.io.File;

/**
 * Defines constants that are used be SPLASH.
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $Author: watkin $
 * @version 1.0
 */

public final class SPLASHDefinition {
    public static class SortMode {
        public final static int NONE = 0;
        public final static int IDNO = 1;
        public final static int SEQNO = 2;
        public final static int TOKNO = 3;
        public final static int PVALUE = 4;
    }

    public static class Algorithm {
        public final static String REGULAR = "regular";
        public final static String EXHAUSTIVE = "exhaustive";
        public final static String HIERARCHICAL = "hierarchical";
    }

    /**
     * The method constructs a string from a file and userName.
     */
    public static String encodeFile(File toEncode, String userName) {
        String databaseName = toEncode.getPath();
        databaseName = databaseName.replace(File.separatorChar, '_').replace(':', '[');

        //append user name to get uniqueness
        databaseName = userName + '_' + databaseName;
        return databaseName;
    }

    public static File decode(String encodedFileName, String userName) {
        String decoded = encodedFileName.replaceAll(userName + "_", "");
        decoded = decoded.replace('_', File.separatorChar).replace('[', ':');
        File f = new File(decoded);
        return f.getAbsoluteFile();
    }
}
