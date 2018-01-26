package android.lightningant.order;

import android.os.Bundle;
import android.text.TextUtils;
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
import inter.baisong.bean.MsgAndStatusBean;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by ${刘全伦} on 2017/10/17.
 * 名称:
 */

public class MeGoogleUnbindActivity extends BaseActivity {
    private TextView tv_title,get_mail,unbind;
    private EditText unbind_code,mail_text;
    private ImageView img_left;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.me_unbind_google;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        get_mail = (TextView) findViewById(R.id.get_mail);
        unbind = (TextView) findViewById(R.id.unbind);
        unbind_code= (EditText) findViewById(R.id.unbind_code);
        mail_text= (EditText) findViewById(R.id.mail_text);
        img_left = find(R.id.img_left);
        tv_title.setText(R.string.unbind);
    }

    @Override
    public void initData() {

    }
    private void getMail(){
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_GOOGLE_EMAIL+"2", "Google,email", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    MsgAndStatusBean bean = ConfigYibaisong.jsonToBean(result,MsgAndStatusBean.class);
                    AppToastMgr.showToast(bean.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {

            }
        });
    }
    private void unbind(){
        if(TextUtils.isEmpty(unbind_code.getText().toString()) && TextUtils.isEmpty(mail_text.getText().toString())){
            AppToastMgr.showToast(getResources().getString(R.string.pfitci));
            return;
        }
        Map<String,String> map  =new HashMap<>();
        map.put("googleCode",unbind_code.getText().toString());
        map.put("emailCode",mail_text.getText().toString());
        BaiSongHttpManager.getInstance().postRequest(URLs.ME_GOOGLE_UNBIND, "Google,unbingding", map,new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    MsgAndStatusBean bean = ConfigYibaisong.jsonToBean(result,MsgAndStatusBean.class);
                    ConfigYibaisong.GOOGLE_CODE="";
                    AppToastMgr.showToast(bean.getMsg());
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {

            }
        });
    }
    @Override
    public void setListener() {
        get_mail.setOnClickListener(this);
        unbind.setOnClickListener(this);
        img_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_mail:
                getMail();
                break;
            case R.id.unbind:
                unbind();
                break;
            case R.id.img_left:
                this.finish();
                break;
        }
    }
}
