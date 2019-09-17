package cn.wuxia.project.storage.upload.service;

import cn.wuxia.common.util.BytesUtil;
import cn.wuxia.project.storage.third.alioss.OssUploader;
import cn.wuxia.project.storage.third.qiniu.QiniuUploader;
import cn.wuxia.project.storage.upload.UploadException;
import cn.wuxia.project.storage.upload.bean.UploadRespone;
import com.qiniu.common.QiniuException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

@Component
@Slf4j
public class UploadHandler {

    OssUploader ossUploader;
    QiniuUploader qiniuUploader;


    public UploadRespone uploadToAliOss(File file, String fileSavePath) throws UploadException {
        if (ossUploader == null) {
            ossUploader = OssUploader.build();
        }
        String fileMD5 = "";
        try {
            fileMD5 = DigestUtils.md5Hex(new FileInputStream(file));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            Map result = ossUploader.upload(file, fileSavePath);
            return new UploadRespone(MapUtils.getString(result, "key"), MapUtils.getString(result, "url") + "/" + MapUtils.getString(result, "key"), ossUploader.privateDownloadUrl(MapUtils.getString(result, "key")), ossUploader.getConfig().getBucketName(), fileMD5);
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }

    public UploadRespone uploadToAliOss(InputStream inputStream, String fileSavePath) throws UploadException {
        if (ossUploader == null) {
            ossUploader = OssUploader.build();
        }
        String fileMD5 = "";
        ByteArrayOutputStream outputStream = BytesUtil.cloneInputStream(inputStream);
        try {
            fileMD5 = DigestUtils.md5Hex(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            Map result = ossUploader.upload(new ByteArrayInputStream(outputStream.toByteArray()), fileSavePath);
            return new UploadRespone(MapUtils.getString(result, "key"), MapUtils.getString(result, "url") + "/" + MapUtils.getString(result, "key"), ossUploader.privateDownloadUrl(MapUtils.getString(result, "key")), ossUploader.getConfig().getBucketName(), fileMD5);
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }


    public UploadRespone uploadToQiniuOss(File file, String fileSavePath) throws UploadException {
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }
        String fileMD5 = "";
        try {
            fileMD5 = DigestUtils.md5Hex(new FileInputStream(file));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            String key = qiniuUploader.upload(file, fileSavePath);
            return new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.privateDownloadUrl(key), qiniuUploader.getConfig().getBucket(), fileMD5);
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }

    public UploadRespone uploadToQiniuOss(File file, String fileSavePath, boolean forceSave) throws UploadException {
        if (!forceSave) {
            return uploadToQiniuOss(file, fileSavePath);
        }
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }
        String fileMD5 = "";
        try {
            fileMD5 = DigestUtils.md5Hex(new FileInputStream(file));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            String key = qiniuUploader.uploadForce(file, fileSavePath);
            return new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.privateDownloadUrl(key), qiniuUploader.getConfig().getBucket(), fileMD5);
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }

    public UploadRespone uploadToQiniuOss(File file, String fileSavePath, String replaceOriginalFileKey) throws UploadException {
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }
        String fileMD5 = "";
        try {
            fileMD5 = DigestUtils.md5Hex(new FileInputStream(file));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            String key = qiniuUploader.uploadForce(file, fileSavePath, replaceOriginalFileKey);
            return new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.privateDownloadUrl(key), qiniuUploader.getConfig().getBucket(), fileMD5);
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }


    public UploadRespone uploadToQiniuOss(InputStream inputStream, String fileSavePath) throws UploadException {
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }
        String fileMD5 = "";
        ByteArrayOutputStream outputStream = BytesUtil.cloneInputStream(inputStream);
        try {
            fileMD5 = DigestUtils.md5Hex(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            String key = qiniuUploader.upload(new ByteArrayInputStream(outputStream.toByteArray()), fileSavePath);

            UploadRespone uploadRespone = new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.privateDownloadUrl(key), qiniuUploader.getConfig().getBucket(), fileMD5);
            return uploadRespone;
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }

    public UploadRespone uploadToQiniuOss(InputStream inputStream, String fileSavePath, boolean forceSave) throws UploadException {
        if (!forceSave) {
            return uploadToQiniuOss(inputStream, fileSavePath);
        }
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }
        String fileMD5 = "";
        ByteArrayOutputStream outputStream = BytesUtil.cloneInputStream(inputStream);
        try {
            fileMD5 = DigestUtils.md5Hex(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (Exception e) {
            log.warn("无法生成md5");
        }
        try {
            String key = qiniuUploader.upload(new ByteArrayInputStream(outputStream.toByteArray()), fileSavePath);

            UploadRespone uploadRespone = new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.privateDownloadUrl(key), qiniuUploader.getConfig().getBucket(), fileMD5);
            return uploadRespone;
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }

    public void deleteFileFromAliOss(String filePath) {
        ossUploader.delete(filePath);
    }

    public void deleteFileFromQiniuOss(String filePath) {
        qiniuUploader.delete(filePath);
    }

    public OssUploader getOssUploader() {
        return ossUploader;
    }

    public void setOssUploader(OssUploader ossUploader) {
        this.ossUploader = ossUploader;
    }

    public QiniuUploader getQiniuUploader() {
        return qiniuUploader;
    }

    public void setQiniuUploader(QiniuUploader qiniuUploader) {
        this.qiniuUploader = qiniuUploader;
    }
}
