import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class NewTagRemover {

	public static void main(String[] args) {
		
		BufferedWriter writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
//		        new FileOutputStream("C:/Java/android/workspace/Utility/fileout.xml"), "utf-8"));
		    	new FileOutputStream("C:/android/workspace/Utility/fileout_new.xml"), "utf-8"));
		    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		    writer.newLine();
		    writer.write("<canti>");
		    writer.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
//		final File folder = new File("C:/Java/android/workspace/Utility/new_files");
		final File folder = new File("C:/android/workspace/Utility/new_files");
		for (final File input : folder.listFiles()) {
		
			String sFileName = input.getName().trim().replaceAll(".htm", "");
			System.out.println(sFileName);
		
			ArrayList<String> lines = new ArrayList<String>();
			String line = null;
 	       	try {
	            BufferedReader br = new BufferedReader(
	            		new InputStreamReader(  
	                    new FileInputStream(input), "UTF-8")); 

	            line = br.readLine();
	            while (line != null) {
//	            	System.out.println(line);
	            	if (line.contains("000000") 
	            	 && !line.contains("FF0000")
	            	 && !line.contains("BGCOLOR")) {
	            		line = line.replaceAll("<H4>", "");
	            		line = line.replaceAll("</H4>", "");
	            		line = line.replaceAll("<FONT COLOR=\"#000000\">", "");
	            		line = line.replaceAll("<FONT COLOR=\"#FF0000\">", "");
	            		line = line.replaceAll("</FONT>", "");
	            		line = line.replaceAll("<H5>", "");
	            		line = line.replaceAll("<H3>", "");
	            		line = line.replaceAll("<H2>", "");
	            		line = line.replaceAll("</H5>", "");
	            		line = line.replaceAll("</H3>", "");
	            		line = line.replaceAll("</H2>", "");
	            		line = line.replaceAll("<I>", "");
	            		line = line.replaceAll("</I>", "");
	            		line = line.replaceAll("<B>", "");
	            		line = line.replaceAll("</B>", "");
	            		line = line.replaceAll("<br>", "");
	            		line = line.replaceAll("C\\+A\\.", "");
	            		line = line.replaceAll("C\\.", "");
	            		line = line.replaceAll("A\\.", "");
	            		line = line.replaceAll("|", "");
	            		System.out.println(line);
	            		line = line.replaceAll("[^\\p{L}\\p{Z}]","");
	            		line = line.trim().toLowerCase();
	            		String nfdNormalizedString = Normalizer.normalize(line, Normalizer.Form.NFD); 
	            		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	            		line =  pattern.matcher(nfdNormalizedString).replaceAll("");
	            		System.out.println(line);
	            		lines.add(line);
	            	}
	            	
	            	if (line.contains("FF0000")
	            	 && line.contains("<H2>")) {
	            		line = line.replaceAll("<H4>", "");
	            		line = line.replaceAll("</H4>", "");
	            		line = line.replaceAll("<FONT COLOR=\"#000000\">", "");
	            		line = line.replaceAll("<FONT COLOR=\"#FF0000\">", "");
	            		line = line.replaceAll("</FONT>", "");
	            		line = line.replaceAll("<H5>", "");
	            		line = line.replaceAll("<H3>", "");
	            		line = line.replaceAll("<H2>", "");
	            		line = line.replaceAll("</H5>", "");
	            		line = line.replaceAll("</H3>", "");
	            		line = line.replaceAll("</H2>", "");
	            		line = line.replaceAll("<I>", "");
	            		line = line.replaceAll("</I>", "");
	            		line = line.replaceAll("<B>", "");
	            		line = line.replaceAll("</B>", "");
	            		line = line.replaceAll("<br>", "");
	            		System.out.println(line);
	            		line = line.replaceAll("[^\\p{L}\\p{Z}\\p{Digit}]"," ");
//	            		line = line.replaceAll("[^\\p{L}\\p{Z}]","");
//	            		System.out.println(line);
	            		line = line.trim().toLowerCase();
	            		String nfdNormalizedString = Normalizer.normalize(line, Normalizer.Form.NFD); 
	            		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	            		line =  pattern.matcher(nfdNormalizedString).replaceAll("");
	            		lines.add(line);
	            	}
	            		
	            	line = br.readLine();
	            }
	            br.close();
				
	            String textLine = "";
	            
	            for(String s : lines) {
//	            	System.out.println(s);
	                 textLine += s + " ";
	            }
	            
				System.out.println(textLine);
				writer.write("<canto>");
				writer.write("<titolo type=\"text\">" + sFileName + "</titolo>");
				writer.write("<testo type=\"text\">" + textLine.trim() + "</testo>");
				writer.write("</canto>");
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			writer.write("</canti>");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
