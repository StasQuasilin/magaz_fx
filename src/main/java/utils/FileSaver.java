package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ZPT_USER on 06.10.2017.
 */
public class FileSaver {

    BufferedWriter writer;

    public FileSaver(File file) throws IOException {
        writer = new BufferedWriter(
                new FileWriter(file)
        );
    }

    public void write(String s) throws IOException {
        writer.write(s);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}
