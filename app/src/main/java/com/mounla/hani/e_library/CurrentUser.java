package com.mounla.hani.e_library;

/**
 * Created by Hani on 2016-12-07.
 */
public class CurrentUser
{
    public static String Name , Password , SecurityLevel;
    CurrentUser(String Name , String Password , String SecurityLevel)
    {
        this.Name = Name;
        this.Password = Password;
        this.SecurityLevel = SecurityLevel;
    }
}
