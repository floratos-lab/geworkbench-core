package org.geworkbench.engine.ccm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.preferences.GlobalPreferences;
import org.geworkbench.util.FilePathnameUtils;

/**
 * Manages the dynamic loading and removal of components.
 * 
 */
public class ComponentConfigurationManager2 {

	private static Log log = LogFactory.getLog(ComponentConfigurationManager2.class);
	
	ComponentConfigurationManager componentConfigurationManager = null;
	private static final String COMPONENT_DESCRIPTOR_EXTENSION = ".cwb.xml";
	List<File> cwbFiles = null;
	private String componentsDirectory = UILauncher.getComponentsDirectory();

	ComponentConfigurationManager2() {
		componentConfigurationManager = ComponentConfigurationManager.getInstance();
		cwbFiles = componentConfigurationManager.cwbFile;
	}
	
    private void updateCwbFileList(File newDir) throws IOException {
       File[] newFiles = newDir.listFiles();
        if(newFiles==null || newFiles.length==0) return;
        for (File newFile : newFiles) {
            if (newFile.isDirectory()) {
            	updateCwbFileList(newFile);
            } else {
                if (newFile.getName().endsWith(COMPONENT_DESCRIPTOR_EXTENSION)) {
                	
                	/* look for old components */
                	String newFileName = newFile.getName();
            		for (int i = 0; i<cwbFiles.size(); i++) {
            			File oldFile = cwbFiles.get(i);
            			String oldFileName = oldFile.getName();
            			if (newFileName.equals(oldFileName)){
            				cwbFiles.set(i, newFile);
            				break;
            			}
            		}
                }
            }
        }
    }

    File getCwbFile(String inFileName){
		for (int i = 0; i<cwbFiles.size(); i++) {
			File file = cwbFiles.get(i);
			String fileName = file.getName();
			if (inFileName.equals(fileName)){
				return file;
			}
		}
    	
    	return null; 
    }
	
