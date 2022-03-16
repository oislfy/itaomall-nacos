package com.wgml.itmall.bean.result;

/**
 * @author oislfy
 * @description 全局统一返回响应编码消息枚举类
 */
public enum ResponseCodeEnume {
    //响应结果枚举对象
    SUCCESS(20000, "操作成功"),
    ERROR(20001, "服务器异常"),
    NOT_FOUND(404, "资源未找到"),
    NOT_AUTHED(403, "无权限，访问拒绝"),
    PARAM_INVAILD(400, "提交参数非法");

    private Integer code;
    private String msg;

    private ResponseCodeEnume(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
