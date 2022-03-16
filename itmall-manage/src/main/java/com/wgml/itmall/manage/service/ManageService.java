package com.wgml.itmall.manage.service;

import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.manage.*;

import java.util.List;

/**
 * @author Ye Linfang
 * @since JDK1.8
 */
public interface ManageService {
    List<BaseCatalog1> getCatalog1();

    List<BaseCatalog2> getCatalog2(String catalog1Id);

    List<BaseCatalog3> getCatalog3(String catalog2Id);

    List<BaseAttrInfo> getAttrList(String catalog3Id);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /*
     *  根据spuInfo查询所有数据
     */

    List<SpuInfo> getSupInfoList(SpuInfo spuInfo);

    /*
     *  查询基本销售属性表
     */

    List<BaseSaleAttr> getBaseSaleAttrList();

    /*
     *  保存spuInfo
     */

    void saveSpuInfo(SpuInfo spuInfo);

    /*
     *  根据spuId获取spuIamgeList
     */

    List<SpuImage> getSpuImageListBySpuId(SpuImage spuImage);

    /*
     *  根据spuid获取销售属性和属性集合
     */
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId);

    /*
     *  保存或更新skuInfo
     */

    void saveSkuInfo(SkuInfo skuInfo);


    /*
     *  根据skuId获取skuInfo和skuImage
     */

    SkuInfo getSkuInfoByRedis(String skuId);

    SkuInfo getSkuInfoDB(String skuId);

    /*
     * 根据skuId和spuId查询spuSaleAttr 用于回显和锁定
     */

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /*
     *  根据spuId获取查询skusaleattrvalue 用来拼接json字符串
     */

    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    /*
     *  根据平台属性值id集合获取平台属性、平台属性值名称等信息
     */

    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);

    /*
     *  //后台拿数据，后台从redis拿skuInfo数据拷贝给skuLsInfo
     */

    SkuLsInfo getSkuLsInfo(String skuId);
}
