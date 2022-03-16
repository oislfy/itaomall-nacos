package com.wgml.itmall.manage.mapper;

import com.wgml.itmall.bean.manage.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Ye Linfang
 * @since JDK1.8
 */
public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    /**
     * @param catalog3Id
     * @return: java.util.List<com.wgml.itmall.bean.manage.BaseAttrInfo>
     * @author Ye Linfang
     * @date 2021/8/8 11:19
     * @description 根据三级分裂id获取平台属性和属性值list集合
     */
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id);
    /**
     * @param valueIds
     * @return: java.util.List<com.wgml.itmall.bean.manage.BaseAttrInfo>
     * @author Ye Linfang
     * @date 2021/8/14 23:26
     * @description 根据平台属性值id集合获取平台属性、平台属性值名称等信息
     */
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String valueIds);
}
