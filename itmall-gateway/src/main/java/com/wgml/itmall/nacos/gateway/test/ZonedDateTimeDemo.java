package com.wgml.itmall.nacos.gateway.test;

import java.time.ZonedDateTime;

/**
 * @author oislfy
 * @date 2021-11-16 16:56
 */
public class ZonedDateTimeDemo
{
    public static void main(String[] args)
    {
        ZonedDateTime zbj = ZonedDateTime.now(); // 默认时区
        System.out.println(zbj);
//        ZonedDateTime zny = ZonedDateTime.now(ZoneId.of("America/New_York")); // 用指定时区获取当前时间
//        System.out.println(zny);
//        ZonedDateTime zny = ZonedDateTime.now(ZoneId.of("America/Shanghai")); // 用指定时区获取当前时间
//        System.out.println(zny);
    }
}
