/*
* Created on :2012-6-5
* Copyright  :All Rights Reserved. Copyright(C) Bamboo Technologies Ltd.
* Author     :focus.huang
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
*/
package cn.wuxia.project.storage.core.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * [ticket id]
 * Description of the class 
 * @author focus.huang
 * @ Version : V<Ver.No> <2012-6-5>
 */
@Getter
@Setter
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

}
