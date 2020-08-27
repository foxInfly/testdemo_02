package com.pupu.testdemo_02.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author lipu
 * @since 2020-08-26 17:17:10
 */
@Slf4j
public class FileUtils {


    /**
     * 获取指定目录下的所有文件（非文件夹）放入list集合
     * @param rootPath 根目录路径
     * @param fileList List<File>
     * @return List<File>
     */
    public static List<File> getSubFiles(String rootPath, List<File> fileList) {
        File[] files = new File(rootPath).listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                List<File> list = getSubFiles(file.getAbsolutePath(), fileList);
            }else {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /** 创建FileItem
    */
    public static FileItem createFileItem(File file, String fieldName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     *
     * @param len the size of the file in bytes
     * @param size the size of file can't exceed
     * @param unit the unit of the file size.
     * @return boolean
     */
    public static boolean checkFileSize(Long len, double size, String unit) {
//        long len = file.length();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }


    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            long length = file.length();
            boolean delete = file.delete();
            if (delete){
                log.info("删除文件成功：{}",filePath);
                return true;
            }else {
                log.info("删除文件失败：{}",filePath);
                return false;
            }

        }
        log.info("路径为文件或者不存在，不予操作： {}",filePath);
        return true;
    }
    /**
     * 删除文件
     *
     * @param file 文件
     * @return
     */
    public static boolean deleteFile(File file) {
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            long length = file.length();
            boolean delete = file.delete();
            if (delete){
                log.info("删除文件成功：{}",file.getAbsolutePath());
                return true;
            }else {
                log.info("删除文件失败：{}",file.getAbsolutePath());
                return false;
            }

        }
        log.info("路径为文件或者不存在，不予操作： {}",file.getAbsolutePath());
        return true;
    }


    public static void main(String[] args) {
        deleteFile("E:\\work\\202007\\开发环境\\项目资料\\写字楼\\download\\20200827_6340547.zip");
//        deleteFile("E:\\work\\202007\\开发环境\\项目资料\\写字楼\\download\\20200827_5880468.zip");
    }
}
