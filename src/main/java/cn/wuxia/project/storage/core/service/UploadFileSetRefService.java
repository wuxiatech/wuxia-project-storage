/*
 * Copyright 2011-2020 www.gzzuji.com All right reserved.
 */
package cn.wuxia.project.storage.core.service;

import java.util.List;

import cn.wuxia.project.storage.core.bean.FileInfoDto;

public interface UploadFileSetRefService {

    /***
     * 获得文件夹里面所有文件路径
     * @param filesetId
     * @return
     */
    List<FileInfoDto> getFileInfo(String filesetId);

    /**
     * 以uploadFileSetInfo ID复制set实体(关联其下所有uploadFileInfo实体）
     * @author modify by songlin
     * @param 返回新的uploadFileSetInfoId
     * @return 返回新的uploadFileSetInfoId
     */
    public String copy(String uploadFileSetInfoId);
}
