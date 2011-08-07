package undermind.utilities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 22:39:07 <br/>
 */
public class Out {
    private static PrintStream out;

    static {
        out = System.out;
//        try {
//            out = new PrintStream(new FileOutputStream("cache.txt"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public static void println(String string) {
        out.println(string);
        out.flush();
    }

    public static void close() {
        out.close();
    }
}
