package com.wgml.itmall.list.service;

import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.list.SkuLsParams;
import com.wgml.itmall.bean.list.SkuLsResult;
import org.elasticsearch.rest.RestStatus;

/**
 * @author Ye Linfang
 * @date 2021/8/13 13:28
 */
public interface ListService {
    RestStatus saveSkuLsInfo(SkuLsInfo skuLsInfo);

    SkuLsResult search(SkuLsParams skuLsParams);

    /**
     * @param skuId
     * @return: java.lang.Boolean
     * @author Ye Linfang
     * @date 2021/8/13 18:33
     * @description 检查商品是否上架 true 上架  false 没上架
     */
    Boolean checkOnSale(String skuId);

    /**
     * @param spuId
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 20:02
     * @description 切换属性值操作：从elasticsearch拿valuesSkuJson进行匹配
     */
    String makeValuesSkuJsonByES(String spuId);

    void incrHotScore(String skuId);
}
