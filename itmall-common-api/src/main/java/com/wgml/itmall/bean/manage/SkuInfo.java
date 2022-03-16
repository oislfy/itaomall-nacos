package com.wgml.itmall.bean.manage;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/8 11:59
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class SkuInfo implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    String id;

    @Column
    String spuId;

    @Column
    BigDecimal price;

    @Column
    String skuName;

    @Column
    BigDecimal weight;

    @Column
    String skuDesc;

    @Column
    String catalog3Id;

    @Column
    String skuDefaultImg;

    @Transient
    List<SkuImage> skuImageList;

    @Transient
    List<SkuAttrValue> skuAttrValueList;

    @Transient
    List<SkuSaleAttrValue> skuSaleAttrValueList;

    //用于缓存而外添加的属性

    @Transient
    List<SpuSaleAttr> SpuSaleAttrList;

    //存sku的所有销售属性和skuId，用于valueid1|valueid2|valueid3|... ：skuId

    @Transient
    private String valuesSkuJson;

}
