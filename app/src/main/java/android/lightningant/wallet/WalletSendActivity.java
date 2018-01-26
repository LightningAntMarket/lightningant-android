package android.lightningant.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.MsgAndStatusBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.BaisongUtils;

/**
 * Created by ${刘全伦} on 2017/9/19.
 * 名称:
 */

public class MeWalletSendActivity extends BaseActivity {
    private TextView zhuanbi,tv_title,availableBalance;
    private EditText toAddress,numbers;
    private String balance,myAddress;
    private static final int DECIMAL_DIGITS = 4;//小数的位数
    private ImageView img_left;
    @Override
    public void initParms(Bundle parms) {
        balance = getIntent().getStringExtra("balance");
        myAddress = getIntent().getStringExtra("myAddress");

    }

    @Override
    public int bindLayout() {
        return R.layout.me_wallet_shoubi;
    }

    @Override
    public void initView() {
        zhuanbi=(TextView) findViewById(R.id.zhuanbi);
        tv_title=(TextView) findViewById(R.id.tv_title);
        availableBalance=(TextView) findViewById(R.id.availableBalance);
        toAddress=(EditText) findViewById(R.id.toAddress);
        numbers=(EditText) findViewById(R.id.numbers);
        img_left =find(R.id.img_left);
        tv_title.setText(getResources().getString(R.string.TransferCurrency));
        availableBalance.setText(balance+"LAP");
        setPoint(numbers);
    }

    @Override
    public void initData() {

    }
    private void sendMoney(){
        if(TextUtils.isEmpty(toAddress.getText().toString())||TextUtils.isEmpty(numbers.getText().toString())){
            AppToastMgr.showToast(getResources().getString(R.string.pfitci));
            return;
        }
        if(TextUtils.isEmpty(ConfigYibaisong.TOKEN_RELEASE)){
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        if(myAddress.equals(toAddress.getText().toString())){// 不能是自己
            AppToastMgr.showToast(getResources().getString(R.string.transferFaile));
            return;
        }

        String s =numbers.getText().toString();
        if(TextUtils.isEmpty(s)){
            return;
        }
        if(Double.parseDouble(s)<1){
            AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_min_10LAP));
            return;
        }

        if(s.contains(".")){
            String s1 = s.substring(0,s.indexOf("."));
            String s2 = s.substring(s.indexOf(".")+1,s.length());
            if(s1.length()>8){
                AppToastMgr.showToast(getResources().getString(R.string.illNum));
                return;
            }else if(s2.length()>4){
                AppToastMgr.showToast(getResources().getString(R.string.illNum));
                return;
            }
        }else if(s.length()>8){
            AppToastMgr.showToast(getResources().getString(R.string.illNum));
            return;
        }
        final BigDecimal bd = new BigDecimal(numbers.getText().toString());
        BigDecimal bd2 = new BigDecimal(balance);
        if(bd2.compareTo(bd)==-1){// 余额小于输入值
            AppToastMgr.showToast(getResources().getString(R.string.balanceNotEnough));
            return;
        }
        if(bd.compareTo(BigDecimal.ZERO)==-1){// 输入值《0
//            AppToastMgr.showToast(getResources().getString(R.string.mmwi));
            return;
        }
//        if(!TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE)&&ConfigYibaisong.NEED_GOOGLE_CHECK){// 如果已绑定谷歌验证需先验证码
//            startActivity(new Intent(this,MeGoogleCheckActivity.class));
//            return;
//        }

        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("privkey",ConfigYibaisong.USER_KEY);
        map.put("to",toAddress.getText().toString());
        map.put("number",numbers.getText().toString());
        map.put("note",getResources().getString(R.string.noTransferDesc));

        BaiSongHttpManager.getInstance().postRequest(URLs.ME_WALLET_SEND, "Block,send",map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    MsgAndStatusBean bean = ConfigYibaisong.jsonToBean(result,MsgAndStatusBean.class);
                    AppToastMgr.showToast(bean.getMsg());
                    if(bean.getStatus()==1){
                        Log.e("sss",bean.getMsg());
                        finish();
                    }else if(bean.getStatus()==16){
                        BaisongUtils.getInstances().goToGoogleCheck(MeWalletSendActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                AppToastMgr.showToast(e.toString());
            }
        });
    }
    private   void setPoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + DECIMAL_DIGITS+1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    public void setListener() {
        zhuanbi.setOnClickListener(this);
        img_left.setOnClickListener(this);
    }
    private void showBindGoogleDialog(){
//        if(TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE)){
//            DialogUtils.getInstance().showAllDialog(this, getResources().getString(R.string.tip), getResources().getString(R.string.bindGoogleTip),
//                    getResources().getString(R.string.cancel),  getResources().getString(R.string.ok), new DialogUtils.CallBack() {
//                        @Override
//                        public void onSure() {
//                            DialogUtils.getInstance().dismiss();
//                            startActivity(new Intent(MeWalletSendActivity.this,MeGoogleBindActivity.class));
//                        }
//
//                        @Override
//                        public void onCancle() {
//                            DialogUtils.getInstance().dismiss();
//                        }
//                    });
//        }else{
            try {
                sendMoney();
            } catch (Exception e) {
                AppToastMgr.showToast(e.toString());
                e.printStackTrace();
            }
//        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.zhuanbi:
                showBindGoogleDialog();
                break;
        }
    }
}
