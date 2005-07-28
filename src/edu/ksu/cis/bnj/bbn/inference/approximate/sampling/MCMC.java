/*
 * Created on Mar 7, 2003
 *
 * This file is part of Bayesian Network tools in Java (BNJ).
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

package edu.ksu.cis.bnj.bbn.inference.approximate.sampling;

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.datagen.DataGenerator;
import edu.ksu.cis.bnj.bbn.inference.ApproximateInference;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.MersenneTwisterFast;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.OptionGUI;

import java.util.*;

/**
 * Super class for MCMC based approximate inference engine
 *
 * @author Roby Joehanes
 */
public abstract class MCMC extends ApproximateInference implements DataGenerator {
    protected static final String OPT_MAX_ITERATION = "maxIteration";
    protected static final String OPT_USE_MARKOV_BLANKET = "useMarkovBlanket";
    protected static final String OPT_GENERATE_DATA = "generateData";
    protected static final String OPT_RUNNING_TIME_LIMIT = "runningTimeLimit"; // XXX

    public static final int defaultMaxIteration = 1000;
    public static final long NO_TIME_LIMIT = 0; // XXX

    protected static MersenneTwisterFast random = Settings.random;

    // XXX  start
    /**
     * Sets the limit (in milliseconds) for how long the procedure should run
     */
    protected long runningTimeLimit = NO_TIME_LIMIT;
    /**
     * flag that signals the getMarginals function of subclasses to abort
     */
    protected boolean abort = false;
    /**
     * Timer used to set the abort flag after a given amount of time
     */
    protected Timer abortTimer;
    // XXX end

    protected int maxIteration;
    protected boolean useMarkovBlanketScore = false;
    protected boolean generateSamples = false;
    protected Table tuples;

    protected LinkedList listeners = new LinkedList();

    public MCMC() {
    }

    /**
     * @param g
     */
    public MCMC(BBNGraph g) {
        super(g);
    }

    public void addListener(MCMCListener l) {
        assert (l != null);
        if (!listeners.contains(l)) listeners.add(l);
    }

