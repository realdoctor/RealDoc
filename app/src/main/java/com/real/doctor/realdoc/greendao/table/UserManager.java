package com.real.doctor.realdoc.greendao.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.NewFriendsMsgsDao;
import com.real.doctor.realdoc.greendao.UserBeanDao;
import com.real.doctor.realdoc.model.UserBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.HuanXinPreferenceManager;
import com.real.doctor.realdoc.widget.Constant;

import org.greenrobot.greendao.query.QueryBuilder;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class UserManager {

    public final static String dbName = "save_doc";
    private Context context;
    private static UserManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    protected Map<Key, Object> valueCache = new HashMap<Key, Object>();
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public UserManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static UserManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (UserManager.class) {
                if (mInstance == null) {
                    mInstance = new UserManager(context);
                    HuanXinPreferenceManager.init(context);
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
     * 查询病历list列表
     */
    public List<UserBean> queryUserBeanList(Context context) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        UserBeanDao userBeanDao = daoSession.getUserBeanDao();
        QueryBuilder<UserBean> qb = userBeanDao.queryBuilder();
        List<UserBean> list = qb.list();
        return list;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertUser(Context context, UserBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        UserBeanDao userBeanDao = daoSession.getUserBeanDao();
        userBeanDao.insertOrReplace(bean);
    }

    /**
     * 更改记录
     *
     * @param bean
     */
    public void saveUser(Context context, UserBean bean) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        UserBeanDao userBeanDao = daoSession.getUserBeanDao();
        List<UserBean> list = queryUserBeanList(context);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setAvater(bean.getAvater());
            list.get(i).setName(bean.getName());
            list.get(i).setRealname(bean.getRealname());
            insertUser(context, list.get(i));
        }
    }

    /**
     * delete a contact
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        UserBeanDao userBeanDao = daoSession.getUserBeanDao();
        userBeanDao.queryBuilder().where(UserBeanDao.Properties.Realname.eq(username)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 插入用户list
     *
     * @param beanList
     */
    public void insertUserList(Context context, List<UserBean> beanList) {
        if (EmptyUtils.isEmpty(beanList)) {
            return;
        }
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        UserBeanDao userBeanDao = daoSession.getUserBeanDao();
        userBeanDao.insertOrReplaceInTx(beanList);
    }

    /**
     * 删除一条记录
     */
    public void deleteUserByName(Context context, String userName) {
        DaoSession daoSession = RealDocApplication.getDaoSession(context);
        UserBeanDao userBeanDao = daoSession.getUserBeanDao();
        userBeanDao.deleteByKey(userName);
    }


    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<String, EaseUser> getContactList(DaoSession session) {
        Map<String, EaseUser> users = new Hashtable<String, EaseUser>();
        String SQL_LIST = "SELECT DISTINCT * FROM " + UserBeanDao.TABLENAME;
        Cursor cursor = session.getDatabase().rawQuery(SQL_LIST, null);
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex(UserBeanDao.Properties.Realname.columnName));
            String nick = cursor.getString(cursor.getColumnIndex(UserBeanDao.Properties.Name.columnName));
            String avatar = cursor.getString(cursor.getColumnIndex(UserBeanDao.Properties.Avater.columnName));
            EaseUser user = new EaseUser(username);
            user.setNick(nick);
            user.setAvatar(avatar);
            if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                    || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT)) {
                user.setInitialLetter("");
            } else {
                EaseCommonUtils.setUserInitialLetter(user);
            }
            users.put(username, user);
        }
        cursor.close();

        return users;
    }

    /**
     * save current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        HuanXinPreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName() {
        return HuanXinPreferenceManager.getInstance().getCurrentUsername();
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        HuanXinPreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if (val == null) {
            val = HuanXinPreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        HuanXinPreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public String getCutomAppkey() {
        return HuanXinPreferenceManager.getInstance().getCustomAppkey();
    }

    public boolean isCustomServerEnable() {
        return HuanXinPreferenceManager.getInstance().isCustomServerEnable();
    }

    public String getRestServer() {
        return HuanXinPreferenceManager.getInstance().getRestServer();
    }

    public void setIMServer(String imServer) {
        HuanXinPreferenceManager.getInstance().setIMServer(imServer);
    }

    public String getIMServer() {
        return HuanXinPreferenceManager.getInstance().getIMServer();
    }

    public boolean isCustomAppkeyEnabled() {
        return HuanXinPreferenceManager.getInstance().isCustomAppkeyEnabled();
    }

    public boolean isSetTransferFileByUser() {
        return HuanXinPreferenceManager.getInstance().isSetTransferFileByUser();
    }

    public boolean isSetAutodownloadThumbnail() {
        return HuanXinPreferenceManager.getInstance().isSetAutodownloadThumbnail();
    }

    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if (val == null) {
            val = HuanXinPreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if (val == null) {
            val = HuanXinPreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if (val == null) {
            val = HuanXinPreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public boolean isContactSynced() {
        return HuanXinPreferenceManager.getInstance().isContactSynced();
    }

    public boolean isGroupsSynced() {
        return HuanXinPreferenceManager.getInstance().isGroupsSynced();
    }

    public boolean isBacklistSynced() {
        return HuanXinPreferenceManager.getInstance().isBacklistSynced();
    }

    public boolean isPushCall() {
        return HuanXinPreferenceManager.getInstance().isPushCall();
    }

    public boolean isChatroomOwnerLeaveAllowed() {
        return HuanXinPreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return HuanXinPreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    public boolean isAutoAcceptGroupInvitation() {
        return HuanXinPreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }

    public void setGroupsSynced(boolean synced) {
        HuanXinPreferenceManager.getInstance().setGroupsSynced(synced);
    }

    public void setBlacklistSynced(boolean synced) {
        HuanXinPreferenceManager.getInstance().setBlacklistSynced(synced);
    }

    enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
    }
}
