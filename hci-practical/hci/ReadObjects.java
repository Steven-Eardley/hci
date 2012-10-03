package hci;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import hci.utils.Point;

public class ReadObjects {
	public void loadFile() {
		try {
			File fXmlFile = new File("/afs/inf.ed.ac.uk/user/s09/s0901522/hci/hci-practical/test.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("objectNodes");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				NodeList nListTwo = doc.getElementsByTagName("nodePoint");
				for (int tempNode = 0; tempNode < nListTwo.getLength(); tempNode++){
					Node nNode = nListTwo.item(tempNode);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						 
						Element eElement = (Element) nNode;
				 
						System.out.println("X: " + getTagValue("x", eElement));
						System.out.println("Y: " + getTagValue("y", eElement));
					    System.out.println("--------------------");
				 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			 
		Node nValue = (Node) nlList.item(0);
			 
		return nValue.getNodeValue();
	}
}
