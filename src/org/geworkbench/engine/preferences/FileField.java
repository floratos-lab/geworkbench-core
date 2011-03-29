package org.geworkbench.engine.preferences;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author John Watkinson
 * @version $Id$
 */
public class FileField extends Field {

	private static final long serialVersionUID = -2701969431540105989L;
	
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
    	if(s.equals("")) {
    		value = null;
    		return;
    	}
         
    	value = new File(s);
        if (! value.exists() )
        	throw new ValidationException(s + " is not valid.");
    }

    public String toString() {
    	if(value!=null)
    		return value.getPath();
    	else
    		return null;
    }

    public FileField clone() {
        FileField clone = new FileField(getName());
        clone.setValue(value);
        return clone;
    }
}
