package org.geworkbench.engine.cascript;

/**
 * This holds the return value for a function
 * A new CasDataType (aka CasReturn) to make sure that the
 * statement that is return the CasDataType is from a return statement,
 * so the CasDataType is wrapped inside CasReturn
 *
 * @author Behrooz Badii
 */
public class CasReturn extends CasDataType {

    CasDataType retValue;

    public CasReturn(CasDataType dataType) {
        retValue = dataType;
    }

    public CasDataType getRetValue() {
        return retValue;
    }

}
