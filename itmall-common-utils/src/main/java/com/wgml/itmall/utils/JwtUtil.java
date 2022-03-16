package com.wgml.itmall.utils;

import io.jsonwebtoken.*;

import java.util.Map;

/**
 * @author Ye Linfang
 * @date 2021/8/16 19:31
 * @description json web token:一种基于JSON的开放标准，制作token
 */
public class JwtUtil {
    /**
     * @param key   公共部分：自定义一个KEY=字符串
     * @param param 私有部分：用户信息 {userId,nickName}
     * @param salt  签名部分：ip地址{服务器的} 192.168.67.1  ip地址+KEY=字符串 = 组成一个生成 jwt 的key ！
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/16 19:43
     * @description base64 编码： 将明文转化为暗文！
     */
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }

    /**
     * @param token
     * @param key
     * @param salt
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author Ye Linfang
     * @date 2021/8/16 19:44
     * @description 根据key和盐，解析token 返回用户信息私有部分
     */
    public static Map<String, Object> decode(String token, String key, String salt) {
        Claims claims = null;
        if (salt != null) {
            key += salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            return null;
        }
        return claims;
    }

}
