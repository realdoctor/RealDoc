package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.RegistrationAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.RegistrationModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class MyRegistrationActivity extends BaseActivity {

    @BindView(R.id.lv_registration)
    ListView lv_registration;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    RegistrationAdapter registrationAdapter;
    ArrayList<RegistrationModel> registrationModelArrayList = new ArrayList<RegistrationModel>();
    private String userId;
    private Dialog mProgressDialog;
    private int mPageNum = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_registration;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mProgressDialog = DocUtils.getProgressDialog(this, "正在加载数据....");
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyRegistrationActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        page_title.setText("我的预约");
        userId = (String) SPUtils.get(MyRegistrationActivity.this, Constants.USER_KEY, "");
        registrationAdapter = new RegistrationAdapter(MyRegistrationActivity.this, registrationModelArrayList);
        lv_registration.setAdapter(registrationAdapter);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                MyRegistrationActivity.this.finish();
                break;
        }

    }

    @Override
    public void doBusiness(Context mContext) {
        getData("1");
        swipeRefresh();
    }

    private void getData(String pageNum) {
        mProgressDialog.show();
        HashMap<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("pageNum", pageNum);
        param.put("pageSize", "10");
        HttpRequestClient.getInstance(MyRegistrationActivity.this).createBaseApi().get("user/myGuahaoOrder/"
                , param, new BaseObserver<ResponseBody>(MyRegistrationActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPageNum == 1) {
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                        ToastUtil.showLong(MyRegistrationActivity.this, "获取我的预约列表失败!");
                        mProgressDialog.dismiss();
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

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        registrationModelArrayList.clear();
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
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONObject obj = object.getJSONObject("data");
                                        if (DocUtils.hasValue(obj, "list")) {
                                            JSONArray jsonObject = obj.getJSONArray("list");
                                            Gson localGson = new GsonBuilder()
                                                    .create();
                                            registrationModelArrayList.addAll((ArrayList<RegistrationModel>) localGson.fromJson(jsonObject.toString(),
                                                    new TypeToken<ArrayList<RegistrationModel>>() {
                                                    }.getType()));
                                            registrationAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(MyRegistrationActivity.this, "获取我的预约列表失败!");
                                }
                                if (mPageNum == 1) {
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadmore();
                                }
                                mProgressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void swipeRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //处理刷新列表逻辑
                getData("1");
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(String.valueOf(++mPageNum));
            }
        });
    }

}
