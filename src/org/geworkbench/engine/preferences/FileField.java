package org.geworkbench.engine.preferences;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author John Watkinson
 * @version $Id: FileField.java,v 1.2 2008-10-28 16:55:18 keshav Exp $
 */
public class FileField extends Field {

    private File value;
    private FileFilter filter;

    public FileField(String fieldName) {
        super(fieldName);
    }

    public FileField(String fieldName, FileFilter filter) {
        super(fieldName);
        this.filter = filter;
    }

    public FileFilter getFilter() {
        return filter;
    }

    public File getValue() {
        return value;
    }

    public void setValue(File value) {
        this.value = value;
    }

    public void copyValueFrom(Field other) {
        if (other instanceof FileField) {
            value = ((FileField) other).getValue();
        }
    }

    public void fromString(String s) throws ValidationException{
         
    	value = new File(s);
        if (! value.exists() )
        	throw new ValidationException(s + " is not valid.");
    }

    public String toString() {
        return value.getPath();
    }

    public FileField clone() {
        FileField clone = new FileField(getName());
        clone.setValue(value);
        return clone;
    }
}
