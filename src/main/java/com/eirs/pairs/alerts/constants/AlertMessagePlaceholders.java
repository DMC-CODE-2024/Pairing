package com.eirs.pairs.alerts.constants;

public enum AlertMessagePlaceholders {
    QUERY("<QUERY>"), EXCEPTION("<EXCEPTION>"),

    CONFIG_KEY("<CONFIG_KEY>"), CONFIG_VALUE("<CONFIG_VALUE>"), LANGUAGE("<LANGUAGE>"), SMS("<SMS>"),

    FEATURE_NAME("<FEATURE_NAME>");

    String placeholder;

    AlertMessagePlaceholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
}
