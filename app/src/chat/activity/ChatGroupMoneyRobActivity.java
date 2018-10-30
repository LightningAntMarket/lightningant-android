package inter.baisong.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.HashMap;
import java.util.Map;

import inter.baisong.widgets.CircleImageView;
import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.chat.adapter.ChatMoneyRobAdapter;
import inter.baisong.chat.bean.ChatMoneyRobDetailBean;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;

/**
 * Created by 于德海 on 2018/1/18.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupMoneyRobActivity extends BaseActivity {
    private ImageView img_left;
    private TextView tv_money_username,tv_money_name,tv_money_rob_price,tv_rob_price_intro,tv_money_receive_people;
    private RecyclerView mRecyclerView;
    private CircleImageView img_face;
    private View mHeaderView;
    private HeaderAndFooterWrapper wrapper;
    private ChatMoneyRobAdapter mAdapter;
    private String money_id;
    private TextView tv_right;
    @Override
    public void initParms(Bundle parms) {
        try {
            money_id = parms.getString("money_id","");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_chat_money_rob;
    }

    @Override
    public void initView() {
        //如果api大于21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#f45656"));
        }
        img_left = find(R.id.img_left);
        tv_right = find(R.id.tv_right);
        mRecyclerView = find(R.id.mRecyclerView);

        //HeaderView
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.header_aty_chat_money_rob,null,false);
        tv_money_username = (TextView) mHeaderView.findViewById(R.id.tv_money_username);
        tv_money_name = (TextView) mHeaderView.findViewById(R.id.tv_money_name);
        tv_money_rob_price = (TextView) mHeaderView.findViewById(R.id.tv_money_rob_price);
        tv_rob_price_intro = (TextView) mHeaderView.findViewById(R.id.tv_rob_price_intro);
        tv_money_receive_people = (TextView) mHeaderView.findViewById(R.id.tv_money_receive_people);
        img_face = (CircleImageView) mHeaderView.findViewById(R.id.img_face);

        mAdapter = new ChatMoneyRobAdapter(this);
        wrapper = new HeaderAndFooterWrapper(mAdapter);
        wrapper.addHeaderView(mHeaderView);

    }

    @Override
    public void initData() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(wrapper);
        requestHttp();
    }

    /***
     * 抢红包
     */
    private void requestHttp() {
        AppToastMgr.getInstances().showUncancelLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("rid",money_id);
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_MONEY_ROB, "RedPacket,getRedPacket", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                //无论成功失败都需要请求下一接口
                        requestInfo();

            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                AppToastMgr.showError();
            }
        });
    }

    /***
     * 请求红包详情
     */
    private void requestInfo() {
        HashMap<String,String> map = new HashMap<>();
        map.put("rid",money_id);
        BaiSongHttpManager.getInstance().postRequest(URLs.CHAT_GROUP_MONEY_ROB_DETAIL, "RedPacket,redPacketDetail", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                ChatMoneyRobDetailBean bean = ConfigYibaisong.jsonToBean(result,ChatMoneyRobDetailBean.class);
                if(bean==null)
                    return;
                if(bean.getStatus()==1){
                    initValue(bean.getInfo());
                    mAdapter.setList(bean.getLog());
                    mAdapter.notifyDataSetChanged();
                }else {
                    AppToastMgr.showToast(bean.getMsg());
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
            }
        });
    }

    /***
     * 赋值
     * @param info
     */
    private void initValue(ChatMoneyRobDetailBean.Info info) {
        if(info==null){
            return;
        }
        if(info.getHasget()==1){
            tv_money_rob_price.setText(info.getMygetlap()+" LAP");
            tv_rob_price_intro.setText(AppResourceMgr.getString(this,R.string.chat_money_rob_price_intro));
        }else {
            tv_rob_price_intro.setVisibility(View.GONE);
            tv_money_rob_price.setText(AppResourceMgr.getString(this,R.string.chat_money_rob_un));
        }
        tv_money_username.setText(info.getNickname());
        tv_money_name.setText(info.getName());
        AppImageLoadHttp.showImageView(this,info.getFace(),img_face);
        tv_money_receive_people.setText(String.format(AppResourceMgr.getString(this,R.string.chat_money_receive_people),
                info.getGetnumber()+"/"+info.getTotalnumber(),info.getLap()));
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
                intent = new Intent(this,ChatMoneyRobListActivity.class);
                intent.putExtra("money_id",money_id);
                startActivity(intent);
                break;
        }
    }
}
