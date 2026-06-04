package com.hospitalmanagement.validation;

import com.hospitalmanagement.dto.PrescriptionItemDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPrescriptionItemValidator implements ConstraintValidator<ValidPrescriptionItem, PrescriptionItemDto> {
    @Override
    public boolean isValid(PrescriptionItemDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean hasMedicationId = value.medicationId() != null;
        boolean hasMedicationName = value.medicationName() != null && !value.medicationName().isBlank();
        return hasMedicationId || hasMedicationName;
    }
}
