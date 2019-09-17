package cn.wuxia.project.storage.core.bean;

import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author songlin
 */
@Getter
@Setter
public class NewFileToSaveDTO extends UploadFileInfo {

    private static final long serialVersionUID = 6475199902359647470L;

    private String uploadFileSetId;
    private UploadFileCategoryEnum uploadFileCategory;
}
