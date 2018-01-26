package android.lightningant.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

import inter.baisong.R;
import inter.baisong.adapter.MeWalletRecordAdapter;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.MeWalletHistoryBean;
import inter.baisong.chat.activity.ChatMoneyRobListActivity;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by ${刘全伦} on 2017/10/26.
 * 名称:
 */

public class MeTransferListActivity extends BaseActivity {
    private RecyclerView mRecycleView;
    private MeWalletRecordAdapter mAdapter;
    private List<MeWalletHistoryBean.MyData> historyData=new ArrayList<>();
    private View foot_view;
    private TextView tv_title;
    private LoadMoreWrapper mLoadMoreWrapper;
    private LinearLayout empty_view;
    private ImageView img_left;
    private ImageView img_right;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.aty_transfer_list;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        img_left= (ImageView) findViewById(R.id.img_left);
        img_right= (ImageView) findViewById(R.id.img_right);
        img_right.setVisibility(View.VISIBLE);
        empty_view= (LinearLayout) findViewById(R.id.empty_view);
        tv_title.setText(getResources().getString(R.string.transationHistory));
        mRecycleView = (RecyclerView)findViewById(R.id.mRecylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(linearLayoutManager);
        mAdapter = new MeWalletRecordAdapter(this,historyData);
        mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
        mLoadMoreWrapper.setLoadMoreView(foot_view);
        foot_view = LayoutInflater.from(this).inflate(R.layout.footer,null);
        mLoadMoreWrapper.setLoadMoreView(foot_view);
        mRecycleView.setAdapter(mLoadMoreWrapper);
        mAdapter.setOnItemClickListener(new MeWalletRecordAdapter.OnItemClickListener() {
            @Override
            public void onitemClick(View v, int position) {
                if(historyData.get(position).getData()==null || historyData.get(position).getData().size()==0||historyData.get(position).getData().size()==1){// 1个参数为手续费
                    startActivity(new Intent(MeTransferListActivity.this,SendMoneyOutDetailActivity.class).putExtra("txid",historyData.get(position).getTxid()));
                }else
                    startActivity(new Intent(MeTransferListActivity.this,SendMoneyDetailActivity.class).putExtra("txid",historyData.get(position).getTxid()));

            }
        });
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(recyclerView.canScrollVertically(-1)){
                        foot_view.setVisibility(View.VISIBLE);
                        getHistory();
                    }
                }
            }
        });
    }
    private  int p=-1;
    private void getHistory(){
        p++;
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_WALLET_HISTORY+"p/"+p, "Block,getLog", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    foot_view.setVisibility(View.INVISIBLE);
                    MeWalletHistoryBean bean = ConfigYibaisong.jsonToBean(result,MeWalletHistoryBean.class);
                    AppToastMgr.showToast(bean.getMsg());
                    if(bean.getStatus()==1){
                        empty_view.setVisibility(View.GONE);
                        mRecycleView.setVisibility(View.VISIBLE);
                        historyData.addAll(bean.getData());
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }else{
                        if(p==0){
                            empty_view.setVisibility(View.VISIBLE);
                            mRecycleView.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {

            }
        });
    }
    @Override
    public void initData() {
        img_right.setImageResource(R.drawable.img_wallet_moeny_icon);
        getHistory();
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        img_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                finish();
                break;
            case R.id.img_right:
                startActivity(new Intent(this, ChatMoneyRobListActivity.class));

                break;
    }
    }
}
