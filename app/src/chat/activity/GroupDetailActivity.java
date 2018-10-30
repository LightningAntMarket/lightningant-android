package inter.baisong.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
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
import inter.baisong.activity.ReportActivity;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.bean.ChatGroupDetailBean;
import inter.baisong.chat.bean.HxGroupBean;
import inter.baisong.db.DbManager;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppLogMessageMgr;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppSysDateMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.DialogUtils;
import inter.baisong.utils.GalleryPicUtils;
import inter.baisong.utils.OSSUtil;
import inter.baisong.widgets.CircleImageView;
import inter.baisong.widgets.SwitchButton;

/**
 * Created by 于德海 on 2018/1/10.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class GroupDetailActivity extends BaseActivity {
    private ImageView img_back;
    private RelativeLayout rl_group_gonggao,rl_group_miaoshu;
    private CircleImageView img_group;
    private TextView tv_edit,tv_group_name,tv_group_intro,tv_group_notice,tv_group_chengyuan,tv_message_clear,tv_group_delete,tv_group_jubao;
    private SwitchButton switch_top,switch_sound;
    private String groupid;
    private ChatGroupDetailBean.Data mData;
    private RelativeLayout rl_managers,rl_group_user,rl_invite,rl_group_name;
    private boolean isOwner;//是否群主
    private boolean isManager;//是否是管理员
    private HxGroupBean hxGroupBean;
    private DbManager dbManager;
    private boolean isChangeFace,isFirst;
    @Override
    public void initParms(Bundle parms) {
        try{
            groupid = parms.getString("group_id");
        }catch (Exception e){

        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_groupdetail;
    }

    @Override
    public void initView() {
        isFirst=true;
        img_back = find(R.id.img_back);
        rl_group_gonggao = find(R.id.rl_group_gonggao);
        rl_group_miaoshu = find(R.id.rl_group_miaoshu);
        img_group = find(R.id.img_group);
        //TextView
        tv_edit = find(R.id.tv_edit);
        tv_group_name = find(R.id.tv_group_name);
        tv_group_intro = find(R.id.tv_group_intro);
        tv_group_notice = find(R.id.tv_group_notice);
        tv_group_chengyuan = find(R.id.tv_group_chengyuan);
        tv_message_clear = find(R.id.tv_message_clear);
        tv_group_delete = find(R.id.tv_group_delete);
        tv_group_jubao = find(R.id.tv_group_jubao);
        //SwitchButton
        switch_top = find(R.id.switch_top);
        switch_sound = find(R.id.switch_sound);
        //Relative
        rl_managers = find(R.id.rl_managers);
        rl_group_user = find(R.id.rl_group_user);
        rl_invite = find(R.id.rl_invite);
        rl_group_name = find(R.id.rl_group_name);
        isManager=isOwner=false;
        dbManager = new DbManager(this);
    }

    @Override
    public void initData() {
        hxGroupBean = dbManager.findHxGroupInfo(groupid);
        if(hxGroupBean!=null){
            if(hxGroupBean.getIstop()==1){
                switch_top.setChecked(true);
            }
            if(hxGroupBean.getIsMute()==1){
                switch_sound.setChecked(true);
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isChangeFace){
            requestHttp();
        }
    }

    /***
     * 群资料详情
     */
    private void requestHttp() {
        if(isFirst){

            AppToastMgr.getInstances().showLoading(this);
        }
        BaiSongHttpManager.getInstance().getRequest(URLs.CHAT_GROUP_DETAIL+"/groupid/"+groupid, "Groupchat, groupsdetails", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                ChatGroupDetailBean bean = ConfigYibaisong.jsonToBean(result,ChatGroupDetailBean.class);
                if(bean==null)
                    return;
                if(bean.getStatus()==1){

                    mData = bean.getData();
                    if(mData==null){
                        return;
                    }
                    if(mData.getOwner().equals(ConfigYibaisong.UID)){
                        isOwner = true;
                    }
                    checkIsManager(bean.getAdmin());
                    if(isOwner||isManager){
                        tv_edit.setVisibility(View.VISIBLE);
                    }else {
                        tv_edit.setVisibility(View.GONE);
                    }
                    hxGroupBean.setGroup_name(mData.getName());
                    hxGroupBean.setImg_url(mData.getGroupface());
                    dbManager.updateHxGroupInfo(hxGroupBean);
                    tv_group_name.setText(mData.getName());
                    tv_group_intro.setText(mData.getDescription());
                    tv_group_notice.setText(mData.getNotice());
                    tv_group_chengyuan.setText(AppResourceMgr.getString(GroupDetailActivity.this,R.string.chat_group_accounts)+" "+mData.getAffiliations_count()+"/"+mData.getMaxusers());
                    if(isFirst){
                        AppImageLoadHttp.showImageView(GroupDetailActivity.this,mData.getGroupface(),img_group);
                        isFirst=false;
                    }
                }else {

                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                AppToastMgr.showError();
            }
        });
    }

    /***
     * 检测是否是群管理
     * @param admin
     */
    private void checkIsManager(List<ChatGroupDetailBean.User> admin) {
        if(admin==null)
            return;
        for (int i=0;i<admin.size();i++){
            if(ConfigYibaisong.UID.equals(admin.get(i).getMember())){
                isManager = true;
                break;
            }
        }
    }

    @Override
    public void setListener() {
        img_back.setOnClickListener(this);
        rl_group_miaoshu.setOnClickListener(this);
        rl_group_gonggao.setOnClickListener(this);
        rl_managers.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        rl_group_user.setOnClickListener(this);
        tv_group_jubao.setOnClickListener(this);
        tv_group_delete.setOnClickListener(this);
        img_group.setOnClickListener(this);
        tv_message_clear.setOnClickListener(this);
        rl_invite.setOnClickListener(this);
        rl_group_name.setOnClickListener(this);
        switch_top.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(hxGroupBean!=null){
                    if(isChecked){
                        hxGroupBean.setIstop(1);
                    }else {
                        hxGroupBean.setIstop(0);
                    }
                    dbManager.updateHxGroupInfo(hxGroupBean);
                }
            }
        });
        switch_sound.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(hxGroupBean!=null){
                    if(isChecked){
                        hxGroupBean.setIsMute(1);
                    }else {
                        hxGroupBean.setIsMute(0);
                    }
                    dbManager.updateHxGroupInfo(hxGroupBean);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.img_back){
            this.finish();
        }
        if(mData==null){
            AppToastMgr.showToast("Error");
            return;
        }
        switch (v.getId()) {
            case R.id.tv_message_clear:
                emptyHistory();
                break;
            case R.id.tv_edit:
            case R.id.img_group://更换群头像
                if(isOwner||isManager){
                    isChangeFace =  true;
                    GalleryConfig config = GalleryPicUtils.startPicforOneCrop(pic_callback);
                    GalleryPick.getInstance().setGalleryConfig(config).open(this);
                }else {
                    return;
                }
                break;
            case R.id.tv_group_delete://删除退出群
                DialogUtils.getInstance().initView(this).setBottonNums(DialogUtils.BtnNumTwo).setTitle(AppResourceMgr.getString(this,R.string.tip))
                        .addCallListener(new DialogUtils.CallBack() {
                            @Override
                            public void onSure() {
                                exitGroup();
                            }

                            @Override
                            public void onCancle() {
                                DialogUtils.getInstance().dismiss();
                            }
                        }).show(AppResourceMgr.getString(this,R.string.chat_group_dialog_exit_group));
                break;
            case R.id.rl_group_name:
                if(isManager){
                    intent = new Intent(this,ChatGroupDetailEditActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("content", mData.getName());
                    intent.putExtra("groupid",groupid);
                    startActivity(intent);
                }else {
                    AppToastMgr.showToast(AppResourceMgr.getString(GroupDetailActivity.this,R.string.chat_group_no_permission));
                }
                break;
            case R.id.rl_group_gonggao:
                if (isManager) {
                    intent = new Intent(this, ChatGroupDetailEditActivity.class);
                } else {
                    intent = new Intent(this, ChatGroupNotiSeeActivity.class);
                    intent.putExtra("name", mData.getName());
                    intent.putExtra("img", mData.getGroupface());
                    intent.putExtra("time",mData.getNoticetime());
                }
                intent.putExtra("groupid",groupid);
                intent.putExtra("type", 1);
                intent.putExtra("content", mData.getNotice());
                startActivity(intent);
                break;
            case R.id.rl_group_miaoshu:
                if(isManager){
                    intent = new Intent(this, ChatGroupDetailEditActivity.class);
                }else {
                    intent = new Intent(this, ChatGroupNotiSeeActivity.class);
                    intent.putExtra("name", mData.getName());
                    intent.putExtra("img", mData.getGroupface());
                    intent.putExtra("time",mData.getDesctime());
                }
                intent.putExtra("type", 0);
                intent.putExtra("groupid",groupid);
                intent.putExtra("content", mData.getDescription());
                startActivity(intent);
                break;
            case R.id.rl_managers:
                if(isOwner){
                    intent = new Intent(this,ChatGroupManagersListActivity.class);
                    intent.putExtra("groupid",groupid);
                    startActivity(intent);
                }else {
                    AppToastMgr.showToast(AppResourceMgr.getString(GroupDetailActivity.this,R.string.chat_group_no_permission));
                }

                break;
            case R.id.rl_group_user:
                intent = new Intent(this,ChatGroupUserListActivity.class);
                intent.putExtra("groupid",groupid);
                intent.putExtra("isManage",isManager);
                startActivity(intent);
                break;
            case R.id.rl_invite:
                intent = new Intent(this,ChatGroupInviteActivity.class);
                intent.putExtra("groupid",groupid);
                startActivity(intent);
                break;
            case R.id.tv_group_jubao:
                intent = new Intent(this, ReportActivity.class);
                intent.putExtra("groupid",groupid);
                intent.putExtra("tyoe",1);
                startActivity(intent);
                break;
        }
    }

    /***
     * 更换群头像
     */
    private void changeGroupFace(String url) {
        final String file_path = "Android/group_img/"+ConfigYibaisong.UID+"_"+ AppSysDateMgr.getNowTime()+".png";
        AppToastMgr.getInstances().showUncancelLoading(this);
        OSSUtil.getInstance(this).asyncPutObjectFromLocalFile(this,url, file_path, new OSSUtil.CallBack() {
            @Override
            public void next(boolean isSuccess) {
                if(isSuccess){
                    isChangeFace = false;
                    hxGroupBean.setImg_url(OSSUtil.OSS_BASEURL+ file_path);
                    Map<String,String> map = new HashMap<>();
                    map.put("groupid",groupid);
                    map.put("groupface",hxGroupBean.getImg_url());
                    dbManager.updateHxGroupInfo(hxGroupBean);
                    BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_INFO_EDIT,"Groupchat,upGroups", map, new BaiSongHttpManager.HttpCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            AppToastMgr.getInstances().cancel();
                            try {
                                JSONObject json = new JSONObject(result);
                                if(json.optInt("status")==1){
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
                }else{
                    AppToastMgr.getInstances().cancel();
                    AppLogMessageMgr.e("upload error  face ");
                }
            }
        });
    }


    /***
     * 退出群聊
     */
    private void exitGroup(){
        AppToastMgr.getInstances().showLoading(this);
        if(isOwner){
            EMClient.getInstance().groupManager().asyncDestroyGroup(groupid, new EMCallBack() {
                @Override
                public void onSuccess() {
                    AppToastMgr.getInstances().cancel();
                    DialogUtils.getInstance().dismiss();
                    GroupDetailActivity.this.finish();
                    ChatActivity.activityInstance.finish();
                }

                @Override
                public void onError(int i, String s) {
                    AppToastMgr.getInstances().cancel();
                    AppToastMgr.showToast(s);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }else {
            EMClient.getInstance().groupManager().asyncLeaveGroup(groupid, new EMCallBack() {
                @Override
                public void onSuccess() {
                    AppToastMgr.getInstances().cancel();
                    DialogUtils.getInstance().dismiss();
                    GroupDetailActivity.this.finish();
                    ChatActivity.activityInstance.finish();
                }

                @Override
                public void onError(int i, String s) {
                    AppToastMgr.getInstances().cancel();
                    AppToastMgr.showToast(s);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

    /**
     * 清空聊天几率
     *
     */
    protected void emptyHistory() {
        String msg = getResources().getString(com.hyphenate.easeui.R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(this,null, msg, null,new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if(confirmed){
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupid, EaseCommonUtils.getConversationType(1), true);
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                }
            }
        }, true).show();
    }

    private IHandlerCallBack pic_callback = new IHandlerCallBack() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(final List<String> photoList) {
            if(photoList!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppImageLoadHttp.showImageView(GroupDetailActivity.this,photoList.get(0),img_group);
                        changeGroupFace(photoList.get(0));
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
