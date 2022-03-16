package com.wgml.itmall.bean.manage;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/4 18:34
 * @description TODO
 * @since JDK1.8
 */
@Data
public class BaseSaleAttr implements Serializable {
    @Id
    @Column
    String id;

    @Column
    String name;

}
