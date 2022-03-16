package com.wgml.itmall.bean.common;//package com.wgml.itmall.bean.common;
//
//import lombok.Builder;
//import lombok.Data;
//import lombok.Singular;
//
//import java.util.Map;
//
///**
// * @author Ye Linfang
// * @date 2021/8/13 12:23
// * @description BaseResponse 统一返回结果数据封装类
// * @since JDK1.8
// */
////@Accessors(prefix = "is")
//@Data
//@Builder(toBuilder = true)
//public class BaseResponse {
//    private Integer code;
//    private String message;
//    private Boolean isSuccess;
//    @Singular("dat")
//    private Map<String, Object> data;
//
//    public static BaseResponseBuilder okBuilder() {
//        return BaseResponse.builder()
//                .code(20000)
//                .isSuccess(true)
//                .message("成功");
//    }
//
//    public static BaseResponseBuilder errBuilder() {
//        return BaseResponse.builder()
//                .code(20001)
//                .isSuccess(false)
//                .message("失败");
//    }
//
//    public static BaseResponse error() {
//        return BaseResponse.builder()
//                .code(20001)
//                .isSuccess(false)
//                .message("失败")
//                .build();
//    }
//
//    public static BaseResponse ok() {
//        return BaseResponse.builder()
//                .code(20000)
//                .isSuccess(true)
//                .message("成功")
//                .build();
//    }
//
//}
