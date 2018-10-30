package inter.baisong.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inter.baisong.widgets.CircleImageView;
import inter.baisong.R;
import inter.baisong.chat.bean.ChatGroupUserManageListBean;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppResourceMgr;

/**
 * Created by 于德海 on 2018/1/13.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class ChatGroupUserManageAdapter extends RecyclerView.Adapter<ChatGroupUserManageAdapter.MyViewHolder>{
    private Context mContext;
    private List<ChatGroupUserManageListBean.User> mList;
    private List<String> mNewManagerList;
    private TextView tv_nums;
    public ChatGroupUserManageAdapter(Context mContext) {
        this.mContext = mContext;
        mNewManagerList = new ArrayList<>();
    }
    public void setData(List<ChatGroupUserManageListBean.User> list){
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_group_usermanage,null,false);
        return new MyViewHolder(view);
    }

    public void setNumView(TextView tv_nums){
        this.tv_nums = tv_nums;
    }
    /***
     * 获取新设置的管理员
     * @return
     */
    public List<String> getNewManagerList() {
        return mNewManagerList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_name.setText(mList.get(position).getNickname());
        AppImageLoadHttp.showImageView(mContext,mList.get(position).getFace(),holder.img_face);
        holder.checkmanager.setTag(mList.get(position).getUid());
        if(mNewManagerList.contains(mList.get(position).getUid())){
            holder.checkmanager.setChecked(true);
        }else {
            holder.checkmanager.setChecked(false);
        }
        holder.checkmanager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!mNewManagerList.contains(mList.get(position).getUid())&&mList.get(position).getUid().equals(holder.checkmanager.getTag())){
                        mNewManagerList.add(mList.get(position).getUid());
                        tv_nums.setText(AppResourceMgr.getString(mContext,R.string.chat_group_operation_sure)+" ( "+mNewManagerList.size()+" )");
                    }
                }else {
                    if(mNewManagerList.contains(mList.get(position).getUid())&&mList.get(position).getUid().equals(holder.checkmanager.getTag())){
                        mNewManagerList.remove(mList.get(position).getUid());
                        tv_nums.setText(AppResourceMgr.getString(mContext,R.string.chat_group_operation_sure)+" ( "+mNewManagerList.size()+" )");
                    }
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
        TextView tv_name;
        CheckBox checkmanager;
        public MyViewHolder(View itemView) {
            super(itemView);
            checkmanager = (CheckBox) itemView.findViewById(R.id.check_manager);
            img_face = (CircleImageView) itemView.findViewById(R.id.img_face);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            checkmanager.setVisibility(View.VISIBLE);

        }
    }
}
