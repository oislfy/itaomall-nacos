package com.wgml.itmall.bean.user;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/7/30 12:44
 * @description 实体类
 * @since JDK1.8
 */

@Data
@Accessors(chain = true)
public class UserInfo implements Serializable {

    @Id
    @Column
    private String id;
    @Column
    private String loginName;
    @Column
    private String nickName;
    @Column
    private String passwd;
    @Column
    private String name;
    @Column
    private String phoneNum;
    @Column
    private String email;
    @Column
    private String headImg;
    @Column
    private String userLevel;

    public String getId() {
        return id;
    }

}
