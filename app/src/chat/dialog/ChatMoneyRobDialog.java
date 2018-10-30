package inter.baisong.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import inter.baisong.R;
import inter.baisong.chat.activity.ChatGroupMoneyRobActivity;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.widgets.CircleImageView;

/**
 * Created by 于德海 on 2018/1/19.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description  抢红包弹窗
 */

public class ChatMoneyRobDialog extends Dialog {
    private Context mContext;
    private TextView tv_money_name,tv_user_name,tv_open;
    private CircleImageView img_face;
    private String money_id;
    public ChatMoneyRobDialog(@NonNull Context context, final String money_id) {
        super(context, R.style.mask_dialog);
        this.mContext = context;
        this.money_id = money_id;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_chat_group_money,null,false);
        setContentView(view);
        tv_money_name  = (TextView) view.findViewById(R.id.tv_money_name);
        tv_user_name  = (TextView) view.findViewById(R.id.tv_user_name);
        tv_open  = (TextView) view.findViewById(R.id.tv_open);
        img_face  = (CircleImageView) view.findViewById(R.id.img_face);

        tv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ChatGroupMoneyRobActivity.class).putExtra("money_id",money_id));
                dismiss();
            }
        });
    }

    public void setValue(String name,String face,String money_name){
        tv_money_name.setText(money_name);
        tv_user_name.setText(name);
        AppImageLoadHttp.showImageView(mContext,face,img_face);
    }

}
