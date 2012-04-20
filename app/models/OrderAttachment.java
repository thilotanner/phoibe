package models;

import play.Play;
import play.mvc.Router;
import play.vfs.VirtualFile;
import util.string.NonEmptyStringBuilder;

import java.io.File;

public class OrderAttachment extends Attachment {

    public OrderAttachment(File file) {
        super(file);
    }

}
