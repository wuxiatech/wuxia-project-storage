/*
 * Copyright 2011-2020 wuxia.gd.cn All right reserved.
 */
package cn.wuxia.project.storage.core.service.impl;

import java.util.List;

import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import cn.wuxia.project.storage.core.bean.FileInfoDto;
import cn.wuxia.project.storage.core.dao.UploadFileSetInfoDao;
import cn.wuxia.project.storage.core.dao.UploadFileSetRefDao;
import cn.wuxia.project.storage.core.service.UploadFileSetRefService;
import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.util.reflection.BeanUtil;

@Service
@Transactional
public class UploadFileSetRefServiceImpl implements UploadFileSetRefService {

    @Autowired
    private UploadFileSetRefDao uploadFileSetRefDao;

    @Autowired
    private UploadFileSetInfoDao uploadFileSetInfoDao;

    @Override
    public List<FileInfoDto> getFileInfo(String filesetId) {
        Assert.notNull(filesetId, "文件id不为空");
        return uploadFileSetRefDao.getFileInfo(filesetId);
    }

    @Override
    public String copy(String uploadFileSetInfoId) {
        Assert.notNull(uploadFileSetInfoId, "uploadFileSetInfoId不能为空");
        //获得文件集合实体
        UploadFileSetInfo setInfo = uploadFileSetInfoDao.findById(uploadFileSetInfoId);
        if (setInfo != null) {
            UploadFileSetInfo newSetInfo = new UploadFileSetInfo();
            //复制文件集合实体
            BeanUtil.copyProperties(newSetInfo, setInfo);
            newSetInfo.setId(null);
            //                newSetInfo.setUploadFileInfos(null);
            uploadFileSetInfoDao.save(newSetInfo);
            //获得文件集合
            List<UploadFileInfo> files = uploadFileSetRefDao.findByUploadSetId(uploadFileSetInfoId);
            List<UploadFileSetRef> list = Lists.newArrayList();
            for (UploadFileInfo file : files) {
                list.add(new UploadFileSetRef(file.getId(), newSetInfo.getId()));
            }
            uploadFileSetRefDao.batchSave(list);
            return newSetInfo.getId();
        } else {
            throw new AppServiceException("无效uploadFileSetInfoId：" + uploadFileSetInfoId);
        }
    }
}
