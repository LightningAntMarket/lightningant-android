package inter.baisong.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import inter.baisong.widgets.CircleImageView;
import inter.baisong.R;
import inter.baisong.chat.bean.ChatMoneyRobDetailBean;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.DateTimerUtils;

/**
 * Created by 于德海 on 2018/1/18.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatMoneyRobAdapter extends RecyclerView.Adapter<ChatMoneyRobAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatMoneyRobDetailBean.Data> mList;

    public ChatMoneyRobAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_chat_money_rob,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(mList.get(position).getNickname());
        holder.tv_time.setText(DateTimerUtils.strToDateLong(mList.get(position).getTime(),"yyyy-MM-dd"));
        AppImageLoadHttp.showImageView(mContext,mList.get(position).getFace(),holder.img_face);
        holder.tv_money_price.setText(mList.get(position).getLap()+" LAP");
    }

    public void setList(List<ChatMoneyRobDetailBean.Data> mList) {
        this.mList = mList;
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
