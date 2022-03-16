package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ye Linfang
 * @date 2021/8/15 1:20
 * @description 面包屑
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class Crumbs {
    /**
     * @description 面包屑名称 格式：（属性名称：属性值名称）
     */
    private String name;
    /**
     * @description 面包屑去除跳转url地址
     */
    private String urlParams;
}
