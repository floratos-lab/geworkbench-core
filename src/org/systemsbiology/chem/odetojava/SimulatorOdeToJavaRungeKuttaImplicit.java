package org.systemsbiology.chem.odetojava;

/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 * 
 * This source code is distributed under the GNU Lesser 
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import odeToJava.ImexSD;
import odeToJava.modules.Btableau;
import odeToJava.modules.ODE;
import odeToJava.modules.Span;
import org.systemsbiology.chem.ISimulator;
import org.systemsbiology.util.IAliasableClass;

public class SimulatorOdeToJavaRungeKuttaImplicit extends SimulatorOdeToJavaBase implements IAliasableClass, ISimulator {
    public static final String CLASS_ALIAS = "ODEtoJava-imex443-stiff";

    public boolean allowsInterrupt() {
        return (false);
    }

    protected void runExternalSimulation(Span pSimulationTimeSpan, double[] pInitialDynamicSymbolValues, double pInitialStepSize, double pMaxAllowedRelativeError, double pMaxAllowedAbsoluteError, String pTempOutputFileName) {
        Btableau simulationButcherTableau = new Btableau("imex443");
        ODE simulationModel = (ODE) this;

        ImexSD imexsd = new ImexSD(simulationModel, pSimulationTimeSpan, pInitialDynamicSymbolValues, pInitialStepSize, simulationButcherTableau, pMaxAllowedAbsoluteError, pMaxAllowedRelativeError, pTempOutputFileName, "Stats_Off");
        imexsd.setRecorder(this);

        imexsd.routine();
    }


}
