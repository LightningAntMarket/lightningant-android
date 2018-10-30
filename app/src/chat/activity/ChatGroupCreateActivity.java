package inter.baisong.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.activity.ChatActivity;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.bean.HxGroupBean;
import inter.baisong.db.DbManager;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppLogMessageMgr;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppSysDateMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.GalleryPicUtils;
import inter.baisong.utils.OSSUtil;
import inter.baisong.widgets.CircleImageView;

/**
 * Created by 于德海 on 2018/1/10.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupCreateActivity extends BaseActivity {
    private ImageView img_back;
    private EditText edit_name;
    private TextView tv_title,tv_create;
    private CircleImageView img_group;
    private String face,groupid;
    private int type; //0创建群  1加入群
    private DbManager dbManager;
    @Override
    public void initParms(Bundle parms) {
        try {
            type = parms.getInt("type",0);
            if(type==1){
                groupid = parms.getString("groupid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_creategroup;
    }

    @Override
    public void initView() {
        img_back = find(R.id.img_back);
        edit_name = find(R.id.edit_name);
        tv_title = find(R.id.tv_title);
        tv_create = find(R.id.tv_create);
        img_group = find(R.id.img_group);
        dbManager = new DbManager(this);
    }

    @Override
    public void initData() {
        if(type==1){
            requestHttp();
            tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_join));
            edit_name.setEnabled(false);
            img_group.setEnabled(false);
            tv_create.setText(AppResourceMgr.getString(this,R.string.chat_group_join));
        }else {
            tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_create_title));
        }
    }


    private void requestHttp() {
        AppToastMgr.getInstances().showLoading(this);
        BaiSongHttpManager.getInstance().getRequest(URLs.CHAT_GROUP_IMG + groupid, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    HxGroupBean bean = dbManager.findHxGroupInfo(groupid);
                    if(bean==null){
                        bean = new HxGroupBean();
                        bean.setGroupid(groupid);
                    }
                    bean.setImg_url(json.optString("groupface"));
                    bean.setGroup_name(json.optString("groupname"));
                    edit_name.setText(json.optString("groupname"));
                    AppImageLoadHttp.showImageView(ChatGroupCreateActivity.this,json.optString("groupface"),img_group);
                    dbManager.addHxGroupInfo(bean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
            }
        });
    }

    @Override
    public void setListener() {
        img_group.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_create.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                this.finish();
                break;
            case R.id.tv_create:
                if(type==1){
                    joinChatGroup();
                }else {
                    if(TextUtils.isEmpty(face)||TextUtils.isEmpty(edit_name.getText().toString().trim())){
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_login_empty));
                        return;
                    }
                    createGroup();
                }


                break;
            case R.id.img_group:
                GalleryConfig config = GalleryPicUtils.startPicforOneCrop(iHandlerCallBack);
                GalleryPick.getInstance().setGalleryConfig(config).open(this);
                break;
        }
    }

    /*****
     * 加入群聊
     */
    private void joinChatGroup() {
        AppToastMgr.getInstances().showLoading(this);
        EMClient.getInstance().groupManager().asyncJoinGroup(groupid, new EMCallBack() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(ChatGroupCreateActivity.this,ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,groupid);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppToastMgr.getInstances().cancel();
                    }
                });
                ChatGroupCreateActivity.this.startActivity(intent);
                ChatGroupCreateActivity.this.finish();
            }

            @Override
            public void onError(int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppToastMgr.getInstances().cancel();
                        ChatGroupCreateActivity.this.finish();
                    }
                });

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /***
     * 创建群聊
     */
    private void createGroup() {
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("groupface",face);
        map.put("groupname",edit_name.getText().toString());
        map.put("hxid", ConfigYibaisong.UID);
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_CREATE, "Groupchat,createGroups", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json =new JSONObject(result);
                    if(json.optInt("status")==1){
                        ChatGroupCreateActivity.this.finish();
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
            }
        });
    }

    IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(final List<String> photoList) {
            if(photoList!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppImageLoadHttp.showImageView(ChatGroupCreateActivity.this,photoList.get(0),img_group);
                        final String file_path = "Android/group_img/"+ConfigYibaisong.UID+"_"+ AppSysDateMgr.getNowTime()+".png";
                        AppToastMgr.getInstances().showUncancelLoading(ChatGroupCreateActivity.this);
                        OSSUtil.getInstance(ChatGroupCreateActivity.this).asyncPutObjectFromLocalFile(ChatGroupCreateActivity.this,photoList.get(0), file_path, new OSSUtil.CallBack() {
                            @Override
                            public void next(boolean isSuccess) {
                                AppToastMgr.getInstances().cancel();
                                if(isSuccess){
                                    face =OSSUtil.OSS_BASEURL+ file_path;
                                }else{
                                    AppLogMessageMgr.e("upload error  face ");
                                }
                            }
                        });



                    }
                });
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError() {

        }
    };
}
