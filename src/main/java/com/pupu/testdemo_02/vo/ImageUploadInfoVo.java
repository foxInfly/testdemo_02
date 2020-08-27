package com.pupu.testdemo_02.vo;

import lombok.Data;

import java.io.Serializable;

/**图片上传信息VO
 * @author lipu
 * @since 2020-08-26 10:58:48
 */
@Data
public class ImageUploadInfoVo implements Serializable {
    private static final long serialVersionUID = 211605255841443585L;


    /**
     * 图片名称
     */
    private String Imagename;
    /**
     * 图片自带手机号
     */
    private String ImagePhone;
    private String msg;
    /**
     * 图片对应的访问url
     */
    private String imageUrl;


}
