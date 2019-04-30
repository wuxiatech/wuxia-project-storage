/*
* Created on :2012-6-5
* Copyright  :All Rights Reserved. Copyright(C) Bamboo Technologies Ltd.
* Author     :focus.huang
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
*/
package cn.wuxia.project.storage.core.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * [ticket id]
 * Description of the class 
 * @author focus.huang
 * @ Version : V<Ver.No> <2012-6-5>
 */
public class FileUploadBean implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -15927203808012128L;

    private String uploadFileSetInfoId;

    private String uploadFileInfoId;

    private String oldFileName;

    private String newFileName;

    private String fileSavePath;

    private String fileSaveDir;

    private String downloadUrl;

    private String fileType;

    private long fileSize;

    private String contentType;

    private String fileMD5;
    /**
     * @return 
     */
    public String getUploadFileSetInfoId() {
        return uploadFileSetInfoId;
    }

    /**
     * @param Integer
     */
    public void setUploadFileSetInfoId(String uploadFileSetInfoId) {
        this.uploadFileSetInfoId = uploadFileSetInfoId;
    }

    /**
     * @return 
     */
    @JsonIgnore
    public String getFileSaveDir() {
        return fileSaveDir;
    }

    /**
     * @param String
     */
    public void setFileSaveDir(String fileSaveDir) {
        this.fileSaveDir = fileSaveDir;
    }

    /**
     * @return 
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param String
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return 
     */
    public String getOldFileName() {
        return oldFileName;
    }

    /**
     * @param String
     */
    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    /**
     * @return 
     */
    public String getNewFileName() {
        return newFileName;
    }

    /**
     * @param String
     */
    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    /**
     * @return 
     */
    @JsonIgnore
    public String getFileSavePath() {
        return fileSavePath;
    }

    /**
     * @param String
     */
    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    /**
     * @return 
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @param long
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return 
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param String
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return 
     */
    public String getUploadFileInfoId() {
        return uploadFileInfoId;
    }

    /**
     * @param Integer
     */
    public void setUploadFileInfoId(String uploadFileInfoId) {
        this.uploadFileInfoId = uploadFileInfoId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }
}
