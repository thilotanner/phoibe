package util.file;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {
    public static File[] dirListByAscendingDate(File folder)
    {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Provided folder is not a directory");
        }
        File files[] = folder.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(final File file1, final File file2) {
                return new Long(file1.lastModified()).compareTo(file2.lastModified());
            }
        });
        return files;
    }
}
