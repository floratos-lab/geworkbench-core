package org.geworkbench.util;

import java.awt.Component;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A geWorkbench level cursor that allows one control/plugin to start displaying
 * a wait cursor and another control/plugin to turn the cursor back to the
 * normal cursor.
 * 
 * In this documentation, control refers to a Java GUI component and plugin
 * refers to a geworkbench component.
 * 
 * In control/plugin #1, before starting computation:
 * 
 * <pre>
 * JComponent[] myControlsToDisableWhileCursorWaits = {...};
 * org.geworkbench.util.Cursor cursor = org.geworkbench.util.Cursor.getCursor();
 * if (cursor.isStarted() && !cursor.isFinished()) { 
 *    return; 
 * } else {
 *    cursor.setAssociatedComponent(myPanel);
 *    cursor.linkCursorToComponents(myControlsToDisableWhileCursorWaits); 
 * } 
 * try {
 *    cursor.start(); 
 *    beginComputationInControlOrPluginNumber2(); 
 * } catch (Exception e){
 *    log.warn(e.getMessage()); 
 *    return; 
 * }
 * </pre>
 * 
 * In control/plugin #2, when the computation finishes:
 * 
 * <pre>
 * org.geworkbench.util.Cursor cursor = org.geworkbench.util.Cursor.getCursor();
 * java.awt.Component c = cursor.getAssociatedComponent();
 * if((cursor.getAssociatedComponent() != null) 
 *  && cursor.isStarted() 
 *  && !cursor.isFinished()){
 *     cursor.setFinished(true); 
 * }
 * </pre>
 * 
 * The cursor is meant to be used for short computations and times out at 1
 * minute. If the computation takes longer than a minute, please use the
 * progress bar.
 * 
 * @author ch2514
 * @version
 */
public class Cursor {
	static Log log = LogFactory.getLog(Cursor.class);

	// curosr times out in 1 min
	private static final long MAX_WAIT_TIME = 60000;

	// singleton cursor for geWorkbench
	private static Cursor cursor;

	// this is the component to which the cursor is set
	private static Component component;

	// this is the list of components which get disabled while the cursor is in
	// wait mode and re-enabled when the computation is done and the cursor
	// turns back to normal.
	private static JComponent[] components;

	private static final java.awt.Cursor hourglassCursor = new java.awt.Cursor(
			java.awt.Cursor.WAIT_CURSOR);

	private static final java.awt.Cursor normalCursor = new java.awt.Cursor(
			java.awt.Cursor.DEFAULT_CURSOR);

	// tracks the computation state
	private static boolean finished = false;

	// tracks the cursor state
	private static boolean started = false;

	// monitors and coordinates between the computation and the cursor states
	private static CursorMonitor cm = null;

	/*
	 * The cursor object is a singleton.
	 */
	private Cursor() {
		cm = new CursorMonitor(this);
	}

	/**
	 * Returns the component to which the cursor is currently set.
	 * 
	 * @return the component to which this cursor is set
	 */
	public Component getAssociatedComponent() {
		return component;
	}

	/**
	 * Specifies the component to which the cursor should be set. Setting the
	 * components restarts the cursor. If any previous computation is still
	 * going, it will be ignored.
	 * 
	 * @param c --
	 *            the component to which the cursor should be set
	 */
	public void setAssociatedComponent(Component c) {
		started = false;
		finished = false;
		component = c;
		if (cm.isAlive())
			cm = new CursorMonitor(this);
	}

	/**
	 * Returns array of components that are linked to the cursor. These
	 * components are disabled while the cursor is in the wait mode. When the
	 * cursor turns back to normal, these components are re-enabled.
	 * 
	 * @return an array of components which is disabled when the cursor turns
	 *         into wait mode.
	 */
	public Component[] getCursorLinkedComponents() {
		return components;
	}

	/**
	 * Specifies which components should be linked to the cursor. These
	 * components are disabled while the cursor is in the wait mode. When the
	 * cursor turns back to normal, these components are re-enabled.
	 * 
	 * @param cs --
	 *            an array of components which is disabled when the cursor turns
	 *            into wait mode.
	 */
	public void linkCursorToComponents(JComponent[] cs) {
		components = cs;
	}

	/**
	 * Specifies whether the computation associated with the cursor has
	 * finished.
	 * 
	 * @return -- true if the computation is done and the cursor is turned back
	 *         to normal. false if the computation is in progress and the cursor
	 *         is in wait mode.
	 */
	public boolean isFinished() {
		return !cm.isAlive();
	}

	/*
	 * This is used by the CursorMonitor to see if the finish flag is set. If
	 * so, the CursorMonitor calls the cursor's stop() method.
	 * 
	 * @return -- true if the finish flag is set, false otherwise.
	 */
	private boolean isFinishedSet() {
		return finished;
	}

	/**
	 * Allows the computing thread to set the finished flag. If the flag is set
	 * (i.e. cursor.setFinished(true)), then the cursor turns back to normal. By
	 * default the finished flag is set to false.
	 * 
	 * @param b --
	 *            true if the computation is done. false otherwise.
	 */
	public void setFinished(boolean b) {
		finished = b;
	}

	/**
	 * Indicates whether the cursor has been started. The cursor is considered
	 * started if it is in wait mode.
	 * 
	 * @return -- true if the cursor has been started and false otherwise.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Allows access to geWorkbench's singleton cursor object.
	 * 
	 * @return -- the singleton cursor object associated with geWorkbench
	 */
	public static Cursor getCursor() {
		if (cursor == null) {
			cursor = new Cursor();
		}
		return cursor;
	}

