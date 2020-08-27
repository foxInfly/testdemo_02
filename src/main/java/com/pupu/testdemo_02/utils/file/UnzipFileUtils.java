package com.pupu.testdemo_02.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 解压缩zip文件
 *
 * @author lipu
 * @since 2020-08-26 14:01:29
 */
@Slf4j
public class UnzipFileUtils {


    /**
     * 解压zip文件到指定路径下，根级文件夹是zip的名字
     *
     * @param needUnzipFile 需要解压的zip源文件
     * @param unzipPath     要解压的目的路径
     */
    public static void unZipFiles(File needUnzipFile, String unzipPath) throws IOException {
        System.out.println("******************unzip: "+needUnzipFile.getName()+" begin********************");
        ZipFile zip = new ZipFile(needUnzipFile, Charset.forName("GBK"));//解决zip文件中有中文目录或者中文文件

        //迭代循环
        for (Enumeration files = zip.entries(); files.hasMoreElements(); ) {
            ZipEntry file = (ZipEntry) files.nextElement();

            //get file name,  used for name the file who is writed  out
            String zipFileName = File.separator+file.getName().replaceAll("\\\\", Matcher.quoteReplacement(File.separator)).replaceAll("/",Matcher.quoteReplacement(File.separator));

            //get file's inputStream, used for write out
            InputStream in = zip.getInputStream(file);

            String name = file.getName();
            String temName = name.split(".").length==0?file.getName():name.split(".")[0];
            String outPath = unzipPath+zipFileName;


            File newfile = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));

            //判断路径是否存在,不存在则创建文件路径
            if (!newfile.exists()) {
                boolean b = newfile.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是,上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }

            //输出文件路径信息
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }

            out.close();
            in.close();
        }
        zip.close();
        System.out.println("******************unzip "+needUnzipFile.getName()+" end. ********************");
    }
}
