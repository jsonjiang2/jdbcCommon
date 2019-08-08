package com.walmart.common.process;


import com.walmart.common.enumeration.AppStatus;

public class ProcessBack {
    private String code;
    private String msg;
    private Object obj;

    public ProcessBack() {
    }

    public ProcessBack(AppStatus appStatus) {
        this(appStatus.getCode(),appStatus.getMsg(),null);
    }

    public ProcessBack(AppStatus appStatus, Object obj) {
        this(appStatus.getCode(),appStatus.getMsg(),obj);
    }

    public ProcessBack(String code,String msg) {
        this(code,msg,null);
    }

    public ProcessBack(String code, String msg, Object obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
