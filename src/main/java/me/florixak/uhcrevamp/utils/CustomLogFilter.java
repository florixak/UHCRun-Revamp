package me.florixak.uhcrevamp.utils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class CustomLogFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        String message = record.getMessage();
        return !(message.contains("Tried to load unrecognized recipe"));
    }
}
