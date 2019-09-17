/*
 * Copyright 2011-2020 www.gzzuji.com All right reserved.
 */
package cn.wuxia.project.storage.core.support;

import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.basic.support.ApplicationPropertiesUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * file upload folder initialization.
 *
 * @author songlin
 * @modified 2018-11-22
 * @since 2012-06-15
 */
public class InitializationFile {
    private static final Logger logger = LoggerFactory.getLogger(InitializationFile.class);

    private final static Properties propertie = PropertiesUtils.loadProperties("classpath:properties/oss.default.properties",
            "classpath:oss.config.properties", "classpath:properties/qiniu.default.properties",
            "classpath:qiniu.config.properties");

    // 获取当前程序发布盘符
    public static String driveUrl = System.getProperty("java.io.tmpdir");

    /**
     * 当使用下载时需要将 driveUrl 替换为 downloadUrl
     */
    public static String downloadUrl = "";


    //判断是否保存到oss上，若需要保存到os则不保存到服务器
    public final static boolean isPutToAliOss = BooleanUtils.toBoolean(propertie.getProperty("isPutToAliOss"));

    //判断是否保存到oss上，若需要保存到os则不保存到服务器
    public final static boolean isPutToQiniuOss =
            BooleanUtils.toBoolean(propertie.getProperty("isPutToQiniuOss"));

    // 初始化各个文件夹
    static {
        logger.info("开始初始化上传配置。。。。。。。。。。");
        String downloadCtx = ApplicationPropertiesUtil.getPropertiesValue("ctx.download");
        if (StringUtil.isNotBlank(downloadCtx)) {
            downloadUrl = downloadCtx;
        }
        String uploadfilePath = ApplicationPropertiesUtil.getPropertiesValue("file.uploadfile.path");
        /**
         * 如果用户有设定存储路径
         */
        if (StringUtil.isNotBlank(uploadfilePath)) {
            driveUrl = uploadfilePath;
        }
        logger.info("文件存放地址：{}， 下载地址：{}，是否使用阿里云oss：{}, 是否使用七牛oss:{}", driveUrl, downloadUrl, isPutToAliOss, isPutToQiniuOss);

        //        String folder = driveUrl + File.separator;

        //        for (UploadFileCategoryEnum fuEnum : UploadFileCategoryEnum.values()) {
        //            try {
        //                File file = new File(folder + fuEnum.name());
        //                if (!file.exists()) {
        //                    FileUtils.forceMkdir(file);
        //                    logger.debug(" mkdir " + folder + fuEnum.name());
        //                }
        //            } catch (IOException e) {
        //                logger.error(fuEnum.name() + " can't mkdir!");
        //            }
        //        }
    }

}
