package edu.ksu.cis.bnj.test;

/*
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.converter.ConverterFactory;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Loader Testing
 *
 * @author Roby Joehanes
 */
public class TestLoader {

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i"); // $NON-NLS-1$
        String outputFormat = params.getString("-f"); // $NON-NLS-1$
        String outputFile = params.getString("-o"); // $NON-NLS-1$
        boolean quiet = params.getBool("-q"); // $NON-NLS-1$

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.test.TestLoader -i:inputfile [-o:outputfile] [-f:saveformat] [-q]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn, libb, dsl, ent}");
            System.out.println("-q: quiet mode");
            System.out.println("If outputfile is not specified, it will default to the standard output.");
            return;
        }


        OutputStream out = System.out;

        if (outputFile != null) {
            try {
                out = new FileOutputStream(outputFile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        Runtime r = Runtime.getRuntime();
        long freemem = r.freeMemory();
        BBNGraph g = null;
        try {
            g = BBNGraph.load(inputFile);
            freemem = freemem - r.freeMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!quiet) {
            System.out.println("Memory needed = " + freemem);
        }

        if (g != null) {
            if (outputFormat == null) outputFormat = "xml"; // $NON-NLS-1$
            try {
                ConverterFactory.save(g, out, outputFormat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
