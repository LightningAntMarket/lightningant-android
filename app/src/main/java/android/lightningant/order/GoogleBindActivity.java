package android.lightningant.order;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.GoogleSkBean;
import inter.baisong.bean.MsgAndStatusBean;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by ${刘全伦} on 2017/10/16.
 * 名称:
 */

public class MeGoogleBindActivity extends BaseActivity{
    private TextView tv_title,secrect_text,copy_secrect,bind,copy_mail;
    private String sk,mailCode,googleCode;
    private EditText bind_text,mail_text;
    private ImageView img_left;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.me_google_activity;
    }

    @Override
    public void initView() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        copy_secrect= (TextView) findViewById(R.id.copy_secrect);
        secrect_text= (TextView) findViewById(R.id.secrect_text);
        copy_mail= (TextView) findViewById(R.id.copy_mail);
        bind= (TextView) findViewById(R.id.bind);
        bind_text= (EditText) findViewById(R.id.bind_text);
        mail_text= (EditText) findViewById(R.id.mail_text);
        tv_title.setText(R.string.googleAuth);
        img_left = find(R.id.img_left);

    }

    @Override
    public void initData() {
        AppToastMgr.getInstances().showUncancelLoading(this);
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_GOOGLE_SK, "Google,getcode", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    GoogleSkBean bean = ConfigYibaisong.jsonToBean(result,GoogleSkBean.class);
                    sk= bean.getCode();

                    secrect_text.setText(bean.getCode());
                } catch (Exception e) {
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
        copy_secrect.setOnClickListener(this);
        bind.setOnClickListener(this);
        copy_mail.setOnClickListener(this);
        img_left.setOnClickListener(this);
    }

    private void getMail(){
        AppToastMgr.getInstances().showLoading(this);
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_GOOGLE_EMAIL+"1", "Google,email", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    MsgAndStatusBean bean = ConfigYibaisong.jsonToBean(result,MsgAndStatusBean.class);
                    AppToastMgr.showToast(bean.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
            }
        });
    }
    private CountDownTimer timer;
    private int count=60;
    private void countTime(){
        copy_mail.setEnabled(false);
        if (timer == null) {
            timer = new CountDownTimer(1000 * 60, 1000) {
                @SuppressWarnings("deprecation")
                public void onTick(long millisUntilFinished) {
                    count--;
                    copy_mail.setText( count+"s");
                    copy_mail.setEnabled(false);
                    copy_mail.setTextColor(getResources().getColor(R.color.a7a7a7));
                }

                @SuppressWarnings("deprecation")
                public void onFinish() {
                    count=60;
                    copy_mail.setText("Get");
                    copy_mail.setEnabled(true);
                    copy_mail.setTextColor(getResources().getColor(R.color._337fdd));
                }

            };
        }
        timer.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timer) {
            timer.cancel();
        }
    }
    private void bindInfo(){
        if(TextUtils.isEmpty(mail_text.getText().toString())||TextUtils.isEmpty(bind_text.getText().toString()) ){
            AppToastMgr.showToast(getResources().getString(R.string.pfitci));
            return;
        }
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map =new HashMap<>();
        map.put("key",sk);
        map.put("emailCode",mail_text.getText().toString());
        map.put("code",bind_text.getText().toString());
        BaiSongHttpManager.getInstance().postRequest(URLs.ME_GOOGLE_BIND, "Google,bingding",map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                MsgAndStatusBean bean = ConfigYibaisong.jsonToBean(result,MsgAndStatusBean.class);
                AppToastMgr.showToast(bean.getMsg());
                if(bean.getStatus()==1){
                    ConfigYibaisong.GOOGLE_CODE="1";
                    finish();
                }
                Log.e("sss",bean.getMsg());
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
            case R.id.copy_secrect:
                if(!TextUtils.isEmpty(sk)){
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(sk);
                    AppToastMgr.showToast(getResources().getString(R.string.tchbc));
                }
//                startActivity(new Intent(this,MeGoogleCheckActivity.class));
                break;
            case R.id.bind:
                bindInfo();
                break;
            case R.id.copy_mail:
                if(count==60){
                    getMail();
                    countTime();
                }

                break;
            case R.id.img_left:
                this.finish();
                break;
        }
    }
}
