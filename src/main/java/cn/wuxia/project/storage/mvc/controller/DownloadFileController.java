package cn.wuxia.project.storage.mvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import cn.wuxia.project.storage.core.enums.UploadFileCategoryEnum;
import cn.wuxia.project.storage.core.support.InitializationFile;
import cn.wuxia.project.basic.mvc.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.img.FontImageUtil;

@Controller
@RequestMapping("/download/*")
public class DownloadFileController extends BaseController {

    // @RequestMapping(value = "/fileDownload")
    // public void downloadFile(HttpServletRequest request, HttpServletResponse
    // response) {
    // String fileInfoId = request.getParameter("fileInfoId");
    // Long uploadFileInfoId = NumberUtil.toLong(fileInfoId);
    // UploadFileInfo uploadFileInfo =
    // uploadFileService.getUploadFileInfoById(uploadFileInfoId);
    // String path = uploadFileInfo.getLocation();
    // if (!StringUtil.startsWith(path, "/")) {
    // path = "/" + path;
    // }
    // path = InitializationFile.driveUrl + path;
    // File file = FileUtil.getFile(path);
    // /**
    // * if file exist
    // */
    // if (file.exists()) {
    // response.reset();
    // ServletUtils.setFileDownloadHeader(request, response,
    // uploadFileInfo.getFileName());
    // int fileLength = (int) file.length();
    // response.setContentLength(fileLength);
    // /**
    // * file size bigger that 0
    // */
    // if (fileLength != 0) {
    //
    // InputStream inStream = null;
    // ServletOutputStream outStream = null;
    // try {
    // inStream = new FileInputStream(file);
    // outStream = response.getOutputStream();
    //
    // byte[] buf = new byte[4096];
    // int readLength;
    // while (((readLength = inStream.read(buf)) != -1)) {
    // outStream.write(buf, 0, readLength);
    // }
    // } catch (FileNotFoundException e) {
    // logger.error(e.getMessage(), e);
    // } catch (IOException ioe) {
    // logger.error(ioe.getMessage(), ioe);
    // } finally {
    // try {
    // inStream.close();
    // outStream.flush();
    // outStream.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // }
    // }
    // }
    // }
    //
    // /**
    // * 通过集合ID查询文件ID
    // *
    // * @param uploadFileSetInfoId 集合ID
    // * @return
    // */
    // @RequestMapping(value = "/uploadFile/getUploadFileInfoIds", method = {
    // RequestMethod.POST, RequestMethod.GET })
    // @ResponseBody
    // public List<Map<Long, String>> getUploadFileInfoIds(Long
    // uploadFileSetInfoId) {
    // List<UploadFileInfo> uploadFileInfoList =
    // uploadFileService.getUploadFileInfoListByUploadFileSetId(uploadFileSetInfoId);
    // List<Map<Long, String>> fileInfoList = null;
    // if (CollectionUtils.isNotEmpty(uploadFileInfoList)) {
    // fileInfoList = new ArrayList<>();
    // for (UploadFileInfo file : uploadFileInfoList) {
    // Map<Long, String> m = Maps.newHashMap();
    // m.put(file.getId(), file.getFileName());
    // fileInfoList.add(m);
    // }
    // }
    // return fileInfoList;
    // }
    //
    // final String fileSaveDir = InitializationFile.driveUrl + "/" +
    // UploadFileCategoryEnum.Temp + "/";
    //

    @RequestMapping("/builddefault")
    public void defaultImage(String display, HttpServletResponse response) throws Exception {
        OutputStream outputStream = response.getOutputStream();
        if (StringUtil.isBlank(display)) {
            String[] defaults = { "恭", "喜", "发", "财" };
            int index = new Random(3).nextInt();
            display = defaults[index];
        }
        String firstChar = StringUtil.substring(display, 0, 1);
        File file = new File(InitializationFile.driveUrl + File.separator + UploadFileCategoryEnum.logo + File.separator + firstChar + ".png");
        if (!file.exists()) {
            FontImageUtil.buildCharImage(firstChar, file.getPath());
        }
        org.apache.commons.io.IOUtils.copy(new FileInputStream(file), outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
