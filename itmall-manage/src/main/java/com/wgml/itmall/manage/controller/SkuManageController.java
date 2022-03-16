package com.wgml.itmall.manage.controller;

import com.wgml.itmall.bean.manage.SkuInfo;
import com.wgml.itmall.bean.manage.SpuImage;
import com.wgml.itmall.bean.manage.SpuSaleAttr;
import com.wgml.itmall.manage.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/8 10:50
 * @since JDK1.8
 */
@RestController
@CrossOrigin
@Api(tags = "后台管理模块API文档-sku")
public class SkuManageController {
    @Resource
    private ManageService manageService;
//    @Resource
//    private ListService listService;

    @ApiOperation(value = "根据spuId获取spu图片")
    @GetMapping("/spuImageList")
    public List<SpuImage> getSpuImageListBySpuId( SpuImage spuImage) {
        if (spuImage != null && !StringUtils.isEmpty(spuImage.getSpuId())) {
            return manageService.getSpuImageListBySpuId(spuImage);
        }
        return null;
    }

    @ApiOperation(value = "根据spuId获取spu销售属性")
    @GetMapping("/spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@RequestParam String spuId) {
        if (!StringUtils.isEmpty(spuId)) {
            return manageService.getSpuSaleAttrListBySpuId(spuId);
        }
        return null;
    }
    @ApiOperation(value = "保存sku")
    @PostMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);
        return "OK";
    }

    //    @RequestMapping("/onSale")
//    @ResponseBody
//    public BaseResponse onSale(@RequestParam String skuId){
//        //检查是否已经上架
//        Boolean checkOnSale = listService.checkOnSale(skuId);
//        //上架过了直接返回
//        if(checkOnSale == true) {
//            return BaseResponse.okBuilder().message("该商品已上架").build();
//        }
//        //上架 把数据库中的数据保存到es
//        SkuLsInfo skuLsInfo = new SkuLsInfo();
//        SkuInfo skuInfoDB = manageService.getSkuInfo(skuId);
//        BeanUtils.copyProperties(skuInfoDB,skuLsInfo);
//        Boolean flat = listService.saveSkuLsInfo(skuLsInfo);
//        if(flat == true){
//            BaseResponse b = BaseResponse.okBuilder().message("上架成功").build();
//            System.out.println(b);
//            return b;
//        }
//        BaseResponse b = BaseResponse.errBuilder().message("上架失败").build();
//        System.out.println(b);
//        return b;
//    }

    /**
     * @param skuId
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 11:37
     * @description 商品上架
     */
//    @RequestMapping("/onSale")
//    @ResponseBody
//    public String onSale(@RequestParam String skuId) {
//        //检查是否已经上架
//        Boolean checkOnSale = listService.checkOnSale(skuId);
//        //上架 把数据库中的数据保存到es
//        //后台拿数据，后台从redis拿skuInfo数据拷贝给skuLsInfo
//        SkuLsInfo skuLsInfo = manageService.getSkuLsInfo(skuId);
//
//        Boolean flat = listService.saveSkuLsInfo(skuLsInfo);
//        if (flat == true) {
//            //上架过了直接返回
//            if (checkOnSale == true) {
//                return "重新上架商品";
//            }
//            return "上架成功";
//        }
//        return "上架失败";
//    }
}
