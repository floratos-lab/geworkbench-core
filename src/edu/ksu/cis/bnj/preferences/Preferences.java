package edu.ksu.cis.bnj.preferences;

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

/**
 * Options is a class to facilitate the customization of the BNJ package.
 *
 * @author James Plummer
 */
public class Preferences {
    /**
     * The static object to store and change the options. *
     */
    private static Preferences instance = new Preferences();
    /**
     * The base options file name. Should alway be in the options directory *
     */
    public static final String preferencesfilename = "./preferences.txt";


    public Preferences() {

    }

    /**
     * load checks to see if the optionsfile is loaded and loads it if it is not.
     *
     * @return - an int representing the error level received. 0 for no error.
     */
    private int load() {

        return 2;
    }

    /**
     * verifyOptions is a function which verifies the state of the Options in load memory compared
     * to the options file. All flags which keep track of state are updated. This should be called when
     * a system error affecting options occurs to sync Options with the operating program.
     *
     * @return - an int representing the restored state or and error. 0 for restored. 1 for error.
     */
    public int verifyPreferences() {

        return 2;
    }
}