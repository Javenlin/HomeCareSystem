package com.osiog.homecaresystem.Model;

/**
 * Created by OSIOG on 2018/7/16.
 */

public class UserCheck {


    private String account;
    private String phone;
    private String type;
    private String result_msg;
    private boolean User_exists;
    private boolean Account_exists;

    public UserCheck() {

    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult_msg() {
        return result_msg;
    }

    public void setResult_msg(String result_msg) {
        this.result_msg = result_msg;
    }

    public boolean isUser_exists() {
        return User_exists;
    }

    public void setUser_exists(boolean user_exists) {
        User_exists = user_exists;
    }

    public boolean isAccount_exists() {
        return Account_exists;
    }

    public void setAccount_exists(boolean account_exists) {
        Account_exists = account_exists;
    }
}
