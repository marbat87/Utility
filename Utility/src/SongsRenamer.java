import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class SongsRenamer {

	public static void main(String[] args) {

		File fXmlFile = new File("C:/android/workspace/Utility/songs_uk/sorgenti.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		//			Document doc = dBuilder.parse(fXmlFile);
		Document doc = null;
		try {
			doc = dBuilder.parse(new FileInputStream(fXmlFile), "utf-8");
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		catch (SAXException f) {
			f.printStackTrace();
			return;
		}

		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		System.out.println(doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getDocumentElement().getChildNodes();
		System.out.println("elementi: " + nList.getLength());

		BufferedWriter bw = null;

		try {

			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(
							"C:/android/workspace/Utility/songs_uk_new/sorgenti.xml"), "utf-8"));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		catch (UnsupportedEncodingException f) {
			f.printStackTrace();
			return;
		}

		try {
			bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			bw.newLine();
			bw.write("<resources>");
			bw.newLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}

		for (int i = 0; i < nList.getLength(); i++)  {

			Node nNode = nList.item(i);
			System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				boolean done = false;

				Element eElement = (Element) nNode;

				System.out.println("tag: " + eElement.getAttribute("name"));
				System.out.println("contenuto: " + eElement.getTextContent());

				String newName = eElement.getAttribute("name").replace("_source", "_uk") + ".htm";

				BufferedReader reader = null;
				BufferedWriter writer = null;
				try {

					reader = new BufferedReader(
							new InputStreamReader(
									new FileInputStream(
											"C:/android/workspace/Utility/songs_uk/"
													+ eElement.getTextContent()
													+ ".htm"), "utf-8"));

					writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(
									"C:/android/workspace/Utility/songs_uk_new/"
											+ newName), "utf-8"));
				}
				catch (FileNotFoundException e) {
					System.out.println("KO");
					e.printStackTrace();
					done = true;
				}
				catch (UnsupportedEncodingException f) {
					f.printStackTrace();
					done = true;
				}

				if (!done) {
					try {
						String line = reader.readLine();
						while (line != null) {
							writer.write(line);
							writer.newLine();
							line = reader.readLine();
						}

						writer.close();
						reader.close();
						String temp = "<string name=\""
								+ eElement.getAttribute("name")
								+ "\">"
								+ newName.replace(".htm", "")
								+ "</string>";
						bw.write(temp);
						System.out.println(temp);
						bw.newLine();

					}
					catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
				else {
					try {
						String temp = "<string name=\""
								+ eElement.getAttribute("name")
								+ "\">no_canto</string>";
						bw.write(temp);
						System.out.println(temp);
						bw.newLine();

					}
					catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}

		}

		try {
			bw.write("</resources>");
			bw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}

	}

}
