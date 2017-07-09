package zw.dobadoba.msgexchange.domain.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by dobadoba on 7/8/17.
 */
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime,Date> {

    public Date convertToDatabaseColumn(LocalDateTime date) {
        if(date==null){
            return null;
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, ZoneId.systemDefault());
        Instant instant = Instant.from(zonedDateTime);
        return Date.from(instant);
    }

    public LocalDateTime convertToEntityAttribute(Date value) {
        if(value==null){
            return null;
        }
        Instant instant = value.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
