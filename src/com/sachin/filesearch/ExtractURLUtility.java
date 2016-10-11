package com.sachin.filesearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.sachin.xmlutility.XMLParser;

/**
 * Utility to search XML Files and extract URL
 * 
 * Things done : 1) Read the XML Files one by one and extract the URLs from the
 * files approx 900 XML Files : Done. Need to test for 900 files 2) Use Parser
 * to extract the URLs : Done 3) store the URLs in a List : Done 4) Print the
 * URLs in a textfile : Done. 5) Store the URLs in a delimited text file : Done
 * 
 * 
 * TODO : 1) Fix Delimitier for files which have multiple URLS. : Done 
 * 		  2) TEST :  test the no of URLs in a file approx 900
 * 		  3) Load config from Properties : DONE 
 * 		  4) Print all the URLs in a textfile. : DONE
 * 		  5) Print outputfile with time : DONE 
 * 		  6) Add Logging
 *        7) XML Files are 1390 globe.xml (with space)
 * 
 * 
 * 
 * things to Note : 1) XML Content may change 2) Memory Leaks
 * 
 * 
 * @author sachin
 *
 */

public class ExtractURLUtility {

	static int count = 0;
	// private String fileNameToSearch;
	private List<String> result = new ArrayList<String>();
	static XMLParser parser = new XMLParser();
	public static String filePath = "";
	public static String outputFilePath = "";
	public static String outputFileName = "";
	public static String fileExt = "";
	private static final Logger logger = Logger.getLogger( ExtractURLUtility.class.getName() );
	private static FileHandler fh = null;

	public static void loadConfig() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;
		fh = new FileHandler("ExtractURLUtilityLog.txt");
		logger.addHandler(fh);
		// Request that every detail gets logged.
        logger.setLevel(Level.ALL);
		logger.info("********** Loading Configurations **********");
		try {

			File jarPath = new File(
					ExtractURLUtility.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			 //Both Works
			// String propertiesPath=jarPath.getParentFile().getAbsolutePath();
			String propertiesPath = jarPath.getParent();
			logger.info("*** Config File found in Path: " + propertiesPath);
			prop.load(new FileInputStream(propertiesPath + "/config.properties"));
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {

			// get the property value and print it out
			filePath = prop.getProperty("filePath");
			outputFilePath = prop.getProperty("outputFilePath");
			fileExt = prop.getProperty("fileExt");

			if (StringUtils.isEmpty(filePath) && StringUtils.isEmpty(outputFilePath) && StringUtils.isEmpty(fileExt)) {
				filePath = "/data/web/absweb/ABS_EM/persistent";
				outputFilePath = "/data/web/absweb/ABS_EM/persistent";
				fileExt = ".txt";

			}

			// DateGeneratedFileName
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = dateFormat.format(cal.getTime());

			outputFileName = dateStr + prop.getProperty("outputFileName") + fileExt;

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public ExtractURLUtility() {
		try {
			ExtractURLUtility.loadConfig();
		} catch (Exception e) {
			logger.log(Level.SEVERE,"**** Exception caught : " + e.getMessage() + "****");
		}

	}
	public List<String> getResult() {
		return result;
	}

	public static void main(String[] args) {

		ExtractURLUtility fileSearch = new ExtractURLUtility();
		String fileName = "";
		fileSearch.searchDirectory1(new File(filePath));
		count = fileSearch.getResult().size();
		if (count == 0 || count < 1) {
			logger.info("\n ********* No URLs found in the XML Files! No Files outputted! ******** ");
		} else {
			logger.info("\n Total No of Files Found " + count); // Tested
			for (String matched : fileSearch.getResult()) {
				
				logger.info("*** XML FIle Found : " + count + " " + matched);
				fileName = matched;
				logger.info("*** Now Processing XML File : " + count + " " + filePath + File.separator + fileName);
				File file = new File(filePath + File.separator + fileName);
				parser.extractUrls(file);
				count = count + 1;
			}
			logger.info("****** URLs has been outputted to : " + filePath + " with total URLS : " + count + " ******* ");
		}
		

	}

	/**
	 * This method will call search1 to return a list of XML Files in that
	 * particular Directory
	 * 
	 * @param directory
	 * @param fileNameToSearch
	 */
	public List<String> searchDirectory1(File directory) {

		if (directory.isDirectory()) {
			return search1(directory);
		} else {
			logger.log(Level.WARNING,directory.getAbsoluteFile() + " is not a directory!");
			return null;
		}
	}

	/**
	 * this method will search the directory for XML files and store the
	 * filenames in a list
	 * 
	 * @param file
	 */
	private List<String> search1(File file) {

		if (file.isDirectory()) {
			logger.info("Searching directory for XML Files ... " + file.getAbsoluteFile());

			// do you have permission to read this directory?
			if (file.canRead()) {
				for (File temp : file.listFiles()) {
					if (temp.isDirectory()) { // If it is a directory, do a
												// recursive Search
						logger.info("********" + temp + " is a Directory. Doing a Recursive Search ********");
						search1(temp);
					} else {
						// Add only XML FIles
						if (temp.isFile() && getFileExtensionName(temp).indexOf("xml") != -1) {
							logger.info("*** Adding File to the list :" + temp.getName() + "***");
							// result.add(temp.getAbsoluteFile().toString());
							// //Add the files
							result.add(temp.getName()); // Add the files
						}

					}
				}

			} else {
				logger.log(Level.WARNING,file.getAbsoluteFile() + "Permission Denied");
			}

			logger.info(" List of XML FILES found : " + result.toString());

		}

		return result;

	}

	// TODO Use this Method if not able to pick up all the XML files
	public static File[] getXMLFiles(File folder) {
		List<File> aList = new ArrayList<File>();

		File[] files = folder.listFiles();
		for (File pf : files) {

			if (pf.isFile() && getFileExtensionName(pf).indexOf("xml") != -1) {
				aList.add(pf);
			}
		}
		return aList.toArray(new File[aList.size()]);
	}

	public static String getFileExtensionName(File f) {
		if (f.getName().indexOf(".") == -1) {
			return "";
		} else {
			return f.getName().substring(f.getName().length() - 3, f.getName().length());
		}
	}

}
