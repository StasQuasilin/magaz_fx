package utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPT_USER on 06.10.2017.
 */
public class Utils {
    public static List<String> parseLine(String line) {

        System.out.println("Parse: " + line);

        List<String> result = new LinkedList<>();

        String s = "";
        boolean lock = false;
        for (char c : line.toCharArray()) {
            if (c == '\"') {
                lock = !lock;
            }

            if (!lock && c == ',') {
                result.add(s);
                s = "";
            } else if (lock){
                s += c;
            } else {
                if (c != ' ') {
                    s += c;
                }
            }

        }

        System.out.println(s);
        result.add(s);

        return result;
    }
}
