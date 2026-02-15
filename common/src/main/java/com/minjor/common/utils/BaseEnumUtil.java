package com.minjor.common.utils;

import com.minjor.common.enums.BaseEnum;
import com.minjor.common.model.LabelValue;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@UtilityClass
public class BaseEnumUtil {

    public static <T extends Enum<T> & BaseEnum<?, ?>> List<LabelValue<?, ?, Object>> getLabelValue(Class<T> enumClass) {
        return getLabelValue(enumClass.getEnumConstants());
    }

    public static <T extends Enum<T> & BaseEnum<?, ?>> List<LabelValue<?, ?, Object>> getLabelValue(Class<T> enumClass, final BiConsumer<T, LabelValue<?, ?, Object>> consumer) {
        return getLabelValue(enumClass.getEnumConstants(), consumer);
    }

    public static <T extends Enum<T> & BaseEnum<?, ?>> List<LabelValue<?, ?, Object>> getLabelValue(T[] enums) {
        return getLabelValue(enums, null);
    }

    public static <T extends Enum<T> & BaseEnum<?, ?>> List<LabelValue<?, ?, Object>> getLabelValue(T[] enums, final BiConsumer<T, LabelValue<?, ?, Object>> consumer) {
        return Arrays.stream(enums)
                .map(enumValue -> {
                    LabelValue<?, ?, Object> labelValue = new LabelValue<>(enumValue.getLabel(), enumValue.name());
                    if (Objects.nonNull(consumer)) {
                        consumer.accept(enumValue, labelValue);
                    }
                    return labelValue;
                })
                .collect(Collectors.toList());
    }
}
