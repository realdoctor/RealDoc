package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.CaseControlAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class CaseControlActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.search_patient)
    TextView searchPatient;
    @BindView(R.id.my_patient_rv)
    RecyclerView myPatientRv;
    CaseControlAdapter caseControlAdapter;
    private List<PatientBean> patientList;
    private static int pageNum = 1;
    private String mUserId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_case_control;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(CaseControlActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        pageTitle.setText("我的患者");
        patientList = new ArrayList<>();
        //添加自定义分割线
        myPatientRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        myPatientRv.addItemDecoration(divider);
        caseControlAdapter = new CaseControlAdapter(R.layout.case_control_item, patientList);
        mUserId = (String) SPUtils.get(CaseControlActivity.this, Constants.USER_KEY, "");
        init();
    }

    private void init() {
        HashMap<String, String> param = new HashMap<>();
        param.put("pageNum", String.valueOf(pageNum));
        param.put("pageSize", "10");
        param.put("userId", mUserId);
        HttpRequestClient.getInstance(CaseControlActivity.this).createBaseApi().get("patient/upload/list"
                , param, new BaseObserver<ResponseBody>(CaseControlActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

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
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONObject obj = object.getJSONObject("data");
                                        if (DocUtils.hasValue(obj, "list")) {
                                            patientList = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), PatientBean.class);
                                            caseControlAdapter = new CaseControlAdapter(R.layout.case_control_item, patientList);
                                            myPatientRv.setAdapter(caseControlAdapter);
                                            initEvent();
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(CaseControlActivity.this, "获取用户信息失败.请确定是否已登录!");
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
    public void initEvent() {
        caseControlAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(CaseControlActivity.this, CaseListActivity.class);
                Bundle mBundle = new Bundle();
                PatientBean patientBean = (PatientBean) adapter.getItem(position);
                mBundle.putString("realName",patientBean.getUserInfo().getRealname());
                mBundle.putParcelable("patient", patientBean);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back, R.id.search_patient})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.search_patient:
                Intent intent = new Intent(this, SearchPatientActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
