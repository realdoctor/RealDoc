package com.real.doctor.realdoc.util;

import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.SortBean;

import java.util.ArrayList;

/**
 */

public class DataUtil {
    public static ArrayList<SortBean> sortBeans=new ArrayList<SortBean>();
    public static ArrayList<HospitalLevelBean> hospitalLevelBeans=new ArrayList<HospitalLevelBean>();
    public static ArrayList<ExpertPostionalBean> expertPostionalBeans=new ArrayList<ExpertPostionalBean>();
    static {
        SortBean bean0=new SortBean();
        bean0.SortId="0";
        bean0.sortName="不限";
        sortBeans.add(bean0);
        SortBean bean=new SortBean();
        bean.SortId="1";
        bean.sortName="综  合";
        sortBeans.add(bean);
        SortBean bean2=new SortBean();
        bean2.SortId="3";
        bean2.sortName="预约量";
        sortBeans.add(bean2);
        HospitalLevelBean hBean0=new HospitalLevelBean();
        hBean0.LevelName="不限";
        HospitalLevelBean hBean=new HospitalLevelBean();
        hBean.LevelName="一级甲等";
        HospitalLevelBean hBean2=new HospitalLevelBean();
        hBean2.LevelName="二级甲等";
        HospitalLevelBean hBean3=new HospitalLevelBean();
        hBean3.LevelName="三级甲等";
        hospitalLevelBeans.add(hBean0);
        hospitalLevelBeans.add(hBean);
        hospitalLevelBeans.add(hBean2);
        hospitalLevelBeans.add(hBean3);
        ExpertPostionalBean ebean0=new ExpertPostionalBean();
        ebean0.postional="不限";
        expertPostionalBeans.add(ebean0);
        ExpertPostionalBean ebean=new ExpertPostionalBean();
        ebean.postional="主任";
        expertPostionalBeans.add(ebean);
        ExpertPostionalBean ebean2=new ExpertPostionalBean();
        ebean2.postional="副主任";
        expertPostionalBeans.add(ebean2);


    }
}
