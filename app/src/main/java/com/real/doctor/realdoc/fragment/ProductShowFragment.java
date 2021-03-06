package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.LoginActivity;
import com.real.doctor.realdoc.activity.ProductShowActivity;
import com.real.doctor.realdoc.adapter.BrandAdapter;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.CategoryBean;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.BrandListView;
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
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ProductShowFragment extends BaseFragment implements OnLoadmoreListener, OnRefreshListener, AdapterView.OnItemClickListener, ProductAdapter.ClickListener {
    private Unbinder unbinder;
    public CategoryBean bean;
    @BindView(R.id.lv_brands)
    BrandListView brandListView;
    @BindView(R.id.lv_products)
    ListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    public BrandAdapter brandAdapter;
    public ProductAdapter productAdapter;
    public String categoryId;
    private ClassicsHeader mClassicsHeader;
    public int pageNum = 1;
    public int pageSize = 10;
    public boolean isFirstEnter = true;
    public String searchKey = "";
    public String userId;
    private PageModel<ProductBean> baseModel = new PageModel<ProductBean>();
    public ArrayList<ProductBean> productList = new ArrayList<ProductBean>();

    public static ProductShowFragment newInstance(CategoryBean bean) {
        ProductShowFragment fragment = new ProductShowFragment();
        Bundle bundel = new Bundle();
        bundel.putSerializable("model", bean);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(final Context mContext) {
        if (getArguments() != null) {
            userId = (String) SPUtils.get(getContext(), Constants.USER_KEY, "");
            bean = (CategoryBean) getArguments().get("model");
            brandAdapter = new BrandAdapter(mContext, DataUtil.brandBeans);
            brandListView.setAdapter(brandAdapter);
            brandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    final int location = position;
                    brandAdapter.setSelectedPosition(position);
                    brandAdapter.notifyDataSetInvalidated();
//                    final BrandBean bean = (BrandBean) brandAdapter.getItem(position);
//                    productAdapter = new ProductAdapter(mContext, bean.orderModels);
//                    listView.setAdapter(productAdapter);
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> arg0, View arg1,
//                                                int position, long arg3) {
//                            ProductInfo product = bean.orderModels.get(position);
//                            Intent intent = new Intent(getActivity(), ProductShowActivity.class);
//                            intent.putExtra("model",product);
//                            startActivity(intent);
//                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                            getActivity().finish();
//
//                        }
//                    });

                }
            });
            selectDefault();
            int deta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
            mClassicsHeader = (ClassicsHeader) refreshLayout.getRefreshHeader();
            ClassicsFooter footer = (ClassicsFooter) refreshLayout.getRefreshFooter();
            refreshLayout.setOnLoadmoreListener(this);
            refreshLayout.setOnRefreshListener(this);
            productAdapter = new ProductAdapter(mContext, productList);
            productAdapter.setmListener(this);
            listView.setAdapter(productAdapter);
            listView.setOnItemClickListener(this);
            getRefreshProducts();
        }
    }

    public void getRefreshProducts() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("categoryId", bean.categoryId);
        param.put("pageNum", pageNum);
        param.put("pageSize", pageSize);
        param.put("searchstr", searchKey);
        HttpRequestClient.getInstance(getContext()).createBaseApi().get(" goods/list/"
                , param, new BaseObserver<ResponseBody>(getContext()) {
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
                                    productList.clear();
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<ProductBean>>() {
                                            }.getType());
                                    productList.addAll(baseModel.list);
                                    productAdapter.notifyDataSetChanged();
                                    refreshLayout.finishRefresh();
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

    public void getLoadProducts() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("categoryId", bean.categoryId);
        param.put("pageNum", pageNum);
        param.put("pageSize", pageSize);
        param.put("searchstr", searchKey);
        HttpRequestClient.getInstance(getContext()).createBaseApi().get("goods/list/"
                , param, new BaseObserver<ResponseBody>(getContext()) {
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
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<ProductBean>>() {
                                            }.getType());
                                    productList.addAll(baseModel.list);
                                    productAdapter.notifyDataSetChanged();
                                    refreshLayout.finishLoadmore();
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
    public void widgetClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //默认选中
    private void selectDefault() {
        final int location = 0;
        brandAdapter.setSelectedPosition(0);
        brandAdapter.notifyDataSetInvalidated();
//        final BrandBean breadBean=	(BrandBean) brandAdapter.getItem(0);
//        productAdapter=new ProductAdapter(getContext(), breadBean.productList);
//        listView.setAdapter(productAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1,
//                                    int position, long arg3) {
//                ProductInfo product= breadBean.productList.get(position);
//                Intent intent = new Intent(getActivity(), ProductShowActivity.class);
//                intent.putExtra("model",product);
//                startActivity(intent);
//                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                getActivity().finish();
//            }
//        });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (pageSize * pageNum > productList.size()) {
            ToastUtil.show(getContext(), "已经是最后一页", Toast.LENGTH_SHORT);
            refreshlayout.finishLoadmore();
            return;
        }
        pageNum++;
        getLoadProducts();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum = 1;
        searchKey = "";
        getRefreshProducts();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProductBean bean = (ProductBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getContext(), ProductShowActivity.class);
        intent.putExtra("model", bean);
        startActivity(intent);
    }

    @Override
    public void clickListener(View v) {
        if (userId == null || userId.length() == 0) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            ProductBean bean = (ProductBean) v.getTag();
            addToCart(bean.getGoodsId(), 1);
        }

    }

    //添加到购物车
    public void addToCart(String goodsId, int num) {
        JSONObject object = new JSONObject();
        try {
            object.put("goodsId", goodsId);
            object.put("num", num);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = (String) SPUtils.get(getContext(), Constants.TOKEN, "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(getContext(), "请确定您的账户已登录!");
            return;
        }
        HttpRequestClient client = HttpRequestClient.getInstance(getContext(), HttpNetUtil.BASE_URL, header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("goods/cart/addCartItem/"
                , body, new BaseObserver<ResponseBody>(getContext()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
//                        ToastUtil.showLong(RegisterActivity.this, e.getMessage());
                        ToastUtil.showLong(getContext(), "加入购物车失败!");
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
                                    ToastUtil.showLong(getContext(), "加入购物车成功!");
                                } else {
                                    ToastUtil.showLong(getContext(), "加入购物车失败!");
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
