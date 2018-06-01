package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.LoginActivity;
import com.real.doctor.realdoc.activity.MyFollowNewsActivity;
import com.real.doctor.realdoc.activity.MyRegistrationActivity;
import com.real.doctor.realdoc.activity.OrderListActivity;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.util.DocUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * user：lqm
 * desc：第四个模块，用户模块
 */

public class UserFragment extends BaseFragment {

    private Unbinder unbinder;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_function_three)
    LinearLayout myOrder;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_user;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    @OnClick({R.id.user_name,R.id.user_function_three,R.id.user_function_four,R.id.user_function_five})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.user_name:
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.user_function_three:
                    Intent intentRegistration = new Intent(getActivity(), MyRegistrationActivity.class);
                    startActivity(intentRegistration);
                    break;
                case R.id.user_function_four:
                    Intent intentFollow = new Intent(getActivity(), MyFollowNewsActivity.class);
                    startActivity(intentFollow);
                    break;
                case R.id.user_function_five:
                    Intent intentOrder = new Intent(getActivity(), OrderListActivity.class);
                    startActivity(intentOrder);
                    break;
            }
            }
        }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
