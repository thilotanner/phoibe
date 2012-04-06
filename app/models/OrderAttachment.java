package models;

import play.Play;
import play.mvc.Router;
import play.vfs.VirtualFile;
import util.string.NonEmptyStringBuilder;

import java.io.File;

public class OrderAttachment {
    
    public static String sanitizeFilename(String filename) {
        filename = filename.replaceAll(File.separator, "-");
        filename = filename.replaceAll(File.pathSeparator, "-");
        filename = filename.replaceAll("\\s+", "_");

        return filename;
    }
    
    private File file;

    public OrderAttachment(File file) {
        this.file = file;
    }
    
    public String getFilename() {
        return file.getName();
    }
    
    public VirtualFile getVirtualFile() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        nesb.setDelimiter(File.separator);
        nesb.append("data");
        boolean attachmentReached = false;
        for(String pathComponent : file.getAbsolutePath().split(File.separator)) {
            if(pathComponent.equals("attachments")) {
                attachmentReached = true;
            }
            
            if(attachmentReached) {
                nesb.append(pathComponent);
            }
        }
        return Play.getVirtualFile(nesb.toString());
    }
    
    public String getDownloadPath() {
        return Router.reverse(getVirtualFile());
    }

    public boolean delete() {
        return file.delete();
    }
}
