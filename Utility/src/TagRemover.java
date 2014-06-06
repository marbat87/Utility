import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class TagRemover {

	public static void main(String[] args) {
		
		BufferedWriter writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
//		        new FileOutputStream("C:/Java/android/workspace/Utility/fileout.xml"), "utf-8"));
		    	new FileOutputStream("C:/Java/workspace/Utility/fileout.xml"), "utf-8"));
		    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		    writer.newLine();
		    writer.write("<canti>");
		    writer.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
//		final File folder = new File("C:/Java/android/workspace/Utility/files");
		final File folder = new File("C:/Java/workspace/Utility/new_files");
		for (final File input : folder.listFiles()) {
		
			String sFileName = input.getName().trim().replaceAll(".html", "");
			System.out.println(sFileName);
			
			Document doc = null;
			try {
				doc = Jsoup.parse(input, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String text = doc.body().text();
			text = text.replaceAll("Do-", "");
			text = text.replaceAll("Re-", "");
			text = text.replaceAll("Mi-", "");
			text = text.replaceAll("Fa-", "");
			text = text.replaceAll("Sol-", "");
			text = text.replaceAll("Sol ", "");
			text = text.replaceAll("La-", "");
			text = text.replaceAll("Si-", "");
			text = text.replaceAll("Do7", "");
			text = text.replaceAll("Re7", "");
			text = text.replaceAll("Mi7", "");
			text = text.replaceAll("Fa7", "");
			text = text.replaceAll("Sol7", "");
			text = text.replaceAll("La7", "");
			text = text.replaceAll("Si7", "");
			text = text.replaceAll("Do#", "");
			text = text.replaceAll("Re#", "");
			text = text.replaceAll("Mi#", "");
			text = text.replaceAll("Fa#", "");
			text = text.replaceAll("Sol#-", "");
			text = text.replaceAll("La#", "");
			text = text.replaceAll("Si#", "");
			text = text.replaceAll("Do#-", "");
			text = text.replaceAll("Re#-", "");
			text = text.replaceAll("Mi#-", "");
			text = text.replaceAll("Fa#-", "");
			text = text.replaceAll("So#-", "");
			text = text.replaceAll("La#-", "");
			text = text.replaceAll("Si#-", "");
			text = text.replaceAll("Dod", "");
			text = text.replaceAll("Red", "");
			text = text.replaceAll("Mid", "");
			text = text.replaceAll("Fad", "");
			text = text.replaceAll("Sold", "");
			text = text.replaceAll("Lad", "");
			text = text.replaceAll("Sid", "");
			text = text.replaceAll("Dod-", "");
			text = text.replaceAll("Red-", "");
			text = text.replaceAll("Mid-", "");
			text = text.replaceAll("Fad-", "");
			text = text.replaceAll("Sold-", "");
			text = text.replaceAll("Lad-", "");
			text = text.replaceAll("Sid-", "");
			text = text.replaceAll("Dob", "");
			text = text.replaceAll("Reb", "");
			text = text.replaceAll("Mib", "");
			text = text.replaceAll("Fab", "");
			text = text.replaceAll("Solb", "");
			text = text.replaceAll("Lab", "");
			text = text.replaceAll("Sib", "");
			text = text.replaceAll("Dob-", "");
			text = text.replaceAll("Reb-", "");
			text = text.replaceAll("Mib-", "");
			text = text.replaceAll("Fab-", "");
			text = text.replaceAll("Solb-", "");
			text = text.replaceAll("Lab-", "");
			text = text.replaceAll("Sib-", "");
//			text = text.replaceAll("ò", "o");
//			text = text.replaceAll("&#242;", "o");
//			text = text.replaceAll("è", "e");
//			text = text.replaceAll("&#232;", "e");
//			text = text.replaceAll("é", "e");
//			text = text.replaceAll("&#233;", "e");
//			text = text.replaceAll("ì", "i");
//			text = text.replaceAll("&#236;", "i");
//			text = text.replaceAll("ù", "u");
//			text = text.replaceAll("&#249;", "u");
//			text = text.replaceAll("à", "a");
//			text = text.replaceAll("&#224;", "a");
//			text = text.replaceAll("á", "a");
//			text = text.replaceAll("&#225;", "a");
//			text = text.replaceAll("í", "i");
//			text = text.replaceAll("&#237;", "i");
//			text = text.replaceAll("ó", "o");
//			text = text.replaceAll("&#243;", "o");
//			text = text.replaceAll("ú", "u");
//			text = text.replaceAll("&#250;", "u");
//			text = text.replaceAll("È", "e");
//			text = text.replaceAll("&#200;", "e");
//			text = text.replaceAll("É", "e");
//			text = text.replaceAll("&#201;", "e");
			text = text.replaceAll("Dod7", "");
			text = text.replaceAll("Red7", "");
			text = text.replaceAll("Mid7", "");
			text = text.replaceAll("Fad7", "");
			text = text.replaceAll("Sold7", "");
			text = text.replaceAll("Lad7", "");
			text = text.replaceAll("Sid7", "");
			text = text.replaceAll("Dob7", "");
			text = text.replaceAll("Reb7", "");
			text = text.replaceAll("Mib7", "");
			text = text.replaceAll("Fab7", "");
			text = text.replaceAll("Solb7", "");
			text = text.replaceAll("Lab7", "");
			text = text.replaceAll("Sib7", "");
			text = text.replaceAll("[^\\p{L}\\p{Z}]","");
			text = text.trim().toLowerCase();
		    String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD); 
		    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		    text =  pattern.matcher(nfdNormalizedString).replaceAll("");
			
			System.out.println(text);
			try {
				writer.write("<canto>");
				writer.write("<titolo type=\"text\">" + sFileName + "</titolo>");
				writer.write("<testo type=\"text\">" + text + "</testo>");
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
