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

import cn.wuxia.project.storage.core.model.UserUploadGroup;
import org.springframework.stereotype.Component;

import cn.wuxia.project.basic.core.common.BaseCommonDao;

@Component
public class UserUploadGroupDao extends BaseCommonDao<UserUploadGroup, String> {

    /**
     * 根据用户查找对应图库分组
     * @author wuwenhao
     * @param uid
     * @return
     */
    public List<Map<String, Object>> findByUserGroup(String uid) {
        String sql = "SELECT gro.ID id,COUNT(GROUP_NAME) content,groRef.ID groRefId,gro.GROUP_NAME groupName, ufi.LOCATION fileLocation FROM u_user_upload_group gro "
                + " LEFT JOIN u_user_upload_group_ref groRef ON gro.ID = groRef.GROUP_ID  AND groRef.USER_ID = ? AND groRef.IS_OBSOLETE_DATE IS NULL "
                + " LEFT JOIN upload_file_info ufi ON groRef.UPLOAD_FILE_ID = ufi.ID"
                + "  WHERE gro.IS_OBSOLETE_DATE IS NULL  AND (gro.USER_ID = ? or gro.USER_ID IS NULL  ) GROUP BY gro.ID ORDER BY gro.CREATED_ON DESC,ufi.CREATED_ON DESC";
        return (List<Map<String, Object>>) queryForMap(sql, uid, uid);
    }
}
