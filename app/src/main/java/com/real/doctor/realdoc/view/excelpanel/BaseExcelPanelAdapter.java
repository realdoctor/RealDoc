package com.real.doctor.realdoc.view.excelpanel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SizeUtils;

import java.util.List;


/**
 * Created by zhujiabin on 2016/12/11.
 */

public abstract class BaseExcelPanelAdapter<T, L, M> implements OnExcelPanelListener {

    public static final int LOADING_VIEW_WIDTH = 30;

    private int leftCellWidth;
    private int topCellHeight;
    protected int amountAxisY = 0;

    private Context mContext;
    private RecyclerViewAdapter topRecyclerViewAdapter;
    private RecyclerViewAdapter leftRecyclerViewAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private View leftTopView;
    private ExcelPanel excelPanel;
    protected RecyclerView.OnScrollListener onScrollListener;
    protected List<T> topData;
    protected List<L> leftData;
    protected List<List<M>> majorData;

    public BaseExcelPanelAdapter(Context context) {
        mContext = context;
        initRecyclerViewAdapter();
    }

    private void initRecyclerViewAdapter() {
        topRecyclerViewAdapter = new TopRecyclerViewAdapter(mContext, topData, this);
        leftRecyclerViewAdapter = new LeftRecyclerViewAdapter(mContext, leftData, this);
        mRecyclerViewAdapter = new MajorRecyclerViewAdapter(mContext, majorData, this);
    }

    public void setTopData(List<T> topData) {
        this.topData = topData;
        topRecyclerViewAdapter.setData(topData);
    }

    public void setLeftData(List<L> leftData) {
        this.leftData = leftData;
        leftRecyclerViewAdapter.setData(leftData);
    }

    public void setMajorData(List<List<M>> majorData) {
        this.majorData = majorData;
        mRecyclerViewAdapter.setData(majorData);
    }

    public void setAllData(List<L> leftData, List<T> topData, List<List<M>> majorData) {
        setLeftData(leftData);
        setTopData(topData);
        setMajorData(majorData);
        excelPanel.scrollBy(0);
        excelPanel.fastScrollVerticalLeft();
        if (!EmptyUtils.isEmpty(leftData) && !EmptyUtils.isEmpty(topData) && excelPanel != null
                && !EmptyUtils.isEmpty(majorData) && leftTopView == null) {
            leftTopView = onCreateTopLeftView();
            excelPanel.addView(leftTopView, new FrameLayout.LayoutParams(leftCellWidth, topCellHeight));
        } else if (leftTopView != null) {
            if (EmptyUtils.isEmpty(leftData)) {
                leftTopView.setVisibility(View.GONE);
            } else {
                leftTopView.setVisibility(View.VISIBLE);
            }
        }
    }

    public RecyclerViewAdapter getmRecyclerViewAdapter() {
        return mRecyclerViewAdapter;
    }

    public RecyclerViewAdapter getLeftRecyclerViewAdapter() {
        return leftRecyclerViewAdapter;
    }

    public RecyclerViewAdapter getTopRecyclerViewAdapter() {
        return topRecyclerViewAdapter;
    }

    public void setLeftCellWidth(int leftCellWidth) {
        this.leftCellWidth = leftCellWidth;
    }

