package com.wgml.itmall.bean.manage;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/4 18:45
 * @description TODO
 * @since JDK1.8
 */
@Data
public class SpuSaleAttr implements Serializable {
    @Id
    @Column
    String id;

    @Column
    String spuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrName;

    @Transient
    List<SpuSaleAttrValue> spuSaleAttrValueList;

}
