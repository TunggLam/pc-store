package com.example.pcstore.utils;

import com.example.pcstore.logging.LoggingFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class PasswordUtils {

    private PasswordUtils() {
    }

    private static final Logger LOGGER = LoggingFactory.getLogger(PasswordUtils.class);

    public static String endCodeMD5(final String s) {
        final var MD5 = "MD5";
        try {
            // Create MD5 Hash
            var digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            var hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e) {
            LOGGER.error("[PASSWORD_UTILS][ERROR_GENERATE_ENCODE_PASSWORD] Exception: {}", e.getMessage());
            return null;
        }
    }

}
