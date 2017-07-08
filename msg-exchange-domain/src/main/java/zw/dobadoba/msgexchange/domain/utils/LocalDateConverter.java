package zw.dobadoba.msgexchange.domain.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by dobadoba on 7/8/17.
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate,Date>{

    public Date convertToDatabaseColumn(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public LocalDate convertToEntityAttribute(Date value) {
        Instant instant = value.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
