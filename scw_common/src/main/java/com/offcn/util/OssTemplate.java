package com.offcn.util;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data
@ToString
public class OssTemplate {

    private String endpoint;        //http://oss-cn-beijing.aliyuncs.com
    private String bucketDomain;
    private String accessKeyId;     //LTAI4G9D6PXc4MGbQknZLuQQ
    private String accessKeySecret; //8wz6FEuBv3qhMo93OfmznVoQJvdzOu
    private String bucketName;      //qyzc202009241024

    public String upload(InputStream inputStream, String fileName){
        //1、加工文件夹和文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folderName = sdf.format(new Date());
        fileName = UUID.randomUUID().toString().replace("-","")+"_"+fileName;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件。
        ossClient.putObject(bucketName,"pic/"+folderName+"/"+fileName,inputStream);

        //4、关闭资源
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭OSSClient。
        ossClient.shutdown();

        String url= "https://"+bucketName+"."+endpoint+"/pic/"+folderName+"/"+fileName;
        System.out.println("上传文件访问路径:"+url);
        return url;

    }

}
