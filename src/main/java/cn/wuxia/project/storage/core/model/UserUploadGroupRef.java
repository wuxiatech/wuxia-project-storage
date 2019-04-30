package cn.wuxia.project.storage.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * The persistent class for the u_user_upload_group_ref database table.
 * 
 */
@Entity
@Table(name = "u_user_upload_group_ref")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
public class UserUploadGroupRef extends ModifyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupId;

    private String uploadFileId;

    private String userId;

    public UserUploadGroupRef() {
        super();
    }

    @Column(name = "GROUP_ID")
    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name = "UPLOAD_FILE_ID")
    public String getUploadFileId() {
        return this.uploadFileId;
    }

    public void setUploadFileId(String uploadFileId) {
        this.uploadFileId = uploadFileId;
    }

    @Column(name = "USER_ID")
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
