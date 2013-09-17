package org.geworkbench.util;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Implements a progress dialog to show progress of application tasks.
 * 
 * 2 static progress dialogs: modal & nonmodal;
 * execute ProgressTask: add progress bar, execute swing task, show dialog;
 * remove ProgressTask: remove progress bar, stop swing task, hide dialog if no tasks left
 * $Id$
 */

public class ProgressDialog extends JDialog {
	
	private static final long serialVersionUID = -1702567559426660298L;
	
	public static final int MODAL_TYPE = 0;
	public static final int NONMODAL_TYPE = 1;
	private static Vector<ProgressTask<?,?>> modaltasks = new Vector<ProgressTask<?,?>>();
	private static Vector<ProgressTask<?,?>> nonmodaltasks = new Vector<ProgressTask<?,?>>();

	private static ProgressDialog modalProgressDialog = new ProgressDialog(true);
	private static ProgressDialog nonmodalProgressDialog = new ProgressDialog(false);
    
	final private Vector<ProgressTask<?,?>> tasks;
	final private JPanel jp;

	public static ProgressDialog getInstance(boolean modal) {
		if (modal) {
			return modalProgressDialog;
		} else {
			return nonmodalProgressDialog;
		}
	}

    private ProgressDialog(boolean modal){
        if (modal) {
            setModal(true);
            tasks = modaltasks;
        } else {
            setModal(false);
            tasks = nonmodaltasks;
        }

        this.setTitle("Progress");
        jp = new JPanel(new GridLayout(0, 1));
        this.add(new JScrollPane(jp));

        this.addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e){
        		cancelAllTasks();
        	}
        });
	}

    /*
     * cancel all tasks associated with this dialog
     */
    public void cancelAllTasks(){
		Vector<ProgressTask<?,?>> cptasks = new Vector<ProgressTask<?,?>>(tasks);
		for (Iterator<ProgressTask<?,?>> it = cptasks.iterator(); it.hasNext();){
			ProgressTask<?,?> task = (ProgressTask<?,?>)it.next();
			if (task != null && !task.isDone()){
				task.cancel(true);
				task = null;
			}
		}    	
    }

    private int maxWidth = 0;

    // call this method to resize dialog if published msg is longer than maxWidth
    public void updateWidth(String msg){
    	if (msg.length() > maxWidth){
    		maxWidth = msg.length();
    		pack();
    	}
    }

	/*
	 * add progress bar to progress dialog
	 * execute SwingWorker task
	 * update tasks list
	 * show progress dialog
	 */
	public void executeTask(ProgressTask<?,?> task){
		if (task == null) return;

		task.addToPanel(jp);
		this.pack();

		task.execute();
		
		tasks.add(task);
        if (!isActive()||!isVisible())  {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(
                (dim.width - this.getWidth()) / 2,
                (dim.height - this.getHeight()) / 2);
            this.setVisible(true);
            this.setTitle("Progress");
        }
	}

	/*
	 * remove progress bar from progress dialog
	 * update tasks list 
	 * hide progress dialog if no bars in it
	 */
	public void removeTask(ProgressTask<?,?> task){
		if (task == null) return;

		task.removeFromPanel(jp);
		repaint();

		if (tasks.contains(task)) tasks.remove(task);
		if (tasks.size() == 0) {
			dispose();
		}
    	maxWidth = 0;
    	pack();
	}

    /**
     * Hides the <code>ProgressDialog</code>
     */
	@Override
    public void dispose() {
        this.setTitle("");
        this.setVisible(false);
    }

}