package com.wgml.itmall.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author Ye Linfang
 * @date 2021/8/16 19:51
 * @description TODO
 * @since JDK1.8
 */
public class UrlEncodeUtil {

    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        str = str.replaceAll("\\+", "%2B");
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        str = str.replaceAll("\\+", "%2B");
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
