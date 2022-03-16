package com.wgml.itmall.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.wgml.itmall.bean.manage.BaseCatalog1;
import com.wgml.itmall.service.manage.ManageServiceFeign;
import com.wgml.itmall.user.handler.MyHanderBlock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author oislfy
 * @date 2021-11-08 0:22
 */
@RestController
@RefreshScope
public class MyTest {

    @Resource
    private ManageServiceFeign manageServiceFeign;

    @Value("${config.info}")
    private String info;


    @GetMapping("/info")
    @SentinelResource(value = "info",
            blockHandlerClass = MyHanderBlock.class,
            blockHandler = "testHandler")
    public String test() {
        return info;
    }


    @GetMapping("/get")
    public List<BaseCatalog1> get() {
        return manageServiceFeign.getCatalog1();
    }
}
