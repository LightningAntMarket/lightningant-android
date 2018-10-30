package inter.baisong.chat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.URLs;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupDetailEditActivity extends BaseActivity {
    private int type;//0miaoshu   1gonggao  2mingchen
    private ImageView img_left;
    private TextView tv_title,tv_nums_limit,tv_right;
    private EditText edit_most_line,edit_one_line;
    private String content,groupid;

    @Override
    public void initParms(Bundle parms) {
        try {
            type = parms.getInt("type",0);
            groupid = parms.getString("groupid","Null");
            content = parms.getString("content","Null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_group_detailedit;
    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        tv_title = find(R.id.tv_title);
        tv_right = find(R.id.tv_right);
        edit_most_line = find(R.id.edit_most_line);
        edit_one_line = find(R.id.edit_one_line);
        tv_nums_limit = find(R.id.tv_nums_limit);
    }

    @Override
    public void initData() {
        tv_right.setText(AppResourceMgr.getString(this,R.string.chat_group_operation_sure));
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setTextColor(AppResourceMgr.getColor(this,R.color.app_color_theme));
        switch (type){
            case 0:
                tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_miaoshu));
                edit_most_line.setVisibility(View.VISIBLE);
                edit_one_line.setVisibility(View.GONE);
                tv_nums_limit.setText(content.length()+"/1000");
                edit_most_line.setText(content);
                break;
            case 1:
                tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_gonggao));
                edit_most_line.setVisibility(View.VISIBLE);
                edit_one_line.setVisibility(View.GONE);
                edit_most_line.setText(content);
                tv_nums_limit.setText(content.length()+"/1000");
                break;
            case 2:
                tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_name_edit));
                edit_most_line.setVisibility(View.GONE);
                edit_one_line.setText(content);
                edit_one_line.setVisibility(View.VISIBLE);
                tv_nums_limit.setText(content.length()+"/35");
                break;
        }


    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.tv_right:
                if(type==2){
                    if(TextUtils.isEmpty(edit_one_line.getText().toString().trim())) {
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_login_empty));
                        return;
                    }
                }else if(TextUtils.isEmpty(edit_most_line.getText().toString().trim())){
                    AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_login_empty));
                    return;
                }
                requestHttp();
                break;
        }
    }

    /****
     * 更新群信息
     */
    private void  requestHttp(){
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("groupid",groupid);
        if(type==0){
            map.put("desc",edit_most_line.getText().toString());
        }else if(type==1){
            map.put("notice",edit_most_line.getText().toString());
        }else {
            map.put("groupname",edit_one_line.getText().toString());
        }
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_INFO_EDIT,"Groupchat,upGroups", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {

                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
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
