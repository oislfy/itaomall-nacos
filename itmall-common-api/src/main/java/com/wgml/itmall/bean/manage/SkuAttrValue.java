package com.wgml.itmall.bean.manage;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/8 12:00
 * @description TODO
 * @since JDK1.8
 */
@Data
public class SkuAttrValue implements Serializable {
    @Id
    @Column
    String id;

    @Column
    String attrId;

    @Column
    String valueId;

    @Column
    String skuId;

}
