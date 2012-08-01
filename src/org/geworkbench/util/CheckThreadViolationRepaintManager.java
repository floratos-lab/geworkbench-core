package org.geworkbench.util;

import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * Adapted from the code from http://www.java2s.com/Code/Java/Swing-JFC/
 * DetectEventDispatchThreadruleviolations.htm
 * 
 * $Id$
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {
	// it is recommended to pass the complete check
	private boolean completeCheck = true;

	private WeakReference<JComponent> lastComponent;

	public CheckThreadViolationRepaintManager(boolean completeCheck) {
		this.completeCheck = completeCheck;
	}

	public CheckThreadViolationRepaintManager() {
		this(true);
	}

	public boolean isCompleteCheck() {
		return completeCheck;
	}

	public void setCompleteCheck(boolean completeCheck) {
		this.completeCheck = completeCheck;
	}

	public synchronized void addInvalidComponent(JComponent component) {
		checkThreadViolations(component);
		super.addInvalidComponent(component);
	}

	public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
		checkThreadViolations(component);
		super.addDirtyRegion(component, x, y, w, h);
	}

	private void checkThreadViolations(JComponent c) {
		if (!SwingUtilities.isEventDispatchThread()
				&& (completeCheck || c.isShowing())) {
			boolean repaint = false;
			boolean fromSwing = false;
			boolean imageUpdate = false;
			StackTraceElement[] stackTrace = Thread.currentThread()
					.getStackTrace();
			for (StackTraceElement st : stackTrace) {
				if (repaint && st.getClassName().startsWith("javax.swing.")) {
					fromSwing = true;
				}
				if (repaint && "imageUpdate".equals(st.getMethodName())) {
					imageUpdate = true;
				}
				if ("repaint".equals(st.getMethodName())) {
					repaint = true;
					fromSwing = false;
				}
			}
			if (imageUpdate) {
				// assuming it is java.awt.image.ImageObserver.imageUpdate(...)
				// image was asynchronously updated, that's ok
				return;
			}
			if (repaint && !fromSwing) {
				// no problems here, since repaint() is thread safe
				return;
			}
			// ignore the last processed component
			if (lastComponent != null && c == lastComponent.get()) {
				return;
			}
			lastComponent = new WeakReference<JComponent>(c);
			violationFound(c, stackTrace);
		}
	}

	protected void violationFound(JComponent c, StackTraceElement[] stackTrace) {
		System.out.println();
		System.out.println("EDT violation detected");
		System.out.println(c);
		for (StackTraceElement st : stackTrace) {
			System.out.println("\tat " + st);
		}
	}
}
