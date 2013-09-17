package org.geworkbench.util.patterns;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Jan 25, 2007
 * Time: 6:38:29 PM
 * 
 * @version $Id$
 */
public class PatternOfflet implements Serializable {
	private static final long serialVersionUID = -6441126412656981789L;
	
	private int position;
    private String token;

    public PatternOfflet(int position, String token) {
        this.position = position;
        this.token = token;
    }

    public int getPosition() {
        return position;
    }

    public String getToken() {
        return token;
    }

}
