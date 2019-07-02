package org.johnmatzakos.xmlutil;

/**
 * @author Ioannis Matzakos
 * @date 28-June-2019
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

public class XMLUtil {
	
	private String filename;
	
	//constructor
	public XMLUtil() {
		this.filename = "";
	}
	
	//setters and getters
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	//read an xml file
	public File readXML(String path) {
		this.setFilename(path);
		File xmlFile = new File(this.getFilename());
		return xmlFile;
	}
	
	//print the values of the fields contained from a particular xml node
	public void printXMLNodes(String nodeName) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(this.getFilename());
		try {
        	Document document = (Document) builder.build(xmlFile);
    		Element rootNode = document.getRootElement();
    		List list = rootNode.getChildren(nodeName);
    		System.out.println("Catalog: \n");
    		for (int i = 0; i < list.size(); i++) {
    			
    			Element node = (Element) list.get(i);
    		
    			System.out.println("Title: " + node.getChildText("TITLE"));
    			System.out.println("Artist: " + node.getChildText("ARTIST"));
    			System.out.println("Country: " + node.getChildText("COUNTRY"));
    			System.out.println("Price: " + node.getChildText("PRICE"));
    			System.out.println("Year: " + node.getChildText("YEAR"));
    			System.out.println();
    		} 
		}catch (JDOMException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	//sort xml nodes based on a particular field
	public void sortXML(String nodeName) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(this.getFilename());
        	
		try {
			Document document = (Document) builder.build(xmlFile);
	    	Element rootElement = document.getRootElement();
		    // Pass the Comparator for sorting the elements
		    rootElement.sortChildren(new ElementComparator());
		    XMLOutputter xmlOutput = new XMLOutputter();
		    // display xml
		    xmlOutput.setFormat(Format.getPrettyFormat());
		    //xmlOutput.output(document, System.out);
		    //store sorted xml to a new file
		    xmlOutput.output(document, new FileOutputStream("cd_catalog1_SORTED.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//convert an xml file into a string
	public String xmlToString() throws SAXException, IOException, ParserConfigurationException {
        File xmlFile = new File(this.getFilename());
        
        //Convert an xml file into a String using BufferedReader
        Reader fileReader = new FileReader(xmlFile);
        BufferedReader bufReader = new BufferedReader(fileReader);
        
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while( line != null){
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }
        String xml2String = sb.toString();
        //System.out.println("XML to String using BufferedReader : ");
        //System.out.println(xml2String);
        
        bufReader.close();
        return xml2String;
	}
	
	//extract a substring from a stringified xml file
	public String extractXML(String regex, String string) {	
		
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);
        String extract = new String();
	    while (matcher.find()) {
	    	//System.out.println("Full match: " + matcher.group(0));
            extract = matcher.group(0);
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("Group " + i + ": " + matcher.group(i));
            }
	    }       
		return extract;
	}
	
	//turns the converted xml to string into one line by removing white spaces and tabs
	public String linefy(String string) {
		//remove all white spaces and non visible characters
        string = string.replaceAll("\\s+","");
		//remove indentation bring everything to the same level
        string = string.replaceAll("\t", "");
        return string;
	}
	
	//creates an xml file from an input string
	public void createXML(String str, String desired_filename) {
		try {
			FileWriter fw = new FileWriter(desired_filename);
	        fw.write(str);    
	        fw.close();   
		} catch (IOException e) {
			System.out.println("Failure..."); 
			e.printStackTrace();
		}    
		System.out.println("Success... " + desired_filename + " was created."); 
	}
	
	//compare two sets of nodes of an xml document
	public void compareXML(String xml1, String xml2) {

		Diff diff =  DiffBuilder.compare(xml2).withTest(xml1)
		    .ignoreWhitespace()
		    .normalizeWhitespace()
		    .withNodeMatcher(new DefaultNodeMatcher(
		        ElementSelectors.conditionalBuilder()
		            .whenElementIsNamed("CD")
		                .thenUse(ElementSelectors.byXPath("./ARTIST", ElementSelectors.byNameAndText))
		            .elseUse(ElementSelectors.byName)
		            .build()
		    ))
		    .withComparisonController(ComparisonControllers.StopWhenSimilar)
		    .checkForSimilar()
		    .build();

		boolean hasDifferences = diff.hasDifferences();
		System.out.println("Result: " + hasDifferences);
		if(hasDifferences == true) {
			System.out.println("Differences: \n" + diff.getDifferences().toString());
		}
		else {
			System.out.println("There are no differences.");
		}
	}
	
	public static void main( String[] args )
    {
		System.out.println("Welcome to XMLUtil! \n");
        try {
        	XMLUtil xml1 = new XMLUtil();
            xml1.setFilename("cd_catalog1.xml");
            xml1.printXMLNodes("CD");
            xml1.sortXML("CD");
            
            XMLUtil xml2 = new XMLUtil();
            xml2.setFilename("cd_catalog1_SORTED.xml");
            xml2.printXMLNodes("CD");
            
            XMLUtil xml3 = new XMLUtil();
            xml3.compareXML(xml1.xmlToString(), xml2.xmlToString());
            String regex ="(?s)(?<=<CATALOG>).*?(?=<\\/CATALOG>)";
            String ext1 = xml3.extractXML(regex, xml1.xmlToString());
            xml3.createXML(ext1, "ext1_EXTRACTED.xml");
            xml3.createXML(xml3.linefy(ext1), "ext1_LINEFIED.xml");
            System.out.println("XML Extract: \n" + ext1);
            String ext2 = xml3.extractXML(regex, xml2.xmlToString());
            System.out.println("XML Extract: \n" + ext2);
            xml3.createXML(ext2, "ext2_EXTRACTED.xml");
            xml3.createXML(xml3.linefy(ext2), "ext2_LINEFIED.xml");
            
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}        
    } 
}

