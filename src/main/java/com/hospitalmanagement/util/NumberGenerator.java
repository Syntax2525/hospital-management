package com.hospitalmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class NumberGenerator {
    private static final DateTimeFormatter DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private NumberGenerator() {
    }

    public static String patientNumber() {
        return "PT-" + suffix();
    }

    public static String encounterNumber() {
        return "ENC-" + DATE.format(LocalDate.now()) + "-" + suffix();
    }

    public static String labOrderNumber() {
        return "LAB-" + DATE.format(LocalDate.now()) + "-" + suffix();
    }

    public static String invoiceNumber() {
        return "INV-" + DATE.format(LocalDate.now()) + "-" + suffix();
    }

    public static String prescriptionNumber() {
        return "RX-" + DATE.format(LocalDate.now()) + "-" + suffix();
    }

    public static String dispenseNumber() {
        return "DSP-" + DATE.format(LocalDate.now()) + "-" + suffix();
    }

    public static String paymentReference() {
        return "PAY-" + DATE.format(LocalDate.now()) + "-" + suffix();
    }

    private static String suffix() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
