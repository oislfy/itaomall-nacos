package com.wgml.itmall.user.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author oislfy
 * @date 2021-11-09 2:31
 */
public class MyHanderBlock {
    public static String testHandler(BlockException blockException) {
        String message = blockException.getMessage();
        return "糟糕，服务繁忙，请稍后再试！(╥﹏╥)o.........";
    }
}
