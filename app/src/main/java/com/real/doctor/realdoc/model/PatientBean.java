package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PatientBean implements Parcelable {
    private String addTime;
    private String src;
    private String num;
    private String pubId;
    private String remark;
    private UserBean userInfo;

    public PatientBean() {
    }

    protected PatientBean(Parcel in) {
        addTime = in.readString();
        src = in.readString();
        num = in.readString();
        pubId = in.readString();
        remark = in.readString();
        userInfo = in.readParcelable(UserBean.class.getClassLoader());
    }

    public static final Creator<PatientBean> CREATOR = new Creator<PatientBean>() {
        @Override
        public PatientBean createFromParcel(Parcel in) {
            return new PatientBean(in);
        }

        @Override
        public PatientBean[] newArray(int size) {
            return new PatientBean[size];
        }
    };

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public UserBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserBean userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addTime);
        dest.writeString(src);
        dest.writeString(num);
        dest.writeString(pubId);
        dest.writeString(remark);
        dest.writeParcelable(userInfo, flags);
    }
}
