package com.callfire.avro.formatters;

import com.callfire.avro.config.FormatterConfig;
import com.callfire.avro.types.Date;

/**
 * @author artur@callfire.com
 */
public class DateFormatter implements Formatter<Date> {

    @Override
    public String toJson(Date date, FormatterConfig config) {
        String template = "{ \"type\":\"%s\", \"logicalType\":\"%s\", \"java-class\":\"%s\" }".replaceAll(":", config.colon());
        return String.format(template, date.getPrimitiveType(), date.getLogicalType(), date.getJavaClass());
    }
}
