package com.pupu.testdemo_02.common.upload;

import com.pupu.testdemo_02.vo.ImageUploadInfoVo;
import lombok.Data;

import java.util.List;

/***图片上传返回实体类
 * @author lipu
 * @since 2020-08-26 10:56:57
 */
@Data
public class ImageUploadResult {

    /**
     * 状态码，200成功，500服务端错误，400客户端错误
     */
    private String code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 失败的图片集合
     */
    private List<ImageUploadInfoVo> failImageList;
    /**
     * 成功的图片集合
     */
    private List<ImageUploadInfoVo> successImageList;
}
