package com.pupu.testdemo_02.controller.upload;

import com.pupu.testdemo_02.common.upload.ImageUploadResult;
import com.pupu.testdemo_02.common.upload.ImageUploadThread;
import com.pupu.testdemo_02.common.upload.UploadDomain;
import com.pupu.testdemo_02.common.upload.UploadSpace;
import com.pupu.testdemo_02.utils.file.FileUtils;
import com.pupu.testdemo_02.utils.file.UnzipFileUtils;
import com.pupu.testdemo_02.vo.ImageUploadInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 文件上传
 *
 * @author lipu
 * @since 2020-08-26 10:54:25
 */
@RestController
@RequestMapping("upload")
@Slf4j
public class UploadController {

    @Value("${qiniu.qiniuAccessKey}")
    private String qiniuAccessKey;
    @Value("${qiniu.qiniuSecretKey}")
    private String qiniuSecretKey;
    @Value("${qiniu.env}")
    private String env;
    @Value("${qiniu.imageNum}")
    private Integer imageNum;
    @Value("${qiniu.imageSize}")
    private Integer imageSize;


    /**
     * @param file    zip图片压缩包
     * @param request request
     * @return json
     */
    @PostMapping("/addImagesOfZip")
    public ImageUploadResult addImagesOfZip(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        log.info("批量上传zip图片包开始请求开始：" + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));

