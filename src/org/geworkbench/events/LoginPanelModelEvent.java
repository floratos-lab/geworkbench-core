package org.geworkbench.events;

import java.util.EventObject;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: LoginPanelModelEvent is used to notify a listener that a  model
 * has changed.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id$
 */

public class LoginPanelModelEvent extends EventObject {

	private static final long serialVersionUID = -8335038947064419272L;

	public LoginPanelModelEvent(Object source) {
        super(source);
    }

}