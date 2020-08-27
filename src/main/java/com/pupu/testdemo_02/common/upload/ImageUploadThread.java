package com.pupu.testdemo_02.common.upload;

import com.google.gson.Gson;
import com.pupu.testdemo_02.utils.file.FileUtils;
import com.pupu.testdemo_02.utils.image.ImageCompressUtil;
import com.pupu.testdemo_02.vo.face.FaceImportFail;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lipu
 * @since 2020-08-27 11:40:51
 */
@Slf4j
public class ImageUploadThread implements Runnable {
//    private IFaceService faceService;


    private String qiniuAccessKey;
    private String qiniuSecretKey;

    private List<File> list;

    // path of zip file in server
    private String zipPath;

    //path of unziped file in server
    private String descPath;

    //domain in qiqiu sotre image
    private String domain;

    //spache in qiniu staore image
    private String bucket;

    private String randomNum;

    private String importTime;

    private String companyId;

    private String modifyUser;

    private String tenantCode;

    public ImageUploadThread(String qiniuAccessKey, String qiniuSecretKey, List<File> list, String zipPath, String descPath, String domain, String bucket, String randomNum, String importTime, String companyId, String modifyUser, String tenantCode) {
        this.qiniuAccessKey = qiniuAccessKey;
        this.qiniuSecretKey = qiniuSecretKey;
        this.list = list;
        this.zipPath = zipPath;
        this.descPath = descPath;
        this.domain = domain;
        this.bucket = bucket;
        this.randomNum = randomNum;
        this.importTime = importTime;
        this.companyId = companyId;
        this.modifyUser = modifyUser;
        this.tenantCode = tenantCode;
    }

