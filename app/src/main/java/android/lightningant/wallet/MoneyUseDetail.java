package android.lightningant.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import inter.baisong.R;
import inter.baisong.base.BaseActivity;

/**
 * Created by 于德海 on 2017/9/19.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description   交易详情
 */

public class MoneyUseDetail extends BaseActivity {
    private ImageView img_left,img_good;
    private TextView tv_good_name,tv_user_name,tv_price,tv_time;
    private CircleImageView img_face;
    private TextView tv_money_style;
    private int type; //0 支出 1收入
    @Override
    public void initParms(Bundle parms) {
        try {
            type = parms.getInt("type",0);
        }catch (Exception e){

        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_moneyuse;
    }

    @Override
    public void initView() {
        img_left = find(R.id.img_left);
        tv_time = find(R.id.tv_time);
        tv_money_style = find(R.id.tv_money_type);
        img_good = find(R.id.img_good);
        tv_good_name = find(R.id.tv_good_name);
        tv_user_name = find(R.id.tv_user_name);
        img_face = find(R.id.img_face);
    }

    @Override
    public void initData() {

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
