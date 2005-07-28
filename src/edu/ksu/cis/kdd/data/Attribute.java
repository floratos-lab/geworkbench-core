package edu.ksu.cis.kdd.data;

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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class Attribute implements Cloneable {
    public static final int DISCRETE = 0;
    public static final int INTEGER = 1;
    public static final int REAL = 2;
    public static final int STRING = 3;

    public static final int NORMAL = 0;
    public static final int PRIMARY = 1;
    public static final int REFERENCE = 2;

    protected String name = ""; //$NON-NLS-1$
    protected int type = DISCRETE;
    protected int key = NORMAL;
    protected LinkedList values = new LinkedList();
    protected String refClassName = null;
    protected String refAttrName = null;
    protected Table owner = null;

    public Attribute() {
    }

    public Attribute(String newName) {
        setName(newName);
    }

    /**
     * @return
     */
    public Table getOwner() {
        return owner;
    }

    /**
     * @param owner
     */
    public void setOwner(Table owner) {
        this.owner = owner;
    }

    public void addValue(Object v) {
        values.add(v);
    }

    public void addValues(Collection c) {
        values.addAll(c);
    }

    public Object clone() {
        Attribute attr = new Attribute(name);
        attr.values.addAll(values);
        attr.type = type;
        return attr;
    }

    public int getArity() {
        return values.size();
    }

    /**
     * Returns the name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the fully qualified name (i.e. tableName.attrName)
     *
     * @return
     */
    public String getFullyQualifiedName() {
        return owner.getName() + "." + name;
    }

    /**
     * Sets the name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        return (o instanceof Attribute) && (name.equals(((Attribute) o).name));
    }

    public String toString() {
        return getName();
    }

    /**
     * Returns the type.
     *
     * @return int
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type The type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    public void setReference(String className, String attrName) {
        refClassName = className;
        refAttrName = attrName;
        key = REFERENCE;
    }

    public String getReferencedTableName() {
        return refClassName;
    }

    public String getReferencedAttributeName() {
        return refAttrName;
    }

    /**
     * Returns the key type { NORMAL, PRIMARY, REFERENCE }
     *
     * @return int
     */
    public int getKey() {
        return key;
    }

    /**
     * Set the key type { NORMAL, PRIMARY, REFERENCE }
     *
     * @param i
     */
    public void setKey(int i) {
        key = i;
    }

    public void setPrimaryKey() {
        key = PRIMARY;
    }

    public boolean isPrimaryKey() {
        return key == PRIMARY;
    }

    public boolean isReferenceKey() {
        return key == REFERENCE;
    }

    /**
     * Returns the values.
     *
     * @return List
     */
    public List getValues() {
        return values;
    }

    /**
     * Sets the values.
     *
     * @param values The values to set
     */
    public void setValues(Collection values) {
        this.values = new LinkedList();
        this.values.addAll(values);
    }

    public int getValueIndex(Object o) {
        return values.indexOf(o);
    }

    public boolean isNumeric() {
        return type == INTEGER || type == REAL;
    }

    public boolean isString() {
        return type == STRING;
    }
}
