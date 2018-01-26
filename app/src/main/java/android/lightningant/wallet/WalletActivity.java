package android.lightningant.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import inter.baisong.R;
import inter.baisong.adapter.MeWalletRecordAdapter;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.MeInfoBean;
import inter.baisong.bean.MeWalletHistoryBean;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.DialogUtils;

/**
 * Created by ${刘全伦} on 2017/9/19.
 * 名称:
 */

public class MeWalletActivity extends BaseActivity {
    private MeWalletRecordAdapter mAdapter;
    private TextView tibi,shoubi,chakan;
    private ImageView img_left;//回退
    private List<MeWalletHistoryBean.MyData> historyData=new ArrayList<>();
    private MeInfoBean.Info info;
    private TextView lockbalance,balance,history;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.me_wallet;
    }

    @Override
    public void initView() {
        mAdapter = new MeWalletRecordAdapter(this,historyData);
        tibi = (TextView)findViewById(R.id.tibi);
        history= (TextView)findViewById(R.id.history);
        lockbalance=(TextView)findViewById(R.id.lockbalance);
        balance=(TextView)findViewById(R.id.balance);
        shoubi = (TextView) findViewById(R.id.shoubi);
        chakan = (TextView) findViewById(R.id.chakan);
        img_left = find(R.id.img_left);
    }

    @Override
    public void initData() {
        BaiSongHttpManager.getInstance().getRequest(URLs.ME_INFO,"User,index", new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    MeInfoBean bean = ConfigYibaisong.jsonToBean(result,MeInfoBean.class);
                    info = bean.getData();
                    if(bean.getStatus()==1){
                        balance.setText(info.getBalance()+"");
                        lockbalance.setText(TextUtils.isEmpty(info.getLockbalance())?0+"":info.getLockbalance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                Log.e("sss",e.toString());
            }
        });
    }


    @Override
    public void setListener() {
        tibi.setOnClickListener(this);
        shoubi.setOnClickListener(this);
        img_left.setOnClickListener(this);
        chakan.setOnClickListener(this);
        history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.tibi:
                startActivity(new Intent(this,MeWalletSendActivity.class)
                        .putExtra("balance",balance.getText().toString())
                        .putExtra("myAddress",info.getBlockaddress()));
                break;
            case R.id.shoubi:
                startActivity(new Intent(this,MeWalletReceiveActivity.class).putExtra("key",info.getBlockaddress()));
                break;
            case R.id.chakan:
                startActivity(new Intent(this,CreateUserKeyActivity.class).putExtra("key",ConfigYibaisong.USER_KEY).putExtra("type",1));
                break;
            case R.id.history:
                startActivity(new Intent(this,MeTransferListActivity.class));
                break;
        }
    }

}
