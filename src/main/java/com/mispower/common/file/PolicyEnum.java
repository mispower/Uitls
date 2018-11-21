package com.mispower.common.file;

/**
 * Policy enum
 */
public enum PolicyEnum {

    /**
     * policy:"change"
     */
    CHANGE("change", 1),

    /**
     * policy:"delete"
     */
    DELETE("delete", 2);

    private String enumName;
    private int index;

    PolicyEnum(String enumName, int index) {
        this.enumName = enumName;
        this.index = index;
    }

    public static int getIndexByName(String enumName) {
        return PolicyEnum.valueOf(enumName).index;
    }

    public static String getNameByIndex(int index) {
        for (PolicyEnum c : PolicyEnum.values()) {
            if (c.index == index) {
                return c.enumName;
            }
        }
        return null;
    }
}