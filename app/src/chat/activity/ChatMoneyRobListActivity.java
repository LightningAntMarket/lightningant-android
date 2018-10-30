package inter.baisong.chat.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.adapter.ChatMoneyRobListAdapter;
import inter.baisong.chat.bean.ChatMoneyRobListBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/19.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatMoneyRobListActivity extends BaseActivity {
    private TextView tv_title;
    private ImageView img_left;
    private RecyclerView mRecyclerView;
    private ChatMoneyRobListAdapter mAdapter;
    private LinearLayout empty_view;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_money_roblist;
    }

    @Override
    public void initView() {
        tv_title = find(R.id.tv_title);
        img_left= find(R.id.img_left);
        empty_view= find(R.id.empty_view);
        mRecyclerView = find(R.id.mRecyclerView);
        mAdapter = new ChatMoneyRobListAdapter(this);
    }

    @Override
    public void initData() {
        tv_title.setText(AppResourceMgr.getString(this,R.string.chat_money_roblist_title));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
        requestHttp();
    }

    /****
     * 红包记录
     */
    private void requestHttp() {
        AppToastMgr.getInstances().showLoading(this);
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_MONEY_LIST, "RedPacket,log",null, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                ChatMoneyRobListBean bean = ConfigYibaisong.jsonToBean(result,ChatMoneyRobListBean.class);
                if(bean==null)
                    return;
                if(bean.getStatus()==1){
                    mAdapter.setList(bean.getData());
                    mAdapter.notifyDataSetChanged();
                }else {
                    empty_view.setVisibility(View.VISIBLE);
                    AppToastMgr.showToast(bean.getMsg());
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                AppToastMgr.showError();
            }
        });
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
        }
    }
}
