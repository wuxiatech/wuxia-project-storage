/*
 * Copyright 2011-2020 wuxia.gd.cn All right reserved.
 */
package cn.wuxia.project.storage.core.service.impl;

import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.FileUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.util.reflection.ConvertUtil;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetRef;
import cn.wuxia.project.storage.core.bean.FileUploadBean;
import cn.wuxia.project.storage.core.bean.UploadFileInfoDTO;
import cn.wuxia.project.storage.core.dao.UploadFileInfoDao;
import cn.wuxia.project.storage.core.dao.UploadFileSetInfoDao;
import cn.wuxia.project.storage.core.dao.UploadFileSetRefDao;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.service.UploadFileService;
import cn.wuxia.project.storage.core.support.InitializationFile;
import cn.wuxia.project.common.support.CacheConstants;
import cn.wuxia.project.common.support.Constants;
import cn.wuxia.project.storage.upload.bean.UploadRespone;
import cn.wuxia.project.storage.upload.service.UploadHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * upload file class.
 *
 * @author xuejiang
 * @since 2012-06-21
 */
@Service
@Transactional
public class UploadFileServiceImpl implements UploadFileService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UploadFileInfoDao uploadFileInfoDao;

    @Autowired
    private UploadFileSetInfoDao uploadFileSetInfoDao;

    @Autowired
    private UploadFileSetRefDao uploadFileSetRefDao;

    @Autowired
    private UploadHandler uploadHandler;
    /**
     * 文件存储起始路径
     */
    protected final static String DRIVER_URL = InitializationFile.driveUrl;

    /**
     * 文件下载起始路径
     */
    protected final static String DOWNLOAD_URL = InitializationFile.downloadUrl;

    @Override
    public void deleteFileInfoByPhysical(String uploadFileInfoId) {
        UploadFileInfo info = uploadFileInfoDao.get(uploadFileInfoId);
        String bucketName = info.getBucketName();
        String endpint = info.getEndpoint();
        String location = info.getLocation();
        if (InitializationFile.isPutToAliOss) {
            try {
                uploadHandler.getOssUploader().getOssClient().deleteObject(bucketName, location);
            } catch (Exception e) {
                logger.error("物理删除异常：" + e.getMessage());
            }
        } else {
            File file = new File(DRIVER_URL + info.getLocation());
            try {
                FileUtil.forceDelete(file);
            } catch (IOException e) {
                throw new AppServiceException("{error.deleteFile}:{}", e.getMessage());
            }
        }
        uploadFileInfoDao.delete(info);
        uploadFileSetRefDao.deleteFile(uploadFileInfoId);
    }

    @Override
    public void deleteFileInfoByLogical(String uploadFileInfoId) {
        uploadFileInfoDao.deleteEntityById(uploadFileInfoId);
        uploadFileSetRefDao.deleteFile(uploadFileInfoId);
    }

    @Override
    public FileUploadBean uploadFile(HttpServletRequest request, String fileInputName) {
        logger.info("测试上传图片开始。。。。");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        if (StringUtil.isBlank(fileInputName)) {
            Iterator<String> it = multipartRequest.getFileNames();
            fileInputName = it.next();
        }
        CommonsMultipartFile uploadFile = (CommonsMultipartFile) multipartRequest.getFile(fileInputName);

        String uploadFileSetId = request.getParameter("uploadFileSetId");
        boolean isGenerateMD5 = BooleanUtils.toBoolean(request.getParameter("generateMD5"));
        logger.info("测试上传图片开始。。。。");
        if (uploadFile == null || uploadFile.isEmpty()) {
            logger.error("the request has no uploadFile!");
            return null;
        }

        String fileName = uploadFile.getOriginalFilename();
        final String currentDate = DateUtil.dateToString(new Date(), "yyyyMM");

        //如 /app/fileupload/Temp/201406/fwv0xtg9nshCxg.JPG
        final String fileSaveDir = DRIVER_URL + "/" + UploadFileCategoryEnum.temp + "/" + currentDate + "/";
        final String fileAbsPath = fileSaveDir + fileName;
        DataOutputStream out = null;
        InputStream is = null;
        FileUploadBean fileUploadBean = null;
        try {
            logger.info("#################Start Upload########################");
            File f = FileUtil.getFile(fileSaveDir);
            if (!f.exists())
                FileUtil.forceMkdir(f);
            Date startTime = new Date();
            out = new DataOutputStream(new FileOutputStream(fileAbsPath));
            is = uploadFile.getInputStream();
            byte[] buffer = new byte[1024];
            while (is.read(buffer) > 0) {
                out.write(buffer);
            }
            String uploadCategory = StringUtil.isNotBlankPlus(request.getParameter("uploadCategory")) ? request.getParameter("uploadCategory")
                    : request.getAttribute("uploadCategory") + "";
            fileUploadBean = uploadFile(new File(fileAbsPath),
                    StringUtil.isNotBlankPlus(uploadCategory) ? UploadFileCategoryEnum.valueOf(uploadCategory) : UploadFileCategoryEnum.temp,
                    StringUtil.isBlank(uploadFileSetId) ? null : uploadFileSetId, isGenerateMD5);
            Date endTime = new Date();
            logger.info(
                    "###################End Upload#########################,total spend time:" + (endTime.getTime() - startTime.getTime()) + "ms");
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.error("Upload File Error:" + exception.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("", e);
                    logger.error("InputStream Close Error:" + e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("", e);
                    logger.error("DataOutputStream Close Error:" + e.getMessage());
                }
            }
        }
        return fileUploadBean;
        // ----upload file-----end---

    }

    @Override
    public FileUploadBean uploadFile(String httpUrl, UploadFileCategoryEnum uploadCategory) {
        FileUploadBean fileBean = null;
        try {
            //获得图片的类型：jpg、png、bmp......
            final String fileType = FilenameUtils.getExtension(httpUrl);
            //格式化当前时间：201411
            final String currentDate = DateUtil.dateToString(new Date(), "yyyyMM");
            //生成新的文件名字
            final String newFileName = FilenameUtils.getName(httpUrl);
            //文件的存储路径
            final String fileSaveDir = DRIVER_URL + "/" + UploadFileCategoryEnum.temp + "/" + currentDate + "/";
            //文件的全路径
            final String fileAbsPath = fileSaveDir + newFileName;
            //生成URL对象
            HttpClientUtil.download(httpUrl, fileAbsPath);
            //保存文件的信息入数据库
            fileBean = uploadFile(new File(fileAbsPath), uploadCategory == null ? UploadFileCategoryEnum.temp : uploadCategory, null);
        } catch (Exception e) {
            logger.error("", e);
        }
        return fileBean;
    }

    @Override
    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory, String uploadFileSetId) {
        return uploadFile(uploadFile, uploadCategory, uploadFileSetId, false);
    }

    @Override
    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory, String uploadFileSetId, boolean genMd5) {
        Assert.notNull(uploadCategory, "uploadCategory 不能为空");
        if (uploadFile == null || !uploadFile.exists()) {
            logger.error("the request has no uploadFile!");
            return null;
        }

        final String fileType = FilenameUtils.getExtension(uploadFile.getName());
        //        final String fileName = StringUtil.substringBeforeLast(uploadFile.getName(), "." + fileType);
        //如果文件名过长则截取值
        final String oldFileName = FilenameUtils.getBaseName(uploadFile.getName());
        final String currentDate = DateUtil.dateToString(new Date(), "yyyy/MM/dd");
        if (StringUtil.equals(fileType, "mp3") || StringUtil.equals(fileType, "rm") || StringUtil.equals(fileType, "wma")) {
            uploadCategory = UploadFileCategoryEnum.music;
        }
        //新名字
        final String newFileName = StringUtil.random(6) + "." + fileType;
        String fileSaveDir = uploadCategory + "/" + currentDate + "/";
        if (!InitializationFile.isPutToAliOss && !InitializationFile.isPutToQiniuOss) {
            //如 /app/fileupload/AD/2014/06/27/fwv0xt.JPG
            fileSaveDir = DRIVER_URL + "/" + fileSaveDir;
        }
        final String fileAbsPath = fileSaveDir + newFileName;
        String downloadUrl = StringUtil.replace(fileAbsPath, DRIVER_URL, DOWNLOAD_URL);
        // ----upload file-----begin---
        if (logger.isDebugEnabled()) {
            logger.debug("###############ContentType:" + new MimetypesFileTypeMap().getContentType(uploadFile));
            logger.debug("###############OriginalFilename:" + uploadFile.getName());
            logger.debug("###############Size:" + FileUtil.sizeOf(uploadFile));
            logger.debug("###############NewFileName:" + newFileName);
        }
        FileUploadBean fileUploadBean = null;
        if (!InitializationFile.isPutToAliOss && !InitializationFile.isPutToQiniuOss) {
            try {
                logger.info("#################Start Upload########################");
                File f = FileUtil.getFile(fileSaveDir);
                if (!f.exists())
                    FileUtil.forceMkdir(f);
                //FileUtil.copyInputStreamToFile( new FileInputStream(uploadFile), f);
                FileUtil.copyFile(uploadFile, new File(fileAbsPath));
                logger.info("###################The file :[" + uploadFile.getName() + "],was uploaded to:[" + fileAbsPath + "] successful!");
            } catch (IOException exception) {
                exception.printStackTrace();
                logger.error("Upload File Error:" + exception.getMessage());
            }
        }
        fileUploadBean = new FileUploadBean();
        fileUploadBean.setOldFileName(oldFileName);
        fileUploadBean.setNewFileName(newFileName);
        fileUploadBean.setContentType(new MimetypesFileTypeMap().getContentType(uploadFile));
        fileUploadBean.setFileType(fileType);
        fileUploadBean.setFileSize(FileUtil.sizeOf(uploadFile));
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
        UploadFileSetInfo uploadFileSetInfo = null;
        if (StringUtil.isNotBlank(uploadFileSetId)) {
            uploadFileSetInfo = uploadFileSetInfoDao.getEntityById(uploadFileSetId);
        }
        if (null == uploadFileSetInfo) {
            uploadFileSetInfo = new UploadFileSetInfo();
            uploadFileSetInfo.setCategory(uploadCategory);
            uploadFileSetInfo.setCreatedOn(DateUtil.newInstanceDate());
            uploadFileSetInfo.setCreatedBy("");
            this.uploadFileSetInfoDao.save(uploadFileSetInfo);
            uploadFileSetId = uploadFileSetInfo.getId();
            fileUploadBean.setUploadFileSetInfoId(uploadFileSetInfo.getId());
        } else {
            uploadFileSetInfo.setId(uploadFileSetId);
            fileUploadBean.setUploadFileSetInfoId(uploadFileSetId);
        }
        //保存图片
        UploadFileInfo uploadFileInfo = new UploadFileInfo();
        uploadFileInfo.setFileEncoding(fileUploadBean.getContentType());
        uploadFileInfo.setFileSize(fileUploadBean.getFileSize());
        uploadFileInfo.setFileName(fileUploadBean.getOldFileName());
        uploadFileInfo.setFileType(fileUploadBean.getFileType());
        //判断是否保存到oss上，若需要保存到oss则把文件上传到oss
        if (InitializationFile.isPutToAliOss) {
            try {
                UploadRespone uploadRespone = uploadHandler.uploadToAliOss(uploadFile, fileSaveDir + newFileName);

                String url = uploadRespone.getFileurl();
                String key = uploadRespone.getFilepath();
                uploadFileInfo.setBucketName(uploadHandler.getOssUploader().getConfig().getBucketName());
                uploadFileInfo.setEndpoint(uploadHandler.getOssUploader().getConfig().getEndpoint());
                uploadFileInfo.setUrl(url + key);
                uploadFileInfo.setLocation(key);
                //音频格式文件，不能使用cdn，故此直接使用阿里云地址
                if ("mp3".equals(fileType.toLowerCase()) || "rm".equals(fileType.toLowerCase()) || "wma".equals(fileType.toLowerCase())) {
                    downloadUrl = uploadFileInfo.getUrl();
                } else {
                    downloadUrl = DOWNLOAD_URL + uploadFileInfo.getLocation();
                }
            } catch (Exception ex) {
                logger.error("实例oss client异常：" + ex);
            }
        } else if (InitializationFile.isPutToQiniuOss) {
            try {

                UploadRespone uploadRespone = uploadHandler.uploadToQiniuOss(uploadFile, fileSaveDir + newFileName);

                String url = uploadRespone.getFileurl();
                String key = uploadRespone.getFilepath();
                uploadFileInfo.setBucketName(uploadHandler.getQiniuUploader().getConfig().getBucket());
                uploadFileInfo.setEndpoint(uploadHandler.getQiniuUploader().getConfig().getZone());
                uploadFileInfo.setUrl(url);
                uploadFileInfo.setLocation(File.separator + key);
                //音频格式文件，不能使用cdn，故此直接使用阿里云地址
                if ("mp3".equals(fileType.toLowerCase()) || "rm".equals(fileType.toLowerCase()) || "wma".equals(fileType.toLowerCase())) {
                    downloadUrl = uploadFileInfo.getUrl();
                } else {
                    downloadUrl = DOWNLOAD_URL + uploadFileInfo.getLocation();
                }
            } catch (Exception ex) {
                logger.error("实例oss client异常：" + ex);
            }
        } else {
            uploadFileInfo.setLocation(StringUtils.substring(fileAbsPath, DRIVER_URL.length()));
            uploadFileInfo.setUrl(DOWNLOAD_URL + uploadFileInfo.getLocation());
        }
        uploadFileInfo.setCreatedOn(DateUtil.newInstanceDate());
        if (genMd5) {
            try {
                String fileMD5 = DigestUtils.md5Hex(new FileInputStream(uploadFile));
                uploadFileInfo.setMd5(fileMD5);
            } catch (Exception e) {
                logger.warn("无法生成md5");
            }
        }
        this.uploadFileInfoDao.save(uploadFileInfo);
        //保存文件夹
        UploadFileSetRef entity = new UploadFileSetRef();
        entity.setUploadFilesetId(uploadFileSetInfo.getId());
        entity.setUploadFileId(uploadFileInfo.getId());
        entity.setSortOrder(System.currentTimeMillis());
        entity.setCreatedOn(DateUtil.newInstanceDate());
        uploadFileSetRefDao.save(entity);
        fileUploadBean.setDownloadUrl(downloadUrl);
        fileUploadBean.setUploadFileInfoId(uploadFileInfo.getId());
        fileUploadBean.setFileMD5(uploadFileInfo.getMd5());
        // save FileUploadBean--end--------------
        return fileUploadBean;
    }

    @Override
    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory) {
        return uploadFile(uploadFile, uploadCategory, null);
    }

    @Override
    public FileUploadBean[] batchUploadFile(File[] uploadFiles, UploadFileCategoryEnum uploadCategory) {
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

    @Override
    public FileUploadBean[] batchUploadFile(File[] uploadFiles, UploadFileCategoryEnum uploadCategory, String uploadFileSetInfoId) {
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

    public boolean checkExistingUploadFile(String uploadFileSetInfoId, String fileName) {
        if (StringUtil.isBlank(uploadFileSetInfoId)) {
            return false;
        }
        List<UploadFileInfo> uploadFileInfoList = uploadFileSetRefDao.findByUploadSetId(uploadFileSetInfoId);
        List<String> nameList = ConvertUtil.convertElementPropertyToList(uploadFileInfoList, "fileName");
        if (nameList.contains(fileName))
            return true;
        return false;
    }

    @Override
    @Cacheable(value = CacheConstants.CACHED_VALUE_1_DAY, key = CacheConstants.CACHED_KEY_DEFAULT + "+#uploadFileInfoId")
    public UploadFileInfo getUploadFileInfoById(String uploadFileInfoId) {
        if (StringUtil.isBlank(uploadFileInfoId))
            return null;
        return uploadFileInfoDao.getEntityById(uploadFileInfoId);
    }


    @Override
    @Deprecated
    public void updateByUploadId(String fileName, String uploadFileInfoId) {
        UploadFileInfo u = uploadFileInfoDao.get(uploadFileInfoId);
        u.setFileName(fileName);
        uploadFileInfoDao.save(u);
    }

    @Override
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#uploadFileInfoId", value = CacheConstants.CACHED_VALUE_1_DAY)
    @Deprecated
    public UploadFileInfoDTO getUploadFileInfoDTOById(String uploadFileInfoId) {
        UploadFileInfo fileInfo = uploadFileInfoDao.getEntityById(uploadFileInfoId);
        if (fileInfo != null) {
            UploadFileInfoDTO dto = new UploadFileInfoDTO();
            BeanUtil.copyProperties(dto, fileInfo);
            return dto;
        }
        return null;
    }

    @Override
    public void saveUploadFileInfo(UploadFileInfo uploadFileInfo) {
        uploadFileInfoDao.save(uploadFileInfo);
    }

    @Override
    public List<UploadFileInfo> findByFileName(String fileName) {
        return uploadFileInfoDao.findBy("fileName", fileName);
    }

    /*
     * (non-Javadoc)
     * @see cn.zuji.fdd.core.resource.service.UploadFileService#uploadFileByBytes(byte[], java.lang.String, cn.zuji.fdd.core.resource.enums.UploadFileCategoryEnum)
     */
    public FileUploadBean uploadFileByBytes(byte[] bytes, String fileName, UploadFileCategoryEnum ufce) {
        logger.info("根据字节和文件名上传文件开始。。。。");
        if (null == bytes || bytes.length <= 0) {
            logger.info("根据字节和文件名上传文件失败，文件字节为空。。。。");
            return null;
        }

        if (null == fileName || fileName.isEmpty()) {
            logger.info("根据字节和文件名上传文件失败，文件名为空。。。。");
            return null;
        }

        final String currentDate = DateUtil.dateToString(new Date(), "yyyyMM");

        ufce = ufce == null ? UploadFileCategoryEnum.temp : ufce;
        final String fileSaveDir = DRIVER_URL + "/" + ufce + "/" + currentDate + "/";
        final String fileAbsPath = fileSaveDir + fileName;
        DataOutputStream out = null;
        InputStream is = null;
        FileUploadBean fileUploadBean = null;
        try {
            logger.info("#################Start Upload########################");
            File f = FileUtil.getFile(fileSaveDir);
            if (!f.exists())
                FileUtil.forceMkdir(f);
            Date startTime = new Date();
            out = new DataOutputStream(new FileOutputStream(fileAbsPath));
            out.write(bytes);
            fileUploadBean = uploadFile(new File(fileAbsPath), ufce, null);
            Date endTime = new Date();
            logger.info(
                    "###################End Upload#########################,total spend time:" + (endTime.getTime() - startTime.getTime()) + "ms");
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.error("根据字节和文件名上传文件异常：" + exception.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("根据字节和文件名上传文件，关闭输入流异常：", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("根据字节和文件名上传文件，关闭数据输出流流异常：" + e.getMessage());
                }
            }
        }
        return fileUploadBean;
    }

    @Override
    public Map<String, Object> moveUploadFile(String moveId, String repId) {
        return uploadFileSetRefDao.moveUploadFile(moveId, repId);
    }

}
