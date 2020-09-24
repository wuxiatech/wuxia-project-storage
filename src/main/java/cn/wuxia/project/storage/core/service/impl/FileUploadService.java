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
import org.nutz.lang.random.R;
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

    private UploadHandler uploadHandler = new UploadHandler();
    /**
     * 文件存储起始路径
     */
    private final static String DRIVER_URL = InitializationFile.driveUrl;

    public FileUploadBean uploadFile(String httpUrl, UploadFileCategoryEnum uploadCategory) throws HttpClientException, UploadException {
        return uploadFile(httpUrl, uploadCategory, null);
    }

    public FileUploadBean uploadFile(String httpUrl, UploadFileCategoryEnum uploadCategory, String uploadFileSetId) throws HttpClientException, UploadException {
        //生成URL对象
        InputStream inputStream = HttpClientUtil.download(httpUrl);
        //保存文件的信息入数据库
        return uploadFile(inputStream, httpUrl, uploadCategory, uploadFileSetId);
    }

    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory, String uploadFileSetId) throws UploadException {
        try {
            return uploadFile(new FileInputStream(uploadFile), uploadFile.getName(), uploadCategory, uploadFileSetId);
        } catch (FileNotFoundException e) {
            throw new UploadException("文件不存在", e);
        }
    }

    public FileUploadBean uploadFile(byte[] bytes, String fileName, UploadFileCategoryEnum fileCategoryEnum) throws UploadException {
        return uploadFile(bytes, fileName, fileCategoryEnum, null);
    }

    public FileUploadBean uploadFile(byte[] bytes, String fileName, UploadFileCategoryEnum fileCategoryEnum, String uploadFileSetId) throws UploadException {
        logger.info("根据字节和文件名上传文件开始。。。。");
        if (null == bytes || bytes.length <= 0) {
            logger.info("根据字节和文件名上传文件失败，文件字节为空。。。。");
            throw new UploadException("根据字节和文件名上传文件失败，文件字节为空。。。。");
        }

        if (null == fileName || fileName.isEmpty()) {
            logger.info("根据字节和文件名上传文件失败，文件名为空。。。。");
            throw new UploadException("根据字节和文件名上传文件失败，文件名为空。。。。");
        }
        return uploadFile(new ByteArrayInputStream(bytes), fileName, fileCategoryEnum, uploadFileSetId);
    }

    public FileUploadBean uploadFile(InputStream inputStream, String fileName, UploadFileCategoryEnum uploadCategory) throws UploadException {
        return uploadFile(inputStream, fileName, uploadCategory, null);
    }

    public FileUploadBean uploadFile(InputStream inputStream, String fileName, UploadFileCategoryEnum uploadCategory, String uploadFileSetId) throws UploadException {
        return uploadFile(inputStream, fileName, uploadCategory, uploadFileSetId, InitializationFile.isPutToAliOss, InitializationFile.isPutToQiniuOss);
    }

    public FileUploadBean uploadFile(InputStream inputStream, String fileName, UploadFileCategoryEnum uploadCategory, String uploadFileSetId, boolean alioss, boolean qiniuoss) throws UploadException {
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
        final String newFileName = R.sg(8).next() + (StringUtil.isBlank(fileType) ? "" : "." + fileType);
        /**
         * 新文件保存的路径
         */
        final String fileSaveDir = uploadCategory + File.separator + DateUtil.dateToString(new Date(), "yyMMdd");
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

        /**
         * 如果是存储在本地
         */
        if (!alioss && !qiniuoss) {
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
        FileUploadBean fileUploadBean = new FileUploadBean();
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
        //判断是否保存到oss上，若需要保存到oss则把文件上传到oss
        if (alioss) {
            logger.info("#################Start Upload To Aliyun OSS ########################");
            UploadRespone uploadRespone = uploadHandler.uploadToAliOss(inputStream, fileAbsPath);
            newFileToSaveDTO.setBucketName(uploadHandler.getOssUploader().getConfig().getBucketName());
            newFileToSaveDTO.setEndpoint(uploadHandler.getOssUploader().getConfig().getEndpoint());
            newFileToSaveDTO.setUrl(uploadRespone.getPublicurl());
            newFileToSaveDTO.setLocation(File.separator + uploadRespone.getFilepath());
            newFileToSaveDTO.setMd5(uploadRespone.getMd5());
            downloadUrl = uploadRespone.getCdnurl();
            logger.info("#################End Upload To Aliyun OSS, URL:{} ########################", downloadUrl);
        }
        if (qiniuoss) {
            logger.info("#################Start Upload To Qiniu OSS ########################");
            UploadRespone uploadRespone = uploadHandler.uploadToQiniuOss(inputStream, fileAbsPath);
            newFileToSaveDTO.setBucketName(uploadHandler.getQiniuUploader().getConfig().getBucket());
            newFileToSaveDTO.setEndpoint(uploadHandler.getQiniuUploader().getConfig().getZone());
            newFileToSaveDTO.setUrl(uploadRespone.getPublicurl());
            newFileToSaveDTO.setLocation(File.separator + uploadRespone.getFilepath());
            newFileToSaveDTO.setMd5(uploadRespone.getMd5());
            downloadUrl = uploadRespone.getCdnurl();
            logger.info("#################End Upload To Aliyun OSS, URL:{} ########################", downloadUrl);
        }
        if (!alioss && !qiniuoss) {
            newFileToSaveDTO.setLocation(File.separator + fileAbsPath);
            newFileToSaveDTO.setUrl(InitializationFile.downloadUrl + newFileToSaveDTO.getLocation());
            logger.info("#################End Upload To Location, URL:{} ########################", newFileToSaveDTO.getUrl());
        }

        uploadFileService.saveNewUploadFile(newFileToSaveDTO);

        fileUploadBean.setDownloadUrl(downloadUrl);
        fileUploadBean.setUploadFileInfoId(newFileToSaveDTO.getId());
        fileUploadBean.setUploadFileSetInfoId(newFileToSaveDTO.getUploadFileSetId());
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
