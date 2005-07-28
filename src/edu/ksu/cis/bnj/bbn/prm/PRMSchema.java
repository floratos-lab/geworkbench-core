package edu.ksu.cis.bnj.bbn.prm;

import edu.ksu.cis.kdd.data.Attribute;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author pbo8844  
 */

/**
 * *********THIS CLASS IS NOT USED ANY MORE***************
 * schema format is as follows:
 * <p/>
 * priamaryKeys, + attributes + referenceAttributes
 * ex:
 * primaryKey1, primaryKey2, attribute1, attribute2, referenceKey1;
 * <p/>
 * default order: primaryKeys followed by attributes followed by referenceKeys
 */


public class PRMSchema {
    LinkedList mPKeys;
    LinkedList mRKeys;
    LinkedList mAttributes;
    LinkedList mPRMSchema;
    private String mName = "noName";
    private int mSchemaSize;

    public PRMSchema() {
        mPRMSchema = new LinkedList();
    }

    public PRMSchema(LinkedList pKeys, LinkedList rKeys, LinkedList atts) {
        mPKeys = pKeys;
        mRKeys = rKeys;
        mAttributes = atts;
        makeList(mPKeys, atts, rKeys);
    }

    private void makeList(LinkedList pKeys, LinkedList atts, LinkedList rKeys) {
        LinkedList list = new LinkedList();
        for (Iterator i = pKeys.iterator(); i.hasNext();)
            list.add(i.next());
        for (Iterator i = atts.iterator(); i.hasNext();)
            list.add(i.next());
        for (Iterator i = rKeys.iterator(); i.hasNext();)
            list.add(i.next());
        mSchemaSize = list.size();
        mPRMSchema = list;
    }

    public void makeList() {
        makeList(mPKeys, mAttributes, mRKeys);
    }


    public LinkedList getPrimaryKeys() {
        return mPKeys;
    }

    public LinkedList getReferenceKeys() {
        return mRKeys;
    }

    public LinkedList getAttributes() {
        return mAttributes;
    }

    public void addPKey(Attribute attr) {
        mPKeys.add(attr);
    }

    public void addRKey(Attribute attr) {
        mRKeys.add(attr);
    }

    public void addPKeys(LinkedList list) {
        mPKeys = list;
    }

    public void addRKeys(LinkedList list) {
        mRKeys = list;
    }

    public void addAttributes(LinkedList list) {
        this.mAttributes = list;
    }

    public void addAttribute(Attribute attr) {
        mAttributes.add(attr);
    }

    public int size() {
        return mSchemaSize;
    }

    public int sizeOfAttributes() {
        return mAttributes.size();
    }

    public int getIndex(Attribute attribute) {
        return (mPRMSchema.indexOf(attribute));
    }

    public void setName(String s) {
        mName = s;
    }

    public String getName() {
        return mName;
    }

    /*
     * checks if a particular attribute belongs to this class
     * here assumption is that the no two classes have same name
     * ******* TO DO: relax the assumption
     * */
    public boolean isMember(String attributeName) {
        if (getAttribute(attributeName) == null)
            return false;
        return true;
    }


    public Attribute getAttribute(String name) {
        Attribute attr = new Attribute(name);
        int idx = mAttributes.indexOf(attr);
        return idx > -1 ? (Attribute) mAttributes.get(idx) : null;
    }


    public void display() {
        System.out.print("\n" + "PrimaryKeys are : ");
        for (Iterator i = mPKeys.iterator(); i.hasNext();)
            System.out.print(i.next() + " ");
        System.out.print("\n" + "Attributes are : ");
        for (Iterator i = mAttributes.iterator(); i.hasNext();)
            System.out.print(i.next() + " ");
        System.out.print("\n" + "ReferenceKeys are : ");
        for (Iterator i = mRKeys.iterator(); i.hasNext();)
            System.out.print(i.next() + " ");
    }
}
