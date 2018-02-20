package com.garry.runningmap.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GaolengYan on 2018/1/19.
 */

public class IsVaildUtil {
    public static Boolean isMailVaild(String mail) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
        Matcher m = pattern.matcher(mail);
        return m.matches();
    }

    public static Boolean isPasswordVaild(String password){
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]{4,20}");
        Matcher m = pattern.matcher(password);
        return m.matches();
    }
    public static Boolean isGameNameVaild(String gameName){
        return !gameName.equals("");
    }
}
