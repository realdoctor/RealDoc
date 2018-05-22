package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/19.
 */

public class UserBean implements Parcelable {
    @SerializedName("userId")
    private String id;
    private String avater;
    @SerializedName("userName")
    private String name;
    private String pwd;
    @SerializedName("mobilePhone")
    private String mobile;
    @SerializedName("realName")
    private String realname;
    @SerializedName("lastUpdateDtime")
    private String lastTime;
    @SerializedName("sentAddress")
    private String address;
    private String email;

    protected UserBean(Parcel in) {
        id = in.readString();
        avater = in.readString();
        name = in.readString();
        pwd = in.readString();
        mobile = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
