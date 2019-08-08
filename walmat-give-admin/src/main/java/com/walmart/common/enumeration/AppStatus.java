package com.walmart.common.enumeration;

public enum AppStatus {
    Success("8888","成功"),
    Fail("0000","失败"),
    NoLogin("1301","用户未登录或失效"),
    NoPermission("1302","您无访问该资源权限"),
    ParamsError("1303","参数错误"),
    ServerError("1304","接口异常"),
    //其他业务返回码扩展
    ;
    private String code;
    private String msg;

    AppStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