	/*
	 * Download GEAR files from Tomcat server using apache's HttpClient
	 * 
	 * @param componentFolderName
	 * 
	 * @return boolean
	 */
	protected boolean downloadGEARFromServer(String componentFolderName) {

//remote_components_config_url=http://califano11.cgc.cpmc.columbia.edu:8080/componentRepository/deploycomponents.txt
//remote_components_url=http://califano11.cgc.cpmc.columbia.edu:8080/componentRepository/

		
//		String url = System.getProperty("remote_components_url");
		GlobalPreferences prefs = GlobalPreferences.getInstance();
		String url = prefs.getRCM_URL().trim();
		if (url == null || url == "") {
			log.info("No Remote Component Manager URL configured.");
			return false;
		}

		url += componentFolderName + ".gear";

		int beginIndex = url.lastIndexOf("/");
		String fileName = url.substring(beginIndex + 1);
		String tempFilePath = FilePathnameUtils
				.getTemporaryFilesDirectoryPath();

		String fullPathAndGEARName = tempFilePath + "\\components\\" + fileName;
		String fullComponentFolderName = tempFilePath + "\\components";
		File fullComponentFolder = new File(fullComponentFolderName);
		if (!fullComponentFolder.exists()) {
			fullComponentFolder.mkdirs();
		}

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(10, false));
		method.setFollowRedirects(true);

		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
				return false;
			}

			InputStream inputStream = method.getResponseBodyAsStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					inputStream);
			FileOutputStream fileOutputStream = new FileOutputStream(
					fullPathAndGEARName);

			byte[] bytes = new byte[8192];
			int count = bufferedInputStream.read(bytes);
			while (count != -1 && count <= 8192) {
				fileOutputStream.write(bytes, 0, count);
				count = bufferedInputStream.read(bytes);
			}
			if (count != -1) {
				fileOutputStream.write(bytes, 0, count);
			}
			fileOutputStream.close();
			bufferedInputStream.close();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		File fullPathAndNameFile = new File(fullPathAndGEARName);
		if (!fullPathAndNameFile.exists()) {
			return false;
		}

		return true;
	}

	

	/*
	 * unzip GEAR file from the users temp
	 * 
	 * @param componentFolderName
	 * 
	 * @return void
	 */
	void installGEAR(String componentFolderName, String version) {
		File componentFolder = null;
		
		try {
			String tempFilePath = FilePathnameUtils.getTemporaryFilesDirectoryPath();
			tempFilePath += "\\components";
			File gearFile = new File(tempFilePath + "\\" + componentFolderName + ".gear");
			FileInputStream fileInputStream = new FileInputStream(gearFile);
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(fileInputStream));
			String outFilePath = UILauncher.getComponentsDirectory();
			String fullComponentPath = outFilePath
					+ FilePathnameUtils.FILE_SEPARATOR + componentFolderName
					+ "." + version;
			try {
				componentFolder = new File(fullComponentPath);
				if (componentFolder.exists()) {
					JOptionPane.showMessageDialog(null,
							"Could not install component "
									+ fullComponentPath
									+ ", please delete directory try again.",
							"Remote Component Update",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				componentFolder.mkdir();
					
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Could not install component " + componentFolderName
								+ ", please restart geWorkbench",
						"Remote Component Update", JOptionPane.ERROR_MESSAGE);
				return;
			}

			FileOutputStream fileOutputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			final int BUFFER = 2048;
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				System.out.println("Extracting: " + zipEntry);
				int count;
				byte data[] = new byte[BUFFER];
				String zipEntryName = zipEntry.getName();
				zipEntryName.replaceFirst(zipEntryName, zipEntryName + "." + version);
				
				String fullPathAndName = fullComponentPath + FilePathnameUtils.FILE_SEPARATOR + zipEntryName;
				
				if (zipEntry.isDirectory()) {
					(new File(fullPathAndName)).mkdir();
					continue;
				}

				File outFile = new File(fullPathAndName);
				new File(outFile.getParent()).mkdirs();
				
				fileOutputStream = new FileOutputStream(outFile);
				bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER);
				while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
					bufferedOutputStream.write(data, 0, count);
				}
				fileOutputStream.flush();
				bufferedOutputStream.flush();
				fileOutputStream.close();
				bufferedOutputStream.close();
			}
			zipInputStream.close();
			fileInputStream.close();

			//TODO Uncoment this line to delete gear files from temp folder 
			//gearFile.delete();

			updateCwbFileList(componentFolder);
			
			
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Http Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "IO Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} finally {
			// Release the connection.
		}
	}

	
	
	/*
	 * Look for GEAR files to install If found, install them.
	 * 
	 * @param void
	 * 
	 * @return void
	 */
	public void installComponents() {

		String componentsDirName = componentsDirectory;
		File componentsDir = new File(componentsDirName);
		if (!componentsDir.isDirectory()) {
			log.error("Component resource path is not a directory: "
					+ componentsDirName);
			return;
		}

		String tempFilePath = FilePathnameUtils.getTemporaryFilesDirectoryPath();
		String gearPathName = tempFilePath + "\\components";
		File gearFolder = new File(gearPathName);
		File[] gearFiles = gearFolder.listFiles();
		for (int i = 0; i < gearFiles.length; i++) {
			File gearFile = gearFiles[i];
			if (gearFile.isDirectory()) {
				continue;
			}
			
			String gearFileName = gearFile.getName();
			if (!gearFileName.endsWith(".gear")) {
				continue;
			}

			String componentFolderName = gearFileName.replace(".gear", "");
			String fullComponentFolderName = componentsDir + "\\"
					+ componentFolderName;
			File oldComponentFolder = new File(fullComponentFolderName);
			if (oldComponentFolder.exists()) {
				try {
					recursiveDelete(oldComponentFolder);
					oldComponentFolder.delete();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							" There was a problem removing the old component "
									+ componentFolderName
									+ ". Please restart again.",
							"Remote Component Update",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if (oldComponentFolder.exists()) {
				JOptionPane
						.showMessageDialog(
								null,
								"The old component "
										+ componentFolderName
										+ " has not been completely removed. Please restart again.",
								"Remote Component Update",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			installComponent(componentFolderName);
			gearFile.delete();
		}
	}

	/*
	 * Delete old component folder and unzip GEAR file from the users temp
	 * directory into the components folder
	 * 
	 * @param componentFolderName
	 * 
	 * @return void
	 */
	private void installComponent(String componentFolderName) {

		try {

			String tempFilePath = FilePathnameUtils
					.getTemporaryFilesDirectoryPath();
			tempFilePath += "\\components";
			File gearFile = new File(tempFilePath + "\\" + componentFolderName + ".gear");
			FileInputStream fileInputStream = new FileInputStream(gearFile);
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(fileInputStream));
			String outFilePath = UILauncher.getComponentsDirectory();
			String fullComponentPath = outFilePath + FilePathnameUtils.FILE_SEPARATOR + componentFolderName;
			try {
				File componentFolder = new File(fullComponentPath);
				if (componentFolder.exists()) {
					JOptionPane.showMessageDialog(null,
							"Could not install component "
									+ componentFolderName
									+ ", please restart geWorkbench",
							"Remote Component Update",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				componentFolder.mkdir();

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Could not install component " + componentFolderName
								+ ", please restart geWorkbench",
						"Remote Component Update", JOptionPane.ERROR_MESSAGE);
				return;
			}

			FileOutputStream fileOutputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			final int BUFFER = 2048;
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				System.out.println("Extracting: " + zipEntry);
				int count;
				byte data[] = new byte[BUFFER];
				String zipEntryName = zipEntry.getName();
				String fullPathAndName = fullComponentPath
						+ FilePathnameUtils.FILE_SEPARATOR + zipEntryName;

				if (zipEntry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					(new File(fullPathAndName)).mkdir();
					continue;
				}

				fileOutputStream = new FileOutputStream(fullPathAndName);
				bufferedOutputStream = new BufferedOutputStream(
						fileOutputStream, BUFFER);
				while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
					bufferedOutputStream.write(data, 0, count);
				}
				fileOutputStream.flush();
				bufferedOutputStream.flush();
				fileOutputStream.close();
				bufferedOutputStream.close();
			}
			zipInputStream.close();
			fileInputStream.close();

			gearFile.delete();

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Http Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "IO Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} finally {
			// Release the connection.
		}
	}

	/*
	 * Recursively delete folders and files under the dirPath
	 * 
	 * TODO move this to a utility class
	 * 
	 * @param dirPath
	 * 
	 * @return void
	 */
	private void recursiveDelete(File dirPath) {
		String[] ls = dirPath.list();

		for (int idx = 0; idx < ls.length; idx++) {
			File file = new File(dirPath, ls[idx]);
			if (file.isDirectory()) {
				recursiveDelete(file);
			}

			file.delete();
		}
	}
}