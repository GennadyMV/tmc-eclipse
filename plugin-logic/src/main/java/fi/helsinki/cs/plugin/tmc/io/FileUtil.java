package fi.helsinki.cs.plugin.tmc.io;

import java.io.File;

public class FileUtil {

    public static String append(String a, String b) {
        return getUnixPath(a) + "/" + getUnixPath(b);
    }

    public static String getUnixPath(String file) {
        String unixPath = file.replace(File.separator, "/");

        if (unixPath.charAt(unixPath.length() - 1) == '/') {
            unixPath = unixPath.substring(0, unixPath.length() - 1);
        }

        return unixPath;
    }

}
