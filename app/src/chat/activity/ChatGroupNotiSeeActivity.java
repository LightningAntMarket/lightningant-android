package inter.baisong.chat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.DateTimerUtils;
import inter.baisong.widgets.CircleImageView;

/**
 * Created by 于德海 on 2018/1/11.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupNotiSeeActivity extends BaseActivity {
    private ImageView img_left;
    private TextView tv_title,tv_group_name;
    private TextView tv_content,tv_time;
    private String content,img_url,name,time;
    private int  type; //0 描述  1 公告
    private CircleImageView img_group;
    @Override
    public void initParms(Bundle parms) {
        try {
            name = parms.getString("name","Null");
            img_url=parms.getString("img");
            type = parms.getInt("type",0);
            content = parms.getString("content","Null");
            time = parms.getString("time","Null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_group_notisee;
    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        tv_title = find(R.id.tv_title);
        tv_group_name = find(R.id.tv_group_name);
        img_group = find(R.id.img_group);
        tv_content = find(R.id.tv_content);
        tv_time = find(R.id.tv_time);
    }

    @Override
    public void initData() {
        if(type==0)
            tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_miaoshu));
        else
            tv_title.setText(AppResourceMgr.getString(this,R.string.chat_group_gonggao));

        if(!TextUtils.isEmpty(time)&&!"Null".equals(time)){
            tv_time.setText(DateTimerUtils.strToDateLong(time,"yyyy-MM-dd"));
        }
        tv_group_name.setText(name);
        AppImageLoadHttp.showImageView(this,img_url,img_group);
        tv_content.setText(content);
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
        }

    }
}
