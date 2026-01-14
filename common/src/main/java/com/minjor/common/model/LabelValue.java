package com.minjor.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LabelValue<L, V, I> {
    private L label;
    private V value;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private I info;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LabelValue<L, V, I> child;

    public LabelValue(L label, V value) {
        this.label = label;
        this.value = value;
    }


}
