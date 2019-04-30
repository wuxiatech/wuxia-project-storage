package cn.wuxia.project.storage.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * The persistent class for the UploadFileInfo database table.
 */
@Entity
@Table(name = "upload_file_info")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class UploadFileInfo extends ModifyInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clientHostName;

    private String fileEncoding;

    private String fileName;

    private Long fileSize;

    private String fileStatus;

    private String fileType;

    private String location;

    private String uploadDate;

    private String endpoint;

    private String bucketName;

    private String url;

    private String md5;
    private String wechatMateriaMediaId;

    public UploadFileInfo() {
        super();
    }

    @Size(max = 50)
    @Column(name = "CLIENT_HOSTNAME")
    public String getClientHostName() {
        return this.clientHostName;
    }

    public void setClientHostName(String clientHostName) {
        this.clientHostName = clientHostName;
    }

    @Size(max = 100)
    @Column(name = "FILE_ENCODING")
    public String getFileEncoding() {
        return this.fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    @Size(max = 100)
    @Column(name = "FILE_NAME")
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "FILE_SIZE")
    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Size(max = 10)
    @Column(name = "FILE_STATUS")
    public String getFileStatus() {
        return this.fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    @Size(max = 10)
    @Column(name = "FILE_TYPE")
    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Size(max = 2000)
    @Column(name = "LOCATION")
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name = "upload_date")
    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Column(name = "endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Column(name = "bucket_name")
    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Column(name = "MEDIA_ID")
    public String getWechatMateriaMediaId() {
        return wechatMateriaMediaId;
    }

    public void setWechatMateriaMediaId(String wechatMateriaMediaId) {
        this.wechatMateriaMediaId = wechatMateriaMediaId;
    }

}
