package inter.baisong.chat.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.adapter.ChatGroupUserManageAdapter;
import inter.baisong.chat.bean.ChatGroupUserManageListBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/14.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description  管理员设置
 */

public class ChatGroupManagerSetActivity extends BaseActivity {
    private int page ;
    private ImageView img_left;
    private TextView tv_title,tv_right;
    private EditText edit_search;
    private RecyclerView mRecyclerView;
    private ChatGroupUserManageAdapter mAdapter;
    private List<ChatGroupUserManageListBean.User> mManagersList;
    private List<ChatGroupUserManageListBean.User> mUserList;
    private String groupid;
    private boolean isLoadMore;
    private LinearLayout ll_search;
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
        return R.layout.aty_chat_group_manager_set;

    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        tv_title = find(R.id.tv_title);
        tv_right = find(R.id.tv_right);
        edit_search = find(R.id.edit_search);
        mRecyclerView = find(R.id.mRecyclerView);
        ll_search = find(R.id.ll_search);
        mAdapter = new ChatGroupUserManageAdapter(this);
        mAdapter.setNumView(tv_right);
        mManagersList = new ArrayList<>();
        mUserList = new ArrayList<>();
        page=1;
        isLoadMore = true;
        ll_search.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_manager_set));
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(AppResourceMgr.getString(this,R.string.chat_group_operation_sure));
        tv_right.setTextColor(AppResourceMgr.getColor(this,R.color.app_color_theme));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
        requestHttp();
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
                        mManagersList.addAll(bean.getAdmin());
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

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
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
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event!=null&&event.getAction() != KeyEvent.ACTION_DOWN){
                    return false;
                }
                if(actionId == EditorInfo.IME_ACTION_SEARCH|| event == null|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    if(TextUtils.isEmpty(edit_search.getText().toString().trim())){
                        AppToastMgr.showToast(AppResourceMgr.getString(ChatGroupManagerSetActivity.this,R.string.toast_login_empty));
                        return true;
                    }else {
                        page=1;
                        searchUser(edit_search.getText().toString().trim());
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.tv_right:
                List<String> managers=mAdapter.getNewManagerList();
                if(managers.size()==0){
                    return;
                }
                setGroupManager(managers);
                break;
        }
    }

    /***
     * 请求内容
     * @param content
     */
    private void searchUser(String content) {
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


    private void setGroupManager(List<String> managers){
        AppToastMgr.getInstances().showLoading(this);
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<managers.size();i++){
            sb.append(managers.get(i));
            if(i!=managers.size()-1){
                sb.append(",");
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("groupid",groupid);
        map.put("hxid",sb.toString());
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_MANAGERS_ADD, "Groupchat,addadmin", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    AppToastMgr.getInstances().cancel();
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
                        ChatGroupManagerSetActivity.this.finish();
                        AppToastMgr.showToast(json.optString("msg"));
                    }else {
                        AppToastMgr.showToast(json.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                AppToastMgr.showError();
            }
        });

    }
}
