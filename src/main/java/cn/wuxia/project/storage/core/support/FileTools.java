/*
* Created on :2018年1月11日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.project.storage.core.support;

import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileTools {
    public static InputStream getInputStream(UploadFileInfo uploadFileInfo) throws IOException {
        try {
            return HttpClientUtil.download(uploadFileInfo.getUrl());
        } catch (HttpClientException e) {
            throw new IOException(e);
        }
    }

    public static File getFile(UploadFileInfo uploadFileInfo) throws IOException {
        String fileName = InitializationFile.driveUrl + uploadFileInfo.getLocation();
        return HttpClientUtil.download(uploadFileInfo.getUrl(), fileName);
    }
}
