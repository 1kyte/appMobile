package com.example.asstmobileapp;

/**
 * Created by Kallen on 6/10/2017.
 */

public class LoginInfo {
    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("account")
    private String mAccount;

    @com.google.gson.annotations.SerializedName("password")
    private String mPassword;

    public String getAccount() {
        return mAccount;
    }

    /**
     * Sets the item text
     *
     * @param account
     *
     */
    public final void setAccount(String account) {
        mAccount = account;
    }

    public String getPassword() {
        return mPassword;
    }

    /**
     * Sets the item text
     *
     * @param password
     *
     */
    public final void setPassword(String password) {
        mPassword = password;
    }

}
