package com.pupu.testdemo_02.common.upload;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lipu
 * @since 2020-08-27 09:59:36
 */
public class UploadSpace {
    public static final Map<String,String> uploadBucketMap = new HashMap<>();
    static {
        uploadBucketMap.put("1-test",QiniuDomain.TEST_FACE.getSpace()) ;
        uploadBucketMap.put("2-test",QiniuDomain.TEST_HEAD.getSpace()) ;
        uploadBucketMap.put("3-test",QiniuDomain.TEST_CAPTURE.getSpace()) ;
        uploadBucketMap.put("4-test",QiniuDomain.TEST_OTHER.getSpace()) ;
        uploadBucketMap.put("1-dev",QiniuDomain.TEST_FACE.getSpace()) ;
        uploadBucketMap.put("2-dev",QiniuDomain.TEST_HEAD.getSpace()) ;
        uploadBucketMap.put("3-dev",QiniuDomain.TEST_CAPTURE.getSpace()) ;
        uploadBucketMap.put("4-dev",QiniuDomain.TEST_OTHER.getSpace()) ;

        uploadBucketMap.put("1-prod",QiniuDomain.PROD_FACE.getSpace()) ;
        uploadBucketMap.put("2-prod",QiniuDomain.PROD_HEAD.getSpace()) ;
        uploadBucketMap.put("3-prod",QiniuDomain.PROD_CAPTURE.getSpace()) ;
        uploadBucketMap.put("4-prod",QiniuDomain.PROD_OTHER.getSpace()) ;
    }
}
