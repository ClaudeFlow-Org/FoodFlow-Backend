package com.foodflow.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;

public final class MeasurementUnitConverter {

    private MeasurementUnitConverter() {
    }

    public static BigDecimal convert(BigDecimal quantity, String fromUnit, String toUnit) {
        if (quantity == null) {
            return BigDecimal.ZERO;
        }

        Unit from = Unit.from(fromUnit);
        Unit to = Unit.from(toUnit);
        if (from.dimension().equals(to.dimension())) {
            return quantity
                    .multiply(from.baseFactor())
                    .divide(to.baseFactor(), 6, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
        }

        throw new IllegalArgumentException("Unit " + fromUnit + " is not compatible with " + toUnit);
    }

    public static boolean areCompatible(String fromUnit, String toUnit) {
        return Unit.from(fromUnit).dimension().equals(Unit.from(toUnit).dimension());
    }

    public static String normalizedLabel(String unit) {
        return Unit.from(unit).label();
    }

    private record Unit(String label, String dimension, BigDecimal baseFactor) {
        private static Unit from(String rawUnit) {
            String normalized = normalize(rawUnit);
            return switch (normalized) {
                case "kg", "kilo", "kilos", "kilogramo", "kilogramos" ->
                        new Unit("kg", "mass", new BigDecimal("1000"));
                case "g", "gr", "grs", "gramo", "gramos" ->
                        new Unit("g", "mass", BigDecimal.ONE);
                case "l", "lt", "lts", "litro", "litros" ->
                        new Unit("l", "volume", new BigDecimal("1000"));
                case "ml", "mililitro", "mililitros" ->
                        new Unit("ml", "volume", BigDecimal.ONE);
                case "u", "und", "unidad", "unidades", "unit", "units" ->
                        new Unit("unidad", "count", BigDecimal.ONE);
                default -> new Unit(normalized, "custom:" + normalized, BigDecimal.ONE);
            };
        }

        private static String normalize(String unit) {
            String value = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
            String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            return withoutAccents.replace(".", "");
        }
    }
}
