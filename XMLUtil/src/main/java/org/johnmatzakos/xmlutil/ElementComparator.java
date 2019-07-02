package org.johnmatzakos.xmlutil;

/**
 * @author Ioannis Matzakos
 * @date 28-June-2019
 */

import java.util.Comparator;

import org.jdom2.Element;

public class ElementComparator implements Comparator<Element>{

	  public int compare(Element e1, Element e2) {
	         int retVal = 0;
	         retVal = e1.getChild("ARTIST").getText().compareTo(e2.getChild("ARTIST")
	                  .getText());
	         if (retVal == 0) {
	            retVal = e1.getChild("TITLE").getText().compareTo(e2.getChild("TITLE")
	                     .getText());
	         }
	        return retVal;
	    }
	  
}
