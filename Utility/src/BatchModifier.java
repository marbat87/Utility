import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class BatchModifier {

	public static void main(String[] args) {
			
		final File folder = new File("C:/android/workspace/Utility/new_songs");
//		final File folder = new File("C:/Java/android/workspace/Utility/new_files");
//		final File folder = new File("C:/Java/workspace/Utility/files");
		System.out.println("INIZIO!");
		for (final File input : folder.listFiles()) {
	       try
	        {
	   			ArrayList<String> lines = new ArrayList<String>();
	   			String line = null;
//	            FileReader fr = new FileReader(input);
//	            BufferedReader br = new BufferedReader(fr);
	            BufferedReader br = new BufferedReader(
	            		new InputStreamReader(  
                        new FileInputStream(input), "UTF-8")); 
	            line = br.readLine();
	            while (line != null)
	            {
	            	line = line.replaceAll("#FF0000", "#A13F3C");
	                lines.add(line);
	                line = br.readLine();
	            }
//	            fr.close();
	            br.close();
	            
	            
//	            FileWriter fw = new FileWriter(input);
//	            BufferedWriter out = new BufferedWriter(fw);
	            BufferedWriter out = new BufferedWriter(
	            		new OutputStreamWriter(
	            		new FileOutputStream(input), "UTF-8"));
	            System.out.println("TITOLO:" + input.getName());
	            for(String s : lines) {
	            	System.out.println(s);
	                 out.write(s);
	                 out.newLine();
	            }
	            out.flush();
	            out.close();
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
		}
		
		System.out.println("FINE!");
		
	}
	
}
