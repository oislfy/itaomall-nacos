package com.wgml.itmall.manage.mapper;

import com.wgml.itmall.bean.manage.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/8 12:11
 * @description TODO
 * @since JDK1.8
 */
public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    /**
     * @param spuId
     * @return: java.util.List<com.wgml.itmall.bean.manage.SkuSaleAttrValue>
     * @author Ye Linfang
     * @date 2021/8/9 15:25
     * @description 根据spuId获取查询skusaleattrvalue 用来切换skuInfo 注意排序skuid，saleattrid
     */
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);
}
