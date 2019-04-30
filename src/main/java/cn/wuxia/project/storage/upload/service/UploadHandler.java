package cn.wuxia.project.storage.upload.service;

import cn.wuxia.project.storage.third.alioss.OssUploader;
import cn.wuxia.project.storage.third.qiniu.QiniuUploader;
import cn.wuxia.project.storage.upload.UploadException;
import cn.wuxia.project.storage.upload.bean.UploadRespone;
import com.qiniu.common.QiniuException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Component
public class UploadHandler {

    OssUploader ossUploader;
    QiniuUploader qiniuUploader;

    public UploadRespone uploadToAliOss(File file, String fileSavePath) throws UploadException {
        if (ossUploader == null) {
            ossUploader = OssUploader.build();
        }
        try {
            Map result = ossUploader.upload(file, fileSavePath);
            return new UploadRespone(MapUtils.getString(result, "key"), MapUtils.getString(result, "url"), ossUploader.getConfig().getBucketName());
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }


    public UploadRespone uploadToQiniuOss(File file, String fileSavePath) throws UploadException {
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }

        try {
            String key = qiniuUploader.upload(file, fileSavePath);
            return new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.getConfig().getBucket());
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }


    public UploadRespone uploadToAliOss(InputStream inputStream, String fileSavePath) throws UploadException {
        if (ossUploader == null) {
            ossUploader = OssUploader.build();
        }
        try {
            Map result = ossUploader.upload(inputStream, fileSavePath);
            return new UploadRespone(MapUtils.getString(result, "key"), MapUtils.getString(result, "url"), ossUploader.getConfig().getBucketName());
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }


    public UploadRespone uploadToQiniuOss(InputStream inputStream, String fileSavePath) throws UploadException {
        if (qiniuUploader == null) {
            qiniuUploader = QiniuUploader.build();
        }

        try {
            String key = qiniuUploader.upload(inputStream, fileSavePath);

            UploadRespone uploadRespone = new UploadRespone(key, QiniuUploader.url(key), qiniuUploader.getConfig().getBucket());
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
