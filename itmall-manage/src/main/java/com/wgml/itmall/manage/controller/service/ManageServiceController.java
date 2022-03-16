package com.wgml.itmall.manage.controller.service;

import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.manage.*;
import com.wgml.itmall.manage.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author oislfy
 * @date 2021-11-14 16:56
 */
@RestController
@Api(tags = "后台管理模块API文档-manage-service")
public class ManageServiceController {
    @Resource
    private ManageService manageService;


    @GetMapping("/manage-service/getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1();
    }

    @GetMapping("/manage-service/getCatalog2")
    public List<BaseCatalog2> getCatalog2(@RequestParam String catalog1Id) {
        return manageService.getCatalog2(catalog1Id);
    }

    @GetMapping("/manage-service/getCatalog3")
    public List<BaseCatalog3> getCatalog3(@RequestParam String catalog2Id) {
        return manageService.getCatalog3(catalog2Id);
    }

    @GetMapping("/manage-service/getAttrListByCatalog3Id")
    public List<BaseAttrInfo> getAttrList(@RequestParam String catalog3Id) {
        return manageService.getAttrList(catalog3Id);
    }

    @GetMapping("/manage-service/getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(@RequestParam String attrId) {
        return manageService.getAttrValueList(attrId);
    }

    @PostMapping("/manage-service/saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
    }

    /*
     *  根据spuInfo查询所有数据
     */

    @GetMapping("/manage-service/getSupInfoList")
    public List<SpuInfo> getSupInfoList(@RequestBody SpuInfo spuInfo) {
        return manageService.getSupInfoList(spuInfo);
    }

    /*
     *  查询基本销售属性表
     */

    @GetMapping("/manage-service/getBaseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return manageService.getBaseSaleAttrList();
    }

    /*
     *  保存spuInfo
     */

    @PostMapping("/manage-service/saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo(spuInfo);
    }

    /*
     *  根据spuId获取spuIamgeList
     */

    @GetMapping("/manage-service/getSpuImageListBySpuId")
    public List<SpuImage> getSpuImageListBySpuId(@RequestBody SpuImage spuImage) {
        return manageService.getSpuImageListBySpuId(spuImage);
    }

    /*
     *  根据spuid获取销售属性和属性集合
     */

    @GetMapping("/manage-service/getSpuSaleAttrListBySpuId")
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@RequestParam String spuId) {
        return manageService.getSpuSaleAttrListBySpuId(spuId);
    }

    /*
     *  保存或更新skuInfo
     */

    @PostMapping("/manage-service/saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);
    }


    /*
     *  根据skuId获取skuInfo和skuImage
     */

    @GetMapping("/manage-service/getSkuInfoByRedis")
    public SkuInfo getSkuInfoByRedis(@RequestParam String skuId) {
        return manageService.getSkuInfoByRedis(skuId);
    }

    @GetMapping("/manage-service/getSkuInfoDB")
    public SkuInfo getSkuInfoDB(@RequestParam String skuId) {
        return manageService.getSkuInfoDB(skuId);
    }

    /*
     * 根据skuId和spuId查询spuSaleAttr 用于回显和锁定
     */

    @GetMapping("/manage-service/getSpuSaleAttrListCheckBySku")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@RequestBody SkuInfo skuInfo) {
        return manageService.getSpuSaleAttrListCheckBySku(skuInfo);
    }

    /*
     *  根据spuId获取查询skusaleattrvalue 用来拼接json字符串
     */

    @GetMapping("/manage-service/getSkuSaleAttrValueListBySpu")
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(@RequestParam String spuId) {
        return manageService.getSkuSaleAttrValueListBySpu(spuId);
    }

    /*
     *  根据平台属性值id集合获取平台属性、平台属性值名称等信息
     */

    @GetMapping("/manage-service/getAttrListByAttrValueIdList")
    public List<BaseAttrInfo> getAttrList(@RequestBody List<String> attrValueIdList) {
        return manageService.getAttrList(attrValueIdList);
    }

    /*
     *  //后台拿数据，后台从redis拿skuInfo数据拷贝给skuLsInfo
     */

    @GetMapping("/manage-service/getSkuLsInfo")
    public SkuLsInfo getSkuLsInfo(@RequestParam String skuId) {
        return manageService.getSkuLsInfo(skuId);
    }

}
