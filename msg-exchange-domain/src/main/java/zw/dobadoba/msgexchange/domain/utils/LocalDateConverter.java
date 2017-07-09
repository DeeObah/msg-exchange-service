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
        return date!=null ? Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()) :null;
    }

    public LocalDate convertToEntityAttribute(Date value) {
        if(value==null){
            return null;
        }
        Instant instant = value.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
