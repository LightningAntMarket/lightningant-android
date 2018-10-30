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
import inter.baisong.chat.adapter.ChatGroupManagersAdapter;
import inter.baisong.chat.bean.ChatGroupManagerListBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description  管理员列表
 */

public class ChatGroupManagersListActivity extends BaseActivity {
    private ImageView img_left;
    private TextView tv_title,tv_add_managers;
    private String groupid;
    private List<ChatGroupManagerListBean.User> mList;
    private RecyclerView mRecyclerView;
    private ChatGroupManagersAdapter mAdapter;
    @Override
    public void initParms(Bundle parms) {
        try {
            groupid = parms.getString("groupid","null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_groupmanagerslist;
    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        tv_title = find(R.id.tv_title);
        tv_add_managers = find(R.id.tv_add_managers);
        mRecyclerView = find(R.id.mRecyclerview);
        mList = new ArrayList<>();
        mAdapter = new ChatGroupManagersAdapter(this);

    }

    @Override
    public void initData() {
        mAdapter.setGroupid(groupid);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
        tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_manager));
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestHttp();
    }

    /***
     * 管理员列表
     */
    private void requestHttp() {
        BaiSongHttpManager.getInstance().getRequest(URLs.CHAT_GROUP_MANAGERS_LIST + "/groupid/" + groupid, "Groupchat,adminList", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                ChatGroupManagerListBean bean = ConfigYibaisong.jsonToBean(result,ChatGroupManagerListBean.class);
                if(bean==null)
                    return;
                if(bean.getStatus()==1){
                    mList.clear();
                    mList.addAll(bean.getData());
                    mAdapter.setData(mList);
                    mAdapter.notifyDataSetChanged();
                }else {
                    AppToastMgr.showToast(bean.getMsg());
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
            }
        });
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        tv_add_managers.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.tv_add_managers:
                intent = new Intent(this,ChatGroupManagerSetActivity.class);
                intent.putExtra("groupid",groupid);
                startActivity(intent);
                break;
        }
    }
}
