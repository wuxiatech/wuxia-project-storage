/*
* Created on :2014年11月19日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.project.storage.core.service;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.storage.core.model.UserUploadGroup;
import cn.wuxia.project.common.service.CommonService;

public interface UserUploadGroupService extends CommonService<UserUploadGroup, String> {

    /**
     * 根据用户id查找用户对应的分组
     * @author wuwenhao
     * @param uid
     * @return
     */
    public List<Map<String, Object>> findByUserGroup(String uid);

    /**
     * 物理删除分组
     * @author 金
     * @param groupId
     * @return
     */
    public Boolean phyDeleteById(String groupId);
}
