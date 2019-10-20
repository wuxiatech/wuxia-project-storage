/*
* Created on :2014年11月19日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.tech All right reserved.
*/
package cn.wuxia.project.storage.core.service.impl;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.storage.core.model.UserUploadGroupRef;
import cn.wuxia.project.storage.core.service.UserUploadGroupRefService;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.wuxia.project.storage.core.dao.UserUploadGroupRefDao;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.common.orm.query.Pages;

@Service
@Transactional
public class UserUploadGroupRefServiceImpl extends CommonServiceImpl<UserUploadGroupRef, String> implements UserUploadGroupRefService {

    @Autowired
    private UserUploadGroupRefDao userUploadGroupRefDao;

    @Override
    protected CommonDao<UserUploadGroupRef, String> getCommonDao() {
        return userUploadGroupRefDao;
    }

    @Override
    public Pages<Map<String, Object>> findByUserPicLibrary(Map<String, Object> params, Pages<Map<String, Object>> page) {
        return userUploadGroupRefDao.findByUserPicLibrary(params, page);
    }

    @Override
    public List<UserUploadGroupRef> findByGroup(String groupId) {
        return userUploadGroupRefDao.find(Restrictions.eq("groupId", groupId));
    }

    @Override
    public List<Map<String, Object>> findAllByUser(String userId, UploadFileCategoryEnum categoryEnum) {
        return userUploadGroupRefDao.findAllByUser(userId, categoryEnum);
    }
}
