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

package edu.ksu.cis.kdd.util;

/**
 * This Class holds Information about a BBN algorithm for registry.
 *
 * @author jplummer
 */
public class Registry {
    public static final String LEARNER = "Learner";
    public static final String INFERENCE = "Inference";
    public static final String DATA_GENERATOR = "Data Generator";
    public static final String CONVERTER = "Converter";
    public static final String DATA_CONVERTER = "Data Converter";
    public static final String UNTYPED = "";

    public String type = UNTYPED;
    public String name = "";
    public String classpackage = "";
    public String classname = "";

    /**
     * Creates an empty BBNTag
     */
    public Registry() {
    }

    public Registry(String type, String name, String classpackage, String classname) {
        this.type = type;
        this.name = name;
        this.classpackage = classpackage;
        this.classname = classname;
        this.checkRegistry();
    }

    private void checkRegistry() {
        if (type == null) {
            throw new RuntimeException("Registry.type cannot be null");
        }
        if (name == null) {
            throw new RuntimeException("Registry.type cannot be null");
        }
        if (classpackage == null) {
            throw new RuntimeException("Registry.type cannot be null");
        }
        if (classname == null) {
            throw new RuntimeException("Registry.type cannot be null");
        }
        Registry.checkType(this);
    }

    private static void checkType(Registry reg) {
        if (reg.type.toLowerCase().equals(Registry.LEARNER.toLowerCase())) {
            reg.type = Registry.LEARNER;
        } else if (reg.type.toLowerCase().equals(Registry.INFERENCE.toLowerCase())) {
            reg.type = Registry.INFERENCE;
        } else if (reg.type.toLowerCase().equals(Registry.DATA_CONVERTER.toLowerCase())) {
            reg.type = Registry.DATA_CONVERTER;
        } else if (reg.type.toLowerCase().equals(Registry.CONVERTER.toLowerCase())) {
            reg.type = Registry.CONVERTER;
        } else if (reg.type.toLowerCase().equals(Registry.DATA_GENERATOR.toLowerCase())) {
            reg.type = Registry.DATA_GENERATOR;
        } else {
            reg.type = Registry.UNTYPED;
        }
    }

    public String getClassName() {
        return classname;
    }
}