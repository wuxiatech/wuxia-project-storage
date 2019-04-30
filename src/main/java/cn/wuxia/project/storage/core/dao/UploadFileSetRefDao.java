package cn.wuxia.project.storage.core.dao;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.storage.core.bean.FileInfoDto;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetRef;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.common.util.StringUtil;

/**
 * [ticket id] Description of the class
 * 
 * @author focus.huang @ Version : V<Ver.No> <2012-6-6>
 */
@Repository
public class UploadFileSetRefDao extends BaseCommonDao<UploadFileSetRef, String> {

    /**
     * 
     * @param uploadFileSetInfoId
     * @return
     */
    public List<UploadFileInfo> findByUploadSetId(String uploadFileSetInfoId) {
        String hql = "select info from UploadFileSetRef ref,UploadFileInfo info where ref.uploadFileId = info.id and ref.uploadFilesetId = ?";
        return find(hql, uploadFileSetInfoId);
    }

    /***
     * 获得文件夹里面所有文件路径
     * @param filesetId
     * @return
     */
    public List<FileInfoDto> getFileInfo(String filesetId) {
        String sql = " SELECT f.FILE_NAME fileName,f.file_type fileType,f.LOCATION location,f.url url,r.ID refId,r.UPLOAD_FILESET_INFO_ID filesetId,r.UPLOAD_FILE_INFO_ID fileId "
                + " FROM upload_file_set_ref r LEFT JOIN "
                + "upload_file_info f ON f.ID=r.UPLOAD_FILE_INFO_ID WHERE r.UPLOAD_FILESET_INFO_ID= :filesetId"
                + " AND r.IS_OBSOLETE_DATE IS NULL and f.IS_OBSOLETE_DATE is null ORDER BY r.SORT_ORDER DESC,f.CREATED_ON DESC ";
        //return (List<Map<String, Object>>) queryForMap(sql, filesetId);
        Map<String, Object> params = Maps.newHashMap();
        params.put("filesetId", filesetId);
        return query (sql, FileInfoDto.class, params);
    }

    /**
     * 移动图片位置
     * @author wuwenhao
     * @param moveId
     * @param repId
     * @param position
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
        if (repOrder == moveOrder) { //防止相同的sortOrder起冲突
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
