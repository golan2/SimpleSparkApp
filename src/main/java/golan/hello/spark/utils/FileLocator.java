package golan.hello.spark.utils;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Created by golaniz on 05/09/2016.
 */
public class FileLocator {

    public static String findFileInClasspath(String filename) throws FileNotFoundException {
        URL url = FileLocator.class.getClassLoader().getResource(filename);
        if (url==null) {
            throw new FileNotFoundException("Can't find file: " + filename);
        }
        return url.getPath();
    }
}
