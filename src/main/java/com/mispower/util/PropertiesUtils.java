package com.mispower.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * read config file
 *
 * @author yuanyifan
 */
public class PropertiesUtils {

    private static Properties properties = new Properties();

    /**
     * 自动读取配置文件
     * 先尝试从文件中获取，再从配置中获取
     *
     * @param propertiesFileName 配置文件名
     * @return
     * @throws IOException
     */
    public static void autoReadProperties(String propertiesFileName) throws IOException {
        try {
            // 尝试读取工作目录下conf文件夹
            readPropertiesFile(propertiesFileName);
        } catch (IOException ex) {
            // 尝试读取资源文件
            readPropertiesResource(propertiesFileName);
        }
    }


    /**
     * 读取配置（从资源文件根目录中）
     *
     * @param propertiesResourceName 配置文件名
     * @return properties对象
     */
    public static void readPropertiesResource(String propertiesResourceName) throws IOException {
        final Properties properties = new Properties();
        final InputStream stream = PropertiesUtils.class.getResourceAsStream("/" + propertiesResourceName);
        properties.load(stream);

    }

    /**
     * 读取配置（默认从工作目录中的conf文件夹）
     *
     * @param propertiesFileName 配置文件名
     * @return properties对象
     */
    public static void readPropertiesFile(String propertiesFileName) throws IOException {
        final Properties properties = new Properties();
        final InputStream stream = new FileInputStream("conf/" + propertiesFileName);
        properties.load(stream);
    }

    /**
     * 根据指定属性名获取属性值.
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return "";
    }


}
