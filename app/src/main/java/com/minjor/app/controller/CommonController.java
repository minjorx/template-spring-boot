package com.minjor.app.controller;

import com.minjor.app.service.CommonService;
import com.minjor.common.model.LabelValue;
import com.minjor.web.controller.BaseController;
import com.minjor.web.model.resp.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/common")
public class CommonController extends BaseController {

    @Autowired
    private CommonService commonService;

    @GetMapping("/labelValue/{key}")
    public ResultJson<Map<String, List<LabelValue<?, ?, ?>>>> getLabelValue(@PathVariable(value = "key", required = false) String key) {
        return success(commonService.getLabelValue(key));
    }
}
