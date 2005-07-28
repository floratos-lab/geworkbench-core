package edu.ksu.cis.bnj.bbn.converter;

/*
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

/**
 * @author Roby Joehanes
 */
public class ConverterData {
    protected String description = "", extension = "", packageName = "", className = ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    public ConverterData() {
    }

    public ConverterData(String newDescription, String newExtension, String newPackageName, String newClassName) {
        setDescription(newDescription);
        setExtension(newExtension);
        setPackageName(newPackageName);
        setClassName(newClassName);
    }

    /**
     * Returns the className.
     *
     * @return String
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the description.
     *
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the extension.
     *
     * @return String
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Returns the packageName.
     *
     * @return String
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the className.
     *
     * @param className The className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Sets the description.
     *
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the extension.
     *
     * @param extension The extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Sets the packageName.
     *
     * @param packageName The packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String toString() {
        return "\"" + description + "\", " + extension + ", " + packageName + ", " + className; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ConverterData)) return false;
        ConverterData cd = (ConverterData) o;
        return cd.className.equals(className) && cd.description.equals(description) && cd.extension.equals(extension) && cd.packageName.equals(packageName);
    }

    public void copy(ConverterData d) {
        className = d.className;
        description = d.description;
        extension = d.extension;
        packageName = d.packageName;
    }
}
