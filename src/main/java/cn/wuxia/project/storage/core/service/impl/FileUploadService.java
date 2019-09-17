/*
 * Copyright 2011-2020 wuxia.gd.cn All right reserved.
 */
package cn.wuxia.project.storage.core.service.impl;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.FileUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.project.storage.core.bean.FileUploadBean;
import cn.wuxia.project.storage.core.bean.NewFileToSaveDTO;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.service.UploadFileInfoService;
import cn.wuxia.project.storage.core.support.InitializationFile;
import cn.wuxia.project.storage.upload.UploadException;
import cn.wuxia.project.storage.upload.bean.UploadRespone;
import cn.wuxia.project.storage.upload.service.UploadHandler;
import org.apache.commons.io.FilenameUtils;
import org.nutz.lang.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.Date;

/**
 * upload file class.
 *
 * @author songlin
 * @since 2012-06-21
 */
@Service
@Transactional
public class FileUploadService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UploadFileInfoService uploadFileService;

    @Autowired
    private UploadHandler uploadHandler;
    /**
     * 文件存储起始路径
     */
    protected final static String DRIVER_URL = InitializationFile.driveUrl;


    public FileUploadBean uploadFile(String httpUrl, UploadFileCategoryEnum uploadCategory) throws HttpClientException, UploadException {
        //生成URL对象
        InputStream inputStream = HttpClientUtil.download(httpUrl);
        //保存文件的信息入数据库
        return uploadFile(inputStream, httpUrl, uploadCategory == null ? UploadFileCategoryEnum.temp : uploadCategory, null);
    }

    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory, String uploadFileSetId) throws UploadException {
        try {
            return uploadFile(new FileInputStream(uploadFile), uploadFile.getName(), uploadCategory, uploadFileSetId);
        } catch (FileNotFoundException e) {
            throw new UploadException("文件不存在", e);
        }
    }


    public FileUploadBean uploadFile(byte[] bytes, String fileName, UploadFileCategoryEnum ufce) throws UploadException {
        logger.info("根据字节和文件名上传文件开始。。。。");
        if (null == bytes || bytes.length <= 0) {
            logger.info("根据字节和文件名上传文件失败，文件字节为空。。。。");
            throw new UploadException("根据字节和文件名上传文件失败，文件字节为空。。。。");
        }

        if (null == fileName || fileName.isEmpty()) {
            logger.info("根据字节和文件名上传文件失败，文件名为空。。。。");
            throw new UploadException("根据字节和文件名上传文件失败，文件名为空。。。。");
        }
        return uploadFile(new ByteArrayInputStream(bytes), fileName, ufce, null);
    }

    public FileUploadBean uploadFile(InputStream inputStream, String fileName, UploadFileCategoryEnum uploadCategory, String uploadFileSetId) throws UploadException {
        Stopwatch sw = Stopwatch.begin();
        logger.info("#################Start Upload {}########################", fileName);
        final String fileType = FilenameUtils.getExtension(fileName);
        //        final String fileName = StringUtil.substringBeforeLast(uploadFile.getName(), "." + fileType);
        if (uploadCategory == null) {
            if (StringUtil.equals(fileType, "mp3") || StringUtil.equals(fileType, "rm") || StringUtil.equals(fileType, "wma")) {
                uploadCategory = UploadFileCategoryEnum.music;
            } else if (StringUtil.equals(fileType, "mp4")) {
                uploadCategory = UploadFileCategoryEnum.video;
            } else {
                uploadCategory = UploadFileCategoryEnum.file;
            }
        }
        //如果文件名过长则截取值
        final String oldFileName = FilenameUtils.getBaseName(fileName);
        /**
         * 新名字
         */
        final String newFileName = StringUtil.random(6) + (StringUtil.isBlank(fileType) ? "" : "." + fileType);
        /**
         * 新文件保存的路径
         */
        final String fileSaveDir = uploadCategory + File.separator + DateUtil.dateToString(new Date(), "yyyy/MM/dd");
        /**
         * 新文件的全路径
         */
        final String fileAbsPath = fileSaveDir + File.separator + newFileName;
        // ----upload file-----begin---
        if (logger.isDebugEnabled()) {
            logger.debug("###############ContentType:" + new MimetypesFileTypeMap().getContentType(fileName));
            logger.debug("###############OriginalFilename:" + fileName);
            logger.debug("###############NewFileName:" + newFileName);
        }
        FileUploadBean fileUploadBean = null;
        /**
         * 如果是存储在本地
         */
        if (!InitializationFile.isPutToAliOss && !InitializationFile.isPutToQiniuOss) {
            try {
                final String localFileSaveDir = DRIVER_URL + File.separator + fileSaveDir;
                File f = FileUtil.getFile(localFileSaveDir);
                if (!f.exists()) {
                    FileUtil.forceMkdir(f);
                }
                final String localFileSavePath = localFileSaveDir + File.separator + newFileName;
                FileUtil.copyInputStreamToFile(inputStream, new File(localFileSavePath));
//                FileUtil.copyFile(uploadFile, new File(fileAbsPath));
                logger.info("###################The upload file :[" + fileName + "],was uploaded to local path :[" + localFileSavePath + "] successful!");
            } catch (IOException exception) {
                throw new UploadException("Upload File Error:", exception);
            }
        }
        fileUploadBean = new FileUploadBean();
        fileUploadBean.setOldFileName(oldFileName);
        fileUploadBean.setNewFileName(newFileName);
        fileUploadBean.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
        fileUploadBean.setFileType(fileType);
        try {
            fileUploadBean.setFileSize(inputStream.available());
        } catch (IOException e) {
            logger.warn("", e.getMessage());
        }

        fileUploadBean.setFileSavePath(fileAbsPath);
        fileUploadBean.setFileSaveDir(fileSaveDir);
        /**
         * 如果上传到临时文件夹，则不作数据库保存记录
         */
        if (uploadCategory.equals(UploadFileCategoryEnum.temp)) {
            return fileUploadBean;
        }

        // ----upload file-----end---

        // save FileUploadBean--begin--------------

        //保存图片
        NewFileToSaveDTO newFileToSaveDTO = new NewFileToSaveDTO();
        newFileToSaveDTO.setFileEncoding(fileUploadBean.getContentType());
        newFileToSaveDTO.setFileSize(fileUploadBean.getFileSize());
        newFileToSaveDTO.setFileName(fileUploadBean.getOldFileName());
        newFileToSaveDTO.setFileType(fileUploadBean.getFileType());
        newFileToSaveDTO.setUploadFileSetId(uploadFileSetId);
        newFileToSaveDTO.setUploadFileCategory(uploadCategory);
        String downloadUrl = "";
        logger.info("#################Start Upload########################");
        //判断是否保存到oss上，若需要保存到oss则把文件上传到oss
        if (InitializationFile.isPutToAliOss) {
            UploadRespone uploadRespone = uploadHandler.uploadToAliOss(inputStream, fileAbsPath);
            newFileToSaveDTO.setBucketName(uploadHandler.getOssUploader().getConfig().getBucketName());
            newFileToSaveDTO.setEndpoint(uploadHandler.getOssUploader().getConfig().getEndpoint());
            newFileToSaveDTO.setUrl(uploadRespone.getPublicurl());
            newFileToSaveDTO.setLocation(File.separator + uploadRespone.getFilepath());
            newFileToSaveDTO.setMd5(uploadRespone.getMd5());
            downloadUrl = uploadRespone.getCdnurl();

        } else if (InitializationFile.isPutToQiniuOss) {
            UploadRespone uploadRespone = uploadHandler.uploadToQiniuOss(inputStream, fileAbsPath);
            newFileToSaveDTO.setBucketName(uploadHandler.getQiniuUploader().getConfig().getBucket());
            newFileToSaveDTO.setEndpoint(uploadHandler.getQiniuUploader().getConfig().getZone());
            newFileToSaveDTO.setUrl(uploadRespone.getPublicurl());
            newFileToSaveDTO.setLocation(File.separator + uploadRespone.getFilepath());
            newFileToSaveDTO.setMd5(uploadRespone.getMd5());
            downloadUrl = uploadRespone.getCdnurl();
        } else {
            newFileToSaveDTO.setLocation(File.separator + fileAbsPath);
            newFileToSaveDTO.setUrl(InitializationFile.downloadUrl + newFileToSaveDTO.getLocation());
        }


        uploadFileService.saveNewUploadFile(newFileToSaveDTO);

        fileUploadBean.setDownloadUrl(downloadUrl);
        fileUploadBean.setUploadFileInfoId(newFileToSaveDTO.getId());
        fileUploadBean.setFileMD5(newFileToSaveDTO.getMd5());
        // save FileUploadBean--end--------------
        sw.stop();
        logger.info("#################End Upload {}, cost:{}########################", fileName, sw.getDuration());
        return fileUploadBean;
    }

    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory) throws UploadException {
        return uploadFile(uploadFile, uploadCategory, null);
    }

    public FileUploadBean[] batchUploadFile(File[] uploadFiles, UploadFileCategoryEnum uploadCategory) throws UploadException {
        FileUploadBean[] fileUploadBeans = new FileUploadBean[uploadFiles.length];
        for (int i = 0; i < uploadFiles.length; i++) {
            File uploadfile = uploadFiles[i];
            if (i == 0) {
                fileUploadBeans[i] = uploadFile(uploadfile, uploadCategory);
            } else {
                fileUploadBeans[i] = uploadFile(uploadfile, uploadCategory, fileUploadBeans[i - 1].getUploadFileSetInfoId());
            }
        }
        return fileUploadBeans;
    }

    public FileUploadBean[] batchUploadFile(File[] uploadFiles, UploadFileCategoryEnum uploadCategory, String uploadFileSetInfoId) throws UploadException {
        if (StringUtil.isBlank(uploadFileSetInfoId)) {
            return batchUploadFile(uploadFiles, uploadCategory);
        }
        FileUploadBean[] fileUploadBeans = new FileUploadBean[uploadFiles.length];
        int i = 0;
        for (File uploadfile : uploadFiles) {
            fileUploadBeans[i++] = uploadFile(uploadfile, uploadCategory, uploadFileSetInfoId);
        }
        return fileUploadBeans;
    }


}
