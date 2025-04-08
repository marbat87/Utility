import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class NewTagRemover {

    public static void main(String[] args) {

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
//                    new FileOutputStream("C:/Users/m.battain/StudioProjects/Utility/Utility/fileout.xml"), StandardCharsets.UTF_8));
//                    new FileOutputStream("C:/Users/m.battain/StudioProjects/Utility/Utility/fileout_en.xml"), StandardCharsets.UTF_8));
//					new FileOutputStream("C:/Users/m.battain/StudioProjects/Utility/Utility/fileout_pl.xml"), StandardCharsets.UTF_8));
//            new FileOutputStream("C:/Users/m.battain/StudioProjects/Utility/Utility/fileout_tr.xml"), StandardCharsets.UTF_8));
//							    	new FileOutputStream("C:/Users/marba/git/Utility/Utility/fileout_new.xml"), "utf-8"));
//					new FileOutputStream("C:/Users/m.battain/StudioProjects/Utility/Utility/fileout_en_PH.xml"), StandardCharsets.UTF_8));
					    new FileOutputStream("C:/Users/m.battain/StudioProjects/Utility/Utility/fileout_uk.xml"), StandardCharsets.UTF_8));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.newLine();
            writer.write("<canti>");
            writer.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //		final File folder = new File("C:/Java/android/workspace/Utility/new_files");
        //		final File folder = new File("C:/android/workspace/Utility/new_files");
        //		final File folder = new File("C:/android/workspace/Utility/new_songs");
