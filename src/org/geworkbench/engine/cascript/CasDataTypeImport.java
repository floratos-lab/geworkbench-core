package org.geworkbench.engine.cascript;

import java.util.HashMap;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Enumeration;

class CasDataTypeImport extends HashMap {
    public CasDataTypeImport() {
        initialize();
        System.out.println(this.get("DSMicroarraySet"));
    }

    void initialize() {
        try {
            InputStream is = CasDataTypeImport.class.getResourceAsStream("datatypes.properties");
            Properties props = new Properties();
            props.load(is);
            this.putAll(props);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CasException("Error occurred loading datatypes and corresponding class locations");
        }
    }
}