	/**
	 * Starts the cursor. The cursor gets into wait mode with the associated GUI
	 * change (e.g. the mouse turns into a hourglass mouse) and waits for the
	 * computation to finish. It also disable components linked to this cursor
	 * until computation is finished. When the computation finishes, the cursor
	 * turns back to normal and all its linked components are enabled again.
	 * 
	 * The cursor does time out in 1 minute (at which point the cursor turns
	 * back to normal and all its linked components are re-enabled). If the
	 * computation takes longer than 1 minute, please use the progress bar.
	 * 
	 * This method throws a runtime exception if:
	 * 
	 * 1) There is no component to which the cursor is set (i.e.
	 * setAssociatedComponent() has not been called)
	 * 
	 * 2) The cursor has already started.
	 * 
	 * 3) The computation the cursor is currently tracking is still running.
	 */
	public void start() {
		if (component == null) {
			throw new RuntimeException(
					"Cannot start geWorkbench cursor: No component associated with Cursor.");
		}
		if (started) {
			throw new RuntimeException(
					"Cannot start geWorkbench cursor: Cursor already started.");
		}
		if (cm.isAlive()) {
			throw new RuntimeException(
					"Cannot start geWorkbench cursor: Cursor is already running");
		}
		if ((components != null) && (components.length > 0)) {
			log.debug("disabling components...");
			for (JComponent c : components) {
				if (c != null) {
					log.debug("component=" + c.getClass().getName());
					c.setEnabled(false);
				}
			}
			component.repaint();
		}
		started = true;
		finished = false;
		log.debug("cursor start: component setting cursor: "
				+ component.getClass().getName());
		component.setCursor(hourglassCursor);
		cm.start();
	}

	/*
	 * Turns the cursor from wait mode back to normal. The CursorMonitor calls
	 * this method after the finished flag is set or if the monitor/computation
	 * encounters an exception.
	 * 
	 * Creates a new monitor to be associated with this cursor (the old monitor
	 * has finished its job and is off the thread queue now). Enables all the
	 * components linked to this cursor.
	 * 
	 * This method throws a runtime exception if:
	 * 
	 * 1) There is no component to which the cursor is set (i.e.
	 * setAssociatedComponent() has not been called)
	 * 
	 * 2) The cursor has not been started.
	 * 
	 */
	private void stop() {
		if (component == null) {
			throw new RuntimeException(
					"Cannot stop geWorkbench cursor: No component associated with Cursor.");
		}
		if (!started) {
			throw new RuntimeException(
					"Cannot stop geWorkbench cursor: Cursor was not started.");
		}
		if ((components != null) && (components.length > 0)) {
			log.debug("Enabling components...");
			for (JComponent c : components) {
				if (c != null) {
					log.debug("component=" + c.getClass().getName());
					c.setEnabled(true);
				}
			}
			component.repaint();
		}
		started = false;
		finished = true;
		cm = new CursorMonitor(this);
		log.debug("cursor stop: component setting cursor: "
				+ component.getClass().getName());
		component.setCursor(normalCursor);
	}

	/*
	 * This thread class monitors progress for the cursor. If the cursor runs
	 * beyond its maximum wait time, the monitor forcibly changes the cursor
	 * back to normal and re-enable all its linked components. This class should
	 * be used by org.geworkbench.util.Cursor only.
	 */
	class CursorMonitor extends Thread {
		private static final long THREAD_SLEEP_INTERVAL = 100; // in ms

		// to keep track of how long this cursor has been in wait mode
		private long startTime;

		// to keep track of how long this cursor has been in wait mode
		private long currentTime;

		// org.geworkbench.util.Cursor associated with this monitor
		private Cursor cursor;

		public CursorMonitor() {
			startTime = 0;
			currentTime = 0;
		}

		public CursorMonitor(Cursor c) {
			this();
			this.cursor = c;
		}

		/*
		 * Only used by org.geworkbench.util.Cursor.
		 */
		Cursor getCursor() {
			return this.cursor;
		}

		/*
		 * Only used by org.geworkbench.util.Cursor.
		 */
		void setCursor(Cursor c) {
			this.cursor = c;
		}

		/*
		 * This thread checks the finished flag periodically to see if the
		 * calculations has finished. It also checks to see how long the cursor
		 * has been waiting. If the cursor has been waiting past the maximum
		 * wait time, then the cursor is forcibly stopped. Also, if the monitor
		 * encounters any exception and the cursor is still running, the cursor
		 * is also forcibly stopped.
		 */
		public void run() {
			startTime = System.currentTimeMillis();
			try {
				while (!this.cursor.isFinishedSet()) {
					this.sleep(THREAD_SLEEP_INTERVAL);
					currentTime = System.currentTimeMillis();
					log.debug("mc: cursor not finished...");
					if ((currentTime - startTime) >= MAX_WAIT_TIME) {
						log.debug("mc: cursor timed out!");
						startTime = 0;
						currentTime = 0;
						this.cursor.setFinished(true);
					}
				}
				log.debug("mc: cursor finished!");
				startTime = 0;
				currentTime = 0;
				this.cursor.stop();

			} catch (Exception e) {
				startTime = 0;
				currentTime = 0;
				if (started)
					this.cursor.stop();
				log.error("Cannot properly run the cursor monitor: "
						+ e.getMessage());
				return;
			}
		}
	}
}
