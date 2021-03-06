package org.popkit.appkit.common.entity;


import com.alibaba.fastjson.JSONObject;

/**
 * Author: aborn.jiang
 * Email : aborn.jiang AT foxmail.com
 * Date  : 4/15/15
 * Time  : 12:06 AM
 */
public class ResponseJSON {
    public static final String STATUS_SUCCESS = "success";              // 成功
    public static final String STATUS_FAILD = "failed";                 // 失败
    public static final String STATUS_NO_AUTHORITY = "no_authority";    // 无权限

    /**
     * 状态
     */
    private String status;

    /**
     * 简单的消息
     */
    private String info;

    public ResponseJSON() {
    }

    public ResponseJSON(String status, String info) {
        this.status = status;
        this.info = info;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("info", info);
        return jsonObject.toString();
    }

    public static ResponseJSON getFailedResponse(String info) {
        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.setStatus(STATUS_FAILD);
        responseJSON.setInfo(info);
        return responseJSON;
    }

    public static ResponseJSON getSuccessResponse(String info) {
        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.setStatus(STATUS_SUCCESS);
        responseJSON.setInfo(info);
        return responseJSON;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
