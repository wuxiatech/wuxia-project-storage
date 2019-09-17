/*
 * Copyright 2011-2020 www.gzzuji.com All right reserved.
 */
package cn.wuxia.project.storage.core.service;

import cn.wuxia.project.common.service.CommonService;
import cn.wuxia.project.storage.core.bean.NewFileToSaveDTO;
import cn.wuxia.project.storage.core.model.UploadFileInfo;

import java.util.List;
import java.util.Map;

/**
 * upload file interface.
 *
 * @author songlin.li
 * @since 2012-06-21
 */
public interface UploadFileInfoService extends CommonService<UploadFileInfo, String> {

    /**
     * 物理删除图片
     *
     * @param uploadFileInfoId
     * @author 金
     */
    public void deleteFileInfoByPhysical(String uploadFileInfoId);

    /**
     * 逻辑删除图片
     *
     * @param uploadFileInfoId
     */
    public void deleteFileInfoByLogical(String uploadFileInfoId);


    /**
     * check Existing UploadFile
     *
     * @param uploadFileSetInfoId
     * @param fileName
     * @return
     * @author songlin.li
     */
    public boolean checkExistingUploadFile(String uploadFileSetInfoId, String fileName);


    /**
     * 修改原图片名称
     *
     * @param fileName         //修改名称
     * @param uploadFileInfoId 修改Id
     * @author wuwenhao
     */
    public void updateByUploadId(String fileName, String uploadFileInfoId);


    /**
     *
     * @param newFileToSaveDTO
     * @author songlin
     */
    public void saveNewUploadFile(NewFileToSaveDTO newFileToSaveDTO);

    /**
     * 根据filename查找记录
     *
     * @param fileName
     * @author songlin.li
     */
    public List<UploadFileInfo> findByFileName(String fileName);


    /**
     * 移动图片位置
     *
     * @param moveId
     * @param repId
     * @return
     * @author wuwenhao
     */
    public Map<String, Object> moveUploadFile(String moveId, String repId);


    /**
     * 以uploadFileSetInfo ID复制set实体(关联其下所有uploadFileInfo实体）
     *
     * @param uploadFileSetInfoId
     * @return 返回新的uploadFileSetInfoId
     * @author modify by songlin
     */
    public String copy(String uploadFileSetInfoId);


    List<UploadFileInfo> findBySetId(String uploadFileSetId);
}
