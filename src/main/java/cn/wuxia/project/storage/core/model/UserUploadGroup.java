package cn.wuxia.project.storage.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * The persistent class for the u_user_upload_group database table.
 * 
 */
@Entity
@Table(name = "u_user_upload_group")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
public class UserUploadGroup extends ModifyInfoEntity implements Serializable {

    //默认分组ID
    public static final String DEFAULTGROUP = "1";

    //默认分组ID
    public static final String DEFAULTLOGO = "2";

    private static final long serialVersionUID = 1L;

    private String groupName;

    private String userId;

    public UserUploadGroup() {
        super();
    }

    @Column(name = "GROUP_NAME")
    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Column(name = "USER_ID")
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
