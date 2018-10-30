package inter.baisong.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.adapter.ChatGroupUserListAdapter;
import inter.baisong.chat.bean.ChatGroupUserManageListBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/14.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupUserListActivity extends BaseActivity {
    private int page ;
    private ImageView img_left,img_right;
    private RelativeLayout rl_search;
    private TextView tv_title;
    private RecyclerView mRecyclerView;
    private List<ChatGroupUserManageListBean.User> mManagersList;
    private List<ChatGroupUserManageListBean.User> mUserList;
    private ChatGroupUserListAdapter mAdapter;
    private String groupid;
    private boolean isLoadMore;
    private boolean isManage;
    @Override
    public void initParms(Bundle parms) {
        try {
            groupid = parms.getString("groupid","null");
            isManage = parms.getBoolean("isManage",false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_group_manager_set;
    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        img_right = find(R.id.img_right);
        rl_search = find(R.id.rl_search);
        tv_title = find(R.id.tv_title);
        mRecyclerView = find(R.id.mRecyclerView);
        mManagersList = new ArrayList<>();
        mUserList = new ArrayList<>();
        mAdapter = new ChatGroupUserListAdapter(this);
        page=1;
        isLoadMore = true;
        rl_search.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        mAdapter.setValue(groupid,isManage);
        img_right.setVisibility(View.VISIBLE);
        img_right.setImageResource(R.drawable.chat_group_user_add);
        tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_user));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
        requestHttp();
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        img_right.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(mRecyclerView.canScrollVertically(-1)&&isLoadMore){
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
            case R.id.rl_search:
                startActivity(new Intent(this,ChatUserSearchActivity.class)
                        .putExtra("isManage",isManage).putExtra("groupid",groupid));
                break;
            case R.id.img_left:
                this.finish();
                break;
            case R.id.img_right:
                intent = new Intent(this,ChatGroupInviteActivity.class);
                intent.putExtra("groupid",groupid);
                startActivity(intent);
                break;
        }
    }
    /***
     * 请求
     */
    private void requestHttp() {
        AppToastMgr.getInstances().showLoading(this);
        BaiSongHttpManager.getInstance().getRequest(URLs.CHAT_GROUP_USER_LIST+"groupid/"+groupid+"/p/"+page, "Groupchat,userlist", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                ChatGroupUserManageListBean bean = ConfigYibaisong.jsonToBean(result,ChatGroupUserManageListBean.class);
                if(bean==null)
                    return;
                if(bean.getStatus()==1){
                    if(page==1){
                        mManagersList.clear();
                        mUserList.clear();
                        if(bean.getAdmin()!=null){
                            mManagersList.addAll(bean.getAdmin());
                            mAdapter.setManager_nums(bean.getAdmin().size());
                            mUserList.addAll(bean.getAdmin());
                        }
                    }
                    mUserList.addAll(bean.getData());
                    mAdapter.setData(mUserList);
                    mAdapter.notifyDataSetChanged();
                }else {
                    isLoadMore = false;
                    AppToastMgr.showToast(bean.getMsg());

                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                isLoadMore = false;
            }
        });
    }
}
