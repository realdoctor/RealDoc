package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class SaveDocManager {
    public final static String dbName = "save_doc";
    public final static String dbPatient = "patient";
    private static SaveDocManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public SaveDocManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static SaveDocManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SaveDocManager.class) {
                if (mInstance == null) {
                    mInstance = new SaveDocManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    public SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    public SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertSaveDoc(Context context, SaveDocBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplace(bean);
    }

    /**
     * 为每一个病人上传病历时插入一条记录
     *
     * @param bean
     */
    public void insertPatientSaveDoc(Context context, SaveDocBean bean, String time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplace(bean);
    }

    /**
     * 插入病历list,并将数据保存在外部文件夹中
     *
     * @param beanList
     */
    public void insertGlobeSaveDoc(Context context, List<SaveDocBean> beanList, String mobile, String folderName) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getGlobeDaoSession(context, mobile, folderName);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 插入病历list
     *
     * @param beanList
     */
    public void insertSaveDoc(Context context, List<SaveDocBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 插入病历list
     *
     * @param beanList
     */
    public void insertGlobedSaveDoc(Context context, List<SaveDocBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        daoSession.deleteAll(SaveDocBean.class);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 查询病历list列表
     */
    public List<SaveDocBean> querySaveDocList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.IsPatient.isNull()).orderDesc(SaveDocBeanDao.Properties.Time).list();
        return list;
    }

    /**
     * 为每一个病人上传病历时查询list列表
     */
    public List<SaveDocBean> queryPatientSaveDocList(Context context, String time, String folderName) {
        DaoSession daoSession = RealDocApplication.getPatientDaoSession(context, time, folderName);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.IsPatient.isNull()).orderDesc(SaveDocBeanDao.Properties.Time).list();
        return list;
    }

    /**
     * 查询databases中list列表
     */
    public List<SaveDocBean> queryGlobeSaveDocList(Context context, String mobile, String folderName) {
        DaoSession daoSession = RealDocApplication.getGlobeDaoSession(context, mobile, folderName);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.IsPatient.isNull()).orderDesc(SaveDocBeanDao.Properties.Time).list();
        return list;
    }

    /**
     * 查询病历list列表其中一条病历
     */
    public List<SaveDocBean> querySaveDocList(Context context, String id) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.Id.notEq(id)).orderDesc(SaveDocBeanDao.Properties.Time).list();
        return list;
    }

    /**
     * 查询病历list列表其中一条病历
     */
    public List<SaveDocBean> querySaveDocListByFolder(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.IsPatient.isNull(), SaveDocBeanDao.Properties.Folder.isNotNull()).orderDesc(SaveDocBeanDao.Properties.Time).list();
        return list;
    }

    /**
     * 通过id查询一份病历
     */
    public List<SaveDocBean> queryRecordId(Context context, String id) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.Id.eq(id)).list();
        return list;
    }

    /**
     * 更新一条记录
     *
     * @param
     */
    public void updateRecord(SaveDocBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.update(bean);
    }
    /**
     * 更新list数据
     * @param
     */
    public void updateRecordList(List<SaveDocBean> list) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.updateInTx(list);
    }

    /**
     * 删除一条记录
     *
     * @param bean
     */
    public void deleteSaveDocList(SaveDocBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        saveDocDao.delete(bean);
    }

    public List<SaveDocBean> queryRecordByTimeList(Context context, String start, String end) {
        return queryRecordByTimeList(context, start, end, true);
    }

    /**
     * 查询规定时间内的病历list列表
     */
    public List<SaveDocBean> queryRecordByTimeList(Context context, String start, String end, boolean isPatient) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        String today = DateUtil.timeToday();
        List<SaveDocBean> list = null;
        if (end.equals(today)) {
            end = String.valueOf(Long.valueOf(end) + (long) 86400000);
            if (isPatient) {
                list = qb.where(SaveDocBeanDao.Properties.Time.ge(Long.valueOf(start)), SaveDocBeanDao.Properties.IsPatient.isNull(), SaveDocBeanDao.Properties.Time.le(Long.valueOf(end))).orderDesc(SaveDocBeanDao.Properties.Time).list();
            } else {
                list = qb.where(SaveDocBeanDao.Properties.Time.ge(Long.valueOf(start)), SaveDocBeanDao.Properties.IsPatient.eq("1"), SaveDocBeanDao.Properties.Time.le(Long.valueOf(end))).orderDesc(SaveDocBeanDao.Properties.Time).list();
            }
        } else if (start.equals(today) && end.equals(today) && start.equals(end)) {
            if (isPatient) {
                list = qb.where(SaveDocBeanDao.Properties.Time.eq(Long.valueOf(today)), SaveDocBeanDao.Properties.IsPatient.isNull()).list();
            } else {
                list = qb.where(SaveDocBeanDao.Properties.Time.eq(Long.valueOf(today)), SaveDocBeanDao.Properties.IsPatient.eq("1")).list();
            }
        } else {
            if (isPatient) {
                list = qb.where(SaveDocBeanDao.Properties.Time.ge(Long.valueOf(start)), SaveDocBeanDao.Properties.IsPatient.isNull(), SaveDocBeanDao.Properties.Time.le(Long.valueOf(end))).orderDesc(SaveDocBeanDao.Properties.Time).list();
            } else {
                list = qb.where(SaveDocBeanDao.Properties.Time.ge(Long.valueOf(start)), SaveDocBeanDao.Properties.IsPatient.eq("1"), SaveDocBeanDao.Properties.Time.le(Long.valueOf(end))).orderDesc(SaveDocBeanDao.Properties.Time).list();
            }
        }

        return list;
    }

    private static final String SQL_DISTINCT_ILL = "SELECT DISTINCT " + SaveDocBeanDao.Properties.Ill.columnName + " FROM " + SaveDocBeanDao.TABLENAME + " WHERE " + SaveDocBeanDao.Properties.IsPatient.columnName + " IS NULL " + " ORDER BY " + SaveDocBeanDao.Properties.Time.columnName + " DESC";

    /**
     * 查询病历一列列表
     */
    public static List<String> queryDiseaseList(DaoSession session) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(SQL_DISTINCT_ILL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    private static final String SQL_HOSPITAL_ILL = "SELECT DISTINCT " + SaveDocBeanDao.Properties.Hospital.columnName + " FROM " + SaveDocBeanDao.TABLENAME + " WHERE " + SaveDocBeanDao.Properties.IsPatient.columnName + " IS NULL " + " ORDER BY " + SaveDocBeanDao.Properties.Time.columnName + " DESC";

    /**
     * 查询病历一列列表
     */
    public static List<String> queryHospitalList(DaoSession session) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(SQL_HOSPITAL_ILL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    /**
     * 模糊查询该疾病的的病历list列表
     */
    public List<SaveDocBean> queryRecordByDiseaseList(Context context, String disease) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.Ill.like("%" + disease + "%"), SaveDocBeanDao.Properties.IsPatient.isNull()).list();
        return list;
    }


    /**
     * 查询该疾病的病历list列表
     */
    public List<SaveDocBean> queryRecordByEqDiseaseList(Context context, String disease) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.Ill.eq(disease), SaveDocBeanDao.Properties.IsPatient.isNull()).list();
        return list;
    }

    /**
     * 查询非该疾病的病历list列表
     */
    public List<SaveDocBean> queryRecordByNotDiseaseList(Context context, String disease) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        List<SaveDocBean> list = qb.where(SaveDocBeanDao.Properties.Ill.notEq(disease), SaveDocBeanDao.Properties.IsPatient.isNull()).list();
        return list;
    }

    /**
     * 查询病历list列表总共条数
     */
    public long getTotalCount() {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
        QueryBuilder<SaveDocBean> qb = saveDocDao.queryBuilder();
        return qb.where(SaveDocBeanDao.Properties.IsPatient.isNull()).buildCount().count();
    }

    private static final String SQL_TIME_ILL = "SELECT DISTINCT " + SaveDocBeanDao.Properties.Time.columnName + " FROM " + SaveDocBeanDao.TABLENAME + " WHERE " + SaveDocBeanDao.Properties.IsPatient.columnName + " IS NULL " + " ORDER BY " + SaveDocBeanDao.Properties.Time.columnName + " DESC";

    /**
     * 查询病历时间一列列表
     */
    public static List<String> queryTimeList(DaoSession session) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(SQL_TIME_ILL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    private static final String SQL_GLOBE_ILL = "SELECT DISTINCT " + SaveDocBeanDao.Properties.Id.columnName + " FROM " + SaveDocBeanDao.TABLENAME + " WHERE " + SaveDocBeanDao.Properties.Folder.columnName + " IS NOT NULL " + " AND " + SaveDocBeanDao.Properties.IsPatient.columnName + " IS NULL ";

    /**
     * 查询病历时间一列列表
     */
    public static List<String> queryGlobeList(DaoSession session) {
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = session.getDatabase().rawQuery(SQL_GLOBE_ILL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll(Context context) {
        boolean flag = false;
        try {
            DaoSession daoSession = RealDocApplication.getDaoSession(context);
            SaveDocBeanDao saveDocDao = daoSession.getSaveDocBeanDao();
            saveDocDao.deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void deleSQL() {
        SQLiteDatabase db = getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoMaster.dropAllTables(daoMaster.getDatabase(), true);
        DaoMaster.createAllTables(daoMaster.getDatabase(), true);
    }
}