    public void setTopCellHeight(int topCellHeight) {
        this.topCellHeight = topCellHeight;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setOnScrollListener(onScrollListener);
        }
    }

    public T getTopItem(int position) {
        if (EmptyUtils.isEmpty(topData) || position < 0 || position >= topData.size()) {
            return null;
        }
        return topData.get(position);
    }

    public L getLeftItem(int position) {
        if (EmptyUtils.isEmpty(leftData) || position < 0 || position >= leftData.size()) {
            return null;
        }
        return leftData.get(position);
    }

    public M getMajorItem(int x, int y) {
        if (EmptyUtils.isEmpty(majorData) || x < 0 || x >= majorData.size() || EmptyUtils
                .isEmpty(majorData.get(x)) || y < 0 || y >= majorData.get(x).size()) {
            return null;
        }
        return majorData.get(x).get(y);
    }

    public void setAmountAxisY(int amountAxisY) {
        this.amountAxisY = amountAxisY;
        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof MajorRecyclerViewAdapter) {
            ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).setAmountAxisY(amountAxisY);
        }
    }

    public void setExcelPanel(ExcelPanel excelPanel) {
        this.excelPanel = excelPanel;
    }

    protected View createTopStaticView() {
        View topStaticView = new View(mContext);
        int loadingWidth = SizeUtils.dp2px(mContext, LOADING_VIEW_WIDTH);
        topStaticView.setLayoutParams(new ViewGroup.LayoutParams(loadingWidth, topCellHeight));

        return topStaticView;
    }

    protected View createMajorLoadingView() {
        int loadingWidth = SizeUtils.dp2px(mContext, LOADING_VIEW_WIDTH);
        LinearLayout loadingView = new LinearLayout(mContext);
        loadingView.setOrientation(LinearLayout.VERTICAL);
        loadingView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(loadingWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        loadingView.setLayoutParams(lpp);

        ProgressBar progressBar = new ProgressBar(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                SizeUtils.dp2px(mContext, 16), SizeUtils.dp2px(mContext, 16)));
        progressBar.setLayoutParams(lp);

        loadingView.addView(progressBar, lp);

        return loadingView;
    }

    public void enableHeader() {
        if (topRecyclerViewAdapter != null && mRecyclerViewAdapter != null && excelPanel != null &&
                (topRecyclerViewAdapter.getHeaderViewsCount() <= 0 || mRecyclerViewAdapter.getHeaderViewsCount() <= 0)) {
            topRecyclerViewAdapter.setHeaderView(createTopStaticView());
            mRecyclerViewAdapter.setHeaderView(createMajorLoadingView());
            excelPanel.setHasHeader(true);
            excelPanel.scrollBy(SizeUtils.dp2px(mContext, LOADING_VIEW_WIDTH));
        }
    }

    public void enableFooter() {
        if (topRecyclerViewAdapter != null && mRecyclerViewAdapter != null && excelPanel != null &&
                (topRecyclerViewAdapter.getFooterViewsCount() <= 0 || mRecyclerViewAdapter.getFooterViewsCount() <= 0)) {
            topRecyclerViewAdapter.setFooterView(createTopStaticView());
            mRecyclerViewAdapter.setFooterView(createMajorLoadingView());
            excelPanel.setHasFooter(true);
        }
    }

    public void disableHeader() {
        if (topRecyclerViewAdapter != null && mRecyclerViewAdapter != null && excelPanel != null &&
                (topRecyclerViewAdapter.getHeaderViewsCount() > 0 || mRecyclerViewAdapter.getHeaderViewsCount() > 0)) {
            topRecyclerViewAdapter.setHeaderView(null);
            mRecyclerViewAdapter.setHeaderView(null);
            excelPanel.setHasHeader(false);
            excelPanel.scrollBy(SizeUtils.dp2px(mContext, LOADING_VIEW_WIDTH));
        }
    }

    public void disableFooter() {
        if (topRecyclerViewAdapter != null && mRecyclerViewAdapter != null && excelPanel != null &&
                (topRecyclerViewAdapter.getFooterViewsCount() > 0 || mRecyclerViewAdapter.getFooterViewsCount() > 0)) {
            topRecyclerViewAdapter.setFooterView(null);
            mRecyclerViewAdapter.setFooterView(null);
            excelPanel.setHasFooter(false);

        }
    }

    @Override
    public int getCellItemViewType(int verticalPosition, int horizontalPosition) {
        return RecyclerViewAdapter.TYPE_NORMAL;
    }

    @Override
    public int getLeftItemViewType(int position) {
        return RecyclerViewAdapter.TYPE_NORMAL;
    }

    @Override
    public int getTopItemViewType(int position) {
        return RecyclerViewAdapter.TYPE_NORMAL;
    }

    public final void notifyDataSetChanged() {
        topRecyclerViewAdapter.notifyDataSetChanged();
        leftRecyclerViewAdapter.notifyDataSetChanged();
        ((MajorRecyclerViewAdapter) mRecyclerViewAdapter).customNotifyDataSetChanged();
    }

    @Override
    public void onAfterBind(RecyclerView.ViewHolder holder, int position) {
        if (excelPanel != null) {
            excelPanel.onAfterBind(holder, position);
        }
    }
}