    @Override
    public void run() {
        log.info("上传图片线程开始："+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        List<MultipartFile> fileList=new ArrayList<>();

        //成功图片
        List<FaceImportFail> successFaceImportFailList=new ArrayList<>();
        //失败图片
        List<FaceImportFail> failFaceImportFailList=new ArrayList<>();
        int result=0;
        try {
            for (File file:list) {
                //图片缩放处理,缩放50%
                MultipartFile mfiles = new CommonsMultipartFile(FileUtils.createFileItem(file, file.getName()));
                BufferedImage bi = ImageIO.read(mfiles.getInputStream());
                int width=(int)(bi.getWidth()*0.5);
                int height=(int)(bi.getHeight()*0.5);
                //图片缩放50%
                ImageCompressUtil.changeSize(width,height,file.getPath());

                //给缩放后的图片进行压缩处理,
                File file1=new File(file.getPath());
                MultipartFile mfile = new CommonsMultipartFile(FileUtils.createFileItem(file1, file1.getName()));
                Long size=mfile.getSize();
                //图片大于1m,进行缩列图处理压缩
                if(!FileUtils.checkFileSize(size,0.9,"M")){
                    ImageCompressUtil.saveMinPhoto(file1.getPath(), file1.getPath(), 3000, 1);
                }

                //将缩放枷锁后的图片上传到七牛云图片
                File file2=new File(file.getPath());
                MultipartFile mfileResult = new CommonsMultipartFile(FileUtils.createFileItem(file2, file2.getName()));
                fileList.add(mfileResult);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("图片压缩处理时异常,上传的所有图片回滚");
            for (File file:list) {
                FaceImportFail faceImportFail=new FaceImportFail();

                String name=file.getName().split("_")[0];
                faceImportFail.setName(name);

                String phone=file.getName().split("_")[1];
                phone=phone.substring(0,phone.indexOf("."));
                faceImportFail.setMsg("图片压缩处理时异常");

                faceImportFail.setPhone(phone);
                failFaceImportFailList.add(faceImportFail);
            }
            //压缩解压图片导致失败
            System.out.println("把压缩失败的图片写入到数据库");
            //通知图片全部上传失败
            result=1;

        }
        if(result==0){
            //处理图片文件到七牛云
            log.info("上传七牛云开始："+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
            String path= qiniuUpload(fileList,domain,bucket,"",qiniuAccessKey,qiniuSecretKey,randomNum);
            log.info("上传七牛云结束："+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
            log.info("图片路径："+path);
        }

        //删除压缩包、压缩文件
        FileUtils.deleteFile(zipPath);
        FileUtils.deleteDirectory(descPath);
    }


    /**
     *
     * @param list image list
     * @param domain domain
     * @param bucket space
     * @param dir dir
     * @param qiniuAccessKey qiniuAccessKey
     * @param qiniuSecretKey qiniuSecretKey
     * @param randomNum randomNum
     * @return String
     */
    private String qiniuUpload(List<MultipartFile> list, String domain, String bucket, String dir,String qiniuAccessKey,String qiniuSecretKey,String randomNum) {
        //成功图片
        List<FaceImportFail> successFaceImportFailList=new ArrayList<>();
        //失败图片
        List<FaceImportFail> failFaceImportFailList=new ArrayList<>();
        try {
            Configuration cfg = null;
            UploadManager uploadManager = null;
            String upToken = null;
            String filePaths = "";
            for (MultipartFile myfile : list) {
                //get authentication of qiniu
                Auth auth = Auth.create(qiniuAccessKey, qiniuSecretKey);
                //get token of qiniu
                upToken = auth.uploadToken(bucket);

                Zone zone = Zone.autoZone();
                cfg = new Configuration(zone);
                uploadManager = new UploadManager(cfg);

                // 获取文件后缀名
                String fileExt = myfile.getOriginalFilename()
                        .substring(myfile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
                if("blob".equals(fileExt)){
                    fileExt = "png";
                }
                // 重构文件名称
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String phone=myfile.getOriginalFilename().split("_")[1];
                String newImgPurfix = randomNum+"_"+myfile.getOriginalFilename();
                String path = bucket + (!StringUtils.isEmpty(dir) ? ("/" + dir) : "") + "/" + newImgPurfix;
                // 上传七牛服务器
                Response response = uploadManager.put(myfile.getInputStream(), path, upToken, null, null);

                //成功上传的图片
                FaceImportFail faceImportFail=new FaceImportFail();
                faceImportFail.setPhone(phone.substring(0,phone.indexOf(".")));
                faceImportFail.setName(myfile.getOriginalFilename().split("_")[0]);
                String imgUrl= domain + path;
                faceImportFail.setImgUrl(imgUrl);
                successFaceImportFailList.add(faceImportFail);

                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                filePaths += domain + path + ",";
            }
            //通知faceService上传成功
//            faceService.bulkImportFace(successFaceImportFailList,failFaceImportFailList,Long.valueOf(companyId),modifyUser,importTime,tenantCode);
            System.out.println("七牛上产成功，记录结果。");
            return filePaths.length() > 0 ? filePaths.substring(0, filePaths.length() - 1) : filePaths;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("qiniu upload file error ==> {}", e);
            //上传七牛云异常
            for (MultipartFile myfile : list) {
                FaceImportFail importFail=new FaceImportFail();
                String name=myfile.getOriginalFilename().split("_")[0];
                importFail.setName(name);
                String phone=myfile.getOriginalFilename().split("_")[1];
                phone=phone.substring(0,phone.indexOf("."));
                importFail.setPhone(phone);
                importFail.setMsg("上传七牛云异常");

                if(successFaceImportFailList!=null && successFaceImportFailList.size()>0){
                    for (FaceImportFail fail:successFaceImportFailList) {
                        if(!phone.equals(fail.getPhone())){
                            failFaceImportFailList.add(importFail);
                        }
                    }
                }else {
                    failFaceImportFailList.add(importFail);
                }
            }
//            faceService.bulkImportFace(successFaceImportFailList,failFaceImportFailList,Long.valueOf(companyId),modifyUser,importTime,tenantCode);
            System.out.println("七牛上产失败，记录结果。");
        }
        return null;
    }
}
