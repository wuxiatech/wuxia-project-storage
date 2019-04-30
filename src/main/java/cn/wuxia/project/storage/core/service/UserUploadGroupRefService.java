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

import cn.wuxia.project.storage.core.model.UserUploadGroupRef;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.common.service.CommonService;
import cn.wuxia.common.orm.query.Pages;

public interface UserUploadGroupRefService extends CommonService<UserUploadGroupRef, String> {

    /**
     * 根据用户Id查找对应图库
     * @author wuwenhao
     * @param param
     * @return
     */
    public Pages<Map<String, Object>> findByUserPicLibrary(Map<String, Object> params, Pages<Map<String, Object>> page);

    public List<UserUploadGroupRef> findByGroup(String groupId);

    /**
     * 按用户查询所有照片
     * @author 金
     * @param userId 用户id
     * @param pathPre 路径前缀
     * @return
     */
    public List<Map<String, Object>> findAllByUser(String userId, UploadFileCategoryEnum categoryEnum);
}
