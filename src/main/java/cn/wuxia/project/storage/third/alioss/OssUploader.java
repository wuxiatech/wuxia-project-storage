package cn.wuxia.project.storage.third.alioss;

import cn.wuxia.aliyun.components.oss.OSSUtils;
import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.EncodeUtils;
import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.storage.upload.UploadException;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.CannedAccessControlList;
import com.qiniu.common.QiniuException;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Getter
public class OssUploader {
    private final Logger logger = LoggerFactory.getLogger(OssUploader.class);
    private String accessDomain;
    private OSSUtils ossClient;
    private OssConfig config;


    public OssUploader(OssConfig config) {
        this.config = config;
        try {
            ossClient = new OSSUtils(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        } catch (Exception e) {
            logger.error("初始化阿里云oss上传报错", e);
        }
    }

    /**
     * properties初始化方式
     *
     * @return
     * @throws UploadException
     */
    public static OssUploader build() throws UploadException {
        Properties properties = PropertiesUtils.loadProperties("classpath:oss.config.properties", "classpath:properties/oss.config.properties");
        OssUploader ossUploader = new OssUploader(OssConfig.buildFromProperties(properties));
        ossUploader.accessDomain = properties.getProperty("oss.domain");
        return ossUploader;
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
        getOssClient().deleteObject(config.getBucketName(), filePath);
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
    public String url(String fileName) {
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String finalUrl = String.format("%s/%s", accessDomain, encodedFileName);
        System.out.println(finalUrl);
        return finalUrl;
    }

    public String privateDownloadUrl(String key) {
        return privateDownloadUrl(key, DateUtil.addHours(new Date(), 1));
    }

    /**
     * 增加支持private bucket 的支持1天的有效期
     *
     * @param key
     * @return
     */
    public String privateDownloadUrl(String key, Date limitDate) {
        if (StringUtil.startsWithIgnoreCase(key, "http://")) {
            key = StringUtil.substringAfter(key, "http://");
            key = StringUtil.substringAfter(key, "/");
        } else if (StringUtil.startsWithIgnoreCase(key, "https://")) {
            key = StringUtil.substringAfter(key, "https://");
            key = StringUtil.substringAfter(key, "/");
        } else if (StringUtil.startsWith(key, "/")) {
            key = StringUtil.substringAfter(key, "/");
        }

        String bucketName = config.getBucketName();
        AccessControlList acl = getOssClient().client.getBucketAcl(bucketName);
        URI uri = getOssClient().client.getEndpoint();
        String domainUri = uri.getScheme() + "://" + bucketName + "." + uri.getAuthority();
        if (acl.getCannedACL() == CannedAccessControlList.Private) {
            String url = EncodeUtils.urlDecode(getOssClient().client.generatePresignedUrl(bucketName, key, limitDate).toString());
            if (StringUtil.isBlank(accessDomain)) {
                return url;
            } else {
                return StringUtil.replace(url, domainUri, accessDomain);
            }
        } else {
            if (StringUtil.isBlank(accessDomain)) {
                return domainUri + "/" + key;
            } else {
                return accessDomain + "/" + key;
            }
        }

    }


}
