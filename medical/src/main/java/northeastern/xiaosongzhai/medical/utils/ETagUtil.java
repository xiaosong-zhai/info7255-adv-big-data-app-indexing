package northeastern.xiaosongzhai.medical.utils;

import org.springframework.util.DigestUtils;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/7 3:11
 * @Description: generate ETag for response
 */
public class ETagUtil {

    /**
     * generate ETag for response
     * @param content json content
     * @return ETag
     */
    public static String generateETag(String content) {
        // use sha1 to generate ETag
        return DigestUtils.md5DigestAsHex(content.getBytes());
    }
}
