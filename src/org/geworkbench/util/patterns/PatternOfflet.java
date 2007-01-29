package org.geworkbench.util.patterns;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Jan 25, 2007
 * Time: 6:38:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternOfflet{
    private int position;
    private String token;

    public int getPosition() {
        return position;
    }

    public PatternOfflet(int position, String token) {
        this.position = position;
        this.token = token;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
