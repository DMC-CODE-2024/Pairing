package com.eirs.pairs.dto;

public enum ErrorEnum {
    OK(0), WARN(1), CRITICAL(2), ERROR(3);

    private int value;

    private ErrorEnum(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }
}
