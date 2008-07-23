package org.geworkbench.util;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cursor {
	static Log log = LogFactory.getLog(Cursor.class);
	
	private static final long MAX_WAIT_TIME = 60000; // 1 min in ms
	
	private static Cursor cursor;
	
	private static JComponent component; // this is the component to which the cursor is set
	
	private static JComponent[] components; // this is the list of components to be disabled while the cursor is "waiting"
	
	private static final java.awt.Cursor hourglassCursor = new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR);

	private static final java.awt.Cursor normalCursor = new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR);
	
	private static boolean finished = false;
	
	private static boolean started = false;
	
	private static CursorMonitor cm = null;
	
	private Cursor(){
		cm = new CursorMonitor(this);
	}
	
	public JComponent getAssociatedComponent(){
		return component;
	}
	
	public void setAssociatedComponent(JComponent c){
		started = false;
		finished = false;
		component = c;
	}
	
	public JComponent[] getCursorLinkedComponents(){
		return components;
	}
	
	public void linkCursorToComponents(JComponent[] cs){
		components = cs;
	}
	
	public boolean isFinished(){
		return !cm.isAlive();
	}
	
	private boolean isFinishedSet(){
		return finished;
	}
	
	public void setFinished(boolean b){
		finished = b;
	}
	
	public boolean isStarted(){
		return started;
	}
	
	public static Cursor getCursor(){
		if(cursor == null){
			cursor = new Cursor();
		}
		return cursor;
	}
	
	public void start(){
		if(component == null){
			throw new RuntimeException("Cannot start geWorkbench cursor: No component associated with Cursor.");
		}
		if(started){
			throw new RuntimeException("Cannot start geWorkbench cursor: Cursor already started.");
		}
		if(cm.isAlive()){
			throw new RuntimeException("Cannot start geWorkbench cursor: Cursor is already running");
		}
		cm.start();		
		component.setCursor(hourglassCursor);
		if((components != null) && (components.length > 0)){
			log.debug("disabling components...");
			for(JComponent c: components){
				log.debug("component=" + c.getClass().getName());
				c.setEnabled(false);
			}
			component.repaint();
		}
		started = true;
		finished = false;
	}
	
	public void stop(){
		if(component == null){
			throw new RuntimeException("Cannot stop geWorkbench cursor: No component associated with Cursor.");
		}
		if(!started){
			throw new RuntimeException("Cannot stop geWorkbench cursor: Cursor was not started.");
		}
		if((components != null) && (components.length > 0)){
			log.debug("Enabling components...");
			for(JComponent c: components){
				log.debug("component=" + c.getClass().getName());
				c.setEnabled(true);
			}
			component.repaint();
		}
		component.setCursor(normalCursor);
		started = false;
		finished = true;
		cm = new CursorMonitor(this);
	}	
	
	class CursorMonitor extends Thread {		
		private static final long THREAD_SLEEP_INTERVAL = 100; //ms
		
		private long startTime;
		
		private long currentTime;
		
		private Cursor cursor;
		
		public CursorMonitor(){
			startTime = 0;
			currentTime = 0;
		}
		
		public CursorMonitor(Cursor c){
			this();
			this.cursor = c;			
		}
		
		Cursor getCursor(){
			return this.cursor;
		}
		
		void setCursor(Cursor c){
			this.cursor = c;
		}
		
		public void run(){
			startTime = System.currentTimeMillis();
			try{
				while(!this.cursor.isFinishedSet()){
					this.sleep(THREAD_SLEEP_INTERVAL);
					currentTime = System.currentTimeMillis();
					log.debug("mc: cursor not finished...");
					if((currentTime - startTime) >= MAX_WAIT_TIME){
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
				
			} catch (Exception e){
				startTime = 0;
				currentTime = 0;
				this.cursor.stop();
				log.error("Cannot properly run the cursor monitor: " + e.getMessage());
				return;
			}			
		}		
	}	
}
