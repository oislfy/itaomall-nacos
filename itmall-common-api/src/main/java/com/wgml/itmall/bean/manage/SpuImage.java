package com.wgml.itmall.bean.manage;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/4 18:45
 * @description TODO
 * @since JDK1.8
 */
@Data
public class SpuImage implements Serializable {
    @Column
    @Id
    private String id;
    @Column
    private String spuId;
    @Column
    private String imgName;
    @Column
    private String imgUrl;

}
