package inter.baisong.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import inter.baisong.R;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.DialogUtils;

/**
 * Created by 于德海 on 2018/1/15.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatInviteUrlShareDialog  extends Dialog implements View.OnClickListener{
    private View view;
    private TextView tv_facebook,tv_weixin,tv_twitter,tv_email;
    private LinearLayout ll_bg;
    private Context mContext;
    private String content;
    public ChatInviteUrlShareDialog(@NonNull Context context,String content) {
        super(context, R.style.mask_dialog);
        mContext =context;
        this.content = content;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_chat_invite_share,null,false);
        setContentView(view);
        tv_facebook = (TextView) view.findViewById(R.id.tv_facebook);
        tv_weixin = (TextView) view.findViewById(R.id.tv_weixin);
        tv_twitter = (TextView) view.findViewById(R.id.tv_twitter);
        tv_email = (TextView) view.findViewById(R.id.tv_email);
        ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);

        tv_facebook.setOnClickListener(this);
        tv_weixin.setOnClickListener(this);
        tv_twitter.setOnClickListener(this);
        tv_email.setOnClickListener(this);
        ll_bg.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_bg:
                dismiss();
                break;
            case R.id.tv_facebook:
                if(!UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.FACEBOOK)){
                    AppToastMgr.showToast(mContext.getResources().getString(R.string.installFaceBook));
                    return;
                }
                showGoDialog(0);
                break;
            case R.id.tv_twitter:
                if(!UMShareAPI.get(mContext).isInstall((Activity) mContext,SHARE_MEDIA.TWITTER)){
                    AppToastMgr.showToast(mContext.getResources().getString(R.string.installTwitter));
                    return;
                }
                showGoDialog(1);
                break;
            case R.id.tv_email:
                showGoDialog(2);

                break;
            case R.id.tv_weixin:
                if(!UMShareAPI.get(mContext).isInstall((Activity) mContext,SHARE_MEDIA.WEIXIN)){
                    AppToastMgr.showToast(mContext.getResources().getString(R.string.installWeiChat));
                    return;
                }
                showGoDialog(3);
                break;

        }
    }

    void  showGoDialog(final int type){
        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("lap copy", content);
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);
        DialogUtils.getInstance().initView(mContext).setBottonNums(DialogUtils.BtnNumTwo)
                .setTitle(AppResourceMgr.getString(mContext,R.string.tip)).setBtnName(AppResourceMgr.getString(mContext,R.string.cancel),"Go")
                .addCallListener(new DialogUtils.CallBack() {
                    @Override
                    public void onSure() {
                        Intent intent;
                        DialogUtils.getInstance().dismiss();
                        switch (type){
                            case 0:
                                intent= mContext.getPackageManager().getLaunchIntentForPackage("facebook");
                                mContext.startActivity(intent);
                                break;
                            case 1:
                                intent= mContext.getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                                mContext.startActivity(intent);
                                break;
                            case 2:
                                Uri uri = Uri.parse("mailto:");
                                intent = new Intent(Intent.ACTION_SENDTO, uri);
                                intent.putExtra(Intent.EXTRA_CC, ""); // 抄送人
                                intent.putExtra(Intent.EXTRA_SUBJECT, "邀请加入"); // 主题
                                intent.putExtra(Intent.EXTRA_TEXT, content); // 正文
                                mContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                                break;
                            case 3:
                                intent= mContext.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                                mContext.startActivity(intent);
                                break;
                        }
                    }

                    @Override
                    public void onCancle() {
                        DialogUtils.getInstance().dismiss();
                    }
                }).show(AppResourceMgr.getString(mContext,R.string.chat_group_invite_dialog_content));
    }
}