        String password = request.getParameter("password");
        String companyId = request.getParameter("companyId");
        String importTime = request.getParameter("importTime");
        String type = request.getParameter("type");
        log.info("MultipartFile: " + file.getClass());
        return uploadZipFiles(file, password, type, companyId, importTime);
    }

    /**
     * 上传zip图片压缩包
     *
     * @param file       zip图片压缩包
     * @param password   password
     * @param type       // 1-人脸空间 2-头像空间 3-抓拍空间 4-未区分空间
     * @param companyId  companyId
     * @param importTime importTime
     * @return json
     */
    public ImageUploadResult uploadZipFiles(MultipartFile file, String password, String type, String companyId, String importTime) throws Exception {
        ImageUploadResult imageUploadResult = new ImageUploadResult();

        String filename = file.getOriginalFilename();
        assert filename != null;

        String fileType = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(Locale.US);
        String randomNum = String.valueOf(new Random().nextInt(10000000));

        if (fileType.equals("zip")) {
            String path = "E:\\work\\202007\\开发环境\\项目资料\\写字楼\\download";
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");


            //-----------1.transfer source file to server and unzip it-------------
            //新建临时文件名
            String newImgfilePrefix = df.format(new Date()) + "_" + new Random().nextInt(10000000);
            //上传文件路径
            String saveImagePathFile = path + File.separator + newImgfilePrefix + "." + fileType;
            //解压后文件路径
            String unzipPath = path + File.separator + newImgfilePrefix;

            File needUnzipFile = new File(saveImagePathFile);
            // Transfer the received file to the given destination file
            file.transferTo(needUnzipFile);

            //unzip the source_file to destination folder
            UnzipFileUtils.unZipFiles(needUnzipFile, unzipPath);

            //-----------2.check image to classify fail,successs list-------------------
            List<File> fileList = new ArrayList<>();
            FileUtils.getSubFiles(unzipPath, fileList);


            //符合条件的图片集合
            List<File> fileArrayList = new ArrayList<>();

            //有问题的图片
            List<ImageUploadInfoVo> failImglist = new ArrayList<>();

            //成功的图片
            List<ImageUploadInfoVo> successImgList = new ArrayList<>();

            //get space and domain for the image will upload
            String domain = UploadDomain.uploadDomainMap.get(type+"-"+env);
            String space = UploadSpace.uploadBucketMap.get(type+"-"+env);
            if (domain == null || space == null) {
                imageUploadResult.setCode("400");
                imageUploadResult.setMsg("图片上传类型错误");
                FileUtils.deleteFile(saveImagePathFile);
                FileUtils.deleteDirectory(unzipPath);
//                needUnzipFile=null;
                return imageUploadResult;
            }


            if(fileList!=null && fileList.size()>0) {
                //单词不能超过400张
                if(fileList.size()>imageNum){
                    imageUploadResult.setCode("400");
                    imageUploadResult.setMsg("单次导入不能超过"+imageNum+"张");
                    return imageUploadResult;
                }
                imageUploadResult.setCode("200");
                for (File image : fileList) {
                    ImageUploadInfoVo imageFail=new ImageUploadInfoVo();
                    ImageUploadInfoVo imageSeccess=new ImageUploadInfoVo();

                    String imageName = image.getName();
                    imageFail.setImagename(imageName);

                    //判断文件类型
                    String failMsg="";
                    String fileTypes=image.getName().substring(image.getName().lastIndexOf(".") + 1).toLowerCase(Locale.US);
                    if(!fileTypes.equals("jpg") && !fileTypes.equals("png") ){
                        failMsg=failMsg+"图片类型有误,";
                        imageFail.setMsg(failMsg.substring(0,failMsg.length()-1));
                        failImglist.add(imageFail);
                        imageUploadResult.setFailImageList(failImglist);
                        continue;
                    }

                    //naming rule ,must contains("_")
                    if (!imageName.contains("_")) {
                        failMsg=failMsg+"图片名称有问题,";
                        imageFail.setMsg(failMsg.substring(0,failMsg.length()-1));
                    } else {
                        String[] imgStr=imageName.split("_");
                        if(imgStr.length>2){
                            if(StringUtils.isEmpty(failMsg)){
                                failMsg=failMsg+"图片名称有问题,";
                            }
                            imageFail.setMsg(failMsg.substring(0,failMsg.length()-1));
                        }else {

//                            String newImgName = randomNum+file1.getName() ;
//                            MultipartFile mfile = new CommonsMultipartFile(FileUtils.createFileItem(file1, newImgName));
                            MultipartFile mfile = new CommonsMultipartFile(FileUtils.createFileItem(image, image.getName()));
                            //验证手机号
                            String phone=imgStr[1].substring(0,imgStr[1].indexOf("."));
                            Pattern p1 = Pattern.compile("^((1[0-9]))\\d{9}$");

                            Long fileSize = mfile.getSize();
                            //单张图片不能超过5M
                            // 判断图片大小
                            if (!FileUtils.checkFileSize(fileSize,imageSize,"M")) {
                                failMsg=failMsg+"图片过大,";
                                imageFail.setMsg(failMsg.substring(0,failMsg.length()-1));
                            }else if(!p1.matcher(phone).matches()){
                                failMsg=failMsg+"手机号有误";
                                imageFail.setMsg(failMsg);
                            } else {
                                imageSeccess.setImagename(imgStr[0]);
                                imageSeccess.setImagePhone(imgStr[1].substring(0,imgStr[1].indexOf(".")));

                                String imgUrl = domain+space + "/" +randomNum+"_"+ image.getName();
                                log.info("给前端返回预成功的imgUrl="+imgUrl);
                                imageSeccess.setImageUrl(imgUrl);
                                successImgList.add(imageSeccess);
                                fileArrayList.add(image);
                            }

                        }

                    }
                    if(!StringUtils.isEmpty(imageFail.getMsg())){
                        failImglist.add(imageFail);
                    }
                }
                imageUploadResult.setFailImageList(failImglist);
                imageUploadResult.setSuccessImageList(successImgList);
            }else {
                //zip没有图片文件
                imageUploadResult.setCode("200");
                imageUploadResult.setMsg("上传的文件没有图片文件");
            }

            //-----------3. upload image list to qiniu server-------------------
            //启用线程批上传照片到七牛云
            if(fileArrayList!=null && fileArrayList.size()>0){

//                String modifyUser= String.valueOf(ShiroUtils.getUserId());
                String modifyUser= "test01";
//                String tenantCode= String.valueOf(ShiroUtils.getSysUser().getTenantCode());
                String tenantCode= "000000";

                ImageUploadThread img=new ImageUploadThread(qiniuAccessKey,qiniuSecretKey,fileArrayList,saveImagePathFile,unzipPath,domain,space,randomNum,importTime,companyId,modifyUser,tenantCode);
                Thread thread=new Thread(img);
                thread.start();
            }

        } else {
            imageUploadResult.setCode("400");
            imageUploadResult.setMsg("the file's type your upload isn't zip");
        }


        return imageUploadResult;
    }

}
