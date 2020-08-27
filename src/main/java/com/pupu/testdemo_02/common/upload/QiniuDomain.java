package com.pupu.testdemo_02.common.upload;

import lombok.Getter;

/**
 * 慧街坊-七牛云图
 */
@Getter
public enum QiniuDomain {
    TEST_FACE("hjf-test-faces", "http://hjftestfaces.zgzhongnan.com/"),     // 测试1-->人脸空间
    TEST_HEAD("hjf-test-heads", "http://hjftestheads.zgzhongnan.com/"),     // 测试2-->头像空间
    TEST_CAPTURE("hjf-test-photos", "http://hjftestphotos.zgzhongnan.com/"), //测试3-->抓拍空间
    TEST_OTHER("hjf-test-other", "http://hjftestother.zgzhongnan.com/"),    // 测试4-->未区分空间
    TEST_APK("hjf-test-apks", "http://hjftestapks.zgzhongnan.com/"),        // 测试5-->APK文件空间
    PROD_FACE("hjf-prod-faces", "http://hjfprodfaces.zgzhongnan.com/"),     // 正式1-->人脸空间
    PROD_HEAD("hjf-prod-heads", "http://hjfprodheads.zgzhongnan.com/"),     // 正式2-->头像空间
    PROD_CAPTURE("hjf-prod-photos", "http://hjfprodphotos.zgzhongnan.com/"), // 正式3-->抓拍空间
    PROD_OTHER("hjf-prod-other", "http://hjfprodother.zgzhongnan.com/"),    // 正式4-->未区分空间
    PROD_APK("hjf-prod-apks", "http://hjfprodapks.zgzhongnan.com/");        // 正式5-->APK文件空间

    /**
     * bucket 目录空间名
     */
    private final String space;
    /**
     * domain 图片对应的域名
     */
    private final String domain;

    QiniuDomain(String space, String domain) {
        this.space = space;
        this.domain = domain;
    }
    public String getSpace() {
        return space;
    }

    public String getDomain() {
        return domain;
    }

}
