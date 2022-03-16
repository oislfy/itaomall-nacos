package com.wgml.itmall.bean.manage;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/2 19:11
 * @description TODO
 * @since JDK1.8
 */
@Data
public class BaseAttrInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //主键自动回填
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;
    @Transient
    private List<BaseAttrValue> attrValueList;

}
