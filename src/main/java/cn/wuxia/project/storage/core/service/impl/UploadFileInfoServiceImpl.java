/*
 * Copyright 2011-2020 wuxia.gd.cn All right reserved.
 */
package cn.wuxia.project.storage.core.service.impl;

import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.FileUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.util.reflection.ConvertUtil;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.project.storage.core.bean.NewFileToSaveDTO;
import cn.wuxia.project.storage.core.dao.UploadFileInfoDao;
import cn.wuxia.project.storage.core.dao.UploadFileSetInfoDao;
import cn.wuxia.project.storage.core.dao.UploadFileSetRefDao;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetRef;
import cn.wuxia.project.storage.core.service.UploadFileInfoService;
import cn.wuxia.project.storage.core.support.InitializationFile;
import cn.wuxia.project.storage.upload.service.UploadHandler;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * upload file class.
 *
 * @author songlin.li
 * @since 2012-06-21
 */
@Service
public class UploadFileInfoServiceImpl extends CommonServiceImpl<UploadFileInfo, String> implements UploadFileInfoService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UploadFileInfoDao uploadFileInfoDao;

    @Autowired
    private UploadFileSetInfoDao uploadFileSetInfoDao;

    @Autowired
    private UploadFileSetRefDao uploadFileSetRefDao;

    @Autowired
    private UploadHandler uploadHandler;
    /**
     * 文件存储起始路径
     */
    protected final static String DRIVER_URL = InitializationFile.driveUrl;

    @Override
    public void deleteFileInfoByPhysical(String uploadFileInfoId) {
        UploadFileInfo info = uploadFileInfoDao.get(uploadFileInfoId);
        String bucketName = info.getBucketName();
        String endpint = info.getEndpoint();
        String location = info.getLocation();
        if (InitializationFile.isPutToAliOss) {
            try {
                uploadHandler.getOssUploader().getOssClient().deleteObject(bucketName, location);
            } catch (Exception e) {
                logger.error("物理删除异常：" + e.getMessage());
            }
        } else {
            File file = new File(DRIVER_URL + info.getLocation());
            try {
                FileUtil.forceDelete(file);
            } catch (IOException e) {
                throw new AppServiceException("{error.deleteFile}:{}", e.getMessage());
            }
        }
        uploadFileInfoDao.delete(info);
        uploadFileSetRefDao.deleteFile(uploadFileInfoId);
    }

    @Override
    public void deleteFileInfoByLogical(String uploadFileInfoId) {
        uploadFileInfoDao.deleteEntityById(uploadFileInfoId);
        uploadFileSetRefDao.deleteFile(uploadFileInfoId);
    }


    @Override
    public boolean checkExistingUploadFile(String uploadFileSetInfoId, String fileName) {
        if (StringUtil.isBlank(uploadFileSetInfoId)) {
            return false;
        }
        List<UploadFileInfo> uploadFileInfoList = uploadFileInfoDao.findByUploadsetId(uploadFileSetInfoId);
        List<String> nameList = ConvertUtil.convertElementPropertyToList(uploadFileInfoList, "fileName");
        if (nameList.contains(fileName))
            return true;
        return false;
    }


    @Override
    @Deprecated
    public void updateByUploadId(String fileName, String uploadFileInfoId) {
        UploadFileInfo u = uploadFileInfoDao.get(uploadFileInfoId);
        u.setFileName(fileName);
        uploadFileInfoDao.save(u);
    }

    @Override
    public void saveNewUploadFile(NewFileToSaveDTO newFileToSaveDTO) {
        UploadFileInfo uploadFileInfo = new UploadFileInfo();
        BeanUtil.copyPropertiesWithoutNullValues(uploadFileInfo, newFileToSaveDTO);
        uploadFileInfoDao.save(uploadFileInfo);
        //保存文件夹
        UploadFileSetInfo uploadFileSetInfo = null;
        if (StringUtil.isNotBlank(newFileToSaveDTO.getUploadFileSetId())) {
            uploadFileSetInfo = uploadFileSetInfoDao.getEntityById(newFileToSaveDTO.getUploadFileSetId());
        }
        if (null == uploadFileSetInfo) {
            uploadFileSetInfo = new UploadFileSetInfo();
            uploadFileSetInfo.setCategory(newFileToSaveDTO.getUploadFileCategory());
            uploadFileSetInfo.setCreatedOn(DateUtil.newInstanceDate());
            uploadFileSetInfo.setCreatedBy("");
            this.uploadFileSetInfoDao.save(uploadFileSetInfo);
        }
        UploadFileSetRef uploadFileSetRef = new UploadFileSetRef();
        uploadFileSetRef.setUploadFilesetId(uploadFileSetInfo.getId());
        uploadFileSetRef.setUploadFileId(uploadFileInfo.getId());
        uploadFileSetRef.setSortOrder(System.currentTimeMillis());
        uploadFileSetRef.setCreatedOn(DateUtil.newInstanceDate());
        uploadFileSetRefDao.save(uploadFileSetRef);

        newFileToSaveDTO.setUploadFileSetId(uploadFileSetInfo.getId());
        newFileToSaveDTO.setId(uploadFileSetInfo.getId());
    }


    @Override
    public List<UploadFileInfo> findByFileName(String fileName) {
        return uploadFileInfoDao.findBy("fileName", fileName);
    }


    @Override
    public Map<String, Object> moveUploadFile(String moveId, String repId) {
        return uploadFileSetRefDao.moveUploadFile(moveId, repId);
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
            List<UploadFileInfo> files = uploadFileInfoDao.findByUploadsetId(uploadFileSetInfoId);
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

    @Override
    public List<UploadFileInfo> findBySetId(String uploadFileSetId) {
        return uploadFileInfoDao.findByUploadsetId(uploadFileSetId);
    }

    @Override
    protected CommonDao<UploadFileInfo, String> getCommonDao() {
        return uploadFileInfoDao;
    }
}
