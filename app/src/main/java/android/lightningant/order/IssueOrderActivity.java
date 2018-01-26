package android.lightningant.order;

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
import inter.baisong.adapter.MeSendModeAdapter;
import inter.baisong.adapter.MeSendOrderReleaseAdapter;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.MeSendOrderBean;
import inter.baisong.bean.MeSendOrderReleaseBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.widgets.CoustomSwipRefresh;

/**
 * Created by ${刘全伦} on 2017/9/6.
 * 名称:
 */

public class MeIssueOrderActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private TextView tv_auction,tv_fixed,tv_release;
    private TextView tv_title;
    private int tag;//auction = 3   fixed = 2  release = 1
    private ImageView img_left;
    private CoustomSwipRefresh swipe;
    private int auction_p,fixed_p,release_p;
    private RecyclerView recycler_auction,recycler_fixed,recycler_release;
    private boolean isFirstFix,isFirstRelease;
    private List<MeSendOrderReleaseBean.GoodInfo> releaseList ;
    private List<MeSendOrderBean.GoodInfo> auctionList;
    private List<MeSendOrderBean.GoodInfo> fixedList;
    private MeSendModeAdapter mAuctionAdapter,mFixedAdapter;
    private MeSendOrderReleaseAdapter mReleaseAdapter;
    private LinearLayout empty_view;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.me_order_issue;
    }

    @Override
    public void initView() {
        tv_title = find(R.id.tv_title);
        img_left = find(R.id.img_left);
        tv_auction = find(R.id.tv_auction);
        tv_fixed = find(R.id.tv_fixedprice);
        tv_release = find(R.id.tv_release);
        swipe = find(R.id.swipe);
        empty_view = find(R.id.empty_view);
        recycler_auction = find(R.id.recycler_auction);
        recycler_fixed = find(R.id.recycler_fixedprice);
        recycler_release = find(R.id.recycler_release);
        releaseList = new ArrayList<>();
        auctionList = new ArrayList<>();
        fixedList = new ArrayList<>();
        mAuctionAdapter = new MeSendModeAdapter(this);
        mFixedAdapter = new MeSendModeAdapter(this);
        mReleaseAdapter = new MeSendOrderReleaseAdapter(this);
        release_p=auction_p=fixed_p=1;
        tag=3;
        isFirstFix = isFirstRelease = true;

    }

    @Override
    public void initData() {
        swipe.setOnRefreshListener(this);
        tv_title.setText(AppResourceMgr.getString(this,R.string.issuedOrder));
        recycler_auction.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recycler_fixed.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recycler_release.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recycler_auction.setAdapter(mAuctionAdapter);
        recycler_fixed.setAdapter(mFixedAdapter);
        recycler_release.setAdapter(mReleaseAdapter);
        swipe.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void setListener() {
        tv_auction.setOnClickListener(this);
        tv_fixed.setOnClickListener(this);
        img_left.setOnClickListener(this);
        tv_release.setOnClickListener(this);
        recycler_auction.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    if(recyclerView.canScrollVertically(-1)&&auctionList.size()>9){
                        auction_p++;
                        requestHttp();
                    }
                }
            }
        });
        recycler_fixed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    if(recyclerView.canScrollVertically(-1)&&fixedList.size()>9){
                        fixed_p++;
                        requestHttp();
                    }
                }
            }
        });
        recycler_release.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    if(recyclerView.canScrollVertically(-1)&&releaseList.size()>9){
                        release_p++;
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
                this.finish();
                break;
            case R.id.tv_auction://竞拍
                tag=3;
                tv_auction.setTextColor(AppResourceMgr.getColor(this,R.color.app_color_theme));
                tv_fixed.setTextColor(AppResourceMgr.getColor(this,R.color.tv_most_black));
                tv_release.setTextColor(AppResourceMgr.getColor(this,R.color.tv_most_black));

                tv_auction.setBackgroundResource(R.drawable.rectangle_bottom_line_blue);
                tv_fixed.setBackgroundResource(R.color.white);
                tv_release.setBackgroundResource(R.color.white);

                recycler_auction.setVisibility(View.VISIBLE);
                recycler_fixed.setVisibility(View.GONE);
                recycler_release.setVisibility(View.GONE);
                if(auctionList.size()==0){
                    isshowEmpty(true);
                }else{
                    isshowEmpty(false);
                }
                break;
            case R.id.tv_release://发布
                tag=1;
                tv_auction.setTextColor(AppResourceMgr.getColor(this,R.color.tv_most_black));
                tv_fixed.setTextColor(AppResourceMgr.getColor(this,R.color.tv_most_black));
                tv_release.setTextColor(AppResourceMgr.getColor(this,R.color.app_color_theme));

                tv_auction.setBackgroundResource(R.color.white);
                tv_fixed.setBackgroundResource(R.color.white);
                tv_release.setBackgroundResource(R.drawable.rectangle_bottom_line_blue);

                recycler_auction.setVisibility(View.GONE);
                recycler_fixed.setVisibility(View.GONE);
                recycler_release.setVisibility(View.VISIBLE);
                if(isFirstRelease){
                    swipe.setRefreshing(true);
                    requestReleaseHttp();
                    isFirstRelease=false;
                }else if(releaseList.size()==0){
                    isshowEmpty(true);
                }else{
                    isshowEmpty(false);
                }
                break;
            case R.id.tv_fixedprice://一口价
                tag=2;
                tv_auction.setTextColor(AppResourceMgr.getColor(this,R.color.tv_most_black));
                tv_fixed.setTextColor(AppResourceMgr.getColor(this,R.color.app_color_theme));
                tv_release.setTextColor(AppResourceMgr.getColor(this,R.color.tv_most_black));

                tv_auction.setBackgroundResource(R.color.white);
                tv_fixed.setBackgroundResource(R.drawable.rectangle_bottom_line_blue);
                tv_release.setBackgroundResource(R.color.white);

                recycler_auction.setVisibility(View.GONE);
                recycler_fixed.setVisibility(View.VISIBLE);
                recycler_release.setVisibility(View.GONE);
                if(isFirstFix){
                    swipe.setRefreshing(true);
                    requestHttp();
                    isFirstFix=false;
                }else if(fixedList.size()==0){
                    isshowEmpty(true);
                }else{
                    isshowEmpty(false);
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        if(tag==3){//auction
            auction_p=1;
            requestHttp();
        }else if(tag==2){//fixed
            fixed_p=1;
            requestHttp();
        }else if(tag==1){//release
            release_p=1;
            requestReleaseHttp();
        }
    }


    /**
     * 是否显示空白页
     * @param isshow
     */
    private void isshowEmpty(boolean isshow){
        if(isshow)
            empty_view.setVisibility(View.VISIBLE);
        else
            empty_view.setVisibility(View.GONE);
    }

    /***
     * 竞拍和一口价送过的订单
     */
    private void requestHttp() {
        int p=1;
        if(tag==3){
            p=auction_p;
        }else if(tag==2){
            p=fixed_p;
        }
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_ORDER_ISSUE + tag+"/p/"+p, "Order,orderSendList", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                swipe.setRefreshing(false);
                MeSendOrderBean bean = ConfigYibaisong.jsonToBean(result,MeSendOrderBean.class);
                if(bean.getStatus()==1){
                    isshowEmpty(false);
                    if(tag==2){//一口价
                        if(fixed_p==1){
                            fixedList.clear();
                        }
                        fixedList.addAll(bean.getData());
                        mFixedAdapter.setData(fixedList);
                        mFixedAdapter.notifyDataSetChanged();
                    }else if(tag==3){//竞拍
                        if(auction_p==1){
                            auctionList.clear();
                        }
                        auctionList.addAll(bean.getData());
                        mAuctionAdapter.setData(auctionList);
                        mAuctionAdapter.notifyDataSetChanged();
                    }
                }else{
                    if(tag==2){
                        if(fixed_p==1){
                            isshowEmpty(true);
                        }
                    }else{
                        if(auction_p==1){
                            isshowEmpty(true);
                        }
                    }
                    AppToastMgr.showToast(bean.getMsg());
                }


            }

            @Override
            public void onError(String error_msg, Throwable e) {
                swipe.setRefreshing(false);
                if(tag==2){
                    if(fixed_p==1){
                        isshowEmpty(true);
                    }
                }else{
                    if(auction_p==1){
                        isshowEmpty(true);
                    }
                }
            }
        });
    }

    /***
     * 商品发布的进行中
     */
    private void requestReleaseHttp(){
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_ORDER_ISSUE_RELEASE+"/p/"+release_p, "goods,mysend", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                swipe.setRefreshing(false);
                MeSendOrderReleaseBean bean = ConfigYibaisong.jsonToBean(result,MeSendOrderReleaseBean.class);
                if(bean.getStatus()==1){
                    isshowEmpty(false);
                    if(release_p==1){
                        releaseList.clear();
                    }
                    releaseList.addAll(bean.getData());
                    mReleaseAdapter.setData(releaseList);
                    mReleaseAdapter.notifyDataSetChanged();
                }else{
                    if(release_p==1){
                        isshowEmpty(true);
                    }
                    AppToastMgr.showToast(bean.getMsg());
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                swipe.setRefreshing(false);
                if(release_p==1){
                    isshowEmpty(true);
                }
            }
        });
    }

}
