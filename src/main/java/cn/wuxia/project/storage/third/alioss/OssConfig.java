/*
 * Copyright 2011-2020 www.gzzuji.com All right reserved.
 */
package cn.wuxia.project.storage.third.alioss;

import cn.wuxia.common.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Properties;

/**
 * file upload folder initialization.
 *
 * @author songlin.li
 * @since 2018-11-22
 */
public class OssConfig {
    private static final Logger logger = LoggerFactory.getLogger(OssConfig.class);

    //oss站点
    private String endpoint;

    //oss访问key id
    private String accessKeyId;

    //oss访问key 密钥
    private String accessKeySecret;

    //oss bucket
    private String bucketName;


    // 初始化各个文件夹
    public static OssConfig buildFromProperties() {
        final Properties propertie = PropertiesUtils.loadProperties("classpath:properties/oss.default.properties",
                "classpath:oss.config.properties");
        //oss站点
        String endpoint = propertie.getProperty("endpoint");

        //oss访问key id
        String accessKeyId = propertie.getProperty("accessKeyId");

        //oss访问key 密钥
        String accessKeySecret = propertie.getProperty("accessKeySecret");

        //oss bucket
        String bucketName = propertie.getProperty("bucketName");
        logger.info("开始初始化上传配置。。。。。。。。。。");

        Assert.notNull(bucketName, "bucketName不能为空");
        Assert.notNull(endpoint, "endpoint不能为空");
        Assert.notNull(accessKeyId, "accessKeyId不能为空");
        Assert.notNull(accessKeySecret, "accessKeySecret不能为空");
        OssConfig ossConfig = new OssConfig();
        ossConfig.setEndpoint(endpoint);
        ossConfig.setAccessKeyId(accessKeyId);
        ossConfig.setAccessKeySecret(accessKeySecret);
        ossConfig.setBucketName(bucketName);
        return ossConfig;

    }


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
