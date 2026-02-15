package com.minjor.data.config;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class P6spyFormatter implements MessageFormattingStrategy {
    private static final List<String> IGNORE_CATEGORY;

    static {
        IGNORE_CATEGORY = List.of(
                "commit",
                "rollback"
        );
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (Objects.equals("SELECT VERSION()", sql)) {
            return null;
        }
        if (IGNORE_CATEGORY.contains(category)) {
            return null;
        }
        String formattedLogLine = String.format("executeSql Spends: %d ms | SQL: %s",
                elapsed, P6Util.singleLine(sql));
        if (Category.ERROR.getName().equals(category)) {
            log.error(formattedLogLine);
        } else if (Category.WARN.getName().equals(category)) {
            log.warn(formattedLogLine);
        } else {
            log.info(formattedLogLine);
        }

        return null;
    }
}
