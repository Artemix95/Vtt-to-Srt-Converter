import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class VttSrtConverter {
    public static void main(String[] args) {
        String input;
        System.out.println("Type \"exit\" to quit");
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


            //save the modified list and clear some white spaces
            String[] tempLines = joinLines(clearLines(lines));
            ArrayList<String> finalLines = new ArrayList<>();
            Collections.addAll(finalLines, tempLines);

            ArrayList<String> toDelete = new ArrayList<>();
            for (String s : finalLines) {
                if (s.isEmpty()) {
                    toDelete.add(s);
                }
            }
            finalLines.removeAll(toDelete);


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
                    for (String s : finalLines) {
                        convertedFile.write(s + "\n");

                    }
                    convertedFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } while (true);
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

    //output a new arraylist cleared from extra words and simbols
    public static ArrayList<String> clearLines(ArrayList<String> lines) {
        ArrayList<String> clearedLines = new ArrayList<>();
        for (String s : lines) {
            //delete unwanted strings from the lines and convert . in ,
            if (s.contains("<c.white><c.mono_sans>") || s.contains("</c.mono_sans></c.white>")) {
                String clearLine = s.replaceAll("<c.white><c.mono_sans>", "");
                String clearLine2 = clearLine.replaceAll("</c.mono_sans></c.white>", "");
                clearedLines.add(clearLine2);
            } else if (s.contains("  position:")) {
                String[] splitString = s.split("  position:");
                String dotToComma = splitString[0].replace('.', ',');
                clearedLines.add(dotToComma);
            } else {

                clearedLines.add(s);
            }
        }
        return clearedLines;
    }

    //join lines with the same time
    public static String[] joinLines(ArrayList<String> clearedLines) {
        //takes in input the output of clearedLines
        String[] tempArrayLines = new String[clearedLines.size()];
        tempArrayLines = clearedLines.toArray(tempArrayLines);


        for (int indexLine = 1; indexLine < tempArrayLines.length; indexLine++) {
            if (indexLine < clearedLines.size() - 3) {
                //is the string contain the time
                if (tempArrayLines[indexLine].matches("[0-9]{2}:[0-9]{2}(.*)")) {
                    if (indexLine < clearedLines.size()-9) {
                        //check the first part of (the time) if is the same swap lines
                        if ((clearedLines.get(indexLine).regionMatches(0, clearedLines.get(indexLine + 4), 0, 28)) &&
                                (clearedLines.get(indexLine).regionMatches(0, clearedLines.get(indexLine + 8), 0, 28))) {
                            tempArrayLines[indexLine + 2] = tempArrayLines[indexLine + 5];
                            tempArrayLines[indexLine + 3] = tempArrayLines[indexLine + 9] + "\n";

                            for (int i = indexLine + 4; i <= indexLine + 9; i++) {
                                tempArrayLines[i] = "";
                            }
                        }
                    } else if (clearedLines.get(indexLine).regionMatches(0, clearedLines.get(indexLine + 4), 0, 28)) {
                        tempArrayLines[indexLine + 2] = tempArrayLines[indexLine + 5] + "\n";
                        tempArrayLines[indexLine + 3] = "";
                        tempArrayLines[indexLine + 4] = "";
                        tempArrayLines[indexLine + 5] = "";

                    }
                } else if (tempArrayLines[indexLine].matches("[0-9]") ||
                        (tempArrayLines[indexLine].matches("[0-9]{2}")) ||
                        (tempArrayLines[indexLine].matches("[0-9]{3}")) ||
                        (tempArrayLines[indexLine].matches("[0-9]{4}"))) {
                    tempArrayLines[indexLine - 1] = tempArrayLines[indexLine - 1] + "\n";
                }

            }

        }

        return tempArrayLines;
    }
}
