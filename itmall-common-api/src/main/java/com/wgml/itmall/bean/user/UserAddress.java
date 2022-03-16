package com.wgml.itmall.bean.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/7/30 21:42
 * @description TODO
 * @since JDK1.8
 */
@Data
public class UserAddress implements Serializable {

    @Column
    @Id
    private String id;
    @Column
    private String userAddress;
    @Column
    private String userId;
    @Column
    private String consignee;
    @Column
    private String phoneNum;
    @Column
    private String isDefault;


}
