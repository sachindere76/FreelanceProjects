package com.sachin.xmlutility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sachin.filesearch.ExtractURLUtility;
import com.sachin.filesearch.PatternClass;
import com.sachin.fileutility.CSVUtils;

/**
 * Parser to extract the URLs
 * @author sachin
 *
 */

public class XMLParser {

	ArrayList<String> listUrls = new ArrayList<String>();
	int totalCount             = 0; //URL Count	
	String outputFile          = null;
	
	public void extractUrls(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db         = dbf.newDocumentBuilder();			
			Document doc               = db.parse(file);
			Element docEle             = doc.getDocumentElement();
//			Print root element of the document
//			Once you get the Root Element, you can traverse through the
//			whole document
			System.out.println("*** Root element of the document: " + docEle.getNodeName() + "***");
				
			// TODO : Should be dynamic as the URLS can be anywhere in the file
			NodeList urlList = docEle.getElementsByTagName("ListingTeaser");
			System.out.println("**** Total ListingTeaser Node found : " + urlList.getLength() + "*****");
			if (urlList != null && urlList.getLength() > 0) {
					for (int i = 0; i < urlList.getLength(); i++) {						
						Node node   = urlList.item(i);						
						String text = node.getTextContent(); //ListingTeaser Node						
						// Returns a list with all links contained in the input
						listUrls    = (ArrayList<String>) PatternClass.extractUrls(text);						
					}					
					outputToFile(listUrls);			
			} else {
				System.out.println(" No URLS found in the XML File : " + file.getName());
			}
				
		} catch (IOException ie) {
			System.out.println("IO Exception caught : " + ie.getMessage() + " Please contact the author at sachindere76@gmail.com ");
		} catch (Exception e) {
			System.out.println("Exception caught : " + e.getMessage() + " Please contact the author at sachindere76@gmail.com ");
		} 
	}
	
	/**
	 * Update File instead
	 * @param data
	 * @throws IOException
	 */
//	public void outputToFile(Hashtable<Integer, String> data) throws IOException {
//		
//		if (!data.isEmpty()) {						
//			FileWriter writer = new FileWriter(outputFile,true); //File opened in Append Mode
//			//TODO Bug Here
//	        List<String> list = new ArrayList<String>(data.values());
//	        System.out.println(" Outputting to the file : " + list.toString()); //Bug Here
//	        //Open the File again for writing
//	        
//			CSVUtils.writeLine(writer, list);		
//		    System.out.println( outputFile + " written  with " + list.size() + " URLS"); // Bug Here
//		    //Very important to flush and close the file
//		    writer.flush();
//		    writer.close();
//		} else {
//			System.out.println(" Nothing to write ");
//		}
//
//	}

	public void outputToFile(ArrayList<String> data) throws IOException {
		
		if (!data.isEmpty()) {	
			if(StringUtils.isNotEmpty(ExtractURLUtility.outputFilePath) && StringUtils.isNotEmpty(ExtractURLUtility.fileExt)) {
				outputFile = ExtractURLUtility.outputFilePath+File.separator+ExtractURLUtility.outputFileName; 
			} else {
				 //DateGeneratedFileName
				outputFile = "data/web/absweb/ABS_EM/persistent/urls.txt"; //HardCoded
			}
			FileWriter writer = new FileWriter(outputFile,true); //File opened in Append Mode
			System.out.println(" Outputting to the file : " + data.toString()); 
	        CSVUtils.writeLine(writer, data);		
		    System.out.println( outputFile + " written  with " + data.size() + " URLS"); 
		    //Very important to flush and close the file
		    writer.flush();
		    writer.close();
		} else {
			System.out.println(" Nothing to write ");
		}

	}

	
	
}
