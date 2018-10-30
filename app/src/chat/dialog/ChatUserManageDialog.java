package inter.baisong.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import inter.baisong.R;
import inter.baisong.activity.PersonalInfoActivity;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaisongUtils;

/**
 * Created by 于德海 on 2018/1/15.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatUserManageDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private View view;
    private TextView tv_remove_user,tv_chat,tv_see_user,tv_cancel;
    private String chat_id,groupid;
    private LinearLayout ll_bg;
    private int position;
    private String chatname;

    public interface CallBack{
        void removeUser(int position);
    }

    /****
     *
     * @param context
     * @param chat_id
     * @param groupid
     * @param isShowRemove
     * @param position  选中用户索引
     */
    public ChatUserManageDialog(@NonNull Context context,String chat_id,String groupid,boolean isShowRemove,int position) {
        super(context, R.style.mask_dialog);
        mContext = context;
        this.chat_id = chat_id;
        this.position = position;
        this.groupid = groupid;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_chat_gorup_usermanage,null,false);
        setContentView(view);
        tv_remove_user = (TextView) view.findViewById(R.id.tv_remove_user);
        tv_chat = (TextView) view.findViewById(R.id.tv_chat);
        ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
        tv_see_user = (TextView) view.findViewById(R.id.tv_see_user);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        if(isShowRemove){
            tv_remove_user.setVisibility(View.VISIBLE);
        }else {
            tv_remove_user.setVisibility(View.GONE);
        }
        tv_remove_user.setOnClickListener(this);
        tv_chat.setOnClickListener(this);
        tv_see_user.setOnClickListener(this);
        ll_bg.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        dialogWindow.setAttributes(lp);
    }
    
    public void setChatName(String name){
        this.chatname = name;
    }
    private CallBack listener;
    public void setListener(CallBack listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_remove_user:
                AppToastMgr.getInstances().showLoading(mContext);
                EMClient.getInstance().groupManager().asyncRemoveUserFromGroup(groupid, chat_id, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        AppToastMgr.getInstances().cancel();
                        listener.removeUser(position);
                        dismiss();
                    }

                    @Override
                    public void onError(int i, String s) {
                        AppToastMgr.getInstances().cancel();
                        AppToastMgr.showToast(s);
                        dismiss();
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });

                break;
            case R.id.tv_chat:
                BaisongUtils.goSingleChat(chat_id,mContext,chatname);
                dismiss();
                break;
            case R.id.tv_see_user:
                BaisongUtils.getInstances().goUserDetailActivity(mContext,chat_id);
                dismiss();
                break;
            case R.id.ll_bg:
                dismiss();
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
