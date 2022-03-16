package com.wgml.itmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.wgml.itmall.utils.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Ye Linfang
 * @date 2021/8/17 17:01
 * @description TODO
 * @since JDK1.8
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        //把token保存到cookie
        if (!StringUtils.isEmpty(token)) {
            CookieUtil.setCookie(request, response, "token", token, WebConst.COOKIE_MAXAGE, false);
        }
        //获取Cookie中的token
        else {
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        if (!StringUtils.isEmpty(token)) {
            //读取token
            Map<String, Object> map = getUserMapByToken(token);
            if (map != null) {
                String nickName = (String) map.get("nickName");
                request.setAttribute("nickName", nickName);
            }
        }
        HandlerMethod handle = null;
        try {
            handle = (HandlerMethod) handler;
        } catch (Exception e) {
            return true;
        }
        LoginRequire loginRequie = handle.getMethodAnnotation(LoginRequire.class);
        if (loginRequie != null) {
            String currentIp = IpAddressUtil.getIpAddress(request);
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + currentIp);
            if ("success".equals(result)) {
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {
                if (loginRequie.autoRedirect()) {
                    String requestURL = request.getRequestURL().toString();
                    String encodeURL = UrlEncodeUtil.getURLEncoderString(requestURL);
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }
            }
        }
        return true;
    }

    private Map<String, Object> getUserMapByToken(String token) {
        try {
            String tokenUserInfo = org.apache.commons.lang3.StringUtils.substringBetween(token, ".");
            Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
            byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
            String tokenJson = new String(tokenBytes, "UTF-8");
            Map map = JSON.parseObject(tokenJson, Map.class);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

