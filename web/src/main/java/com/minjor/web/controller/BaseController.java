package com.minjor.web.controller;

import com.minjor.web.enums.ResultStatus;
import com.minjor.web.model.resp.ResultJson;

public class BaseController {
    public static final String FAIL = "fail";

    public ResultJson<Void> success() {
        return new ResultJson<Void>(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS, null);
    }

    public <T> ResultJson<T> success(T data) {
        return new ResultJson<>(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS, data);
    }

    public ResultJson<Void> fail() {
        return new ResultJson<Void>(ResultStatus.FAIL_CODE, ResultStatus.FAIL, null);
    }
}
