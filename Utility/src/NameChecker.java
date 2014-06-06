import java.io.File;


public class NameChecker {

	public static void main(String[] args) {
				
		final File folder = new File("C:/Java/android/workspace/Utility/files");
		String[] oldNames = new String[folder.listFiles().length];
		int i = 0;
//		final File folder = new File("C:/Java/workspace/Utility/files");
		for (final File input : folder.listFiles()) {
			String sFileName = input.getName().trim().replaceAll(".html", "");
			System.out.println("VECCHIO N°" + (i+1) + ": " + sFileName);
			oldNames[i++] = sFileName;
		}
		
		final File newFolder = new File("C:/Java/android/workspace/Utility/new_files");
		String[] newNames = new String[newFolder.listFiles().length];
		i = 0;
//		final File folder = new File("C:/Java/workspace/Utility/files");
		for (final File input : newFolder.listFiles()) {
			String sFileName = input.getName().trim().replaceAll(".htm", "");
			System.out.println("NUOVO N°" + (i+1) + ": " + sFileName);
			newNames[i++] = sFileName;
		}
		
		i = 0;
		int j = 0;
		
		while (i < oldNames.length && j < newNames.length) {
			if (oldNames[i].compareToIgnoreCase(newNames[j]) == 0) {
				i++;
				j++;
			}
			else {
				if (oldNames[i].compareToIgnoreCase(newNames[j]) < 0) {
					System.out.println("NON PRESENTE NEI NUOVI: " + oldNames[i]);
					i++;
				}
				else {
					System.out.println("NON PRESENTE NEI VECCHI: " + newNames[j]);
					j++;
				}
			}
			
		}
		
	}
	
}
