package com.wgml.itmall.hanlder;

import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.manage.*;
import com.wgml.itmall.service.manage.ManageServiceFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oislfy
 * @date 2021-11-08 14:33
 */
@Component
public class ManageFallbackHandler implements ManageServiceFeign {


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return null;
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        return null;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        return null;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        return null;
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        return null;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

    }

    @Override
    public List<SpuInfo> getSupInfoList(SpuInfo spuInfo) {
        return null;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return null;
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

    }

    @Override
    public List<SpuImage> getSpuImageListBySpuId(SpuImage spuImage) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId) {
        return null;
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

    }

    @Override
    public SkuInfo getSkuInfoByRedis(String skuId) {
        return null;
    }

    @Override
    public SkuInfo getSkuInfoDB(String skuId) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return null;
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return null;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        return null;
    }

    @Override
    public SkuLsInfo getSkuLsInfo(String skuId) {
        return null;
    }
}
