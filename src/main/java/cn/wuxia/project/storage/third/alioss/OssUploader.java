package cn.wuxia.project.storage.third.alioss;

import cn.wuxia.aliyun.components.oss.OSSUtils;
import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import com.qiniu.common.QiniuException;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

public class OssUploader {
    private final Logger logger = LoggerFactory.getLogger(OssUploader.class);
    static Properties properties = PropertiesUtils.loadProperties("classpath:oss.config.properties", "classpath:properties/oss.config.properties");
    static String callbackUrl = properties.getProperty("qiniu.callbackUrl");
    private OSSUtils ossClient;
    private OssConfig config;

    public OssUploader() {

    }

    public OssUploader(OssConfig config) {
        this.config = config;
        try {
            ossClient = new OSSUtils(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        } catch (Exception e) {
            logger.error("初始化阿里云oss上传报错", e);
        }
    }


    public OssConfig getConfig() {
        return config;
    }

    public void setConfig(OssConfig config) {
        this.config = config;
    }

    public OssUploader init() {
        return new OssUploader(OssConfig.buildFromProperties());
    }

    public static OssUploader build() {
        return new OssUploader().init();
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public Map upload(File file, String fileSavePath) throws Exception {
        String newFileName = StringUtil.substringAfterLast(fileSavePath, File.separator);
        String fileSaveDir = StringUtil.substringBeforeLast(fileSavePath, File.separator);
        Map<String, Object> ossMap = ossClient.putObject(config.getBucketName(), fileSaveDir, newFileName, file);
        if (StringUtil.isNotBlank(MapUtils.getString(ossMap, "msg"))) {
            logger.error("{}", ossMap);
            throw new Exception(MapUtils.getString(ossMap, "msg"));
        }
        return ossMap;
    }

    /**
     * 上传文件流
     *
     * @param inputStream
     * @return
     * @throws QiniuException
     */
    public Map upload(InputStream inputStream, String fileSavePath) throws Exception {
        String newFileName = StringUtil.substringAfterLast(fileSavePath, File.separator);
        String fileSaveDir = StringUtil.substringBeforeLast(fileSavePath, File.separator);
        Map<String, Object> ossMap = ossClient.putObject(config.getBucketName(), fileSaveDir, newFileName, inputStream);
        if (StringUtil.isNotBlank(MapUtils.getString(ossMap, "msg"))) {
            logger.error("{}", ossMap);
            throw new Exception(MapUtils.getString(ossMap, "msg"));
        }
        return ossMap;
    }

    public void delete(String filePath) {
        getOssClient().deleteObject(getConfig().getBucketName(), filePath);
    }

    public OSSUtils getOssClient() {
        return ossClient;
    }

    public void setOssClient(OSSUtils ossClient) {
        this.ossClient = ossClient;
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
