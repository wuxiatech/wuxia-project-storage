package cn.wuxia.project.storage.upload;

public class UploadException extends Exception {

    public UploadException(String msg) {
        super(msg);
    }

    public UploadException(String message, Throwable e) {
        super(message, e);
    }

    public UploadException(Throwable e) {
        super(e);
    }
}
