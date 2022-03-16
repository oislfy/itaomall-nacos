package com.wgml.itmall.manage.controller;

import com.wgml.itmall.bean.manage.*;
import com.wgml.itmall.manage.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author oislfy
 * @date 2021-11-07 17:27
 */
@RestController
@CrossOrigin
@RefreshScope
@Api(tags = "后台管理模块API文档-manage")
public class ManageController {

    @Resource
    private ManageService manageService;

    @Value("${config.info}")
    private String info;

    @ApiOperation(value = "testconfig")
    @GetMapping("/info")
    public String info() {
        return info;
    }

    /**
     * 获取1级分类
     */

    @ApiOperation(value = "获取1级分类")
    @PostMapping("/getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1();
    }

    /*
     *  获取2级分类
     */

    @ApiOperation(value = "获取2级分类")
    @PostMapping("/getCatalog2")
    public List<BaseCatalog2> getCatalog2(@RequestParam("catalog1Id") String catalog1Id) {
        return manageService.getCatalog2(catalog1Id);
    }

    /*
     *  获取3级分类
     */

    @ApiOperation(value = "获取3级分类")
    @PostMapping("/getCatalog3")
    public List<BaseCatalog3> getCatalog3(@RequestParam("catalog2Id") String catalog2Id) {
        return manageService.getCatalog3(catalog2Id);
    }

    /*
     *  根据三级id获取平台属性数据
     */
    @ApiOperation(value = "根据三级id获取平台属性数据")
    @GetMapping("/attrInfoList")
    public List<BaseAttrInfo> attrInfoList(@RequestParam("catalog3Id") String catalog3Id) {
        return manageService.getAttrList(catalog3Id);
    }

    /*
     *  根据属性id获取属性值
     */

    @ApiOperation(value = "根据属性id获取属性值")
    @PostMapping("/getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(@RequestParam("attrId") String attrId) {
        return manageService.getAttrValueList(attrId);
    }

    /*
     *  保存平台属性
     */

    @ApiOperation(value = "保存平台属性")
    @PostMapping("/saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
    }
}
