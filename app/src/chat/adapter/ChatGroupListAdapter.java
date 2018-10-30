package inter.baisong.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;

import java.util.ArrayList;
import java.util.List;

import inter.baisong.R;
import inter.baisong.activity.ChatActivity;
import inter.baisong.bean.data.ChatGroupData;
import inter.baisong.chat.activity.GroupDetailActivity;
import inter.baisong.chat.bean.HxGroupBean;
import inter.baisong.db.DbManager;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;

/**
 * Created by 于德海 on 2018/1/10.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupListAdapter extends RecyclerView.Adapter<ChatGroupListAdapter.MyViewHolder>{

    private Context mContext;
    private List<ChatGroupData> groupList,hotlist,mList;
    private DbManager dbManager;
    public ChatGroupListAdapter(Context mContext) {
        this.mContext = mContext;
        dbManager = new DbManager(mContext);
        mList = new ArrayList<>();
    }

    public void clearData(){
        mList.clear();
    }
    public void setGroupList(List<ChatGroupData> list){
        groupList = list;
        if(groupList ==null)
            groupList = new ArrayList<>();
        mList.addAll(groupList);

    }
    public void setHotlist(List<ChatGroupData> list){
        hotlist = list;
        if(hotlist==null)
            hotlist = new ArrayList<>();
        mList.addAll(hotlist);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_group,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_name.setText(mList.get(position).getGroupname());
        AppImageLoadHttp.showImageView(mContext,mList.get(position).getGroupface(),holder.img_group);
        HxGroupBean oldbean = dbManager.findHxGroupInfo(mList.get(position).getGroupid());
        if(oldbean==null){
            HxGroupBean bean = new HxGroupBean();
            bean.setImg_url(mList.get(position).getGroupface());
            bean.setGroupid(mList.get(position).getGroupid());
            bean.setGroup_name(bean.getGroup_name());
            dbManager.addHxGroupInfo(bean);
        }else {
            HxGroupBean bean = new HxGroupBean();
            bean.setImg_url(mList.get(position).getGroupface());
            bean.setGroupid(mList.get(position).getGroupid());
            bean.setGroup_name(bean.getGroup_name());
            bean.setIstop(oldbean.getIstop());
            bean.setIsMute(oldbean.getIsMute());
            dbManager.updateHxGroupInfo(bean);
        }
        if(position==0||position==groupList.size()){
            holder.tv_title.setVisibility(View.VISIBLE);
            if(groupList.size()>0){
                if(position==0){
                    holder.tv_title.setText(AppResourceMgr.getString(mContext,R.string.frag_chat_group_joined));
                }else
                    holder.tv_title.setText(AppResourceMgr.getString(mContext,R.string.frag_chat_group_hot));
            }else {
                holder.tv_title.setText(AppResourceMgr.getString(mContext,R.string.frag_chat_group_hot));
            }
        }else {
            holder.tv_title.setVisibility(View.GONE);
        }
        if(position>=groupList.size()){
            holder.tv_join.setVisibility(View.VISIBLE);
        }else {
            holder.tv_join.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<groupList.size()){
                    Intent intent = new Intent(mContext,ChatActivity.class);
                    intent.putExtra(EaseConstant.EXTRA_USER_ID,mList.get(position).getGroupid());
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                    mContext.startActivity(intent);
                }

            }
        });
        holder.tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinChatGroup(mList.get(position).getGroupid(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(groupList ==null)
            groupList = new ArrayList<>();
        if(hotlist==null)
            hotlist = new ArrayList<>();
        return hotlist.size()+groupList.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView img_group;
        TextView tv_name,tv_title,tv_join;

        public MyViewHolder(View itemView) {
            super(itemView);
            img_group = (ImageView) itemView.findViewById(R.id.img_group);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_join = (TextView) itemView.findViewById(R.id.tv_join);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    /*****
     * 加入群聊
     */
    private void joinChatGroup(final String groupid, final int position) {
        AppToastMgr.getInstances().showLoading(mContext);
        EMClient.getInstance().groupManager().asyncJoinGroup(groupid, new EMCallBack() {
            @Override
            public void onSuccess() {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppToastMgr.getInstances().cancel();
                        groupList.add(hotlist.get(position-groupList.size()));
                        hotlist.remove(position-(groupList.size()-1));
                        mList.clear();
                        mList.addAll(groupList);
                        mList.addAll(hotlist);
                        notifyDataSetChanged();
                        Intent intent = new Intent(mContext,ChatActivity.class);
                        intent.putExtra(EaseConstant.EXTRA_USER_ID,groupid);
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                        mContext.startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(int i, final String s) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppToastMgr.showToast(s);
                        AppToastMgr.getInstances().cancel();
                    }
                });

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
