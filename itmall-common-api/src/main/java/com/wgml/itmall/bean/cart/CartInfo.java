package com.wgml.itmall.bean.cart;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ye Linfang
 * @date 2021/8/18 21:17
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class CartInfo implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    String id;
    @Column
    String userId;
    @Column
    String skuId;
    @Column
    BigDecimal cartPrice;
    @Column
    Integer skuNum;
    @Column
    String imgUrl;
    @Column
    String skuName;
    @Column
    Date updateTime;

    // 实时价格

    @Transient
    BigDecimal skuPrice;

    // 下订单的时候，商品是否勾选

    @Transient
    String isChecked = "0";

}
