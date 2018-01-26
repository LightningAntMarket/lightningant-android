package android.lightningant.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inter.baisong.R;
import inter.baisong.adapter.MeRobModeAdapter;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.MeRobOrderBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.widgets.CoustomSwipRefresh;

/**
 * Created by ${刘全伦} on 2017/9/6.
 * 名称:
 */

public class MeOrderFixedPriceActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecylerview;
    private ImageView img_left;
    private int page;
    private CoustomSwipRefresh swipe;
    private TextView tv_title;
    private MeRobModeAdapter mAdapter;
    private List<MeRobOrderBean.GoodInfo> mList;
    private LinearLayout empty_view;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.me_order_fixed;
    }

    @Override
    public void initView() {
        mRecylerview = (RecyclerView) findViewById(R.id.mRecylerview);
        img_left = (ImageView) findViewById(R.id.img_left);
        swipe = find(R.id.swipe);
        tv_title = find(R.id.tv_title);
        tv_title.setText(AppResourceMgr.getString(this,R.string.fixed_order));
        empty_view = find(R.id.empty_view);
        mAdapter = new MeRobModeAdapter(this);
        mList = new ArrayList<>();
    }

    @Override
    public void initData() {
        mRecylerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecylerview.setAdapter(mAdapter);
        swipe.setOnRefreshListener(this);
        swipe.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        mRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    if(mRecylerview.canScrollVertically(-1)&&mList.size()>9){
                        page++;
                        requestHttp();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        page=1;
        requestHttp();
    }

    private void requestHttp(){
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_ORDER_DONE+2+"/p/"+page,"Order,orderListByGet", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                swipe.setRefreshing(false);
                MeRobOrderBean bean = ConfigYibaisong.jsonToBean(result,MeRobOrderBean.class);
                if(bean.getStatus()==1){
                    if(page==1){
                        mList.clear();
                    }
                    empty_view.setVisibility(View.GONE);
                    mList.addAll(bean.getData());
                    mAdapter.setData(mList);
                    mAdapter.notifyDataSetChanged();
                }else{
                    if(page==1){
                        empty_view.setVisibility(View.VISIBLE);
                    }
                    AppToastMgr.showToast(bean.getMsg());
                }

            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.showError();
                swipe.setRefreshing(false);
                if(page==1){
                    empty_view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200 &&data.getIntExtra("position",-1)!=-1){
            mList.get(data.getIntExtra("position",-1)).setOstate("10");
            mAdapter.notifyDataSetChanged();
        }
    }
}
