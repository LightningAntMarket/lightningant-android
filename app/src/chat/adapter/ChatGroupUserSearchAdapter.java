package inter.baisong.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import inter.baisong.R;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.chat.bean.ChatGroupUserManageListBean;
import inter.baisong.chat.dialog.ChatUserManageDialog;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.BaisongUtils;
import inter.baisong.widgets.CircleImageView;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupUserSearchAdapter extends RecyclerView.Adapter<ChatGroupUserSearchAdapter.MyViewHolder>{
    private Context mContext;
    private List<ChatGroupUserManageListBean.User> mList;
    private List<ChatGroupUserManageListBean.User> mAdminList;
    private boolean isManager;
    private String groupid;

    public ChatGroupUserSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }



    public void setValue(String groupid,boolean isManager){
        this.isManager = isManager;
        this.groupid = groupid;
    }


    /***
     * 管理员数量
     * @param list
     */
    public void setManagerList(List<ChatGroupUserManageListBean.User> list) {
        this.mAdminList = list;
    }

    public void setData(List<ChatGroupUserManageListBean.User> list){
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_group_usermanage,null,false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_name.setText(mList.get(position).getNickname());
        AppImageLoadHttp.showImageView(mContext,mList.get(position).getFace(),holder.img_face);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShowRemove =false;
                if(isManager&&!BaisongUtils.getInstances().checkIsAdmin(mAdminList,mList.get(position).getUid())){
                    isShowRemove=true;
                }
                ChatUserManageDialog dialog =  new ChatUserManageDialog(mContext,mList.get(position).getUid(),groupid,isShowRemove,position);
                dialog.setChatName(mList.get(position).getNickname());
                dialog.setListener(new ChatUserManageDialog.CallBack() {
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
                if(!ConfigYibaisong.UID.equals(mList.get(position).getUid())){
                    dialog.show();
                }
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
        TextView tv_name,tv_user_type;
        public MyViewHolder(View itemView) {
            super(itemView);
            img_face = (CircleImageView) itemView.findViewById(R.id.img_face);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_user_type = (TextView) itemView.findViewById(R.id.tv_user_type);
        }
    }
}
