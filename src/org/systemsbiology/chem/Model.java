package org.systemsbiology.chem;

/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 *
 * This source code is distributed under the GNU Lesser
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import org.systemsbiology.math.Symbol;
import org.systemsbiology.math.SymbolValue;
import org.systemsbiology.util.DataNotFoundException;
import org.systemsbiology.util.DebugUtils;

import java.util.*;

/**
 * A named collection of {@link Reaction} and
 * {@link Parameter} objects, which represents
 * a system of interacting chemical {@link Species}.
 * The chemical Species are contained in the Reaction
 * objects.
 *
 * @author Stephen Ramsey
 */
public class Model {
    private HashMap mReactionsMap;
    private String mName;
    private HashMap mDynamicSymbolsMap;
    private HashMap mSymbolsMap;
    private HashMap mParametersMap;
    private SpeciesRateFactorEvaluator mSpeciesRateFactorEvaluator;
    public static final String NAMESPACE_IDENTIFIER = "::";

    //AM--3/31 contains the species in the order they were read
    public Vector vecOrderedSpecies = new Vector();

    public Model() {
        mReactionsMap = new HashMap();
        mDynamicSymbolsMap = new HashMap();
        mSymbolsMap = new HashMap();
        mParametersMap = new HashMap();
        setSpeciesRateFactorEvaluator(new SpeciesRateFactorEvaluatorCombinatoric());
        setName(null);
    }

    public void setSpeciesByName(String name, Species pSpeciesName) {
        mSymbolsMap.put(name, pSpeciesName);
    }

    public Model(Model foo) {
        mReactionsMap = new HashMap(foo.mReactionsMap);
        mDynamicSymbolsMap = new HashMap(foo.mDynamicSymbolsMap);
        mSymbolsMap = new HashMap(foo.mSymbolsMap);
        mParametersMap = new HashMap(foo.mParametersMap);
        setSpeciesRateFactorEvaluator(foo.getSpeciesRateFactorEvaluator()); //SKETCHY?!?!?!?
        setName(foo.getName());

    }

    public Model(String pName) {
        this();
        setName(pName);
    }

    /**
     * Sets the model name to be the string contained in <code>pName</code>.
     * This overrides the model name passed to the constructor.
     */
    public void setName(String pName) {
        mName = pName;
    }

    public String getName() {
        return (mName);
    }

    public void setSpeciesRateFactorEvaluator(SpeciesRateFactorEvaluator pSpeciesRateFactorEvaluator) {
        mSpeciesRateFactorEvaluator = pSpeciesRateFactorEvaluator;
    }

    public SpeciesRateFactorEvaluator getSpeciesRateFactorEvaluator() {
        return (mSpeciesRateFactorEvaluator);
    }

    HashMap getSymbolsMap() {
        return (mSymbolsMap);
    }

    HashMap getDynamicSymbolsMap() {
        return (mDynamicSymbolsMap);
    }

    ArrayList constructReactionsList() {
        Reaction[] sampleArray = new Reaction[0];
        Reaction[] intArray = (Reaction[]) getReactionsMap().values().toArray(sampleArray);
        int numReactions = intArray.length;
        ArrayList reactionsList = new ArrayList();

        for (int reactionCtr = 0; reactionCtr < numReactions; ++reactionCtr) {
            reactionsList.add((Reaction) intArray[reactionCtr].clone());
            //            reactionsList.add((Reaction) intArray[reactionCtr]);
        }

        return (reactionsList);
    }

    Reaction[] constructReactionsArray() {
        Reaction[] sampleArray = new Reaction[0];
        Reaction[] intArray = (Reaction[]) getReactionsMap().values().toArray(sampleArray);
        int numReactions = intArray.length;
        Reaction[] retArray = new Reaction[numReactions];
        for (int reactionCtr = 0; reactionCtr < numReactions; ++reactionCtr) {
            retArray[reactionCtr] = (Reaction) intArray[reactionCtr].clone();
            //            retArray[reactionCtr] = (Reaction) intArray[reactionCtr];
        }
        return (retArray);
    }

    ArrayList constructDynamicSymbolsList() {
        Species[] sampleArray = new Species[0];
        Species[] intArray = (Species[]) getDynamicSymbolsMap().values().toArray(sampleArray);
        int numSpecies = intArray.length;
        ArrayList symbolsList = new ArrayList();
        for (int speciesCtr = 0; speciesCtr < numSpecies; ++speciesCtr) {
            symbolsList.add((Species) intArray[speciesCtr].clone());
        }
        return (symbolsList);
    }

    Species[] constructDynamicSymbolsArray() {
        Species[] sampleArray = new Species[0];
        Species[] intArray = (Species[]) getDynamicSymbolsMap().values().toArray(sampleArray);
        int numSpecies = intArray.length;
        Species[] retArray = new Species[numSpecies];
        for (int speciesCtr = 0; speciesCtr < numSpecies; ++speciesCtr) {
            retArray[speciesCtr] = (Species) intArray[speciesCtr].clone();
        }
        return (retArray);
    }

    SymbolValue[] constructGlobalNonDynamicSymbolsArray() {
        HashMap symbolsMap = getSymbolsMap();
        HashMap dynamicSpeciesMap = getDynamicSymbolsMap();
        Iterator symbolsIter = symbolsMap.keySet().iterator();
        ArrayList retList = new ArrayList();
        while (symbolsIter.hasNext()) {
            String symbolName = (String) symbolsIter.next();
            if (null == dynamicSpeciesMap.get(symbolName)) {
                SymbolValue symbolValue = (SymbolValue) symbolsMap.get(symbolName);
                assert (null != symbolValue.getValue()) : "null value for symbol: " + symbolName;
                SymbolValue newSymbolValue = (SymbolValue) symbolValue.clone();
                retList.add(newSymbolValue);
            }
        }
        SymbolValue[] sampleArray = new SymbolValue[0];
        return ((SymbolValue[]) retList.toArray(sampleArray));
    }

