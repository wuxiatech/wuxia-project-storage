/*
* Created on :2014年11月19日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.project.storage.core.dao;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.model.UserUploadGroup;
import cn.wuxia.project.storage.core.model.UserUploadGroupRef;
import org.springframework.stereotype.Component;

import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.common.orm.query.Pages;
import cn.wuxia.common.util.DaoUtil;
import cn.wuxia.common.util.StringUtil;

@Component
public class UserUploadGroupRefDao extends BaseCommonDao<UserUploadGroupRef, String> {

    /**
     * 根据用户Id查找对应图库
     * 
     * @author wuwenhao
     * @param param
     * @return
     */
    public Pages<Map<String, Object>> findByUserPicLibrary(Map<String, Object> params, Pages page) {
        DaoUtil daoUtil = new DaoUtil();
        String sql = "SELECT groupRef.id id,gro.GROUP_NAME groupName,fileInfo.id fileId,fileInfo.LOCATION location,fileInfo.FILE_NAME originalName, fileInfo.url url FROM u_user_upload_group_ref groupRef"
                + " LEFT JOIN u_user_upload_group gro ON gro.ID = groupRef.GROUP_ID"
                + " LEFT JOIN upload_file_info fileInfo ON fileInfo.id = groupRef.UPLOAD_FILE_ID"
                + " WHERE gro.IS_OBSOLETE_DATE IS NULL AND groupRef.IS_OBSOLETE_DATE IS NULL AND fileInfo.IS_OBSOLETE_DATE IS NULL ";
        if (null != params) {
            if (StringUtil.isBlank(params.get("defaultLogo"))) {
                sql += " AND gro.id != '" + UserUploadGroup.DEFAULTLOGO + "'";
            }
            if (params.get("userId") != null && StringUtil.equals(UserUploadGroup.DEFAULTLOGO, (String) params.get("groupId")) == false) {
                sql += " AND groupRef.USER_ID = '" + params.get("userId") + "'";
            }
            if (params.get("groupId") == null) {
                sql += " AND groupRef.GROUP_ID IS NULL ORDER BY fileInfo.CREATED_ON desc";
            } else {
                sql += " AND groupRef.GROUP_ID = '" + params.get("groupId") + "' ORDER BY fileInfo.CREATED_ON desc";
            }
        }
        return (Pages<Map<String, Object>>) findPageBySql(page, sql);
    }

    /**
     * 根据用户查找对应图库分组
     * 
     * @author wuwenhao
     * @param uid
     * @return
     */
    public List<UserUploadGroupRef> findByUserGroup(String uid) {
        return findBy("userId", uid);
    }

    /**
     * 按用户查询所有照片
     * 
     * @author 金
     * @param userId
     *            用户id
     * @modifiedBy songlin.li
     * @param pathPre
     *            路径前缀
     * @return
     */
    public List<Map<String, Object>> findAllByUser(String userId, UploadFileCategoryEnum category) {
        String sql = "SELECT file.LOCATION url, ref.CREATED_ON mtime  FROM u_user_upload_group_ref ref,upload_file_info file WHERE ref.UPLOAD_FILE_ID = file.ID AND file.IS_OBSOLETE_DATE IS NULL AND ref.USER_ID = ? AND file.LOCATION LIKE ?";
        return queryToMap(sql, userId, "/" + category + "%");
    }
}
