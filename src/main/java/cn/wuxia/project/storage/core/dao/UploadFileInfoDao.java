package cn.wuxia.project.storage.core.dao;

import java.util.List;

import cn.wuxia.project.storage.core.model.UploadFileInfo;
import org.springframework.stereotype.Repository;

import cn.wuxia.project.basic.core.common.BaseCommonDao;

/**
 * [ticket id] Description of the class
 * 
 * @author focus.huang @ Version : V<Ver.No> <2012-6-6>
 */
@Repository
public class UploadFileInfoDao extends BaseCommonDao<UploadFileInfo, String> {

	 public List<UploadFileInfo> findPatientFileInfo(List<String> filesetids) {
 		String sql = "SELECT ufi.* FROM upload_file_info ufi LEFT JOIN upload_file_set_ref ufsr "
 				+ " ON ufsr.UPLOAD_FILE_INFO_ID = ufi.id WHERE ufsr.UPLOAD_FILESET_INFO_ID in (?)";
 		return query(sql, UploadFileInfo.class, String.join(",", filesetids));
	 }
	
}
