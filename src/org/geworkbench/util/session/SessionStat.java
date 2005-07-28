package org.geworkbench.util.session;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: This class holds information for a session. </p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public class SessionStat {
    private String name;
    private String type;
    private double percentComplete;
    private String other;
    private int sessionId;

    /**
     * Get the name of the session.
     *
     * @return String session's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the session.
     *
     * @return String descriptive type.
     */
    public String getType() {
        return type;
    }

    /**
     * The percent of the completion of the work that is done
     *
     * @return String
     */
    public double getPercentComplete() {
        return percentComplete;
    }

    /**
     * This fields is for adding specific session info
     *
     * @return String
     */
    public String getOther() {
        return other;
    }

    /**
     * Return the session id.
     *
     * @return int
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Constructs a SessionStat object.
     *
     * @param name    the name of the session
     * @param type    the type of the session
     * @param percent the percent completion of the operation in the session
     * @param other   other information about the session
     * @throws SessionOperationException
     */
    public SessionStat(int sessionId, String name, String type, double percent, String other) throws SessionOperationException {
        if (percent < 0.0 || percent > 1.0) {
            throw new SessionOperationException("Invalid argument: percent=" + percent);
        }
        this.sessionId = sessionId;
        this.name = name;
        this.type = type;
        this.percentComplete = percent;
        this.other = other;
    }
}
