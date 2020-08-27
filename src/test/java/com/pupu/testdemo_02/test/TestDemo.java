package com.pupu.testdemo_02.test;

import com.pupu.testdemo_02.common.upload.UploadDomain;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Matcher;


/**
 * @author lipu
 * @since 2020-08-26 11:20:29
 */
public class TestDemo {


    @Test
    public void test01(){
        File file = new File("C:\\Users\\Administrator\\Desktop\\temp\\111.zip");
        System.out.println("file.getName(): "+file.getName());
        System.out.println("file.getPath(): "+file.getPath());
        System.out.println("file.getAbsolutePath(): "+file.getAbsolutePath());
        System.out.println("file.getParent(): "+file.getParent());
    }

    @Test
    public void test02(){
//        File file = new File("C:\\Users\\Administrator\\Desktop\\temp\\111.zip");
        File file = new File("TestDemo");
//        new MockMultipartFile(file.getName(), file.getName(),ContentType.A"C:\\Users\\Administrator\\Desktop\\temp\\111.zip");
        File file11 = new File("C:\\Users\\Administrator\\Desktop\\temp\\11");
        if (!file11.exists()) {
            file11.mkdir();
        }
//        file.tr
        System.out.println("file.getName(): "+file.getName());
        System.out.println("file.getPath(): "+file.getPath());
        System.out.println("file.getAbsolutePath(): "+file.getAbsolutePath());
        System.out.println("file.getParent(): "+file.getParent());
    }

    @Test
    public void test03(){
        String a = "C:\\Users\\Administrator\\Desktop\\temp/***111.zip";
        String s = a.replaceAll("/", Matcher.quoteReplacement(File.separator));
        System.out.println(a);
        System.out.println(s);
        String b = "人脸图片》10/";
        String name = b;
        String temName = name.split(".").length==0?b:name.split(".")[0];
        System.out.println(temName);
//        System.out.println(b.split(".")[0]);
    }
    @Test
    public void test04(){
        String a = "C:\\Users\\Administrator\\Desktop\\temp/***111.zip";
        String s = a.replaceAll("/", Matcher.quoteReplacement(File.separator));
        System.out.println(".".lastIndexOf(a));
        System.out.println(a.lastIndexOf("."));

    }

    @Test
    public void test05(){
        String domain = "";
        String bucket = "";
        domain = UploadDomain.uploadDomainMap.get("2");
        bucket = UploadDomain.uploadDomainMap.get("1-test");
        System.out.println(domain);
        System.out.println(bucket);

    }

}
