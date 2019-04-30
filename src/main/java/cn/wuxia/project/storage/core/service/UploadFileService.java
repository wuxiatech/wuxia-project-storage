/*
 * Copyright 2011-2020 www.gzzuji.com All right reserved.
 */
package cn.wuxia.project.storage.core.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.wuxia.project.storage.core.bean.FileUploadBean;
import cn.wuxia.project.storage.core.bean.UploadFileInfoDTO;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.model.UploadFileInfo;

/**
 * upload file interface.
 * 
 * @author songlin.li
 * @since 2012-06-21
 */
public interface UploadFileService {

    /**
     * 物理删除图片
     * 
     * @author 金
     * @param uploadFileInfoId
     */
    public void deleteFileInfoByPhysical(String uploadFileInfoId);

    /**
     * 逻辑删除图片
     * @param uploadFileInfoId
     */
    public void deleteFileInfoByLogical(String uploadFileInfoId);

    /**
     * uploadFile
     * 
     * @author songlin.li
     * @param request
     * @return
     */
    public FileUploadBean uploadFile(HttpServletRequest request, String FileInputName);

    /**
     * 上传网络图片
     * @author 金
     * @param httpUrl   图片url
     * @param uploadCategory    保存图片地址分组
     * @return
     */
    public FileUploadBean uploadFile(String httpUrl, UploadFileCategoryEnum uploadCategory);

    /**
     * 上传指定文件
     * @author songlin
     * @param uploadFile
     * @param uploadCategory
     * @param uploadFileSetId
     * @return
     */
    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory, String uploadFileSetId, boolean genMD5);

    /**
     * 上传指定文件
     * @author songlin
     * @param uploadFile
     * @param uploadCategory
     * @return
     */
    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory, String uploadFileSetId);

    /**
     * 上传指定文件
     * @author songlin
     * @param uploadFile
     * @param uploadCategory
     * @return
     */
    public FileUploadBean uploadFile(File uploadFile, UploadFileCategoryEnum uploadCategory);

    /**
     * 批量上传指定文件
     * @author songlin
     * @param uploadFiles
     * @param uploadCategory
     * @return
     */
    public FileUploadBean[] batchUploadFile(File[] uploadFiles, UploadFileCategoryEnum uploadCategory);

    /**
     * 批量上传
     * @param uploadFiles
     * @param uploadCategory
     * @param uploadFileSetInfoId
     * @return
     */
    public FileUploadBean[] batchUploadFile(File[] uploadFiles, UploadFileCategoryEnum uploadCategory, String uploadFileSetInfoId);

    /**
     * check Existing UploadFile
     * 
     * @author songlin.li
     * @param uploadFileSetInfoId
     * @param fileName
     * @return
     */
    public boolean checkExistingUploadFile(String uploadFileSetInfoId, String fileName);

    /**
     * get UploadFileInfo By uploadFileInfoId
     * 
     * @author songlin.li
     * @param uploadFileInfoId
     * @return
     */
    public UploadFileInfo getUploadFileInfoById(String uploadFileInfoId);

    /**
     * 修改原图片名称
     * @author wuwenhao
     * @param fileName //修改名称
     * @param uploadFileInfoId 修改Id
     */
    public void updateByUploadId(String fileName, String uploadFileInfoId);

    /***
     * 根据id获得素材dto
     * @author huangzhihua
     * @param uploadFileInfoId
     * @return
     */
    public UploadFileInfoDTO getUploadFileInfoDTOById(String uploadFileInfoId);

    /**
     * 修改UploadFileInfo
     * @author XiaoBing
     * 
     * @param uploadFileInfo
     */
    public void saveUploadFileInfo(UploadFileInfo uploadFileInfo);

    /**
     * 根据filename查找记录
     * 
     * @author songlin.li
     * @param fileName
     */
    public List<UploadFileInfo> findByFileName(String fileName);

    /**
     * 根据字节和文件名称上传文件
     * @param bytes 文件字节对象
     * @param fileName文件名称，包括文件后缀
     * @param 上传目录
     * @return 上传文件属性
     * @author Wind.Zhao
     * @date 2015/11/27
     */
    public FileUploadBean uploadFileByBytes(byte[] bytes, String fileName, UploadFileCategoryEnum ufce);

    /**
     * 移动图片位置
     * @author wuwenhao
     * @param moveId
     * @param repId
     * @param position
     * @return
     */
    public Map<String, Object> moveUploadFile(String moveId, String repId);

}
