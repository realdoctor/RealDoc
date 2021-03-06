package com.real.doctor.realdoc.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.real.doctor.realdoc.activity.ProgressBarActivity;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.util.ZipUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/26.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UpdateService extends JobService {

    public static final String TAG = UpdateService.class.getSimpleName();
    private List<SaveDocBean> mList = new ArrayList<>();
    private List<String> mImgList = new ArrayList<>();
    private List<Boolean> mFlag = new ArrayList<>();
    private String inquery;
    private String doctorUserId;
    private String desease;
    private String questionId;
    private String patientRecordId;
    private String orderNo;
    private boolean zip = false;
    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    //数据库处理
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    private List<RecordBean> audioList;
    private List<VideoBean> videoList;
    private String time;
    private File file;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //插入要咨询的内容
            mFlag.clear();
            time = DateUtil.timeStamp();
            //新建doctor文件夹
            String folderName = SDCardUtils.getGlobalDir() + "doctor" + time + File.separator;
            //图片存储路径
            String imgPath = folderName + "img" + File.separator;
            if (mList.size() > 0) {
                file = new File(folderName);
                if (!file.exists())
                    file.mkdirs();
            }
            for (int k = 0; k < mList.size(); k++) {
                SaveDocBean bean = mList.get(k);
                //如果不存在则创建
                String mId = bean.getId();
                String mFolder = bean.getFolder();
                //数据库处理
                //将该条数据插入到patient数据库中
                instance.insertPatientSaveDoc(UpdateService.this, bean, time, folderName);
                if (EmptyUtils.isNotEmpty(mFolder)) {
                    //从数据库中获得音频列表,装入patient数据库中
                    audioList = recordInstance.queryRecordWithFolder(UpdateService.this, mFolder);
                    //将该音频数据插入到patient数据库中
                    int audioLength = audioList.size();
                    for (int i = 0; i < audioLength; i++) {
                        //更换文件路径
                        audioList.get(i).setFilePath(SDCardUtils.getGlobalDir() + "doctor" + time + File.separator + "music" + File.separator + audioList.get(i).getFileName());
//                    audioList.get(i).setFilePath(SDCardUtils.getGlobalDir() + "patient" + File.separator + "doctor" + time + File.separator + "music" + File.separator + audioList.get(i).getFileName());
                    }
                    recordInstance.insertPatientRecordList(UpdateService.this, audioList, time, folderName);
                    //从数据库获得视频列表,装入patient数据库中
                    videoList = videoInstance.queryVideoWithFolder(UpdateService.this, mFolder);
                    //将该视频数据插入到patient数据库中
                    int videoLength = videoList.size();
                    for (int i = 0; i < videoLength; i++) {
                        //更换文件路径
                        videoList.get(i).setFilePath(SDCardUtils.getGlobalDir() + "doctor" + time + File.separator + "movie" + File.separator + videoList.get(i).getFileName());
//                    videoList.get(i).setFilePath(SDCardUtils.getGlobalDir() + "patient" + File.separator + "doctor" + time + File.separator + "movie" + File.separator + videoList.get(i).getFileName());
                    }
                    videoInstance.insertPatientVideoList(UpdateService.this, videoList, time, folderName);
                }
                List<ImageListBean> imageRecycleList = imageRecycleInstance.queryImageListById(UpdateService.this, mId);
                //该张表不需要处理,直接插入即可
                imageRecycleInstance.insertPatientImageListList(UpdateService.this, imageRecycleList, time, folderName);
                //从数据库获得图片列表,装入patient数据库中
                int imageListLength = imageRecycleList.size();
                for (int i = 0; i < imageListLength; i++) {
                    String imageId = imageRecycleList.get(i).getId();
                    List<ImageBean> imageList = imageInstance.queryImageByImageId(UpdateService.this, imageId);
                    for (int j = 0; j < imageList.size(); j++) {
                        String imgUrl = imageList.get(j).getImgUrl();
                        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.length());
                        //替换文件路径
                        imageList.get(j).setImgUrl(SDCardUtils.getGlobalDir() + "doctor" + time + File.separator + "img" + File.separator + fileName);
//                        imageList.get(j).setImgUrl(SDCardUtils.getGlobalDir() + "patient" + File.separator + "doctor" + time + File.separator + "img" + File.separator + fileName);
                    }
                    imageInstance.insertPatientImageList(UpdateService.this, imageList, time, folderName);
                }
                //文件夹处理
                if (EmptyUtils.isNotEmpty(mId)) {
                    List<String> idOneList = imageRecycleInstance.queryIdList(RealDocApplication.getDaoSession(UpdateService.this), mId);
                    for (int i = 0; i < idOneList.size(); i++) {
                        List<String> mImageUrlList = imageInstance.queryImageUrlList(RealDocApplication.getDaoSession(UpdateService.this), idOneList.get(i));
                        mImgList.addAll(mImageUrlList);
                    }
                    if (mImgList.size() == 0) {
                        mFlag.add(false);
                    } else {
                        mFlag.add(true);
                    }
                    if (SDCardUtils.isSDCardEnable()) {
                        boolean isEmptyFolder = FileUtils.deleteAllInDir(SDCardUtils.getSDCardPath() + "doctor");
                        if (isEmptyFolder) {
                            //图片
                            File fileImg = new File(imgPath);
                            if (!fileImg.exists())
                                fileImg.mkdirs();
                            for (int i = 0; i < mImgList.size(); i++) {
                                String img = mImgList.get(i);
                                Bitmap bitmap = BitmapFactory.decodeFile(img);
                                if (EmptyUtils.isNotEmpty(bitmap)) {
                                    SDCardUtils.saveToSdCard(imgPath, bitmap, img.substring(img.lastIndexOf("/") + 1, img.length()));
                                }
                            }
                            //视频,音频
                            String path = SDCardUtils.getGlobalDir() + bean.getFolder() + File.separator;
                            if (FileUtils.isFileExists(path)) {
                                if (FileUtils.isFileExists(path + "movie" + File.separator)) {
                                    String videoFolder = folderName + "movie" + File.separator;
                                    //创建新的movie路径
                                    File fileVideo = new File(videoFolder);
                                    fileVideo.mkdirs();
                                    mFlag.add(true);
                                    FileUtils.copyDir(path + "movie" + File.separator, videoFolder);
                                }
                                if (FileUtils.isFileExists(path + "music" + File.separator)) {
                                    String audioFolder = folderName + "music" + File.separator;
                                    //创建新的movie路径
                                    File fileAudio = new File(audioFolder);
                                    fileAudio.mkdirs();
                                    mFlag.add(true);
                                    FileUtils.copyDir(path + "music" + File.separator, audioFolder);
                                }
                            }
                        }
                    }
                }
            }
