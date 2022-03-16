package com.wgml.itmall.manage.controller;

import com.wgml.itmall.bean.manage.BaseSaleAttr;
import com.wgml.itmall.bean.manage.SpuInfo;
import com.wgml.itmall.manage.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/4 13:22
 * @since JDK1.8
 */
@RestController
@CrossOrigin
@Api(tags = "后台管理模块API文档-spu")
public class SpuManageController {

    @Resource
    ManageService manageService;

    /*
     *  根据spuinfo获取属性列表  根据三级分类id获取属性列表
     */

    @ApiOperation(value = "根据spuinfo获取属性列表  根据三级分类id获取属性列表")
    @GetMapping("/spuList")
    public List<SpuInfo> getSpuList(SpuInfo spuInfo) {
        if (spuInfo == null) {
            return null;
        }
        return manageService.getSupInfoList(spuInfo);
    }

    /*
     *  获取基本销售属性列表
     */

    @ApiOperation(value = "获取基本销售属性列表")
    @PostMapping("/baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList() {
        return manageService.getBaseSaleAttrList();
    }

    /*
     *  保存spuInfo
     */

    @ApiOperation(value = "保存spuInfo")
    @PostMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo(spuInfo);
        return "OK";
    }


}
