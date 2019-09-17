package cn.wuxia.project.storage.upload.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadRespone {
    String filepath;
    String publicurl;
    String cdnurl;
    String bucket;
    String md5;

}
