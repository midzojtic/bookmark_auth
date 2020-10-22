package com.example.bookmarkauth.model;

import java.util.Arrays;

/**
 * This is helper enum for user type
 */
public enum UserTypeEnum {

    PRIVATE("private"), PUBLIC("public");

    private String value;


    UserTypeEnum(String value) {
        this.value = value;
    }

    /**
     * This method returns value for user type
     *
     * @return value
     */
    public String getValue(){
        return value;
    }

    /**
     * Method fetches enum for given enum value from paramterization
     * @param value
     * @return user type enum
     */
    public static UserTypeEnum fromString(String value){

        return Arrays.stream(UserTypeEnum.values()).filter(bl -> bl.value.equalsIgnoreCase(value)).findFirst().orElse(null);

    }
}
