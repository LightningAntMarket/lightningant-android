package inter.baisong.chat.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.adapter.ChatGroupUserSearchAdapter;
import inter.baisong.chat.bean.ChatGroupUserManageListBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/30.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatUserSearchActivity extends BaseActivity {
    private EditText edit_username;
    private LinearLayout ll_bg;
    private String groupid;
    private RecyclerView mRecyclerView;
    private TextView tv_cancel;
    private List<ChatGroupUserManageListBean.User> mManagersList;
    private List<ChatGroupUserManageListBean.User> mUserList;
    private ChatGroupUserSearchAdapter mAdapter;
    private boolean isLoadMore,isManage;
    private int page;
    @Override
    public void initParms(Bundle parms) {
        try {
            groupid = parms.getString("groupid");
            isManage = parms.getBoolean("isManage",false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_user_search;
    }

    @Override
    public void initView() {
        //如果api大于21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#efeff4"));
        }
        page=1;
        ll_bg = find(R.id.ll_bg);
        edit_username = find(R.id.edit_username);
        mRecyclerView = find(R.id.mRecyclerView);
        tv_cancel = find(R.id.tv_cancel);
        mAdapter = new ChatGroupUserSearchAdapter(this);
        mManagersList=new ArrayList<>();
        mUserList = new ArrayList<>();
    }

    @Override
    public void initData() {
        mAdapter.setValue(groupid,isManage);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        tv_cancel.setOnClickListener(this);
        edit_username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event!=null&&event.getAction() != KeyEvent.ACTION_DOWN){
                    return false;
                }
                if(actionId == EditorInfo.IME_ACTION_SEARCH|| event == null|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    if(TextUtils.isEmpty(edit_username.getText().toString().trim())){
                        AppToastMgr.showToast(AppResourceMgr.getString(ChatUserSearchActivity.this,R.string.toast_login_empty));
                        return true;
                    }else {
                        page=1;
                        ll_bg.setBackgroundColor(Color.WHITE);
                        requestHttp(edit_username.getText().toString().trim());
                    }
                }
                return true;
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    if(mUserList.size()>10&&isLoadMore&&mRecyclerView.canScrollVertically(-1)){
                        page++;
                        requestHttp(edit_username.getText().toString());
                    }
                }
            }
        });
    }

    /***
     * 请求内容
     * @param content
     */
    private void requestHttp(String content) {
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map= new HashMap<>();
        map.put("groupid",groupid);
        map.put("nickname",content);
        map.put("p",page+"");
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_USER_SEARCH, "Groupchat,findUser", map, new BaiSongHttpManager.HttpCallBack() {
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
                            mAdapter.setManagerList(bean.getAdmin());
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
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                this.finish();
                break;
        }

    }
}
