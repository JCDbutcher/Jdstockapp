package utils;

import entity.ShippingStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShippingStatusConverter implements AttributeConverter<ShippingStatus, String> {
    @Override
    public String convertToDatabaseColumn(ShippingStatus status) {
        if (status == null) {
            return null;
        }
        return status.getLabel();
    }

    @Override
    public ShippingStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return ShippingStatus.fromString(dbData);
    }
}


