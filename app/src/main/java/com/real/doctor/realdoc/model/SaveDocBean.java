package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/4/24.
 */
@Entity
public class SaveDocBean implements Parcelable {

    @Id
    private String id;
    private String ill;
    private String hospital;
    private String doctor;
    private String imgs;

    public SaveDocBean() {
    }


    protected SaveDocBean(Parcel in) {
        id = in.readString();
        ill = in.readString();
        hospital = in.readString();
        doctor = in.readString();
        imgs = in.readString();
    }


    @Generated(hash = 2002800419)
    public SaveDocBean(String id, String ill, String hospital, String doctor,
            String imgs) {
        this.id = id;
        this.ill = ill;
        this.hospital = hospital;
        this.doctor = doctor;
        this.imgs = imgs;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ill);
        dest.writeString(hospital);
        dest.writeString(doctor);
        dest.writeString(imgs);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getId() {
        return this.id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getIll() {
        return this.ill;
    }


    public void setIll(String ill) {
        this.ill = ill;
    }


    public String getHospital() {
        return this.hospital;
    }


    public void setHospital(String hospital) {
        this.hospital = hospital;
    }


    public String getDoctor() {
        return this.doctor;
    }


    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }


    public String getImgs() {
        return this.imgs;
    }


    public void setImgs(String imgs) {
        this.imgs = imgs;
    }



    public static final Creator<SaveDocBean> CREATOR = new Creator<SaveDocBean>() {
        @Override
        public SaveDocBean createFromParcel(Parcel in) {
            return new SaveDocBean(in);
        }

        @Override
        public SaveDocBean[] newArray(int size) {
            return new SaveDocBean[size];
        }
    };
}
