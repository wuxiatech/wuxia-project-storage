package cn.wuxia.project.storage.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * The persistent class for the UploadFileSetInfo database table.
 */
@Entity
@Table(name = "upload_fileset_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UploadFileSetInfo extends ModifyInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private UploadFileCategoryEnum category;

//    private List<UploadFileInfo> uploadFileInfos;

    public UploadFileSetInfo() {
        super();
    }

    public UploadFileSetInfo(String id) {
        super.setId(id);
    }

    // bi-directional many-to-one association to UploadFileInfo
//    @OneToMany(mappedBy = "uploadFileSetInfoId")
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//    public List<UploadFileInfo> getUploadFileInfos() {
//        return this.uploadFileInfos;
//    }
//
//    public void setUploadFileInfos(List<UploadFileInfo> uploadFileInfos) {
//        this.uploadFileInfos = uploadFileInfos;
//    }

    @Column(name = "FILESET_CATEGORY")
    @Enumerated(EnumType.STRING)
    public UploadFileCategoryEnum getCategory() {
        return category;
    }

    public void setCategory(UploadFileCategoryEnum category) {
        this.category = category;
    }

}