    protected void sendEvent(MCMCEvent e) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            MCMCListener l = (MCMCListener) i.next();
            l.callback(e);
        }
    }

    public void removeListener(MCMCListener l) {
        assert (l != null);
        listeners.remove(l);
    }

    /**
     * Get Markov blanket scoring.
     *
     * @param n      The node we want to have the Markov blanket scored upon
     * @param chosen The chosen values so far
     * @return Hashtable The table from value -> score
     */
    public Hashtable getMarkovBlanketScore(BBNNode n, Hashtable chosen) {
        Hashtable result = new Hashtable();
        String nodeName = n.getLabel();
        Object oldVal = chosen.get(nodeName);
        BBNDiscreteValue dval = (BBNDiscreteValue) n.getValues();
        double total = 0.0;

        for (Iterator i = dval.iterator(); i.hasNext();) {
            String val = i.next().toString();
            chosen.put(nodeName, val);
            double p = n.query(chosen);
            HashSet seenBefore = new HashSet();
            seenBefore.add(n);
            for (Iterator j = n.getChildren().iterator(); j.hasNext();) {
                BBNNode child = (BBNNode) j.next();
                seenBefore.add(child);
                p *= child.query(chosen);
                for (Iterator k = child.getParents().iterator(); k.hasNext();) {
                    BBNNode childParent = (BBNNode) k.next();
                    if (seenBefore.contains(childParent)) continue;
                    p *= childParent.query(chosen);
                }
            }
            for (Iterator k = n.getParents().iterator(); k.hasNext();) {
                BBNNode parent = (BBNNode) k.next();
                if (seenBefore.contains(parent)) continue;
                p *= parent.query(chosen);
            }
            total += p;
            result.put(val, new Double(p));
        }

        // Normalize
        if (total > 0) {
            for (Enumeration e = result.keys(); e.hasMoreElements();) {
                Object val = e.nextElement();
                double p = ((Double) result.get(val)).doubleValue();
                result.put(val, new Double(p / total));
            }
        } else { // work around for 0.0/0.0
            Object val = result.keys().nextElement();
            result.put(val, new Double(1.0));
        }

        chosen.put(nodeName, oldVal);
        return result;
    }

    public Table getData() {
        return tuples;
    }

    public void generateData(boolean d) {
        options.put(MCMC.OPT_GENERATE_DATA, new Boolean(d));
        generateSamples = d;
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.datagen.DataGenerator#generateData(int)
     */
    public Table generateData(int howmuch) {
        setMaxIteration(howmuch);
        generateData(true);
        getMarginals();

        return tuples;
    }

    /**
     * @return boolean
     */
    public boolean isUseMarkovBlanketScore() {
        return useMarkovBlanketScore;
    }

    /**
     * Sets the useMarkovBlanketScore.
     *
     * @param useMarkovBlanketScore The useMarkovBlanketScore to set
     */
    public void setUseMarkovBlanketScore(boolean useMarkovBlanketScore) {
        this.useMarkovBlanketScore = useMarkovBlanketScore;
        options.put(MCMC.OPT_USE_MARKOV_BLANKET, new Boolean(useMarkovBlanketScore));
    }


    /**
     * @return int
     */
    public int getMaxIteration() {
        return maxIteration;
        //return ((Integer) options.get(MCMC.OPT_MAX_ITERATION)).intValue();
    }

    /**
     * Sets the maxIteration.
     *
     * @param maxIteration The maxIteration to set
     */
    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
        options.put(MCMC.OPT_MAX_ITERATION, new Integer(maxIteration));
    }

    /**
     * TODO: need to read from config file and/or through Settings.java
     *
     * @see edu.ksu.cis.kdd.util.gui.Optionable#getDefaultOptions()
     */
    public Hashtable getDefaultOptions() {
        options = new Hashtable();
        setMaxIteration(MCMC.defaultMaxIteration);
        generateData(false);
        setUseMarkovBlanketScore(false);
        setRunningTimeLimit(MCMC.NO_TIME_LIMIT);
        return options;
    }

    public OptionGUI getOptionsDialog() {
        MCMCOptionGUI gui = new MCMCOptionGUI(this);
        return gui;
    }

    // XXX start
    /**
     * Sets the Running time limit, which methods can use to abort an
     * inference after a specific amount of time. The procedures do
     * not stop abruptly at the limit; they stop after completing the
     * current iteration
     *
     * @param seconds The time in milliseconds that the inf method will run.
     */
    public void setRunningTimeLimit(long mseconds) {
        runningTimeLimit = mseconds;
        options.put(MCMC.OPT_RUNNING_TIME_LIMIT, new Long(runningTimeLimit));
    }

    /**
     * Gets the Running time limit, which methods can use to abort an
     * inference after a specific amount of time. The procedures do
     * not stop abruptly at the limit; they stop after completing the
     * current iteration
     */
    public long getRunningTimeLimit() {
        return runningTimeLimit;
    }

    /**
     * Set a timer to expire in the number of milliseconds specified
     * by runningTimeLimit. When the timer expires, it will set the
     * abort flag to true, signaling the getMarginals function to exit
     * before performing all iterations.
     */
    protected void setAbortTimer() {
        if (this.runningTimeLimit > 0) {
            abortTimer = new Timer();
            abortTimer.schedule(new AbortTask(), this.runningTimeLimit);
        }
    }

    /**
     * Cancel the abort timer. This should be called by subclasses that
     * call setAbortTimer. It is necessary to handle the case where
     * the inference procedure completes all iterations before the timer
     * expires. Currently, not calling this routine will not cause a
     * problem, but later additions may change this.
     */
    protected void cancelAbortTimer() {
        abort = false;
        if (abortTimer != null) {
            abortTimer.cancel();
        }
    }

    /**
     * Sets the abort flag to true, which tells the getMarginals functions
     * to exit at the end of the current iteration.
     */
    protected void abort() {
        abort = true;
    }

    /**
     * Handles the expiration of the abort Timer.
     *
     * @author Bart Peintner
     */
    class AbortTask extends TimerTask {
        public void run() {
            abort();
        }
    }
    // XXX end

}
