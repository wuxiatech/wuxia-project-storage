package cn.wuxia.project.test;

import cn.wuxia.aliyun.components.ApiAccount;
import cn.wuxia.project.common.third.aliyun.RjrzService;
import cn.wuxia.project.common.third.aliyun.VmsSend;
import cn.wuxia.project.common.third.aliyun.bean.RjrzResponse;
import cn.wuxia.project.storage.third.alioss.OssUploader;
import cn.wuxia.project.storage.third.qiniu.QiniuUploader;
import cn.wuxia.project.storage.upload.UploadException;
import cn.wuxia.project.storage.upload.bean.UploadRespone;
import cn.wuxia.project.storage.upload.service.UploadHandler;
import com.aliyuncs.dyvmsapi.model.v20170525.IvrCallResponse;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByVoiceResponse;
import com.aliyuncs.exceptions.ClientException;
import com.qiniu.common.QiniuException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class QiniuTest {


    public static void main(String[] args) throws QiniuException, ClientException, InterruptedException, UploadException {
        QiniuUploader qiniuUploader = QiniuUploader.build();
//            String key = qiniuUploader.upload(new File("/Users/songlin/Desktop/tongguheren.png"), "abc.png");
//            QiniuUploader.url(key);
        String key = "abc.png";
        System.out.println(qiniuUploader.privateDownloadUrl(key));


//        try {
////          NerBean nerBean = BosonUtil.ner("15日，备受关注的电影《黄金时代》在北京举行了电影发布会，导演许鞍华和编剧李樯及汤唯、冯绍峰等众星悉数亮相。据悉，电影确定将于10月1日公映。本片讲述了“民国四大才女”之一的萧红短暂而传奇的一生，通过她与萧军、汪恩甲、端木蕻良、洛宾基四人的情感纠葛，与鲁迅、丁玲等人一起再现上世纪30年代的独特风貌。电影原名《穿过爱情的漫长旅程》，后更名《黄金时代》，这源自萧红写给萧军信中的一句话：“这不正是我的黄金时代吗？”");
////            System.out.println(nerBean.toString());
//
//        } catch (HttpClientException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void test0() throws Exception {
        OssUploader ossUploader = OssUploader.build();
        Map key = ossUploader.upload(new File("/Users/songlin/Desktop/tongguheren.png"), "abc.png");
        System.out.println(key);


    }
    @Test
    public void test11() throws UploadException {
        UploadHandler uploadHandler = new UploadHandler();
        UploadRespone uploadRespone = uploadHandler.uploadToAliOss(new File("/Users/songlin/Desktop/tongguheren.png"), "temp1/abc.png");
        System.out.println(ToStringBuilder.reflectionToString(uploadRespone));
        System.out.println(uploadRespone.getCdnurl());
//        UploadRespone uploadRespone2 = uploadHandler.uploadToQiniuOss(new File("/Users/songlin/Desktop/tongguheren.png"), "temp/abc.png");
//        System.out.println(ToStringBuilder.reflectionToString(uploadRespone2));
//        System.out.println(uploadRespone2.getCdnurl());
    }

    @Test
    public void test1() {
        try {
            RjrzResponse rjrzResponse =
                    RjrzService.build(new ApiAccount("LTAI7UhWqmx8roVp", "TxgF0epkEwUdN2nttGSNE2Y0K3kdW0")).renzheng("nc1-01W8fUo7tiNBKrkhR8ACXXKeH_-DhU-PAZJq0WyRap7YTfLSt84ikiztWaNoGzp8obC-nc2-0561M3bob-R8gv1KNXk15v5WWNf1bqIUB2ADxKwPgHxe_27MtxO74ul7LHuQ5MYIhpfWM8F5WZA0qPmX747MXUPRim91VpSsi5piCyScAwjvSJid9EohwDXqqaq9ECvwXE6YwLBXaxmIci5uutEeL13PaZ3lKulzuUonfc4M5esMuvQ0f8mfTnzLrIItCm6YX98sABVZr53XCQtxJeXpWddZa-1C_L0UJiMRonMKL2sUVxVmES5S7aTEkViND8mh7hJLxAikwtjuzchOwC49PPHXii9tY_MPcaoTW-7FdMTvLHA6D1bqbm5qzdDlnZ2Kf88Q7ZCwbi5aDELsVJhbTwWVsAxQHojeOF39MSL4bxBD6AaDeRX8m5WfVHkNs0GF8sudaRo2GxNaIcWapqIai8cqfHDDGRXoTq2zlPF1x5dSc-nc3-01TzB8fzgmvi9w1w4Zzf-Aot6BpRyNyAEtkQoA0gMZO0csTC7kUIHloOBGwoa0C-3Z4y8X_8DPogqv3mVjqLy7HkQK4EuvLb0mgvDVcgkFU4pE4ypEMha_eJWcDtSmxP5SJ1cArGIkAQHa4ZcpKRDKqA-nc4-FFFFA0000000018121B6");
            System.out.println(ToStringBuilder.reflectionToString(rjrzResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() throws InterruptedException, ClientException {
        VmsSend vmsSend = VmsSend.build(new ApiAccount("", ""));
//            SingleCallByTtsResponse singleCallByTtsResponse = vmsSend.singleCallByTts();
//            System.out.println("文本转语音外呼---------------");
//            System.out.println("RequestId=" + singleCallByTtsResponse.getRequestId());
//            System.out.println("Code=" + singleCallByTtsResponse.getCode());
//            System.out.println("Message=" + singleCallByTtsResponse.getMessage());
//            System.out.println("CallId=" + singleCallByTtsResponse.getCallId());

        Thread.sleep(20000L);

        SingleCallByVoiceResponse singleCallByVoiceResponse = vmsSend.singleCallByVoice();
        System.out.println("语音文件外呼---------------");
        System.out.println("RequestId=" + singleCallByVoiceResponse.getRequestId());
        System.out.println("Code=" + singleCallByVoiceResponse.getCode());
        System.out.println("Message=" + singleCallByVoiceResponse.getMessage());
        System.out.println("CallId=" + singleCallByVoiceResponse.getCallId());

        Thread.sleep(20000L);

        IvrCallResponse ivrCallResponse = vmsSend.ivrCall();
        System.out.println("交互式语音应答---------------");
        System.out.println("RequestId=" + ivrCallResponse.getRequestId());
        System.out.println("Code=" + ivrCallResponse.getCode());
        System.out.println("Message=" + ivrCallResponse.getMessage());
        System.out.println("CallId=" + ivrCallResponse.getCallId());

    }

}
