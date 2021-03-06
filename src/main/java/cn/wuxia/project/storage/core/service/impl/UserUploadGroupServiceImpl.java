/*
* Created on :2014年11月19日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.tech All right reserved.
*/
package cn.wuxia.project.storage.core.service.impl;

import cn.wuxia.common.exception.AppDaoException;
import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.project.storage.core.dao.UserUploadGroupDao;
import cn.wuxia.project.storage.core.model.UserUploadGroup;
import cn.wuxia.project.storage.core.model.UserUploadGroupRef;
import cn.wuxia.project.storage.core.service.UserUploadGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserUploadGroupServiceImpl extends CommonServiceImpl<UserUploadGroup, String> implements UserUploadGroupService {

    @Autowired
    private UserUploadGroupDao userUploadGroupDao;

    @Autowired
    private UserUploadGroupRefServiceImpl userUploadGroupRefServiceImpl;

    @Override
    protected CommonDao<UserUploadGroup, String> getCommonDao() {
        return userUploadGroupDao;
    }

    @Override
    public List<Map<String, Object>> findByUserGroup(String uid) {
        return userUploadGroupDao.findByUserGroup(uid);
    }

    @Override
    public Boolean phyDeleteById(String groupId) {
        if (StringUtil.isNotBlank(groupId)) {
            List<UserUploadGroupRef> refList = userUploadGroupRefServiceImpl.findByGroup(groupId);
            for (UserUploadGroupRef ref : refList) {
                ref.setGroupId(UserUploadGroup.DEFAULTGROUP);
            }
            try {
                userUploadGroupRefServiceImpl.batchSave(new HashSet<>(refList));
            } catch (AppDaoException e) {
               throw new AppServiceException("保存失败");
            }
            userUploadGroupDao.delete(groupId);
            return true;
        }
        return false;
    }

}
