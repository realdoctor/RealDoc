package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ExpertAdapter;
import com.real.doctor.realdoc.adapter.NewsAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class MyFollowDoctorsActivity extends BaseActivity implements OnLoadmoreListener, OnRefreshListener, AdapterView.OnItemClickListener,ExpertAdapter.MyClickListener {
    @BindView(R.id.lv_news)
    ListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.page_title)
    TextView page_title;

    public ExpertAdapter newsAdapter;
    public ArrayList<ExpertBean> newModels = new ArrayList<ExpertBean>();
    private PageModel<ExpertBean> baseModel = new PageModel<ExpertBean>();
    public int pageNum = 1;
    public int pageSize = 10;
    public String userId;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_follow_doctor;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyFollowDoctorsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        userId = (String) SPUtils.get(MyFollowDoctorsActivity.this, Constants.USER_KEY, "");
        page_title.setText("关注医生列表");
        newsAdapter = new ExpertAdapter(MyFollowDoctorsActivity.this, newModels,this);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        getData();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                MyFollowDoctorsActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (pageSize * pageNum > newModels.size()) {
            ToastUtil.show(MyFollowDoctorsActivity.this, "已经是最后一页", Toast.LENGTH_SHORT);
            refreshlayout.finishLoadmore();
            return;
        }
        pageNum++;
        getData();
        refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum = 1;
        newModels.clear();
        getData();
        refreshLayout.finishRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExpertBean bean=(ExpertBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(MyFollowDoctorsActivity.this, PatientEduListActivity.class);
        intent.putExtra("id",bean.doctorCode);
        startActivity(intent);
    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        HttpRequestClient.getInstance(MyFollowDoctorsActivity.this).createBaseApi().get("user/mydoctor/list"
                , params, new BaseObserver<ResponseBody>(MyFollowDoctorsActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<ExpertBean>>() {
                                            }.getType());
                                    newModels.addAll(baseModel.list);
                                    newsAdapter.notifyDataSetChanged();
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    @Override
    public void clickListener(View v) {
        ExpertBean bean = (ExpertBean) v.getTag();
        orderExpert(bean);
    }
    public void orderExpert(ExpertBean bean) {
        JSONObject object = new JSONObject();
        try {
            object.put("deptId", bean.deptId);
            object.put("doctorCode", bean.doctorCode);
            object.put("hospitalDoctorDutyId", bean.hospitalDoctorDutyId);
            object.put("hospitalId", bean.hospitalId);
            object.put("orderDay", bean.dutyDtime);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = (String) SPUtils.get(MyFollowDoctorsActivity.this, Constants.TOKEN, "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(MyFollowDoctorsActivity.this, "请确定您的账户已登录!");
            return;
        }
        HttpRequestClient client = HttpRequestClient.getInstance(MyFollowDoctorsActivity.this, HttpNetUtil.BASE_URL, header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("guahao/fastorder/"
                , body, new BaseObserver<ResponseBody>(MyFollowDoctorsActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    ToastUtil.showLong(MyFollowDoctorsActivity.this, "预约成功!");
                                } else {
                                    ToastUtil.showLong(MyFollowDoctorsActivity.this, "预约失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });

    }
}
