package com.wgml.itmall.bean.result;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> 返回结果数据类型
 * @author oislfy
 * @description 全局统一返回响应结果数据封装类
 * @since JDK1.8
 */
@Data
public class R<T> implements Serializable {

    /*
     *  是否成功
     */
    private boolean success;

    /*
     *  返回码
     */
    private Integer code;

    /*
     *  返回消息
     */
    private String message;

    /*
     *  返回数据
     */
    private Map<String, T> data = new HashMap<String, T>();

    /*
     *  无参构造器
     */
    private R() {
    }

    public static <T> R<T> ok(Map<String, T> data) {
        R<T> r = new R<>();
        r.setSuccess(true);
        r.setCode(ResponseCodeEnume.SUCCESS.getCode());
        r.setMessage(ResponseCodeEnume.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    /*
     *  快速响应成功
     */
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setSuccess(true);
        r.setCode(ResponseCodeEnume.SUCCESS.getCode());
        r.setMessage(ResponseCodeEnume.SUCCESS.getMsg());
        return r;
    }

    /*
     *  快速响应失败
     */
    public static <T> R<T> error(Map<String, T> data) {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(ResponseCodeEnume.ERROR.getCode());
        r.setMessage(ResponseCodeEnume.ERROR.getMsg());
        r.setData(data);
        return r;
    }

    /*
     *  快速响应失败
     */
    public static <T> R<T> error() {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(ResponseCodeEnume.ERROR.getCode());
        r.setMessage(ResponseCodeEnume.ERROR.getMsg());
        return r;
    }

    /*
     *  更改返回消息
     */
    public R<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    /*
     *  更改返回编码
     */
    public R<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    /*
     *  添加一对map集合key，value属性
     */
    public R<T> data(String key, T value) {
        this.data.put(key, value);
        return this;
    }

    /*
     *  添加、修改map集合
     */
    public R<T> data(Map<String, T> map) {
        this.setData(map);
        return this;
    }

    public R<T> data(T bean) {
        return this.data("r",bean);
    }
}
