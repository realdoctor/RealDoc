package com.real.doctor.realdoc.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.real.doctor.realdoc.model.DownInfo;
import com.real.doctor.realdoc.model.DrugBean;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.InqueryBean;
import com.real.doctor.realdoc.model.MessageBean;
import com.real.doctor.realdoc.model.NewFriendsMsgs;
import com.real.doctor.realdoc.model.PrefBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.RobotBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.UserBean;
import com.real.doctor.realdoc.model.VideoBean;

import com.real.doctor.realdoc.greendao.DownInfoDao;
import com.real.doctor.realdoc.greendao.DrugBeanDao;
import com.real.doctor.realdoc.greendao.ImageBeanDao;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;
import com.real.doctor.realdoc.greendao.InqueryBeanDao;
import com.real.doctor.realdoc.greendao.MessageBeanDao;
import com.real.doctor.realdoc.greendao.NewFriendsMsgsDao;
import com.real.doctor.realdoc.greendao.PrefBeanDao;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.greendao.RobotBeanDao;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.greendao.UserBeanDao;
import com.real.doctor.realdoc.greendao.VideoBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig downInfoDaoConfig;
    private final DaoConfig drugBeanDaoConfig;
    private final DaoConfig imageBeanDaoConfig;
    private final DaoConfig imageListBeanDaoConfig;
    private final DaoConfig inqueryBeanDaoConfig;
    private final DaoConfig messageBeanDaoConfig;
    private final DaoConfig newFriendsMsgsDaoConfig;
    private final DaoConfig prefBeanDaoConfig;
    private final DaoConfig recordBeanDaoConfig;
    private final DaoConfig robotBeanDaoConfig;
    private final DaoConfig saveDocBeanDaoConfig;
    private final DaoConfig userBeanDaoConfig;
    private final DaoConfig videoBeanDaoConfig;

    private final DownInfoDao downInfoDao;
    private final DrugBeanDao drugBeanDao;
    private final ImageBeanDao imageBeanDao;
    private final ImageListBeanDao imageListBeanDao;
    private final InqueryBeanDao inqueryBeanDao;
    private final MessageBeanDao messageBeanDao;
    private final NewFriendsMsgsDao newFriendsMsgsDao;
    private final PrefBeanDao prefBeanDao;
    private final RecordBeanDao recordBeanDao;
    private final RobotBeanDao robotBeanDao;
    private final SaveDocBeanDao saveDocBeanDao;
    private final UserBeanDao userBeanDao;
    private final VideoBeanDao videoBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        downInfoDaoConfig = daoConfigMap.get(DownInfoDao.class).clone();
        downInfoDaoConfig.initIdentityScope(type);

        drugBeanDaoConfig = daoConfigMap.get(DrugBeanDao.class).clone();
        drugBeanDaoConfig.initIdentityScope(type);

        imageBeanDaoConfig = daoConfigMap.get(ImageBeanDao.class).clone();
        imageBeanDaoConfig.initIdentityScope(type);

        imageListBeanDaoConfig = daoConfigMap.get(ImageListBeanDao.class).clone();
        imageListBeanDaoConfig.initIdentityScope(type);

        inqueryBeanDaoConfig = daoConfigMap.get(InqueryBeanDao.class).clone();
        inqueryBeanDaoConfig.initIdentityScope(type);

        messageBeanDaoConfig = daoConfigMap.get(MessageBeanDao.class).clone();
        messageBeanDaoConfig.initIdentityScope(type);

        newFriendsMsgsDaoConfig = daoConfigMap.get(NewFriendsMsgsDao.class).clone();
        newFriendsMsgsDaoConfig.initIdentityScope(type);

        prefBeanDaoConfig = daoConfigMap.get(PrefBeanDao.class).clone();
        prefBeanDaoConfig.initIdentityScope(type);

        recordBeanDaoConfig = daoConfigMap.get(RecordBeanDao.class).clone();
        recordBeanDaoConfig.initIdentityScope(type);

        robotBeanDaoConfig = daoConfigMap.get(RobotBeanDao.class).clone();
        robotBeanDaoConfig.initIdentityScope(type);

        saveDocBeanDaoConfig = daoConfigMap.get(SaveDocBeanDao.class).clone();
        saveDocBeanDaoConfig.initIdentityScope(type);

        userBeanDaoConfig = daoConfigMap.get(UserBeanDao.class).clone();
        userBeanDaoConfig.initIdentityScope(type);

        videoBeanDaoConfig = daoConfigMap.get(VideoBeanDao.class).clone();
        videoBeanDaoConfig.initIdentityScope(type);

        downInfoDao = new DownInfoDao(downInfoDaoConfig, this);
        drugBeanDao = new DrugBeanDao(drugBeanDaoConfig, this);
        imageBeanDao = new ImageBeanDao(imageBeanDaoConfig, this);
        imageListBeanDao = new ImageListBeanDao(imageListBeanDaoConfig, this);
        inqueryBeanDao = new InqueryBeanDao(inqueryBeanDaoConfig, this);
        messageBeanDao = new MessageBeanDao(messageBeanDaoConfig, this);
        newFriendsMsgsDao = new NewFriendsMsgsDao(newFriendsMsgsDaoConfig, this);
        prefBeanDao = new PrefBeanDao(prefBeanDaoConfig, this);
        recordBeanDao = new RecordBeanDao(recordBeanDaoConfig, this);
        robotBeanDao = new RobotBeanDao(robotBeanDaoConfig, this);
        saveDocBeanDao = new SaveDocBeanDao(saveDocBeanDaoConfig, this);
        userBeanDao = new UserBeanDao(userBeanDaoConfig, this);
        videoBeanDao = new VideoBeanDao(videoBeanDaoConfig, this);

        registerDao(DownInfo.class, downInfoDao);
        registerDao(DrugBean.class, drugBeanDao);
        registerDao(ImageBean.class, imageBeanDao);
        registerDao(ImageListBean.class, imageListBeanDao);
        registerDao(InqueryBean.class, inqueryBeanDao);
        registerDao(MessageBean.class, messageBeanDao);
        registerDao(NewFriendsMsgs.class, newFriendsMsgsDao);
        registerDao(PrefBean.class, prefBeanDao);
        registerDao(RecordBean.class, recordBeanDao);
        registerDao(RobotBean.class, robotBeanDao);
        registerDao(SaveDocBean.class, saveDocBeanDao);
        registerDao(UserBean.class, userBeanDao);
        registerDao(VideoBean.class, videoBeanDao);
    }
    
    public void clear() {
        downInfoDaoConfig.clearIdentityScope();
        drugBeanDaoConfig.clearIdentityScope();
        imageBeanDaoConfig.clearIdentityScope();
        imageListBeanDaoConfig.clearIdentityScope();
        inqueryBeanDaoConfig.clearIdentityScope();
        messageBeanDaoConfig.clearIdentityScope();
        newFriendsMsgsDaoConfig.clearIdentityScope();
        prefBeanDaoConfig.clearIdentityScope();
        recordBeanDaoConfig.clearIdentityScope();
        robotBeanDaoConfig.clearIdentityScope();
        saveDocBeanDaoConfig.clearIdentityScope();
        userBeanDaoConfig.clearIdentityScope();
        videoBeanDaoConfig.clearIdentityScope();
    }

    public DownInfoDao getDownInfoDao() {
        return downInfoDao;
    }

    public DrugBeanDao getDrugBeanDao() {
        return drugBeanDao;
    }

    public ImageBeanDao getImageBeanDao() {
        return imageBeanDao;
    }

    public ImageListBeanDao getImageListBeanDao() {
        return imageListBeanDao;
    }

    public InqueryBeanDao getInqueryBeanDao() {
        return inqueryBeanDao;
    }

    public MessageBeanDao getMessageBeanDao() {
        return messageBeanDao;
    }

    public NewFriendsMsgsDao getNewFriendsMsgsDao() {
        return newFriendsMsgsDao;
    }

    public PrefBeanDao getPrefBeanDao() {
        return prefBeanDao;
    }

    public RecordBeanDao getRecordBeanDao() {
        return recordBeanDao;
    }

    public RobotBeanDao getRobotBeanDao() {
        return robotBeanDao;
    }

    public SaveDocBeanDao getSaveDocBeanDao() {
        return saveDocBeanDao;
    }

    public UserBeanDao getUserBeanDao() {
        return userBeanDao;
    }

    public VideoBeanDao getVideoBeanDao() {
        return videoBeanDao;
    }

}
