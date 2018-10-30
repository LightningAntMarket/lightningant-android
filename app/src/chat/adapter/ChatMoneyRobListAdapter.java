package inter.baisong.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import inter.baisong.R;
import inter.baisong.chat.bean.ChatMoneyRobListBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.DateTimerUtils;
import inter.baisong.widgets.CircleImageView;

/**
 * Created by 于德海 on 2018/1/18.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatMoneyRobListAdapter extends RecyclerView.Adapter<ChatMoneyRobListAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatMoneyRobListBean.MoneyLog> mList;

    public ChatMoneyRobListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_chat_money_rob,null,false);
        return new ViewHolder(view);
    }


    public void setList(List<ChatMoneyRobListBean.MoneyLog> mList) {
        this.mList = mList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String score = mList.get(position).getScore();
        if(!TextUtils.isEmpty(score)){
            if(score.startsWith("-")){
                holder.tv_money_price.setText(score+" LAP");
                holder.tv_name.setText(AppResourceMgr.getString(mContext,R.string.chat_money_item_send));
            }else {
                holder.tv_money_price.setText("+"+score+" LAP");
                holder.tv_name.setText(AppResourceMgr.getString(mContext,R.string.chat_money_item_receive));
            }
        }
        holder.tv_time.setText(DateTimerUtils.strToDateLong(mList.get(position).getTime(),"MM-dd HH:mm:ss"));
    }

    @Override
    public int getItemCount() {
        if(mList==null)
            return 0;
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img_face;
        TextView tv_name,tv_time,tv_money_price;
        public ViewHolder(View itemView) {
            super(itemView);
            img_face = (CircleImageView) itemView.findViewById(R.id.img_face);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_money_price = (TextView) itemView.findViewById(R.id.tv_money_price);

        }
    }
}
