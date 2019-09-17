package cn.wuxia.project.storage.core.dao;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.storage.core.model.UploadFileSetRef;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * [ticket id] Description of the class
 * 
 * @author focus.huang @ Version : V<Ver.No> <2012-6-6>
 */
@Repository
public class UploadFileSetRefDao extends BaseCommonDao<UploadFileSetRef, String> {


    /**
     * 移动图片位置
     * @author wuwenhao
     * @param moveId
     * @param repId
     * @return
     */
    public Map<String, Object> moveUploadFile(String moveId, String repId) {
        Map<String, Object> ret = Maps.newHashMap();
        // 判断移动ID和替换ID不能为空
        if (StringUtil.isBlank(moveId) || StringUtil.isBlank(repId)) {
            ret.put("success", false);
            ret.put("msg", "系统错误：请刷 新页面");
            return ret;
        }
        // 获取移动对象的信息
        UploadFileSetRef moveSet = findUniqueBy("uploadFileId", moveId);
        // 获取替换对象的信息
        UploadFileSetRef repSet = findUniqueBy("uploadFileId", repId);
        // 判断替换对象和移动对象不能为空
        if (null == moveSet || null == repSet) {
            ret.put("success", false);
            ret.put("msg", "系统错误：请刷 新页面");
            return ret;
        }
        Long repOrder = repSet.getSortOrder();
        Long moveOrder = moveSet.getSortOrder();
        if (repOrder.equals( moveOrder)) { //防止相同的sortOrder起冲突
            moveOrder++;
        }
        moveSet.setSortOrder(repOrder);
        repSet.setSortOrder(moveOrder);
        save(moveSet);
        save(repSet);
        ret.put("success", true);
        return ret;
    }


    /**
     * 删除关联数据
     * @param fileInfoId
     */
    public void deleteFile(String fileInfoId){
        String hql = "delete from UploadFileSetRef where uploadFileId=?";
        batchExecute(hql);
    }
    
    
}
