package inter.baisong.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.activity.MeGoogleBindActivity;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.BaisongUtils;
import inter.baisong.utils.DialogUtils;

/**
 * Created by 于德海 on 2018/1/18.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description  群组红包发送
 */

public class ChatRedMoneySendActivity extends BaseActivity{
    private int mermbers;
    private ImageView img_left;
    private TextView tv_title,tv_money_send,tv_group_members,tv_money_style,tv_percent_money,tv_me_lap,tv_money_price_all,rl_left_1;
    private EditText edit_price_all,edit_money_nums,edit_money_intro;
    private int tag ;//1 普通红包  2random红包
    @Override
    public void initParms(Bundle parms) {
        try {
            mermbers = parms.getInt("group_members",0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_money_send;
    }

    @Override
    public void initView() {
        tag=1;

        img_left = find(R.id.img_left);
        //TextView
        tv_title = find(R.id.tv_title);
        tv_money_send = find(R.id.tv_money_send);
        tv_group_members = find(R.id.tv_group_members);
        tv_money_style = find(R.id.tv_money_style);
        rl_left_1 = find(R.id.rl_left_1);
        tv_percent_money = find(R.id.tv_percent_money);
        tv_me_lap = find(R.id.tv_me_lap);
        tv_money_price_all = find(R.id.tv_money_price_all);
        //EditText
        edit_price_all = find(R.id.edit_price_all);
        edit_money_nums = find(R.id.edit_money_nums);
        edit_money_intro = find(R.id.edit_money_intro);
    }

    @Override
    public void initData() {
        tv_title.setText(AppResourceMgr.getString(this,R.string.chat_money_send));
        String str = String.format(AppResourceMgr.getString(this,R.string.chat_money_group_members),mermbers+"");
        tv_group_members.setText(str);
        requestHttp();

    }



    private void requestHttp() {
        BaiSongHttpManager.getInstance().getRequest(URLs.CHAT_GROUP_MONEY_MEINFO, "RedPacket,balance", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
                        JSONObject bean = json.optJSONObject("data");
                        tv_me_lap.setText(String.format(AppResourceMgr.getString(ChatRedMoneySendActivity.this,R.string.chat_money_me_lap),bean.optString("balance")));
                        tv_percent_money.setText(String.format(AppResourceMgr.getString(ChatRedMoneySendActivity.this,R.string.chat_money_percent),bean.opt("FEE")));
                    }
                } catch (JSONException e) {
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
        tv_money_style.setOnClickListener(this);
        img_left.setOnClickListener(this);
        tv_money_send.setOnClickListener(this);
        edit_price_all.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if("00".equals(temp)){
                    edit_price_all.setText("0");
                    return;
                }
                if (posDot>0&&temp.length() - posDot - 1 > 4)
                {
                    edt.delete(posDot + 5, posDot + 6);
                }
                if(tag==2){
                    tv_money_price_all.setText(edit_price_all.getText().toString()+" LAP");
                }
                if(tag==1&&!TextUtils.isEmpty(edit_price_all.getText().toString())&&!TextUtils.isEmpty(edit_money_nums.getText().toString())){
                    int nums = Integer.parseInt(edit_money_nums.getText().toString());
                    double price = Double.parseDouble(edit_price_all.getText().toString());
                    tv_money_price_all.setText(new DecimalFormat("#0.0000").format(price*nums)+" LAP");
                }
            }
        });
        edit_money_nums.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(tag==1&&!TextUtils.isEmpty(edit_price_all.getText().toString())&&!TextUtils.isEmpty(edit_money_nums.getText().toString())){
                    int nums = Integer.parseInt(edit_money_nums.getText().toString());
                    double price = Double.parseDouble(edit_price_all.getText().toString());
                    tv_money_price_all.setText((nums*price)+" LAP");
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_money_style:
                changeMoneyType();

                break;
            case R.id.tv_money_send:
                if(TextUtils.isEmpty(edit_money_intro.getText().toString().trim())||
                        TextUtils.isEmpty(edit_money_nums.getText().toString())||TextUtils.isEmpty(edit_price_all.getText().toString())){
                    AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_login_empty));
                    return;
                }
