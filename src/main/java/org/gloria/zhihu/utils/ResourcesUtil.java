package org.gloria.zhihu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Create on 2016/12/22 22:33.
 *
 * @author : gloria.
 */
public class ResourcesUtil {

    private static final String EXCEPTION_INFO="Could not find resource : ";
    
    
    private ResourcesUtil() {

    }

    public static InputStream getResourceAsStream(final String resource) throws IOException {
        InputStream in = null;
        ClassLoader loader = ResourcesUtil.class.getClassLoader();
        if (loader != null) {
            in = loader.getResourceAsStream(resource);
        }
        if (in == null) {
            in = ClassLoader.getSystemResourceAsStream(resource);
        }
        if (in == null) {
            throw new IOException(EXCEPTION_INFO.concat(resource));
        }
        return in;
    }

    public static InputStream getResourceAsStream(final ClassLoader loader, final String resource) throws IOException {
        InputStream in = null;
        if (loader != null) {
            in = loader.getResourceAsStream(resource);
        }
        if (in == null) {
            in = ClassLoader.getSystemResourceAsStream(resource);
        }
        if (in == null) {
            throw new IOException(EXCEPTION_INFO.concat(resource));
        }
        return in;
    }


    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        String propfile = resource;
        InputStream in = getResourceAsStream(propfile);
        props.load(in);
        in.close();
        return props;
    }


    public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
        Properties props = new Properties();
        String propfile = resource;
        InputStream in = getResourceAsStream(loader, propfile);
        props.load(in);
        in.close();
        return props;
    }


}
