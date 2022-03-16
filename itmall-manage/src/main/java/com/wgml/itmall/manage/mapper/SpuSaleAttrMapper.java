package com.wgml.itmall.manage.mapper;

import com.wgml.itmall.bean.manage.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/4 18:50
 * @description TODO
 * @since JDK1.8
 */
public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    /**
     * @param spuId
     * @return: java.util.List<com.wgml.itmall.bean.manage.SpuSaleAttr>
     * @author Ye Linfang
     * @date 2021/8/8 11:49
     * @description 根据spuid获取销售属性和属性集合
     */
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId);

    /**
     * @param skuId
     * @param spuId
     * @return: java.util.List<com.wgml.itmall.bean.manage.SpuSaleAttr>
     * @author Ye Linfang
     * @date 2021/8/9 13:29
     * @description 根据skuId和spuId查询spuSaleAttr
     */
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId, String spuId);


}