//                if(TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE)){
//                    showBindGoogleDialog();
//                }else {
//                    if (!TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE) && ConfigYibaisong.NEED_GOOGLE_CHECK) {
//                        startActivity(new Intent(this, MeGoogleCheckActivity.class));
//                        return;
//                    }
                    double all_money = Double.parseDouble(edit_price_all.getText().toString());
                    if(all_money<0.1){
                        AppToastMgr.showToast("More than 0.1 LAP");
                        return;
                    }
                    if(tag==1&&all_money>100){
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.chat_money_send_one_max_price));
                        return;
                    }
                    int nums= Integer.parseInt(edit_money_nums.getText().toString());
                    if(nums==0){
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.chat_money_send_min_nums));
                        return;
                    }
                    if(nums>mermbers){
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.chat_money_send_max_nums));
                        return;
                    }

                    if(tag==2&&(Double.parseDouble(edit_price_all.getText().toString())/Integer.parseInt(edit_money_nums.getText().toString())<0.1)){
                        AppToastMgr.showToast("More than 0.1 LAP");
                        return;
                    }
                    requestSendMoney();
//                }
                break;
            case R.id.img_left:
                this.finish();
                break;
        }
    }

    /***
     * 更换红包类型
     */
    private void changeMoneyType() {
        if(tag==1){//更换为随机
            tag=2;
            rl_left_1.setText(AppResourceMgr.getString(this,R.string.chat_money_price_all));
            tv_money_style.setText(AppResourceMgr.getString(this,R.string.chat_money_style_two_random));
            tv_money_price_all.setText(edit_price_all.getText().toString()+" LAP");
        }else {//更换为普通
            tag=1;
            rl_left_1.setText(AppResourceMgr.getString(this,R.string.chat_money_price_single));
            tv_money_style.setText(AppResourceMgr.getString(this,R.string.chat_money_style_two_common));
            if(!TextUtils.isEmpty(edit_price_all.getText().toString())&&!TextUtils.isEmpty(edit_money_nums.getText().toString())){
                int nums = Integer.parseInt(edit_money_nums.getText().toString());
                double price = Double.parseDouble(edit_price_all.getText().toString());
                tv_money_price_all.setText((nums*price)+" LAP");
            }
        }
    }

    /***
     * 发红包
     */
    private void requestSendMoney() {
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("privkey",ConfigYibaisong.USER_KEY);
        map.put("price",edit_price_all.getText().toString());
        map.put("number",edit_money_nums.getText().toString());
        map.put("name",edit_money_intro.getText().toString());
        map.put("type",tag+"");
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_MONEY_SEND, "RedPacket,Index", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
                        Intent intent = new Intent();
                        intent.putExtra("money_id",json.optString("rid"));
                        intent.putExtra("money_name",edit_money_intro.getText().toString());
                        setResult(Activity.RESULT_OK,intent);
                        ChatRedMoneySendActivity.this.finish();
                    }else if(json.optInt("status")==16) {
                        BaisongUtils.getInstances().goToGoogleCheck(ChatRedMoneySendActivity.this);
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

    private void showBindGoogleDialog(){
        DialogUtils.getInstance().showAllDialog(this, getResources().getString(R.string.tip), getResources().getString(R.string.bindGoogleTip),
                getResources().getString(R.string.cancel),  getResources().getString(R.string.ok), new DialogUtils.CallBack() {
                    @Override
                    public void onSure() {
                        DialogUtils.getInstance().dismiss();
                        startActivity(new Intent(ChatRedMoneySendActivity.this,MeGoogleBindActivity.class));
                    }

                    @Override
                    public void onCancle() {
                        DialogUtils.getInstance().dismiss();
                    }
                });

    }
}
