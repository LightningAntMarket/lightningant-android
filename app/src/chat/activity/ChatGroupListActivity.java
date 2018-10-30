package inter.baisong.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.adapter.ChatGroupListAdapter;
import inter.baisong.chat.bean.CahtGroupListBean;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/10.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupListActivity extends BaseActivity {
    private TextView tv_create_group;
    private RecyclerView mRecycler;
    private ImageView img_back;
    private ChatGroupListAdapter mAdapter;
    private List<CahtGroupListBean.ChatGroupInfo> list;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_group_list;
    }

    @Override
    public void initView() {
        img_back = find(R.id.img_back);
        mRecycler=find(R.id.recycler);
        tv_create_group=find(R.id.tv_create_group);
        mAdapter = new ChatGroupListAdapter(this);
        list = new ArrayList<>();
    }

    @Override
    public void initData() {
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        tv_create_group.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestHttp();
    }

    /***
     * 群组列表
     */
    private void requestHttp() {
        BaiSongHttpManager.getInstance().getRequest(URLs.CHAT_GROUP_LIST, "Groupchat,groupslist", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                CahtGroupListBean bean = ConfigYibaisong.jsonToBean(result,CahtGroupListBean.class);
                if(bean==null)
                    return;
                if(bean.getStatus()==1){
                    list.clear();
                    list.addAll(bean.getData());
//                    mAdapter.setList(list);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                this.finish();
                break;
            case R.id.tv_create_group:
                startActivity(new Intent(this,ChatGroupCreateActivity.class).putExtra("type",0));
                break;
        }
    }
}