//        final File folder = new File("C:/Users/m.battain/StudioProjects/Utility/Utility/songs_extended");
//		final File folder = new File("C:/Users/marba/git/Utility/Utility/canti_it_2020");
//		final File folder = new File("C:/Users/m.battain/StudioProjects/Utility/Utility/songs_en");
//		final File folder = new File("C:/Users/m.battain/StudioProjects/Utility/Utility/songs_en_PH");
//        final File folder = new File("C:/Users/m.battain/StudioProjects/Utility/Utility/songs_pl");
//        final File folder = new File("C:/Users/m.battain/StudioProjects/Utility/Utility/songs_tr");
				final File folder = new File("C:/Users/m.battain/StudioProjects/Utility/Utility/songs_uk_new");
        for (final File input : Objects.requireNonNull(folder.listFiles())) {

            //			String sFileName = input.getName().trim().replaceAll(".htm", "");
            String sFileName = input.getName().trim();
            sFileName = sFileName.replaceAll("_ii_", "_II_");
            System.out.println(sFileName);
            //			sFileName = sFileName.substring(0, sFileName.length()-3);
            //			sFileName = sFileName.substring(0, sFileName.length());

            ArrayList<String> lines = new ArrayList<>();
            String line;
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(input), StandardCharsets.UTF_8));

                line = br.readLine();
                while (line != null) {
                    System.out.println(line);
                    //	            	System.out.println(line);
                    if (line.startsWith("</FONT><FONT COLOR=\"#000000\">")
                            && !line.startsWith("</FONT><FONT COLOR=\"#A13F3C\">")
                            && !line.contains("BGCOLOR")
                            && !line.contains("Barrè")) {
//						line = line.replaceAll("<H4>", "");
//						line = line.replaceAll("</H4>", "");
//						line = line.replaceAll("<FONT COLOR=\"#000000\">", "");
//						line = line.replaceAll("<FONT COLOR=\"#A13F3C\">", "");
//						line = line.replaceAll("</FONT>", "");
//						line = line.replaceAll("<H5>", "");
//						line = line.replaceAll("<H3>", "");
//						line = line.replaceAll("<H2>", "");
//						line = line.replaceAll("</H5>", "");
//						line = line.replaceAll("</H3>", "");
//						line = line.replaceAll("</H2>", "");
//						line = line.replaceAll("<I>", "");
//						line = line.replaceAll("</I>", "");
//						line = line.replaceAll("<i>", "");
//						line = line.replaceAll("</i>", "");
//						line = line.replaceAll("<u>", "");
//						line = line.replaceAll("</u>", "");
//						line = line.replaceAll("<B>", "");
//						line = line.replaceAll("</B>", "");
//						line = line.replaceAll("<br>", "");
//						line = line.replaceAll("C\\+A\\.", "");
//						line = line.replaceAll("C\\.", "");
//						line = line.replaceAll("A\\.", "");
//						line = line.replaceAll("K\\+C\\.", "");
//						line = line.replaceAll("K\\.", "");
//						line = line.replaceAll("|", "");
//						System.out.println(line);
//						line = line.replaceAll("[^\\p{L}\\p{Z}]","");

                        for (Map.Entry<String, String> entry : STROKE_LETTERS_000000.entrySet()) {
                            line = line.replaceAll(entry.getKey(), entry.getValue());
                        }
                        line = line.replaceAll("[^\\p{L}\\p{Z}]", "");
                        System.out.println(line);

                        line = line.trim().toLowerCase();
                        String nfdNormalizedString = Normalizer.normalize(line, Normalizer.Form.NFD);
                        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                        line = pattern.matcher(nfdNormalizedString).replaceAll("");
                        System.out.println(line);
                        lines.add(line);
                    }

                    if (line.contains("A13F3C")
                            && line.contains("<H2>")) {
                        //	            		line = line.replaceAll("<H4>", "");
                        //	            		line = line.replaceAll("</H4>", "");
                        //	            		line = line.replaceAll("<FONT COLOR=\"#000000\">", "");
                        //	            		line = line.replaceAll("<FONT COLOR=\"#A13F3C\">", "");
                        //	            		line = line.replaceAll("</FONT>", "");
                        //	            		line = line.replaceAll("<H5>", "");
                        //	            		line = line.replaceAll("<H3>", "");
                        //	            		line = line.replaceAll("<H2>", "");
                        //	            		line = line.replaceAll("</H5>", "");
                        //	            		line = line.replaceAll("</H3>", "");
                        //	            		line = line.replaceAll("</H2>", "");
                        //	            		line = line.replaceAll("<I>", "");
                        //	            		line = line.replaceAll("</I>", "");
                        //	            		line = line.replaceAll("<u>", "");
                        //	            		line = line.replaceAll("</u>", "");
                        //	            		line = line.replaceAll("<B>", "");
                        //	            		line = line.replaceAll("</B>", "");
                        //	            		line = line.replaceAll("<br>", "");
                        //	            		line = line.replaceAll("<i>", "");
                        //	            		line = line.replaceAll("</i>", "");

                        for (Map.Entry<String, String> entry : STROKE_LETTERS_A13F3C.entrySet()) {
                            line = line.replaceAll(entry.getKey(), entry.getValue());
                        }
                        line = line.replaceAll("[^\\p{L}\\p{Z}\\p{Digit}]", " ");
                        System.out.println(line);
                        //	            		line = line.replaceAll("[^\\p{L}\\p{Z}\\p{Digit}]"," ");
                        //	            		line = line.replaceAll("[^\\p{L}\\p{Z}]","");
                        //	            		System.out.println(line);
                        line = line.trim().toLowerCase();
                        String nfdNormalizedString = Normalizer.normalize(line, Normalizer.Form.NFD);
                        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                        line = pattern.matcher(nfdNormalizedString).replaceAll("");
                        lines.add(line);
                    }

                    line = br.readLine();
                }
                br.close();

                StringBuilder textLine = new StringBuilder();

                for (String s : lines) {
                    //	            	System.out.println(s);
                    textLine.append(s).append(" ");
                }

                System.out.println(textLine);
                assert writer != null;
                writer.write("<canto>");
                writer.write("<titolo type=\"text\">" + sFileName + "_source</titolo>");
                writer.write("<testo type=\"text\">" + textLine.toString().trim() + "</testo>");
                writer.write("</canto>");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            assert writer != null;
            writer.write("</canti>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static final Map<String, String> STROKE_LETTERS_A13F3C = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("Ł", "L"),
            new AbstractMap.SimpleEntry<>("ł", "l"),
            new AbstractMap.SimpleEntry<>("<H4>", ""),
            new AbstractMap.SimpleEntry<>("</H4>", ""),
            new AbstractMap.SimpleEntry<>("<FONT COLOR=\\\"#000000\\\">", ""),
            new AbstractMap.SimpleEntry<>("<FONT COLOR=\\\"#A13F3C\\\">", ""),
            new AbstractMap.SimpleEntry<>("</FONT>", ""),
            new AbstractMap.SimpleEntry<>("<H5>", ""),
            new AbstractMap.SimpleEntry<>("<H3>", ""),
            new AbstractMap.SimpleEntry<>("<H2>", ""),
            new AbstractMap.SimpleEntry<>("</H5>", ""),
            new AbstractMap.SimpleEntry<>("</H3>", ""),
            new AbstractMap.SimpleEntry<>("</H2>", ""),
            new AbstractMap.SimpleEntry<>("<I>", ""),
            new AbstractMap.SimpleEntry<>("</I>", ""),
            new AbstractMap.SimpleEntry<>("<u>", ""),
            new AbstractMap.SimpleEntry<>("</u>", ""),
            new AbstractMap.SimpleEntry<>("<B>", ""),
            new AbstractMap.SimpleEntry<>("</B>", ""),
            new AbstractMap.SimpleEntry<>("<br>", ""),
            new AbstractMap.SimpleEntry<>("<i>", ""),
            new AbstractMap.SimpleEntry<>("</i>", ""));
