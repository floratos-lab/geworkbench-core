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
	
	private static final long serialVersionUID = 1L;
	public static final int MODAL_TYPE = 0;
	public static final int NONMODAL_TYPE = 1;
	private static Vector<ProgressTask<?,?>> modaltasks = new Vector<ProgressTask<?,?>>();
	private static Vector<ProgressTask<?,?>> nonmodaltasks = new Vector<ProgressTask<?,?>>();
	private Vector<ProgressTask<?,?>> tasks = new Vector<ProgressTask<?,?>>();
	private static ProgressDialog modalProgressDialog = new ProgressDialog(MODAL_TYPE);
	private static ProgressDialog nonmodalProgressDialog = new ProgressDialog(NONMODAL_TYPE);
    private JPanel jp = null;
    private GridLayout gl = null;
    private JScrollPane jsp = null;

    public static ProgressDialog create(int type) {
        if (type == MODAL_TYPE)         return modalProgressDialog;
        else if (type == NONMODAL_TYPE) return nonmodalProgressDialog;
        return null;
    }

    private ProgressDialog(int type){
        if (type == MODAL_TYPE) {
            setModal(true);
            tasks = modaltasks;
        }
        else if (type == NONMODAL_TYPE) {
            setModal(false);
            tasks = nonmodaltasks;
        }

        this.setTitle("Progress");
    	gl = new GridLayout(0, 1); 
        jp = new JPanel(gl);
    	jsp = new JScrollPane(jp);
        this.add(jsp);

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

	/*
	 * add progress bar to progress dialog
	 * execute SwingWorker task
	 * update tasks list
	 * show progress dialog
	 */
	public void executeTask(ProgressTask<?,?> task){
		if (task == null) return;

		ProgressItem pb = task.getProgressItem();
		jp.add(pb);
		this.pack();

		task.execute();
		
		tasks.add(task);
        if (!isActive()||!isVisible())  start();
	}

	/*
	 * remove progress bar from progress dialog
	 * update tasks list 
	 * hide progress dialog if no bars in it
	 */
	public void removeTask(ProgressTask<?,?> task){
		if (task == null) return;

		ProgressItem pb = task.getProgressItem();
		jp.remove(pb);
		repaint();

		if (tasks.contains(task)) tasks.remove(task);
		if (tasks.size() == 0) stop();
	}

    public void setTitle(String title) {
        super.setTitle(title);
    }

    /**
     * Hides the <code>ProgressDialog</code>
     */
    public void dispose() {
        this.setTitle("");
        this.setVisible(false);
    }
    
    public void start() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
            (dim.width - this.getWidth()) / 2,
            (dim.height - this.getHeight()) / 2);
        this.setVisible(true);
        this.setTitle("Progress");
        //updateTo(jProgressBar.getMinimum());
    }

    public void stop() {
    	dispose();
    }

}