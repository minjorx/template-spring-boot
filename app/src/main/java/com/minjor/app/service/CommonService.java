package com.minjor.app.service;

import com.minjor.common.model.LabelValue;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class CommonService {
    private final Map<String, Supplier<Map<String, List<LabelValue<?, ?, ?>>>>> labelValueMapSupplier = new HashMap<>();

    /**
     * 注册名称值
     */
    public void registerLabelValue(String key, Supplier<Map<String, List<LabelValue<?, ?, ?>>>> labelValues) {
        labelValueMapSupplier.put(key, labelValues);
    }

    /**
     * 获取名称值
     */
    public Map<String, List<LabelValue<?, ?, ?>>> getLabelValue(String key) {
        if (Strings.isBlank(key)) {
            Map<String, List<LabelValue<?, ?, ?>> > labelValues = new HashMap<>();
            labelValues.put("keys", getKeys());
            return labelValues;
        }
        return Optional.ofNullable(labelValueMapSupplier.get(key))
                .map(Supplier::get)
                .orElse( null);
    }

    public List<LabelValue<?, ?, ?>> getKeys() {
        return labelValueMapSupplier.keySet().stream().map(key -> new LabelValue<>(key, key)).collect(Collectors.toList());
    }
}
