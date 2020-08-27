package com.pupu.testdemo_02.common.upload;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lipu
 * @since 2020-08-27 09:42:13
 */
public class UploadDomain {
    public static final Map<String,String> uploadDomainMap = new HashMap<>();
    static {
        uploadDomainMap.put("1-test",QiniuDomain.TEST_FACE.getDomain()) ;
        uploadDomainMap.put("2-test",QiniuDomain.TEST_HEAD.getDomain()) ;
        uploadDomainMap.put("3-test",QiniuDomain.TEST_CAPTURE.getDomain()) ;
        uploadDomainMap.put("4-test",QiniuDomain.TEST_OTHER.getDomain()) ;
        uploadDomainMap.put("1-dev",QiniuDomain.TEST_FACE.getDomain()) ;
        uploadDomainMap.put("2-dev",QiniuDomain.TEST_HEAD.getDomain()) ;
        uploadDomainMap.put("3-dev",QiniuDomain.TEST_CAPTURE.getDomain()) ;
        uploadDomainMap.put("4-dev",QiniuDomain.TEST_OTHER.getDomain()) ;

        uploadDomainMap.put("1-prod",QiniuDomain.PROD_FACE.getDomain()) ;
        uploadDomainMap.put("2-prod",QiniuDomain.PROD_HEAD.getDomain()) ;
        uploadDomainMap.put("3-prod",QiniuDomain.PROD_CAPTURE.getDomain());
        uploadDomainMap.put("4-prod",QiniuDomain.PROD_OTHER.getDomain()) ;

    }
}
