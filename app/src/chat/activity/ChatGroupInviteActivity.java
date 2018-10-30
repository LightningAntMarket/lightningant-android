package inter.baisong.chat.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

import inter.baisong.R;
import inter.baisong.activity.ChatActivity;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.HxUserBean;
import inter.baisong.chat.bean.HxGroupBean;
import inter.baisong.chat.dialog.ChatInviteUrlShareDialog;
import inter.baisong.db.DbManager;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/14.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupInviteActivity extends BaseActivity {
    private ImageView img_left;
    private TextView tv_title,tv_group_inviteurl,tv_copy,tv_share,tv_invite;
    private String groupid;
    private EditText edit_user_uid;
    private DbManager dbManager;
    @Override
    public void initParms(Bundle parms) {
        try {
            groupid = parms.getString("groupid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_group_invite;
    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        tv_copy = find(R.id.tv_copy);
        tv_share = find(R.id.tv_share);
        tv_invite = find(R.id.tv_invite);
        edit_user_uid = find(R.id.edit_user_uid);
        tv_title = find(R.id.tv_title);
        tv_group_inviteurl = find(R.id.tv_group_inviteurl);
        dbManager = new DbManager(this);
    }

    @Override
    public void initData() {
        tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_url));
        tv_group_inviteurl.setText(ConfigYibaisong.SERVICE_URL_GOOGLE+"Home/Share/group/domain/"+ConfigYibaisong.APP_TYPE+"/groupid/"+groupid);
    }




    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        tv_copy.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_invite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.tv_invite:
                 if(TextUtils.isEmpty(edit_user_uid.getText().toString().trim())){
                     AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_noempty));
                     return;
                 }
                 requestUserInfo();
                 break;
             case R.id.img_left:
                 this.finish();
                 break;
             case R.id.tv_copy:
                 ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                 //创建ClipData对象
                 ClipData clipData = ClipData.newPlainText("lap copy", tv_group_inviteurl.getText().toString());
                 //添加ClipData对象到剪切板中
                 clipboardManager.setPrimaryClip(clipData);
                 AppToastMgr.showToast("Success");
                 break;
             case R.id.tv_share:
                 ChatInviteUrlShareDialog dialog = new ChatInviteUrlShareDialog(this,tv_group_inviteurl.getText().toString());
                 dialog.show();
                 break;
         }
    }
    /***
     * 请求用户信息
     *
     */
    private void requestUserInfo() {
        AppToastMgr.getInstances().showLoading(this);
        BaiSongHttpManager.getInstance().getRequest(URLs.UID_INFO + edit_user_uid.getText().toString().trim(), new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
                        JSONObject bean = json.optJSONObject("data");
                        HxUserBean data = new HxUserBean();
                        data.setUid(edit_user_uid.getText().toString().trim());
                        data.setFace(bean.optString("face"));
                        data.setName(bean.optString("nickname"));
                        data.setEmail(bean.optString("email"));
                        dbManager.addHxUser(data);
                        HxGroupBean groupBean = dbManager.findHxGroupInfo(groupid);
                        Intent intent = new Intent(ChatGroupInviteActivity.this, ChatActivity.class);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID,edit_user_uid.getText().toString().trim());
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                        intent.putExtra("group_id",groupid);
                        intent.putExtra("type",3);
                        intent.putExtra("group_name",groupBean.getGroup_name());
                        intent.putExtra("group_url",groupBean.getImg_url());
                        if(ChatActivity.activityInstance!=null){
                            ChatActivity.activityInstance.finish();
                        }
                        startActivity(intent);
                    }else{
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
}
