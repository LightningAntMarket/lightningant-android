package inter.baisong.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import inter.baisong.widgets.CircleImageView;
import inter.baisong.R;
import inter.baisong.chat.bean.ChatGroupManagerListBean;
import inter.baisong.chat.dialog.ChatManagerManageDialog;
import inter.baisong.utils.AppImageLoadHttp;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupManagersAdapter extends RecyclerView.Adapter<ChatGroupManagersAdapter.MyViewHolder>{
    private Context mContext;
    private List<ChatGroupManagerListBean.User> mList;
    private String groupid;
    public ChatGroupManagersAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<ChatGroupManagerListBean.User> list){
        this.mList = list;
    }

    public void setGroupid(String groupid){
        this.groupid = groupid;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_group_user,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_name.setText(mList.get(position).getNickname());
        AppImageLoadHttp.showImageView(mContext,mList.get(position).getFace(),holder.img_face);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatManagerManageDialog dialog = new ChatManagerManageDialog(mContext,mList.get(position).getUid(),groupid,position);
                dialog.setChatName(mList.get(position).getNickname());
                dialog.setListener(new ChatManagerManageDialog.CallBack() {
                    @Override
                    public void removeUser(int position) {
                        mList.remove(position);
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mList==null)
            return 0;
        else
            return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img_face;
        TextView tv_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            img_face = (CircleImageView) itemView.findViewById(R.id.img_face);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);

        }
    }
}
