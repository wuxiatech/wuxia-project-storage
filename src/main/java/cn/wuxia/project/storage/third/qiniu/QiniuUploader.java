package cn.wuxia.project.storage.third.qiniu;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.storage.third.alioss.OssUploader;
import cn.wuxia.project.storage.upload.UploadException;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

/**
 * @author songlin
 */
@Getter
public class QiniuUploader {
    private final Logger logger = LoggerFactory.getLogger(OssUploader.class);
    private String accessDomain;
    private boolean isPrivate;

    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private QiniuConfig config;
    private Auth auth;


    public QiniuUploader(QiniuConfig config) {
        this.config = config;

        //构造一个带指定Zone对象的配置类
        Zone zone = Zone.autoZone();
        if (StringUtils.isNotBlank(config.getZone())) {
            try {
                zone = (Zone) Zone.class.getMethod(config.getZone()).invoke(null);
            } catch (Exception e) {
                logger.error("初始化七牛上传报错", e);
            }
        }
        Configuration cfg = new Configuration(zone);
        this.uploadManager = new UploadManager(cfg);
        this.auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        this.bucketManager = new BucketManager(auth, cfg);
    }

    /**
     * 单列properties配置
     *
     * @return
     * @throws UploadException
     */
    public static QiniuUploader build() throws UploadException {
        Properties properties = PropertiesUtils.loadProperties("classpath:qiniu.config.properties", "classpath:properties/qiniu.config.properties");
        QiniuConfig qiniuConfig = QiniuConfig.buildFromProperties(properties);
        QiniuUploader qiniuUploader = new QiniuUploader(qiniuConfig);
        qiniuUploader.accessDomain = properties.getProperty("qiniu.domain");
        qiniuUploader.isPrivate = BooleanUtils.toBoolean(properties.getProperty("qiniu.bucket.private"));
        return qiniuUploader;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws QiniuException
     */
    public String upload(File file, String fileSavePath) throws QiniuException {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Response response = uploadManager.put(file, fileSavePath, getUpToken());
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

    /**
     * 上传文件
     *
     * @param file
     * @param fileSavePath
     * @param replaceFileKey 被覆盖的文件key
     * @return
     * @throws QiniuException
     */
    public String uploadForce(File file, String fileSavePath, String replaceFileKey) throws QiniuException {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Response response = uploadManager.put(file, fileSavePath, getUpToken(replaceFileKey));
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

    /**
     * 上传文件,如果存在则会被覆盖
     *
     * @param file
     * @param fileSavePath
     * @return
     * @throws QiniuException
     */
    public String uploadForce(File file, String fileSavePath) throws QiniuException {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Response response = uploadManager.put(file, fileSavePath, getUpToken(fileSavePath));
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

    /**
     * 上传文件流
     *
     * @param inputStream
     * @return
     * @throws QiniuException
     */
    public String upload(InputStream inputStream, String fileSavePath) throws QiniuException {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Response response = uploadManager.put(inputStream, fileSavePath, getUpToken(), null, "application/octet-stream");
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

    /**
     * 上传文件流
     *
     * @param inputStream
     * @return
     * @throws QiniuException
     */
    public String uploadForce(InputStream inputStream, String fileSavePath) throws QiniuException {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Response response = uploadManager.put(inputStream, fileSavePath, getUpToken(fileSavePath), null, "application/octet-stream");
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

    public void delete(String filePath) {
        try {
            bucketManager.delete(config.getBucket(), filePath);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

    /**
     * 上传文件流
     *
     * @param bytes
     * @return
     * @throws QiniuException
     */
    public String upload(byte[] bytes) throws QiniuException {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Response response = uploadManager.put(bytes, key, getUpToken());
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

    /**
     * 简单上传，使用默认策略，只需要设置上传的空间名就可以了
     */
    public String getUpToken() {
        StringMap putPolicy = new StringMap();
/**
 * 自定义返回格式
 */
//        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
        long expireSeconds = 3600;
        return auth.uploadToken(config.getBucket(), null, expireSeconds, putPolicy);

    }

    /**
     * 覆盖上传
     */
    public String getUpToken(String replaceFileKey) {
        StringMap putPolicy = new StringMap();
        /**
         * 自定义返回格式
         */
//        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
        long expireSeconds = 3600;
        return auth.uploadToken(config.getBucket(), replaceFileKey, expireSeconds, putPolicy);
    }

    /**
     * 公有空间返回文件URL
     *
     * @param fileName
     * @return
     */
    public String url(String fileName) {
        String encodedFileName = fileName;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalUrl = String.format("%s/%s", accessDomain, encodedFileName);
        System.out.println(finalUrl);
        return finalUrl;
    }

    /**
     * 增加支持private bucket 的支持1天的有效期
     *
     * @param key
     * @return
     */
    public String privateDownloadUrl(String key) {
        return privateDownloadUrl(key, DateUtil.addDays(new Date(), 1));
    }

    /**
     * 增加支持private bucket 的支持1天的有效期
     *
     * @param key
     * @return
     */
    public String privateDownloadUrl(String key, Date limitDate) {
        if (!isPrivate) {
            return url(key);
        }
        long expires = (limitDate.getTime() - System.currentTimeMillis()) / 1000L;
        if (StringUtil.startsWithIgnoreCase(key, "http://") || StringUtil.startsWithIgnoreCase(key, "https://")) {
            return auth.privateDownloadUrl(key, expires);
        } else {
            return auth.privateDownloadUrl(accessDomain + "/" + key, expires);
        }
    }


    public String getAccessDomain() {
        return accessDomain;
    }

    public void setAccessDomain(String accessDomain) {
        this.accessDomain = accessDomain;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
