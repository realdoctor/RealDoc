package com.real.doctor.realdoc.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.PayResult;
import com.real.doctor.realdoc.util.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class PayActivity extends BaseActivity {
    @BindView(R.id.pay_type)
    RadioGroup rgPayType;
    @BindView(R.id.zhifubao)
    RadioButton rbAlipay;
    @BindView(R.id.weixin)
    RadioButton rbWechat;
    @BindView(R.id.tv_count_price)
    TextView tvCountprice;
    @BindView(R.id.bt_pay)
    Button btPay;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private String zhifu_type="";
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    @Override
    public int getLayoutId() {
        return R.layout.activity_commit_order;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        api.registerApp(Constants.WX_APP_ID);
        tvCountprice.setText("0.01元");
        pageTitle.setText("支付");
    }

    @Override
    public void initEvent() {
        rgPayType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if(checkedId== rbAlipay.getId())
                {
                    zhifu_type="1";
                }else if(checkedId== rbWechat.getId())
                {
                    zhifu_type="0";
                }
            }
        });
    }
    private  boolean isWXAppInstalledAndSupported(Context context,
                                                  IWXAPI api) {
        // LogOutput.d(TAG, "isWXAppInstalledAndSupported");
        boolean	sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
                && api.isWXAppSupportAPI();
        if (!sIsWXAppInstalledAndSupported) {
            Toast.makeText(context, "尚未安装微信客户端或者微信版本不支持", Toast.LENGTH_SHORT).show();
        }
        return sIsWXAppInstalledAndSupported;
    }
    @Override
    @OnClick({R.id.bt_pay,R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.bt_pay:
                   if(zhifu_type.length()==0){
                       ToastUtil.show(PayActivity.this,"请选择支付方式",Toast.LENGTH_SHORT);
                       return;
                   }else{
                       if(zhifu_type.equals("1")){
                           payOrderByAlipay();
                       }else if(zhifu_type.equals("0"))
                       {
                           if(isWXAppInstalledAndSupported(PayActivity.this,api))
                           {
                               payOrderByWechat();
                           }
                       }
                   }
                break;
            case R.id.finish_back:
                PayActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    public void payOrderByAlipay(){
        JSONObject json = new JSONObject();
        try {
            json.put("name","ddd");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(PayActivity.this).createBaseApi().json("user/regist/"
                , body, new BaseObserver<ResponseBody>(PayActivity.this) {

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
                                    final String orderInfo=object.getString("rs");

                                    Runnable payRunnable = new Runnable() {

                                        @Override
                                        public void run() {
                                            // 构造PayTask 对象
                                            PayTask alipay = new PayTask(PayActivity.this);
                                            // 调用支付接口，获取支付结果
                                            String result = alipay.pay(orderInfo, true);

                                            Message msg = new Message();
                                            msg.what = SDK_PAY_FLAG;
                                            msg.obj = result;
                                            mHandler.sendMessage(msg);
                                        }
                                    };

                                    // 必须异步调用
                                    Thread payThread = new Thread(payRunnable);
                                    payThread.start();
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
    public void payOrderByWechat(){

        JSONObject json = new JSONObject();
        try {
            json.put("name","ddd");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(PayActivity.this).createBaseApi().json("user/regist/"
                , body, new BaseObserver<ResponseBody>(PayActivity.this) {

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
                                    String pay_str=	object.getString("rs");

                                    JSONObject ob=new JSONObject(pay_str);

                                    String prepayId=ob.getString("prepayid");
                                    String nonceStr=ob.getString("noncestr");
                                    String timeStamp=ob.getString("timestamp");
                                    String packageValue=ob.getString("package");
                                    String sign        =ob.getString("sign");
                                    String partnerId	=ob.getString("partnerid");


                                    PayReq req = new PayReq();
                                    req.appId = Constants.WX_APP_ID;
                                    req.partnerId = partnerId;
                                    req.prepayId = prepayId;
                                    req.nonceStr = nonceStr;
                                    req.timeStamp = String.valueOf(timeStamp);
                                    req.packageValue = packageValue;
                                    req.sign = sign;

                                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                    boolean flag=	api.sendReq(req);
                                    if(flag)
                                    {
                                        PayActivity.this.finish();
                                    }
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

}