//            if (FileUtils.isFileExists(folderName)) {
            zip = false;
            //多条病历打成包
            try {
                zip = ZipUtils.zipFile(folderName, SDCardUtils.getGlobalDir() + "doctor" + time + ".zip", "doctor");
                //删除掉原来的文件夹
                FileUtils.deleteDir(folderName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (isTrue() && zip) {
                //动态注册广播
                Intent intent = new Intent(ProgressBarActivity.HAVE_IMG);
                intent.putExtra("folderName", SDCardUtils.getGlobalDir() + "doctor" + time + ".zip");
                uploadData();
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
            } else {
                Intent intent = new Intent(ProgressBarActivity.HAVE_NOTHING);
                intent.putExtra("folderName", SDCardUtils.getGlobalDir() + "doctor" + time + ".zip");
                uploadData();
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
            }
//            }
            return true;
        }

        private boolean isTrue() {
            if (mFlag.size() > 0) {
                for (boolean flag : mFlag) {
                    if (flag) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void uploadData() {
            if (NetworkUtil.isNetworkAvailable(UpdateService.this)) {
                Map<String, RequestBody> maps = new HashMap<>();
                File file = new File(SDCardUtils.getGlobalDir() + "doctor" + time + ".zip");
                if (file.exists()) {
                    RequestBody requestBody = DocUtils.toRequestBodyOfImage(file);
                    maps.put("attach\"; filename=\"" + file.getName() + "", requestBody);//Hhead_img图片key
                }
                maps.put("content", DocUtils.toRequestBodyOfText(inquery));
                maps.put("title", DocUtils.toRequestBodyOfText(desease));
                maps.put("doctorUserId", DocUtils.toRequestBodyOfText(doctorUserId));
                maps.put("patientRecordId", DocUtils.toRequestBodyOfText(patientRecordId));
                maps.put("messageId", DocUtils.toRequestBodyOfText(orderNo));
                if (EmptyUtils.isNotEmpty(questionId)) {
                    maps.put("questionId", DocUtils.toRequestBodyOfText(questionId));
                }
                HttpRequestClient.getInstance(UpdateService.this).createBaseApi().uploads("upload/uploadPatient/", maps, new BaseObserver<ResponseBody>(UpdateService.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        //上传文件成功
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传成功!");
                                } else {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
                        Log.d(TAG, e.getMessage());
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }
                });
            }
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = SaveDocManager.getInstance(UpdateService.this);
        imageInstance = ImageManager.getInstance(UpdateService.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(UpdateService.this);
        recordInstance = RecordManager.getInstance(UpdateService.this);
        videoInstance = VideoManager.getInstance(UpdateService.this);
        if (intent != null && intent.getExtras() != null) {
            mList = intent.getParcelableArrayListExtra("mList");
            inquery = intent.getExtras().getString("inquery");
            desease = intent.getExtras().getString("desease");
            doctorUserId = intent.getExtras().getString("doctorUserId");
            questionId = intent.getExtras().getString("questionId");
            patientRecordId = intent.getExtras().getString("patientRecordId");
            orderNo = intent.getExtras().getString("orderNo");
        }
        Message m = Message.obtain();
        handler.sendMessage(m);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        jobFinished(params, false);//任务执行完后记得调用jobFinsih通知系统释放相关资源
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeCallbacksAndMessages(null);
        Log.i(TAG, "onStopJob:" + params.getJobId());
        return false;
    }
}
