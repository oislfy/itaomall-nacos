package com.wgml.itmall.service.manage;

import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.manage.*;
import com.wgml.itmall.hanlder.ManageFallbackHandler;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author oislfy
 * @date 2021-11-08 14:17
 */
@FeignClient(value = "itmall-manage", fallback = ManageFallbackHandler.class)
public interface ManageServiceFeign {

    @GetMapping("/manage-service/getCatalog1")
    public List<BaseCatalog1> getCatalog1();

    @GetMapping("/manage-service/getCatalog2")
    public List<BaseCatalog2> getCatalog2(@RequestParam("catalog1Id") String catalog1Id);

    @GetMapping("/manage-service/getCatalog3")
    public List<BaseCatalog3> getCatalog3(@RequestParam("catalog2Id") String catalog2Id);

    @GetMapping("/manage-service/getAttrListByCatalog3Id")
    public List<BaseAttrInfo> getAttrList(@RequestParam("catalog3Id") String catalog3Id);

    @GetMapping("/manage-service/getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(@RequestParam("attrId") String attrId);

    @PostMapping("/manage-service/saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo);
    /*
     *  根据spuInfo查询所有数据
     */

    @GetMapping("/manage-service/getSupInfoList")
    public List<SpuInfo> getSupInfoList(@RequestBody SpuInfo spuInfo);

    /*
     *  查询基本销售属性表
     */

    @GetMapping("/manage-service/getBaseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList();

    /*
     *  保存spuInfo
     */

    @PostMapping("/manage-service/saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo);

    /*
     *  根据spuId获取spuIamgeList
     */

    @GetMapping("/manage-service/getSpuImageListBySpuId")
    public List<SpuImage> getSpuImageListBySpuId(@RequestBody SpuImage spuImage);

    /*
     *  根据spuid获取销售属性和属性集合
     */

    @GetMapping("/manage-service/getSpuSaleAttrListBySpuId")
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@RequestParam("spuId") String spuId);

    /*
     *  保存或更新skuInfo
     */

    @PostMapping("/manage-service/saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo);


    /*
     *  根据skuId获取skuInfo和skuImage
     */

    @GetMapping("/manage-service/getSkuInfoByRedis")
    public SkuInfo getSkuInfoByRedis(@RequestParam("skuId") String skuId);

    @GetMapping("/manage-service/getSkuInfoDB")
    public SkuInfo getSkuInfoDB(@RequestParam("skuId")String skuId);
    /*
     * 根据skuId和spuId查询spuSaleAttr 用于回显和锁定
     */

    @GetMapping("/manage-service/getSpuSaleAttrListCheckBySku")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@RequestBody SkuInfo skuInfo);

    /*
     *  根据spuId获取查询skusaleattrvalue 用来拼接json字符串
     */

    @GetMapping("/manage-service/getSkuSaleAttrValueListBySpu")
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(@RequestParam("spuId") String spuId);

    /*
     *  根据平台属性值id集合获取平台属性、平台属性值名称等信息
     */

    @GetMapping("/manage-service/getAttrListByAttrValueIdList")
    public List<BaseAttrInfo> getAttrList(@RequestBody List<String> attrValueIdList);

    /*
     *  //后台拿数据，后台从redis拿skuInfo数据拷贝给skuLsInfo
     */

    @GetMapping("/manage-service/getSkuLsInfo")
    public SkuLsInfo getSkuLsInfo(@RequestParam("skuId") String skuId);
}
