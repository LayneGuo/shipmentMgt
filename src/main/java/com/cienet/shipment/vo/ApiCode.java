package com.cienet.shipment.vo;

public enum ApiCode {

    SUCCESS(200, "success"),

    UNAUTHORIZED(401, "access denied!"),

    NOT_PERMISSION(403, "access denied!"),

    NOT_FOUND(404, "no page found"),

    FAIL(500, "failure");

    private final int code;
    private final String msg;

    ApiCode(final int code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ApiCode getApiCode(int code) {
        ApiCode[] ecs = ApiCode.values();
        for (ApiCode ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
