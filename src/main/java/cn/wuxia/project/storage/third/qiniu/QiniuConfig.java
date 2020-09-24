package cn.wuxia.project.storage.third.qiniu;

import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.storage.upload.UploadException;
import com.qiniu.common.Zone;
import lombok.Data;

import java.util.Properties;

@Data
public class QiniuConfig {

    //...生成上传凭证，然后准备上传
    String accessKey;
    String secretKey;
    String bucket;
    /**
     * 华东	Zone.zone0()
     * 华北	Zone.zone1()
     * 华南	Zone.zone2()
     * 北美	Zone.zoneNa0()
     * 东南亚	Zone.zoneAs0()
     */
    String zone;

    public QiniuConfig() {
    }

    public QiniuConfig(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static QiniuConfig buildFromProperties(Properties properties) throws UploadException {
        if (properties == null) {
            throw new UploadException("properties不能为空");
        }
        QiniuConfig qiniuConfig = new QiniuConfig();
        qiniuConfig.setAccessKey(properties.getProperty("qiniu.accessKey"));
        qiniuConfig.setSecretKey(properties.getProperty("qiniu.secretKey"));
        qiniuConfig.setBucket(properties.getProperty("qiniu.bucket"));
        qiniuConfig.setZone(properties.getProperty("qiniu.zone"));

        /**
         * 预检查
         */
        if (StringUtil.isNotBlank(qiniuConfig.getZone())) {
            try {
                Zone.class.getMethod(properties.getProperty("qiniu.zone"));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("不存在分区" + qiniuConfig.getZone());
            }
        }
        return qiniuConfig;
    }

    public static QiniuConfig buildFromProperties() throws UploadException {
        Properties properties = PropertiesUtils.loadProperties("classpath:qiniu.config.properties", "classpath:properties/qiniu.config.properties");
        return buildFromProperties(properties);
    }

}
