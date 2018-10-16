package com.mispower.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author wuguolin
 */
public class ExportUtil {

    /**
     * basic path
     */
    private final static String BASIC_PATH = "export/";

    /**
     * generator BufferedWriter
     *
     * @param pathName full path
     * @return BufferedWriter
     */
    private static BufferedWriter buildStream(String pathName) {

        File csvFile;
        BufferedWriter csvFileOutputStream = null;

        //detection Basic Path is exist or not,if not create Basic Path
        File file = new File(BASIC_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        //create file
        csvFile = new File(pathName);

        //initialize BufferedWriter with UTF-8
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(csvFile);
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(fileOutputStream,
                    StandardCharsets.UTF_8), 1024);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return csvFileOutputStream;
    }

    /**
     * generateFile
     *
     * @param fileName filename
     * @param postfix  postfix
     * @param title    title
     * @param data     data
     * @return String
     */
    public static String generateFile(String fileName, String postfix, String title, List data) {

        String filePath = BASIC_PATH + fileName + "." + postfix;
        if (!isExist(fileName, postfix)) {
            final BufferedWriter bufferedWriter = buildStream(filePath);
            try {
                if (null != title) {
                    bufferedWriter.write(title);
                    bufferedWriter.newLine();
                }
                generateRows(bufferedWriter, data);
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * download file: file is already exist
     *
     * @param resp     HttpServletResponse
     * @param filePath file path
     */
    public static void download(HttpServletResponse resp, String filePath) {
        File file = new File(filePath);
        //set response parameters
        resp.setContentType("application/octet-stream");
        resp.setContentType("application/force-download");
        resp.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

        BufferedInputStream bis = null;
        OutputStream os;
        try {
            os = resp.getOutputStream();
            //add bom to avoid chinese garbled
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            bis = new BufferedInputStream(new FileInputStream(file));
            //buff copy data form bis
            byte[] buff = new byte[bis.available()];
            int len;
            while ((len = bis.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * download:file is not exist.
     *
     * @param resp     HttpServletResponse
     * @param fileName 文件名
     * @param postfix  后缀
     * @param title    标题
     * @param data     数据
     */
    public static void download(HttpServletResponse resp, String fileName, String postfix, String title, List data) {
        String pathName = ExportUtil.generateFile(fileName, postfix, title, data);
        download(resp, pathName);
    }

    /**
     * file is exist or not
     *
     * @param fileName 文件名
     * @param postfix  后缀
     * @return boolean
     */
    public static boolean isExist(String fileName, String postfix) {
        String pathName = BASIC_PATH + fileName + "." + postfix;
        //检测文件是否存在
        File file = new File(pathName);
        return file.isFile();
    }

    /**
     * 获取文件路径
     *
     * @param fileName 文件名
     * @param postfix  后缀
     * @return String
     */
    public static String getFilePath(String fileName, String postfix) {
        return BASIC_PATH + fileName + "." + postfix;
    }

    /**
     * generate rows for export file. each element of the data must already format before you call generate file.also you
     * can overwrite this function to define your processor logic,this way is more flexible.
     *
     * @param bufferedWriter bufferedWriter
     * @param data           data
     */
    public static void generateRows(BufferedWriter bufferedWriter, List data) {

        try {
            for (Object o : data) {
                bufferedWriter.write(o.toString());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
