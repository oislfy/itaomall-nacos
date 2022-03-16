package com.wgml.itmall.bean.manage;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/8 12:01
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class SkuSaleAttrValue implements Serializable {
    @Id
    @Column
    String id;

    @Column
    String skuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrValueId;

    @Column
    String saleAttrName;

    @Column
    String saleAttrValueName;

}
