package common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class fileWriter {
    private String path;

    public fileWriter (String path) {
        this.path = path;
    }

    public void writeFileAndCreateIfNeeded(String data) {
        File f = new File(path);
        if (!f.exists()) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(path, "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("Error while creating file " + this.path);
            }
            assert writer != null;
            writer.close();
        }
        try {
            Files.write(Paths.get(path), data.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while writing " + data + " to file " + this.path);
        }
    }
}