    SymbolValue getSymbolByName(String pSymbolName) {
        return ((SymbolValue) getSymbolsMap().get(pSymbolName));
    }

    HashMap getReactionsMap() {
        return (mReactionsMap);
    }

    public void addParameter(Parameter pParameter) {
        SymbolValueChemSimulation.addSymbolValueToMap(mSymbolsMap, pParameter.getSymbolName(), pParameter);
        SymbolValueChemSimulation.addSymbolValueToMap(mParametersMap, pParameter.getSymbolName(), pParameter);
    }

    //AM--3/31 added this method to add species to a vector in the order they were read
    public void addSpecies(Species pSpecies) {
        vecOrderedSpecies.add(pSpecies.getName());
        pSpecies.addSymbolsToGlobalSymbolMap(mSymbolsMap);
    }

    public Collection getDynamicSymbols() {
        return (mDynamicSymbolsMap.values());
    }

    //AM--3/30 Added this method
    public String[] getDynamicSymbolNames() {
        String[] strArr = new String[0];
        return (String[]) getDynamicSymbolsMap().keySet().toArray(strArr);
    }

    public Collection getReactions() {
        return (mReactionsMap.values());
    }

    public Collection getSymbols() {
        return (mSymbolsMap.values());
    }

    /**
     * It is illegal to add the reaction with a given name, twice
     */
    public void addReaction(Reaction pReaction) throws IllegalStateException {
        String reactionName = pReaction.getName();

        Reaction storedReaction = (Reaction) mReactionsMap.get(reactionName);
        if (null != storedReaction) {
            throw new IllegalStateException("reaction is already added to this model: " + reactionName);
        } else {
            mReactionsMap.put(reactionName, pReaction);
        }

        pReaction.addDynamicSpeciesToGlobalSpeciesMap(getDynamicSymbolsMap());
        pReaction.addSymbolsToGlobalSymbolMap(getSymbolsMap());
    }

    public void setReaction(String name, Reaction pReaction) throws IllegalStateException {
        //mReactionsMap.remove(name);
        mReactionsMap.put(name, pReaction);
    }

    public void setSymbol(String name, Symbol pReaction) throws IllegalStateException {
        //mReactionsMap.remove(name);
        mSymbolsMap.put(name, pReaction);
    }


    public Species getSpeciesByName(String pSpeciesName) throws DataNotFoundException {
        SymbolValue symbolValue = (SymbolValue) mSymbolsMap.get(pSpeciesName);
        if (null == symbolValue) {
            throw new DataNotFoundException("could not find species: " + pSpeciesName);
        }
        if (!(symbolValue instanceof Species)) {
            throw new IllegalArgumentException("requested item is not a species: " + pSpeciesName);
        }
        return ((Species) symbolValue);
    }

    public String[] getOrderedSpeciesNamesArray() {
        List speciesNamesList = new LinkedList();
        Iterator symbolValuesIter = mSymbolsMap.values().iterator();
        while (symbolValuesIter.hasNext()) {
            SymbolValue symbolValue = (SymbolValue) symbolValuesIter.next();
            if (symbolValue instanceof Species) {
                speciesNamesList.add(symbolValue.getSymbol().getName());
            }
        }
        Collections.sort(speciesNamesList);
        return ((String[]) speciesNamesList.toArray(new String[0]));
    }

    public String[] getOrderedSpeciesNamesArray(Comparator comp) {
        List speciesNamesList = new LinkedList();
        Iterator symbolValuesIter = mSymbolsMap.values().iterator();
        while (symbolValuesIter.hasNext()) {
            SymbolValue symbolValue = (SymbolValue) symbolValuesIter.next();
            if (symbolValue instanceof Species) {
                speciesNamesList.add(symbolValue.getSymbol().getName());
            }
        }
        Collections.sort(speciesNamesList, comp);
        return ((String[]) speciesNamesList.toArray(new String[0]));
    }


    public String[] getOrderedNonConstantSymbolNamesArray() throws IllegalStateException {
        List symbolNamesList = new LinkedList();
        Iterator symbolValuesIter = mSymbolsMap.values().iterator();
        while (symbolValuesIter.hasNext()) {
            SymbolValue symbolValue = (SymbolValue) symbolValuesIter.next();
            if (null == symbolValue.getValue()) {
                throw new IllegalStateException("symbol has no value associated with it: " + symbolValue.getSymbol().getName());
            }
            if (symbolValue.getValue().isExpression() || symbolValue instanceof Species) {
                symbolNamesList.add(symbolValue.getSymbol().getName());
            }
        }
        Collections.sort(symbolNamesList);
        return ((String[]) symbolNamesList.toArray(new String[0]));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Model: ");
        sb.append(getName());
        sb.append("\n\n");
        sb.append("Parameters: \n");
        String separatorString = ",\n";
        DebugUtils.describeSortedObjectList(sb, mParametersMap, separatorString);
        sb.append("\n\n");
        sb.append("Compartments: \n");
        DebugUtils.describeSortedObjectList(sb, mSymbolsMap, Compartment.class, separatorString);
        sb.append("\n\n");
        sb.append("Species: \n");
        DebugUtils.describeSortedObjectList(sb, mSymbolsMap, Species.class, separatorString);
        sb.append("\n\n");
        sb.append("Reactions: \n");
        DebugUtils.describeSortedObjectList(sb, mReactionsMap, separatorString);
        return (sb.toString());
    }
}
