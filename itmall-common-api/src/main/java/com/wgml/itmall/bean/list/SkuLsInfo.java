package com.wgml.itmall.bean.list;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/13 1:58
 * @description TODO
 * @since JDK1.8
 */
@Data
@ApiModel(value = "SkuInfo保存到es对象",description = "SkuInfo保存到es对象封装")
@Accessors(chain = true)//链式setter方法
public class SkuLsInfo implements Serializable {

    @ApiModelProperty(value = "skuId")
    private String id;

    @ApiModelProperty(value = "spuId")
    private String spuId;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "sku名称")
    private String skuName;

    @ApiModelProperty(value = "三级分类id")
    private String catalog3Id;

    @ApiModelProperty(value = "skuDefaultImg")
    private String skuDefaultImg;

    @ApiModelProperty(value = "hotScore")
    private Long hotScore = 0L;

    @ApiModelProperty(value = "weight")
    private BigDecimal weight;

    @ApiModelProperty(value = "skuDesc")
    private String skuDesc;

    @ApiModelProperty(value = "skuAttrValueList")
    private List<SkuLsAttrValue> skuAttrValueList;

    @ApiModelProperty(value = "valuesSkuJson")
    private String valuesSkuJson;

}