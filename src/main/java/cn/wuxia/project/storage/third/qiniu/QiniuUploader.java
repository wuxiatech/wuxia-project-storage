package cn.wuxia.project.storage.third.qiniu;

import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.project.storage.third.alioss.OssUploader;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

public class QiniuUploader {
    private final Logger logger = LoggerFactory.getLogger(OssUploader.class);
    static Properties properties = PropertiesUtils.loadProperties("classpath:qiniu.config.properties", "classpath:properties/qiniu.config.properties");
    static String callbackUrl = properties.getProperty("qiniu.callbackUrl");
    private UploadManager uploadManager;
    private QiniuConfig config;

    public QiniuUploader() {

    }

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
    }


    public UploadManager getUploadManager() {
        return uploadManager;
    }

    public void setUploadManager(UploadManager uploadManager) {
        this.uploadManager = uploadManager;
    }


    public QiniuConfig getConfig() {
        return config;
    }

    public void setConfig(QiniuConfig config) {
        this.config = config;
    }

    public QiniuUploader init() {
        return new QiniuUploader(QiniuConfig.buildFromProperties());
    }

    public static QiniuUploader build() {
        return new QiniuUploader().init();
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

    public void delete(String filePath) {

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

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        StringMap putPolicy = new StringMap();
//        putPolicy.put("callbackUrl", "http://api.example.com/qiniu/upload/callback");
//        putPolicy.put("callbackBody", "key=$(key)&hash=$(etag)&bucket=$(bucket)&fsize=$(fsize)");
        long expireSeconds = 3600;
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        return auth.uploadToken(config.getBucket(), null, expireSeconds, putPolicy);

    }

    /**
     * 公有空间返回文件URL
     *
     * @param fileName
     * @return
     */
    public static String url(String fileName) {
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalUrl = String.format("%s/%s", callbackUrl, encodedFileName);
        System.out.println(finalUrl);
        return finalUrl;
    }


}
