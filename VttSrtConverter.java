import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class VttSrtConverter {
    public static void main(String[] args) {
        String input;
        System.out.println("Type \"exit\" to quite");
        do {

            //input file from user
            Scanner in = new Scanner(System.in);
            System.out.println("What is the filename? ");
            input = in.nextLine();
            //check if there are quotation marks (start end), and removes it.
            if ((input.charAt(0) == '"') && (input.charAt(input.length() - 1) == '"')) {
                input = input.substring(1, input.length() - 1);
            }
            if (input.equals("exit")) {
                break;
            }

            //copy all lines of the file in a List
            ArrayList<String> lines = new ArrayList<>();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            clearTop(lines);

            ArrayList<String> clearedLines = swapLines(clearLines(lines));

            //Set new name for output converted file
            String newFileName = input;
            if (input.contains(".vtt")) {
                newFileName = input.replace(".vtt", ".srt");
            } else if (input.contains(".srt")) {
                newFileName = input.replace(".srt", "fixed.srt");
            }

            //create a new file and check if the name already exists
            File f = new File(newFileName);
            if (f.exists() && !f.isDirectory()) {
                System.out.println("The file already exists, delete it and try again");

            } else {
                //write down the new clearedList
                try {
                    BufferedWriter convertedFile = Files.newBufferedWriter(Paths.get(newFileName));
                    for (String s : clearedLines) {
                        convertedFile.write(s + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } while(true);
    }

    //remove the top lines in the vtt file
    public static void clearTop(ArrayList<String> lines) {
        int lastLineToRemove = 0;
        for (String s : lines) {
            if (s.equals("1")) {
                break;
            }
            lastLineToRemove += 1;
        }
        lines.subList(0, lastLineToRemove).clear();
    }

    //output a new arraylist cleared
    public static ArrayList<String> clearLines(ArrayList<String> lines) {
        ArrayList<String> clearedLines = new ArrayList<>();
        for (String s : lines) {
            //delete unwanted strings from the lines
            if (s.contains("<c.white><c.mono_sans>") || s.contains("</c.mono_sans></c.white>")) {
                String clearLine = s.replaceAll("<c.white><c.mono_sans>", "");
                String clearLine2 = clearLine.replaceAll("</c.mono_sans></c.white>", "");
                clearedLines.add(clearLine2);
            } else if (s.contains("  position:")) {
                String[] splitString = s.split("  position:");
                clearedLines.add(splitString[0]);
            } else {
                clearedLines.add(s);
            }
        }
        return clearedLines;
    }

    public static ArrayList<String> swapLines(ArrayList<String> clearedLines) {
        //takes in input the output of clearedLines
        int indexTime = 1;
        for (String s : clearedLines) {
            if (indexTime < clearedLines.size() - 7) {
                if (s.length() > 28) {
                    //check the first part of (the time) if is the same swap lines
                    if ((clearedLines.get(indexTime).regionMatches(0, clearedLines.get(indexTime + 4), 0, 28)) &&
                            (clearedLines.get(indexTime).regionMatches(0, clearedLines.get(indexTime + 8), 0, 28))) {
                        Collections.swap(clearedLines, indexTime + 1, indexTime + 9);
                        Collections.swap(clearedLines, indexTime + 5, indexTime + 9);
                    } else if (clearedLines.get(indexTime).regionMatches(0, clearedLines.get(indexTime + 4), 0, 28)) {
                        Collections.swap(clearedLines, indexTime + 1, indexTime + 5);
                    }
                }
                //is the string contain the time update the index
                if (s.matches("[0-9]{2}:[0-9]{2}(.*)")) {
                    indexTime = indexTime + 4;
                }

            }

        }
        return clearedLines;
    }

}
