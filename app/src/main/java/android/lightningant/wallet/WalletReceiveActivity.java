package android.lightningant.wallet;

import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.utils.AppToastMgr;

/**
 * Created by ${刘全伦} on 2017/9/19.
 * 名称:
 */

public class MeWalletReceiveActivity extends BaseActivity {
    private String key;
    private TextView address,copy,tv_title;
    private ImageView img_left;
    @Override
    public void initParms(Bundle parms) {
        key = getIntent().getStringExtra("key");
    }

    @Override
    public int bindLayout() {
        return R.layout.me_wallet_receive_head;
    }

    @Override
    public void initView() {
        tv_title= (TextView) findViewById(R.id.tv_title);
        address = (TextView) findViewById(R.id.address);
        copy= (TextView)findViewById(R.id.copy);
        address.setText(key);
        tv_title.setText(getResources().getString(R.string.receivingCurrency));
        img_left = find(R.id.img_left);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {
        copy.setOnClickListener(this);
        img_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(address.getText());
                AppToastMgr.showToast(getResources().getString(R.string.tchbc));
                break;
        }
    }
}
