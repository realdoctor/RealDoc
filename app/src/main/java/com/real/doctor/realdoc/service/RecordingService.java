package com.real.doctor.realdoc.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.real.doctor.realdoc.activity.RecordActivity;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.MySharedPreferences;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Daniel on 12/28/2014.
 */
public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;
    private MediaRecorder mRecorder = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private TimerTask mIncrementTimerTask = null;
    private String mFolder;
    private String mModifyId;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        mFolder = intent.getStringExtra("folder");
        mModifyId = intent.getStringExtra("modifyId");
        startRecording();
        return new StopBinder();
    }

    public void startRecording() {
        mStartingTimeMillis = Long.valueOf(DateUtil.timeStamp());

        setFileNameAndPath();
        if (EmptyUtils.isEmpty(mRecorder)) {
            startRecord();
        } else {
            mRecorder.stop();
            mRecorder.release();
            startRecord();
        }
    }

    public void startRecord() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        if (MySharedPreferences.getPrefHighQuality(this)) {
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        }

        try {
            mRecorder.prepare();
            mRecorder.start();
            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setFileNameAndPath() {
        mFileName = mStartingTimeMillis + ".mp3";
        mFilePath = SDCardUtils.getSDCardPath() + "RealDoc" + File.separator + mFolder + File.separator + "music" + File.separator;
        //创建路径
        File folderFile = new File(mFilePath);
        if (!folderFile.exists())
            folderFile.mkdirs();
        mFilePath += mFileName;
    }

    public void stopRecordService() {
        try {
            RecordBean bean = new RecordBean();
            if (EmptyUtils.isNotEmpty(mModifyId)) {
                bean.setRecordId(mModifyId);
            }
            bean.setFileName(mFileName);
            bean.setFilePath(mFilePath);
            bean.setDate(String.valueOf(mStartingTimeMillis));
            bean.setElapsedMillis(String.valueOf(mElapsedMillis));
            bean.setFolder(mFolder);
            //广播给activity
            //通知页面刷新数据
            Intent intent = new Intent(RecordActivity.RECORD_SERVICE);
            intent.putExtra("record", bean);
            LocalBroadcastManager.getInstance(RecordingService.this).sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
        }
    }

    public void stopRecording() {
        if (EmptyUtils.isNotEmpty(mRecorder)) {
            mRecorder.setOnErrorListener(null);
            mRecorder.setOnInfoListener(null);
            mRecorder.setPreviewDisplay(null);
            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mRecorder = null;
                mRecorder = new MediaRecorder();
            }
            mRecorder.release();
            mRecorder = null;
        }
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        ToastUtil.showLong(RecordingService.this, "录音完成!");
        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;
    }

    private class StopBinder extends Binder implements StopRecordService {

        @Override
        public void stopRecord() {
            stopRecording();
        }

        @Override
        public void stopService() {
            stopRecordService();
        }
    }
}
