package com.wgml.itmall.list.controller;

import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.bean.list.SkuLsParams;
import com.wgml.itmall.bean.list.SkuLsResult;
import com.wgml.itmall.list.service.ListService;
import com.wgml.itmall.service.manage.ManageServiceFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.rest.RestStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author oislfy
 * @date 2021-11-09 18:45
 */
@RestController
@Api(tags = "商品列表API接口文档")
public class ListController {
    @Resource(name = "listServiceImpl")
    private ListService listService;
    @Resource
    private ManageServiceFeign manageServiceFeign;

    @ApiOperation("保存skuLs到es")
    @PostMapping("/saveSkuLsInfo")
    public RestStatus saveSkuLsInfo(@RequestBody SkuLsInfo skuLsInfo) {
        return listService.saveSkuLsInfo(skuLsInfo);
    }

    @ApiOperation("使用feign调用manage接口getSkuLsInfo,再保存skuLs到es ====》商品上架")
    @GetMapping("/onSale/{skuId}")
    public RestStatus onSale(@PathVariable String skuId) {
        SkuLsInfo skuLsInfo = manageServiceFeign.getSkuLsInfo(skuId);
        if (null != skuLsInfo){
            return listService.saveSkuLsInfo(skuLsInfo);
        }else {
            return RestStatus.BAD_REQUEST;
        }
    }

    @ApiOperation("全文检索")
    @PostMapping("/search")
    public SkuLsResult search(@RequestBody SkuLsParams skuLsParams) {
        return listService.search(skuLsParams);
    }

    /**
     * @param skuId
     * @return: java.lang.Boolean
     * @author Ye Linfang
     * @date 2021/8/13 18:33
     * @description 检查商品是否上架 true 上架  false 没上架
     */
    @ApiOperation("检查商品是否上架 true 上架  false 没上架")
    @PostMapping("/checkOnSale")
    public Boolean checkOnSale(@RequestParam String skuId) {
        return listService.checkOnSale(skuId);
    }

    /**
     * @param spuId
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 20:02
     * @description 切换属性值操作：从elasticsearch拿valuesSkuJson进行匹配
     */
    @ApiOperation("切换属性值操作：从elasticsearch拿valuesSkuJson进行匹配")
    @PostMapping("/makeValuesSkuJsonByES")
    public String makeValuesSkuJsonByES(@RequestParam String spuId) {
        return listService.makeValuesSkuJsonByES(spuId);
    }

    @ApiOperation("热点商品访问次数每次加一")
    @PostMapping("/incrHotScore")
    public void incrHotScore(@RequestParam String skuId) {
        listService.incrHotScore(skuId);
    }

}
