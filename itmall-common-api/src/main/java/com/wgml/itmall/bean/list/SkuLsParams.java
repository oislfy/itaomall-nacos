package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/13 17:40
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class SkuLsParams implements Serializable {
    String keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo = 1;

    int pageSize = 20;
}
