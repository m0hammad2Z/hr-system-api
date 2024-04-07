package org.hrsys.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Constants {
    public static final String SECRET;
    public static final long EXPIRATION_TIME = 864_000_000;
    public static final String HEADER_STRING = "Authorization";

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/application.properties"));
            SECRET = properties.getProperty("JWT_SECRET").replace("\"", "");
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties file", e);
        }
    }
}