package nl.marisabel.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NoteUtil {
 private static final String FILE_PATH = "data/note.txt";

 public static void appendToFile(String text) throws IOException {
  BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false));
  writer.append(' ');
  writer.append(text);
  writer.newLine();
  writer.close();
 }

 public static String readNote() {
  StringBuilder content = new StringBuilder();
  try {
   BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
   String line;
   while ((line = reader.readLine()) != null) {
    content.append(line).append("<br>");
   }
   reader.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
  return content.toString();
 }

}
