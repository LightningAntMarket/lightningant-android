package android.lightningant.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.GoodJoinBean;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.DateTimerUtils;

/**
 * Created by 于德海 on 2017/9/14.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description
 */

public class GoodJoinActivity extends BaseActivity {
    private TextView tv_title;
    private ImageView img_left;
    private RecyclerView recyclerView;
    private String mode,gid;
    private List<GoodJoinBean.Info> mList;
    private MyRecyclerAdapter mAdapter;
    private int p;
    private boolean isLoading;
    @Override
    public void initParms(Bundle parms) {
        try{
            gid = parms.getString("gid");
            mode = parms.getString("mode");
        }catch (Exception e){


        }
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_goodjoin;
    }

    @Override
    public void initView() {
        isLoading =true;
        tv_title = find(R.id.tv_title);
        img_left = find(R.id.img_left);
        mList = new ArrayList<>();
        recyclerView = find(R.id.recycler);
        mAdapter = new MyRecyclerAdapter();
        p=1;
    }

    @Override
    public void initData() {
        tv_title.setText(AppResourceMgr.getString(this,R.string.gooddetail_auction_title));
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
        requestHttp();
    }

    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if(isLoading&&recyclerView.canScrollVertically(-1)&&mList.size()>19){
                            isLoading=false;
                            p++;
                            requestHttp();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
        }
    }

    private void requestHttp(){
        AppToastMgr.getInstances().showLoading(this);
        String url;
        String controller;
        if(ConfigYibaisong.AUCTION_MODE.equals(mode)){
            url = URLs.MORE_AUCTION;
            controller="goods,getMoreAuctionJoins";
        }else{
            url = URLs.MORE_FIXED;
            controller="goods,getMoreRandomJoins";
        }
        BaiSongHttpManager.getInstance().getRequest(url+"/gid/"+gid+"/p/"+p, controller, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                GoodJoinBean bean = ConfigYibaisong.jsonToBean(result,GoodJoinBean.class);
                if(bean.getStatus()==1){
                    isLoading =true;
                    if(p==1){
                        mList.clear();
                    }
                    mList.addAll(bean.getData());
                    mAdapter.notifyDataSetChanged();
                }else{
                    AppToastMgr.showToast(bean.getMsg());
                }
                Log.d("at",result);
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
            }
        });

    }


    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(GoodJoinActivity.this).inflate(R.layout.item_gooddetail_joinuser,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final GoodJoinBean.Info bean = mList.get(position);
            holder.tv_name.setText(bean.getNickname());
            AppImageLoadHttp.getInstance().loadImage(GoodJoinActivity.this,bean.getFace()).into(holder.img_face);
            holder.img_face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GoodJoinActivity.this, PersonalInfoActivity.class).putExtra("uid", bean.getUid()));
                }
            });
            holder.tv_price.setText("LAP:"+bean.getMoney());
            holder.tv_time.setText(DateTimerUtils.getWhatTime(GoodJoinActivity.this,Long.parseLong(bean.getTime())));
        }

        @Override
        public int getItemCount() {
            if(mList==null)
                return 0;
            else
                return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
             CircleImageView img_face;
             TextView tv_name,tv_price,tv_time;
            public ViewHolder(View itemView) {
                super(itemView);
                img_face = (CircleImageView) itemView.findViewById(R.id.img_face);
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_price = (TextView) itemView.findViewById(R.id.tv_price);
                tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            }
        }
    }
}
