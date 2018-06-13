package com.example.apple.myapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**********************************************************************
 *
 *
 * @类名 TestZIP
 * @包名 com.example.apple.myapplication
 * @author 谢晗超
 * @创建日期 2018/6/12
 ***********************************************************************/
public class TestZIP {

    private static OnZipFinishListener mOnZipFinishListener;

    public static void setZipFinishListener(OnZipFinishListener onZipFinishListener){
        mOnZipFinishListener = onZipFinishListener;
    }

    /**
     * 功能:压缩多个文件成一个zip文件
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles(Set<File> srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            Iterator<File> iterator = srcfile.iterator();
            while (iterator.hasNext()){
                File file = iterator.next();
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            mOnZipFinishListener.onZipFinish(true);
        } catch (Exception e) {
            mOnZipFinishListener.onZipFinish(false);
        }
    }

    /**
     * 功能:解压缩
     * @param zipfile：需要解压缩的文件
     * @param descDir：解压后的目标目录
     */
    public static void unZipFiles(File zipfile, String descDir) {
        try {
            ZipFile zf = new ZipFile(zipfile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zf.getInputStream(entry);
                OutputStream out = new FileOutputStream(descDir + zipEntryName);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
                System.out.println("解压缩完成.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    /**功能:
//     * @param args
//     */
//    public static void main(String[] args) {
//        //2个源文件
//        File f1 = new File("D:\\test\\1.csv");
//        File f2 = new File("D:\\test\\2.xlsx");
//        File[] srcfile = { f1, f2 };
//
//        //压缩后的文件
//        File zipfile = new File("D:\\test\\3.zip");
//        TestZIP.zipFiles(srcfile, zipfile);
//
//        //需要解压缩的文件
//        File file = new File("D:\\test\\3.zip");
//        //解压后的目标目录
//        String dir = "D:\\test\\1\\";
//        TestZIP.unZipFiles(file, dir);
//    }
}
