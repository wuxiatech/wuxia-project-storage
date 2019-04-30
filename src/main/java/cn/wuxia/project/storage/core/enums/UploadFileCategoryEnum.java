/*
 * Copyright 2011-2020 www.gzzuji.com All right reserved.
 */
package cn.wuxia.project.storage.core.enums;

import java.io.File;

import cn.wuxia.project.storage.core.support.InitializationFile;
import cn.wuxia.common.util.StringUtil;

/**
 * file upload folder enum.
 * 
 * @author songlin.li
 * @since 2012-06-15
 */
public enum UploadFileCategoryEnum {
	def, temp, gallery, news, logo, qrcode, music, video, vocie, wechat, file;

	public static UploadFileCategoryEnum get(String value) {
		for (UploadFileCategoryEnum e : UploadFileCategoryEnum.values()) {
			if (StringUtil.equalsIgnoreCase(value, e.name())) {
				return e;
			}
		}
		return def;
	}

	/**
	 * 得到文件保存的绝对路径
	 * 
	 * @author songlin
	 * @param e
	 * @return
	 */
	public static String getUploadAbsolutePath(UploadFileCategoryEnum e) {
		return InitializationFile.driveUrl + File.separator + e.name();
	}
}
