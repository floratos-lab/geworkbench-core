 package org.geworkbench.engine.preferences;

/**
 * @author John Watkinson
 */
public class IntegerField extends Field {
	 
	private static final long serialVersionUID = -8862041536368416988L;
	
	private int value;

    public IntegerField(String fieldName) {
        super(fieldName);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void copyValueFrom(Field other) {
        if (other instanceof IntegerField) {
            value = ((IntegerField) other).getValue();
        }
    }

    public void fromString(String s) throws ValidationException {
        try {
            value = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new ValidationException("Number required.", nfe);
        }
    }

    public String toString() {
        return "" + value;
    }

    public IntegerField clone() {
    	IntegerField clone = new IntegerField(getName());
        clone.setValue(value);
        return clone;
    }

}
