package cn.wuxia.project.storage.support;

import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.storage.core.bean.FileInfoDto;
import cn.wuxia.project.storage.core.bean.UploadFileInfoDTO;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.service.UploadFileService;
import cn.wuxia.project.storage.core.service.UploadFileSetRefService;
import cn.wuxia.project.storage.core.support.InitializationFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StorageFunctions {

    protected final static Logger logger = LoggerFactory.getLogger(StorageFunctions.class);

    private static UploadFileSetRefService ufsrService = SpringContextHolder.getBean(UploadFileSetRefService.class);

    private static UploadFileService ufiService = SpringContextHolder.getBean(UploadFileService.class);

    /**
     * 多个文件
     *
     * @param filesetId
     * @return
     * @author songlin
     */
    public static String[] getFilepathBySetId(String filesetId) {
        String[] filespath = {};
        if (StringUtils.isNotBlank(filesetId)) {
            List<FileInfoDto> list = ufsrService.getFileInfo(filesetId);
            if (ListUtil.isNotEmpty(list)) {
                for (FileInfoDto m : list) {
                    String location = m.getLocation() == null ? "" : m.getLocation();
                    filespath = ArrayUtils.add(filespath, InitializationFile.downloadUrl + location);
                }
            }
        }
        return filespath;
    }

    /**
     * 获取一个路径
     *
     * @param fileInfoId
     * @return
     * @author songlin
     */
    public static String getFilepathById(String fileInfoId) {
        if (StringUtils.isNotBlank(fileInfoId)) {
            UploadFileInfoDTO dto = ufiService.getUploadFileInfoDTOById(fileInfoId);
            if (dto == null) {
                return fileInfoId;
            }
            return InitializationFile.downloadUrl + dto.getLocation();

        }
        return "";
    }

    /**
     * 获取一个路径
     *
     * @param fileInfoId
     * @return
     * @author songlin
     */
    public static String getThumbFilepathById(String fileInfoId) {
        if (StringUtils.isNotBlank(fileInfoId)) {

            UploadFileInfoDTO dto = ufiService.getUploadFileInfoDTOById(fileInfoId);
            if (dto == null) {
                return fileInfoId;
            }
            String location = "/" + UploadFileCategoryEnum.temp + "/20" + StringUtil.substringAfter(dto.getLocation(), "/20");
            return InitializationFile.downloadUrl + location;
        }
        return "";
    }


}
