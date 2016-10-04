package com.sachin.fileutility;

import java.io.FileWriter;
import java.util.Arrays;

/**
 *  This is a test Class
 *  
 * @author sachin
 *
 */
public class CSVUtilExample {
	
	public static void main(String[] args) throws Exception {

		String outputFile = "/home/sachin/Desktop/FreeLancerProjects/ExtractURLFromXMLFile/url.txt"; 
        FileWriter writer = new FileWriter(outputFile);

        CSVUtils.writeLine(writer, Arrays.asList("a", "b", "c", "d"));

        //custom separator + quote
        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ',', '"');

        //custom separator + quote
        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc,c"), '|', '\'');

        //double-quotes
        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc\"c"));


        writer.flush();
        writer.close();

    }

}



