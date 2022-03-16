package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/13 1:58
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)//链式setter方法
public class SkuLsAttrValue implements Serializable {
    private static final long serialVersionUID = -5862751017418735484L;
    private String valueId;
}
