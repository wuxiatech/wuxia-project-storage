package cn.wuxia.project.storage.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * The persistent class for the upload_file_set_ref database table.
 * 文件中间表
 */
@Entity
@Table(name = "upload_file_set_ref")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class UploadFileSetRef extends ModifyInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uploadFileId;

    private String uploadFilesetId;

    private Long sortOrder;

    private String fromId;

    public UploadFileSetRef() {
    }

    public UploadFileSetRef(String id) {
        super(id);
    }

    public UploadFileSetRef(String uploadFileId, String uploadFilesetId) {
        this.uploadFileId = uploadFileId;
        this.uploadFilesetId = uploadFilesetId;
    }

    @Column(name = "UPLOAD_FILE_INFO_ID")
    public String getUploadFileId() {
        return this.uploadFileId;
    }

    public void setUploadFileId(String fileId) {
        this.uploadFileId = fileId;
    }

    @Column(name = "UPLOAD_FILESET_INFO_ID")
    public String getUploadFilesetId() {
        return this.uploadFilesetId;
    }

    public void setUploadFilesetId(String filesetId) {
        this.uploadFilesetId = filesetId;
    }

    @Column(name = "SORT_ORDER")
    public Long getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name = "FROM_ID")
    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
}
