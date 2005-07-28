/*
 * Created on Mar 6, 2003
 * 
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
 *
 */
package edu.ksu.cis.bnj;

import edu.ksu.cis.bnj.bbn.converter.ConverterFactory;
import edu.ksu.cis.bnj.bbn.inference.approximate.sampling.*;
import edu.ksu.cis.bnj.bbn.inference.elimbel.ElimBel;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.inference.pearl.Pearl;
import edu.ksu.cis.bnj.bbn.learning.analysis.Robustness;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.GreedySL;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk.GAWK;
import edu.ksu.cis.bnj.gui.DatabaseGUI;
import edu.ksu.cis.bnj.gui.GUI;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.Settings;

/**
 * Command Line Interface. Still very crude. Refine later.
 *
 * @author Roby Joehanes
 */
public final class BNJ {

    private static void help() {
        System.out.println("Usage: edu.ksu.cis.bnj.BNJ [-r:randomseed] [-c:customconfigfile] [modulename] (moduleoptions)*");
        System.out.println("-r = Setting the random seed (only if you want reproducability, otherwise it is set to system clock).");
        System.out.println("-c = Load the specified configuration file name instead of the default.");
        System.out.println("No module names will bring you to the GUI.");
        System.out.println();
        System.out.println("Available modules are:");
        System.out.println();
        System.out.println("Exact Inference:");
        System.out.println("ls = Lauritzen-Spiegelhalter inference algorithm.");
        System.out.println("elimbel = Variable elimination inference algorithm.");
        System.out.println("pearl = Pearl's inference algorithm for singly connected networks");
        System.out.println();
        System.out.println("Approximate Inference:");
        System.out.println("ss = Simple sampling only for inference.");
        System.out.println("logicsampling = Logic sampling for both inference and data generation.");
        System.out.println("lw = Likelihood weighting for both inference and data generation.");
        System.out.println("sis = Self importance sampling for both inference and data generation.");
        System.out.println("ais = Adaptive importance sampling for both inference and data generation.");
        System.out.println("pearlmcmc = Pearl MCMC method for both inference and data generation.");
        System.out.println("chavez = Chavez MCMC for both inference and data generation.");
        System.out.println();
        System.out.println("Structure Learning:");
        System.out.println("k2 = K2 structure learning.");
        System.out.println("gawk = Genetic Algorithm Wrapper for K2.");
        System.out.println("greedysl = Greedy Structure Learning with Perturbation.");
        System.out.println();
        System.out.println("Data Generator:");
        System.out.println("logicsampling = Logic sampling for both inference and data generation.");
        System.out.println("lw = Likelihood weighting for both inference and data generation.");
        System.out.println("sis = Self importance sampling for both inference and data generation.");
        System.out.println("ais = Adaptive importance sampling for both inference and data generation.");
        System.out.println("pearlmcmc = Pearl MCMC method for both inference and data generation.");
        System.out.println("chavez = Chavez MCMC for both inference and data generation.");
        System.out.println();
        System.out.println("Analysis:");
        System.out.println("robustness = Robustness analysis for structure learning.");
        System.out.println();
        System.out.println("Utilities:");
        System.out.println("converter = Bayesian Network file format converter.");
        System.out.println("dataconverter = Data file format converter.");
        System.out.println("convertergui = Converter GUI.");
        System.out.println("sqltestgui = SQL Testing GUI.");
        System.out.println();
        System.out.println("For individual module parameter helps, invoke the module without parameters.");
    }

    public static void main(String[] args) {
        System.out.println("Bayesian Network for Java (BNJ) v" + Settings.versionString);
        System.out.println("KDD Research");
        System.out.println("http://www.kddresearch.org"); // $NON-NLS-1$
        System.out.println("http://bndev.sourceforge.net"); // $NON-NLS-1$

        ParameterTable params = Parameter.processCurrentParams(args);
        String[] newArgs = Parameter.spliceSubModuleParams(args);
        String moduleName = Parameter.getSubModuleName(args);

        long randomSeedString = params.getLong("-r", -1); // $NON-NLS-1$
        String customConfigFile = params.getString("-c"); // $NON-NLS-1$

        boolean isHelp = params.getBool("/help") || params.getBool("-h") || params.getBool("--h") || // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
                params.getBool("-help") || params.getBool("--help") || params.getBool("/h") || // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
                params.getBool("-?") || params.getBool("--?") || params.getBool("/?"); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$

        if (isHelp) {
            help();
            return;
        }

        if (randomSeedString > -1) Settings.setRandomSeed(randomSeedString);
        if (customConfigFile != null) Settings.load(customConfigFile, moduleName == null);

        System.out.println("Random seed = " + Settings.randomSeed);

        if (moduleName == null) {
            GUI.main(newArgs);
            return;
        }

        if (moduleName.equals("ls")) { // $NON-NLS-1$
            LS.main(newArgs);
        } else if (moduleName.equals("elimbel")) { // $NON-NLS-1$
            ElimBel.main(newArgs);
        } else if (moduleName.equals("pearl")) { // $NON-NLS-1$
            Pearl.main(newArgs);
        } else if (moduleName.equals("k2")) { // $NON-NLS-1$
            K2.main(newArgs);
        } else if (moduleName.equals("gawk")) { // $NON-NLS-1$
            GAWK.main(newArgs);
        } else if (moduleName.equals("greedysl")) { // $NON-NLS-1$
            GreedySL.main(newArgs);
        } else if (moduleName.equals("converter")) { // $NON-NLS-1$
            ConverterFactory.main(newArgs);
        } else if (moduleName.equals("dataconverter")) { // $NON-NLS-1$
            Database.main(newArgs);
        } else if (moduleName.equals("convertergui")) { // $NON-NLS-1$
            edu.ksu.cis.bnj.gui.ConverterGUI.main(newArgs);
        } else if (moduleName.equals("sqltestgui")) { // $NON-NLS-1$
            DatabaseGUI.main(newArgs);
        } else if (moduleName.equals("robustness")) { // $NON-NLS-1$
            Robustness.main(newArgs);
        } else if (moduleName.equals("ss")) { // $NON-NLS-1$
            ForwardSampling.main(newArgs);
        } else if (moduleName.equals("logicsampling")) { // $NON-NLS-1$
            LogicSampling.main(newArgs);
        } else if (moduleName.equals("lw")) { // $NON-NLS-1$
            LikelihoodWeighting.main(newArgs);
        } else if (moduleName.equals("sis")) { // $NON-NLS-1$
            SelfImportance.main(newArgs);
        } else if (moduleName.equals("ais")) { // $NON-NLS-1$
            AIS.main(newArgs);
        } else if (moduleName.equals("pearlmcmc")) { // $NON-NLS-1$
            PearlMCMC.main(newArgs);
        } else if (moduleName.equals("chavezmcmc")) { // $NON-NLS-1$
            ChavezMCMC.main(newArgs);
        } else if (moduleName.equals("help")) { // $NON-NLS-1$
            help();
        } else {
            System.out.println("Unknown module name " + moduleName);
            help();
        }
    }
}
