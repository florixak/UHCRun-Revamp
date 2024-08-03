package me.florixak.uhcrevamp.utils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class CustomLogFilter implements Filter {

	@Override
	public boolean isLoggable(final LogRecord record) {
		final String message = record.getMessage();
		return !(message.contains("Tried to load unrecognized recipe"));
	}
}
