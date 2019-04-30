package cn.wuxia.project.storage.upload.bean;

public class UploadRespone {
    String filepath;
    String fileurl;
    String bucket;
    String md5;

    public UploadRespone() {
    }

    public UploadRespone(String filepath, String fileurl, String bucket) {
        this.filepath = filepath;
        this.fileurl = fileurl;
        this.bucket = bucket;
    }
    public UploadRespone(String filepath, String fileurl, String bucket, String md5) {
        this.filepath = filepath;
        this.fileurl = fileurl;
        this.bucket = bucket;
        this.md5 = md5;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
