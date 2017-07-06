package cn.edu.gzhu.dp.Datas;

import java.io.Serializable;

/**
 * Created by LRC on 2017/3/30.
 */

public class UserInfo implements Serializable {

    private String account;
    private String u_no;
    private String name;
    private String sex;
    private String phone;
    private String email;
    private String password;

    public UserInfo(String account, String name, String sex, String phone, String email, String password) {
        this.account = account;
        this.name = name;
        this.sex = sex;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public UserInfo() {}

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getU_no() {
        return u_no;
    }

    public void setU_no(String u_no) {
        this.u_no = u_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }
}