//			new AbstractMap.SimpleEntry<String, String>("[^\\p{L}\\p{Z}\\p{Digit}]"," "));

    private static final Map<String, String> STROKE_LETTERS_000000 = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("Ł", "L"),
            new AbstractMap.SimpleEntry<>("ł", "l"),
            new AbstractMap.SimpleEntry<>("<H4>", ""),
            new AbstractMap.SimpleEntry<>("</H4>", ""),
            new AbstractMap.SimpleEntry<>("</FONT>", ""),
            new AbstractMap.SimpleEntry<>(">C\\+A.", ""),
            new AbstractMap.SimpleEntry<>(">C\\.A.", ""),
            new AbstractMap.SimpleEntry<>(">K\\+C\\.", ""),
            new AbstractMap.SimpleEntry<>(">C\\.", ""),
            new AbstractMap.SimpleEntry<>(">P\\.", ""),
            new AbstractMap.SimpleEntry<>(">A\\.", ""),
            new AbstractMap.SimpleEntry<>(">K\\.", ""),
            new AbstractMap.SimpleEntry<>(">W\\.", ""),
            new AbstractMap.SimpleEntry<>(">C\\:", ""),
            new AbstractMap.SimpleEntry<>(">M\\:", ""),
            new AbstractMap.SimpleEntry<>(">З\\.", ""),
            new AbstractMap.SimpleEntry<>("<FONT COLOR=\"#000000\">", ""),
            new AbstractMap.SimpleEntry<>("<FONT COLOR=\"#A13F3C\">", ""),
            new AbstractMap.SimpleEntry<>("<FONT COLOR=\"#000000\"", ""),
            new AbstractMap.SimpleEntry<>("<FONT COLOR=\"#A13F3C\"", ""),
            new AbstractMap.SimpleEntry<>("<H5>", ""),
            new AbstractMap.SimpleEntry<>("<H3>", ""),
            new AbstractMap.SimpleEntry<>("<H2>", ""),
            new AbstractMap.SimpleEntry<>("</H5>", ""),
            new AbstractMap.SimpleEntry<>("</H3>", ""),
            new AbstractMap.SimpleEntry<>("</H2>", ""),
            new AbstractMap.SimpleEntry<>("<I>", ""),
            new AbstractMap.SimpleEntry<>("</I>", ""),
            new AbstractMap.SimpleEntry<>("<u>", ""),
            new AbstractMap.SimpleEntry<>("</u>", ""),
            new AbstractMap.SimpleEntry<>("<B>", ""),
            new AbstractMap.SimpleEntry<>("</B>", ""),
            new AbstractMap.SimpleEntry<>("<br>", ""),
            new AbstractMap.SimpleEntry<>("<i>", ""),
            new AbstractMap.SimpleEntry<>("</i>", ""),

            new AbstractMap.SimpleEntry<>("|", ""));
//			new AbstractMap.SimpleEntry<String, String>("[^\\p{L}\\p{Z}\\p{Digit}]"," "));


}
