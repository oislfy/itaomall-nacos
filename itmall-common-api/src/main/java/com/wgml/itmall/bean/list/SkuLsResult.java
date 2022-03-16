package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/13 17:44
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class SkuLsResult implements Serializable {
    List<SkuLsInfo> skuLsInfoList;
    long total;
    long totalPages;
    List<String> attrValueIdList;
}
