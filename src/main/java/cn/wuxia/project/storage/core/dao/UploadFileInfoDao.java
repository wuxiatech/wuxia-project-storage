package cn.wuxia.project.storage.core.dao;

import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * [ticket id] Description of the class
 *
 * @author songlin.li
 */
@Repository
public class UploadFileInfoDao extends BaseCommonDao<UploadFileInfo, String> {
	/**
	 *
	 * @param uploadFilesetId
	 * @return
	 */
	public List<UploadFileInfo> findByUploadsetId(String uploadFilesetId) {
		String hql = "select info from UploadFileSetRef ref,UploadFileInfo info where ref.uploadFileId = info.id and ref.uploadFilesetId = ?";
		return find(hql, uploadFilesetId);
	}
}
