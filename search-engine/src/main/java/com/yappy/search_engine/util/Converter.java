package com.yappy.search_engine.util;

public class Converter {
    public static final double[] EMPTY_VECTOR;
    private final static int EMBEDDING_LENGTH = 640;

    public static double[] convertToDoubleArray(String input) {
        if (input == null || input.trim().isEmpty()) {
            return EMPTY_VECTOR;
        }
        input = input.replaceAll("[\\[\\]]", "");
        String[] parts = input.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }
        return result;
    }

    static {
        EMPTY_VECTOR = new double[EMBEDDING_LENGTH]; //sb.toString();
        for (int i = 0; i < EMBEDDING_LENGTH; i++) {
            EMPTY_VECTOR[i] = 1.0;
        }
    }
}
