package com.minjor.web.model.resp;

public record ResultJson<T>(Integer code, String message, T data) {}