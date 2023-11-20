package app.core.model.profile;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import javax.ws.rs.FormParam;

public class MultiPartFormData {
    @FormParam("file")
    @PartType("application/octet-stream")
    private FileUpload file;

    @FormParam("text")
    @PartType("text/plain")
    private String text;

    public FileUpload getFile() {
        return file;
    }

    public void setFile(FileUpload file) {
        this.file = file;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
