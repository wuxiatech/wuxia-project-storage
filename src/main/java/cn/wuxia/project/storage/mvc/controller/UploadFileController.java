/*
 * Copyright 2011-2020 wuxia.gd.cn All right reserved.
 */
package cn.wuxia.project.storage.mvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.wuxia.project.storage.core.bean.FileUploadBean;
import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.model.UserUploadGroup;
import cn.wuxia.project.storage.core.model.UserUploadGroupRef;
import cn.wuxia.project.storage.core.service.UploadFileService;
import cn.wuxia.project.storage.core.service.UserUploadGroupRefService;
import cn.wuxia.project.storage.core.service.UserUploadGroupService;
import cn.wuxia.project.basic.mvc.controller.BaseController;
import cn.wuxia.project.common.security.UserContextUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

import cn.wuxia.common.exception.AppWebException;
import cn.wuxia.common.util.FileUtil;
import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

/**
 * Upload File Controller.
 *
 * @author songlin.li
 * @since 2013-06-25
 */
@Controller
@RequestMapping("/upload/*")
public class UploadFileController extends BaseController {
    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private UserUploadGroupRefService userUploadGroupRefService;

    @Autowired
    private UserUploadGroupService userUploadGroupService;

    /**
     * 上传文件
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author 金
     */
    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public FileUploadBean uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileUploadBean fileUploadBean = uploadFileService.uploadFile(request, null);
        fileUploadBean.setOldFileName(URLEncoder.encode(fileUploadBean.getOldFileName(), "utf-8"));
        return fileUploadBean;
    }

    @RequestMapping(value = "checkExist", method = RequestMethod.POST)
    @ResponseBody
    public String checkExistingUploadFile(HttpServletRequest request, HttpServletResponse response) {
        String uploadFileSetInfoId = request.getParameter("uploadFileSetId");
        String fileName = request.getParameter("filename");
        boolean isExist = uploadFileService.checkExistingUploadFile(uploadFileSetInfoId, fileName);
        return isExist ? "1" : "0";
    }

    /**
     * 删除上传文件
     *
     * @return
     * @author wuwenhao
     */
    @RequestMapping(value = "delete/uploadFile")
    @ResponseBody
    public String delUploadFile(String fileInfoId) {
        try {
            uploadFileService.deleteFileInfoByLogical(fileInfoId);
            return "1";
        } catch (Exception e) {
            logger.error("删除文件有误：", e);
        }
        return "0";
    }

    /**
     * @throws IOException
     * @upfile 就是上面提到的upfile，要对应一致
     */
    @RequestMapping("/ueditor/upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response, String action) throws IOException {
        String userid = UserContextUtil.getId();
        if (StringUtil.equals("uploadimage", action)) {
            request.setAttribute("uploadCategory", UploadFileCategoryEnum.gallery);
            FileUploadBean fileUploadBean = uploadFileService.uploadFile(request, "file[]");
            Map<String, Object> result = Maps.newHashMap();
            result.put("name", fileUploadBean.getNewFileName());
            result.put("originalName", fileUploadBean.getOldFileName());
            result.put("size", fileUploadBean.getFileSize());
            result.put("state", "SUCCESS");
            result.put("type", fileUploadBean.getFileType());
            result.put("url", fileUploadBean.getDownloadUrl() + "?x-oss-process=image/resize,m_fixed,w_650");
            if (StringUtil.isNotBlank(userid)) {
                UserUploadGroupRef gorupRef = new UserUploadGroupRef();
                gorupRef.setUploadFileId(fileUploadBean.getUploadFileInfoId());
                gorupRef.setUserId(userid);
                if (StringUtil.isNotBlank(request.getParameter("groupId"))) {
                    gorupRef.setGroupId(request.getParameter("groupId"));
                } else {
                    gorupRef.setGroupId(UserUploadGroup.DEFAULTGROUP);
                }
                userUploadGroupRefService.save(gorupRef);
            }
            return result;
        } else if (StringUtil.equals("listimage", action)) {
            Map<String, Object> result = Maps.newHashMap();
            List<Map<String, Object>> list = userUploadGroupRefService.findAllByUser(userid, UploadFileCategoryEnum.gallery);
            result.put("state", "SUCCESS");
            result.put("list", list);
            result.put("start", 0);
            result.put("total", list.size());
            return result;
        } else if (StringUtil.equals("listfile", action)) {
            Map<String, Object> result = Maps.newHashMap();
            List<Map<String, Object>> list = userUploadGroupRefService.findAllByUser(userid, UploadFileCategoryEnum.file);
            result.put("state", "SUCCESS");
            result.put("list", list);
            result.put("start", 0);
            result.put("total", list.size());
            return result;
        } else if (StringUtil.equals("uploadfile", action)) {
            request.setAttribute("uploadCategory", UploadFileCategoryEnum.file);
            FileUploadBean fileUploadBean = uploadFileService.uploadFile(request, "upfile");
            Map<String, Object> result = Maps.newHashMap();
            result.put("name", fileUploadBean.getOldFileName());
            result.put("originalName", fileUploadBean.getOldFileName());
            result.put("size", fileUploadBean.getFileSize());
            result.put("state", "SUCCESS");
            result.put("type", fileUploadBean.getFileType());
            result.put("url", fileUploadBean.getDownloadUrl());
            if (StringUtil.isNotBlank(userid)) {
                UserUploadGroupRef gorupRef = new UserUploadGroupRef();
                gorupRef.setUploadFileId(fileUploadBean.getUploadFileInfoId());
                gorupRef.setUserId(userid);
                if (StringUtil.isNotBlank(request.getParameter("groupId"))) {
                    gorupRef.setGroupId(request.getParameter("groupId"));
                } else {
                    gorupRef.setGroupId(UserUploadGroup.DEFAULTGROUP);
                }
                userUploadGroupRefService.save(gorupRef);
            }
            return result;
        } else if (StringUtil.equals("uploadvideo", action)) {
            request.setAttribute("uploadCategory", UploadFileCategoryEnum.video);
            FileUploadBean fileUploadBean = uploadFileService.uploadFile(request, "upfile");
            Map<String, Object> result = Maps.newHashMap();
            result.put("name", fileUploadBean.getOldFileName());
            result.put("originalName", fileUploadBean.getOldFileName());
            result.put("size", fileUploadBean.getFileSize());
            result.put("state", "SUCCESS");
            result.put("type", fileUploadBean.getFileType());
            result.put("url", fileUploadBean.getDownloadUrl());
            if (StringUtil.isNotBlank(userid)) {
                UserUploadGroupRef gorupRef = new UserUploadGroupRef();
                gorupRef.setUploadFileId(fileUploadBean.getUploadFileInfoId());
                gorupRef.setUserId(userid);
                if (StringUtil.isNotBlank(request.getParameter("groupId"))) {
                    gorupRef.setGroupId(request.getParameter("groupId"));
                } else {
                    gorupRef.setGroupId(UserUploadGroup.DEFAULTGROUP);
                }
                userUploadGroupRefService.save(gorupRef);
            }
            return result;
        } else {
            String path = request.getServletContext().getRealPath("/");
            File f = FileUtil.getFile(path + "/resources/script/ueditor/upload.config.json");
            InputStream inputStream = null;
            if (f != null && f.exists()) {
                inputStream = new FileInputStream(f);
            } else {
                try {
                    inputStream = HttpClientUtil.download(getServerHttpPath() + "/commons/js/ueditor/upload.config.json");
                } catch (HttpClientException e) {
                    throw new AppWebException(e.getMessage());
                }
            }
            String config = IOUtils.toString(inputStream);
            Map<String, Object> c = JsonUtil.fromJson(config);
            return c;
        }
    }
}
