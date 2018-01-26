package android.lightningant.order;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.AddressBean;
import inter.baisong.bean.GoodDetailBean;
import inter.baisong.bean.HxUserBean;
import inter.baisong.bean.ShareBean;
import inter.baisong.db.DbManager;
import inter.baisong.dialog.BigPhotoDialog;
import inter.baisong.dialog.ShareDialog;
import inter.baisong.utils.AppImageLoadHttp;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.BaisongUtils;
import inter.baisong.utils.DateTimerUtils;
import inter.baisong.utils.DialogUtils;
import inter.baisong.widgets.CustomAuctionTextView;

import static inter.baisong.R.id.aty_dialog_now_r;

/**
 * Created by 于德海 on 2017/8/23.
 * 因变量命名较为直白，相关注释就省略了。
 *
 * @description 商品详情页面
 */

public class GoodsDetailActivity extends BaseActivity {
    private ImageView img_share,img_back,jubao;
    private CircleImageView img_face;
    private ViewPager viewPager;
    private String gid;
    private LinearLayout ll_join_user,ll_other_release;
    private TextView tv_good_name,tv_good_mode,tv_good_author,tv_good_startprice,tv_good_deliverymode,
            tv_good_area,tv_good_endtime,tv_good_desc,tv_good_price,tv_buy,tv_auction_more;
    private RelativeLayout rl_chat;
    private LinearLayout ll_auctions;
    private String img_url;
    private RecyclerView recyclerView;
    private GoodDetailBean.GoodData mGoodData;
    private BigPhotoDialog photoDialog;
    private boolean isOver;
    private String mode,email;
    private String mybanlances;//我的钱包
    //出价Dialog 相关
    private Dialog dialog;
    private ImageView img_jia,img_jian;
    private TextView tv_jia10,tv_jia50,tv_jia100,tv_now_r,tv_me_r,tv_rob;
    private EditText dialog_price;
    private String now_price,good_price;//当前价格
    private View view_line_endtime;
    private CountDownTimer timer;
    private TextView review_all,review_score;
    private RelativeLayout review_all_layout;
    @Override
    public void initParms(Bundle parms) {
        try {
            gid= parms.getString("gid");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int bindLayout() {
        return R.layout.aty_gooddetail;
    }

    @Override
    public void initView() {
        jubao= find(R.id.jubao);
        review_all_layout= find(R.id.review_all_layout);
        review_all= find(R.id.review_all);
        review_score= find(R.id.review_score);
        img_share = find(R.id.img_share);
        img_back = find(R.id.img_back);
        img_face = find(R.id.img_face);
        viewPager = find(R.id.viewpager);
        recyclerView = find(R.id.recycler);
        //line
        view_line_endtime =find(R.id.view_line_endtime);
        //TextView
        tv_good_name = find(R.id.tv_good_name);
        tv_good_author = find(R.id.tv_good_author);
        tv_good_mode = find(R.id.tv_good_mode);
        tv_good_startprice = find(R.id.tv_good_startprice);
        tv_good_deliverymode = find(R.id.tv_good_delivermode);
        tv_good_area = find(R.id.tv_good_area);
        tv_good_endtime = find(R.id.tv_good_endtime);
        tv_good_desc = find(R.id.tv_good_desc);
        tv_auction_more = find(R.id.tv_auction_more);
        tv_good_price = find(R.id.tv_price);
        tv_buy = find(R.id.tv_buy);

        //ViewGroup
        rl_chat = find(R.id.rl_chat);
        ll_auctions = find(R.id.ll_auctions);
        ll_join_user = find(R.id.ll_join_user);
        ll_other_release = find(R.id.ll_other_release);

        //Auction Dialog
        View view = LayoutInflater.from(this).inflate(R.layout.aty_auction_join_dialog, null, false);
        img_jia = (ImageView) view.findViewById(R.id.img_jia);
        img_jian = (ImageView) view.findViewById(R.id.img_jian);
        tv_jia10 = (CustomAuctionTextView) view.findViewById(R.id.tv_jia10);
        tv_jia50 = (CustomAuctionTextView) view.findViewById(R.id.tv_jia50);
        tv_jia100 = (CustomAuctionTextView) view.findViewById(R.id.tv_jia100);
        dialog_price = (EditText) view.findViewById(R.id.dialog_price);
        tv_now_r = (TextView) view.findViewById(aty_dialog_now_r);
        tv_me_r = (TextView) view.findViewById(R.id.aty_dialog_me_r);
        tv_rob = (TextView) view.findViewById(R.id.tv_rob);
        dialog = new Dialog(this, R.style.mask_dialog);

        dialog.setContentView(view);


    }


    @Override
    public void setListener() {
        dialog_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable edt)
            {
                String temp = edt.toString();
                if(TextUtils.isEmpty(temp)){
                    return;
                }
                double price = Double.parseDouble(temp);
                if(price<0){
                    dialog_price.setText("0");
                    return;
                }
                int posDot = temp.indexOf(".");
                if (posDot <= 0)
                {
                    now_price = temp;
                    return;
                }
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                }
                now_price =  edt.toString();
            }
        });
        img_jia.setOnClickListener(this);
        img_jian.setOnClickListener(this);
        tv_jia10.setOnClickListener(this);
        tv_jia50.setOnClickListener(this);
        tv_jia100.setOnClickListener(this);
        img_share.setOnClickListener(this);
        img_face.setOnClickListener(this);
        rl_chat.setOnClickListener(this);
        tv_auction_more.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_buy.setOnClickListener(this);
        tv_rob.setOnClickListener(this);
        jubao.setOnClickListener(this);
        review_all_layout.setOnClickListener(this);
    }

    @Override
    public void initData() {
        AppToastMgr.getInstances().showLoading(this);
        reQuestHttp();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.img_back){
            this.finish();
            return;
        }
        if(mGoodData==null){
            AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.data_error));
            return;
        }
        switch (v.getId()){
            case R.id.review_all_layout:
                startActivity(new Intent(this,GoodsAllReviewActivity.class).putExtra("uid",mGoodData.getUid()));
                break;
            case R.id.jubao:
                startActivity(new Intent(this,ReportActivity.class).putExtra("type",0).putExtra("gid",mGoodData.getGid()));
                break;
            case R.id.rl_chat:
                if(BaisongUtils.getInstances().hasToken(this)){
                    if(!ConfigYibaisong.UID.equals(mGoodData.getUid())){
                        BaisongUtils.goSingleChat(mGoodData.getUid(),this);
                    }
                }
                break;
            case R.id.img_share:
                ShareBean share = new ShareBean();
                share.setTitle(mGoodData.getTitle());
                share.setContent(mGoodData.getDescription());
                share.setUrl(URLs.DETAIL_GOODS_SHARE+mGoodData.getGid()+"/l/"+ConfigYibaisong.LANGUAGE);
                share.setImg_url(img_url);
                new ShareDialog(this,R.style.mask_dialog,share,true).show();
                break;
            case R.id.tv_auction_more:
                intent = new Intent(this,GoodJoinActivity.class);
                intent.putExtra("gid",gid);
                intent.putExtra("mode",mGoodData.getModetype());
                startActivity(intent);
                break;
            case R.id.tv_rob:
                if(!BaisongUtils.getInstances().hasToken(this)){
                    return;
                }
//                if(TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE)){
//                    showBindGoogleDialog();
//                }else{

                    if(TextUtils.isEmpty(dialog_price.getText().toString())){
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_gooddetail_price_null));
                        return;
                    }

                    double price = Double.parseDouble(dialog_price.getText().toString());
                    if(price>Double.parseDouble(good_price)){
                        requestBuy();
                    }else{
                        AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.chujia_error_di));
                    }
//                }


                break;
            case R.id.tv_buy:
                if(!BaisongUtils.getInstances().hasToken(this)){
                    return;
                }
                if(ConfigYibaisong.UID.equals(mGoodData.getUid())){
                    AppToastMgr.showToast(AppResourceMgr.getString(this,R.string.toast_notbuy_me));
                    return;
                }
//                if(TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE)){
//                    showBindGoogleDialog();
//                }else{
//                    if(!TextUtils.isEmpty(ConfigYibaisong.GOOGLE_CODE)&&ConfigYibaisong.NEED_GOOGLE_CHECK){
//                        startActivity(new Intent(this,MeGoogleCheckActivity.class));
//                        return;
//                    }
                    if(ConfigYibaisong.AUCTION_MODE.equals(mode)){
                        showCostDialog();
                    }else{
                        requestBuy();
                    }
//                }

                break;
            case R.id.img_jia:
                if(TextUtils.isEmpty(now_price)){
                    now_price ="0";
                }
                dialog_price.setText((Double.parseDouble(now_price)+1)+"");
                break;
            case R.id.img_jian:
                if(TextUtils.isEmpty(now_price)){
                    now_price ="0";
                }
                dialog_price.setText((Double.parseDouble(now_price)-1)+"");
                break;
            case R.id.tv_jia10:
                if(TextUtils.isEmpty(now_price)){
                    now_price ="0";
                }
                dialog_price.setText((Double.parseDouble(now_price)+10)+"");
                break;
            case R.id.tv_jia50:
                if(TextUtils.isEmpty(now_price)){
                    now_price ="0";
                }
                dialog_price.setText((Double.parseDouble(now_price)+50)+"");
                break;
            case R.id.tv_jia100:
                if(TextUtils.isEmpty(now_price)){
                    now_price ="0";
                }
                dialog_price.setText((Double.parseDouble(now_price)+100)+"");
                break;
            case R.id.img_face:
                intent = new Intent(this,PersonalInfoActivity.class);
                intent.putExtra("uid",mGoodData.getUid());
                startActivity(intent);
                break;
        }
    }

    /***
     * 购买
     */
    private void requestBuy() {
        AppToastMgr.getInstances().showLoading(this);
        HashMap<String,String> map = new HashMap<>();
        map.put("gid",gid);
        map.put("address",null);
        map.put("privkey",ConfigYibaisong.USER_KEY);
        if(ConfigYibaisong.AUCTION_MODE.equals(mGoodData.getModetype())){
            map.put("userUpPrice",dialog_price.getText().toString());
        }
        BaiSongHttpManager.getInstance().postRequest(URLs.JOIN, "Join,index", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    switch (json.optInt("status")){
                        case 6://成功
                            AppToastMgr.showToast(json.optString("msg"));
                            dialog.dismiss();
                            reQuestHttp();
                            break;
                        case 12://有默认地址
                            AddressBean.AddressData bean = ConfigYibaisong.jsonToBean(json.optString("address"),AddressBean.AddressData.class);
                            select_add(bean);
                            break;
                        case 11://没有默认地址
                            DialogUtils.getInstance().initView(GoodsDetailActivity.this).setBottonNums(DialogUtils.BtnNumTwo)
                                    .setTitle(AppResourceMgr.getString(GoodsDetailActivity.this,R.string.tip)).addCallListener(new DialogUtils.CallBack() {
                                @Override
                                public void onSure() {
                                    select_add(null);
                                    DialogUtils.getInstance().dismiss();
                                }
                                @Override
                                public void onCancle() {
                                    DialogUtils.getInstance().dismiss();
                                }
                            }).show(AppResourceMgr.getString(GoodsDetailActivity.this,R.string.set_address));

                            break;
                        case 16:
                                BaisongUtils.getInstances().goToGoogleCheck(GoodsDetailActivity.this);
                            break;
                        default:
                            AppToastMgr.showToast(json.optString("msg"));
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
            }
        });
    }


    /***
     * 选择地址
     * @param bean
     */
    private void select_add(AddressBean.AddressData bean){
        intent = new Intent(this,SelectAddressActivity.class);
        if(ConfigYibaisong.AUCTION_MODE.equals(mode)){
            intent.putExtra("price",dialog_price.getText().toString().trim());
        }
        intent.putExtra("gid",gid);
        intent.putExtra("data",bean);
        startActivityForResult(intent,66);
    }




    /***
     * 网络请求
     */
    private void reQuestHttp(){
        BaiSongHttpManager.getInstance().getRequest(URLs.GOOD_DETAIL + gid, "goods,detail",new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                GoodDetailBean bean = ConfigYibaisong.jsonToBean(result,GoodDetailBean.class);
                if(bean.getStatus()==1){
                    if(bean.getScore().equals("0.0")){
                        review_all_layout.setVisibility(View.GONE);
                    }else{
                        review_all_layout.setVisibility(View.VISIBLE);
                    }
                    review_score.setText(bean.getScore());
                    mybanlances = bean.getMyBalance();
                    if(!TextUtils.isEmpty(mybanlances)){
                        if(!"0".equals(mybanlances)){
                            if(mybanlances.length()>8){
                                int index = mybanlances.lastIndexOf(".");
                                mybanlances = mybanlances.substring(0,index+3);
                            }
                            tv_me_r.setText(AppResourceMgr.getString(GoodsDetailActivity.this,R.string.gooddetail_dialog_mybid)+mybanlances);
                        }

                    }

                    initGoodValue(bean.getData());
                    initJoinUser(bean.getJoinlog());
                    initSendGoods(bean.getSendlog());
                }else {
                    AppToastMgr.showToast(bean.getMsg());
                }

            }



            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
            }
        });
    }

    /***
     * 送过的
     * @param sendlog
     */
    private void initSendGoods(List<GoodDetailBean.SendGood> sendlog) {
        if(sendlog==null){
            return;
        }
        if(sendlog.size()>0){
            ll_other_release.setVisibility(View.VISIBLE);
        }
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new MyRecyclerAdapter(sendlog));
    }

    /***
     * 参加的人
     * @param joinlog
     */
    private void initJoinUser(List<GoodDetailBean.JoinUser> joinlog) {
        if(joinlog==null){
            return;
        }
        if(joinlog.size()>0){
            ll_join_user.setVisibility(View.VISIBLE);
        }
        ll_auctions.removeAllViews();
        for (int i=0;i<(joinlog.size()>3?3:joinlog.size());i++){
            View view = LayoutInflater.from(this).inflate(R.layout.item_gooddetail_joinuser,null,false);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
            TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
            CircleImageView img_face = (CircleImageView) view.findViewById(R.id.img_face);
            GoodDetailBean.JoinUser bean =joinlog.get(i);
            AppImageLoadHttp.getInstance().loadImage(this,bean.getFace()).into(img_face);
            tv_name.setText(bean.getNickname());
            tv_price.setText("LAP:"+bean.getMoney());
            tv_time.setText(DateTimerUtils.getWhatTime(this,Long.parseLong(bean.getTime())));
            ll_auctions.addView(view);
        }


    }
    /***
     * 出价，竞拍dialog
     */
    private void showCostDialog() {
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams dialogParmas = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        dialogParmas.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialogParmas.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.show();
    }


    /***
     * 初始化物品相关
     * @param data
     */
    private void initGoodValue(GoodDetailBean.GoodData data) {
        if(data==null){
            return;
        }
        DbManager dbManager = new DbManager(this);
        now_price = data.getPrice();
        good_price = now_price;
        HxUserBean bean = dbManager.findHxUser(data.getUid());
        if(bean==null){
            bean = new HxUserBean();
            bean.setName(data.getNickname());
            bean.setFace(data.getFace());
            bean.setEmail(data.getEmail());
            bean.setUid(data.getUid());
            dbManager.addHxUser(bean);
        }
        email = data.getEmail();
        String status;
        if("2".equals(data.getModetype())){
            tv_good_endtime.setVisibility(View.GONE);
            view_line_endtime.setVisibility(View.GONE);
        }else{
            tv_good_endtime.setVisibility(View.VISIBLE);
            view_line_endtime.setVisibility(View.VISIBLE);
        }
        if("0".equals(data.getIs_sale())){//结束
            tv_good_endtime.setText(AppResourceMgr.getString(this,R.string.gooddetail_endtime)+" "+AppResourceMgr.getString(this,R.string.good_over));
            tv_buy.setBackgroundResource(R.drawable.grey_rectangle_solid);
            tv_buy.setEnabled(false);
            isOver  =true;
            status = AppResourceMgr.getString(this,R.string.good_over);
        }else{//进行中
            isOver = false;
            if("2".equals(data.getModetype())){
                tv_good_endtime.setText(AppResourceMgr.getString(this,R.string.gooddetail_endtime)+AppResourceMgr.getString(this,R.string.longTime));
            }else{
                if(!TextUtils.isEmpty(data.getDown_time())){

                    initCountDownTimer(data.getDown_time());
                }
            }

            status = AppResourceMgr.getString(this,R.string.good_loading);
            tv_buy.setBackgroundResource(R.drawable.blue_rectangle_solid);
            tv_buy.setEnabled(true);
        }
        AppImageLoadHttp.getInstance().loadImage(this,data.getFace()).into(img_face);
        mGoodData = data;
        tv_good_name.setText(data.getTitle());
        if("2".equals(data.getModetype())){
            mode="2";
            tv_good_mode.setText(AppResourceMgr.getString(this,R.string.mode_fixed)+"~ "+status);
            tv_good_startprice.setText(AppResourceMgr.getString(this,R.string.gooddetail_price)+" "+now_price+" LAP");
            tv_good_startprice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gooddetail_fixedprice,0,0,0);
        }else{
            mode="3";
            tv_good_mode.setText(AppResourceMgr.getString(this,R.string.mode_auction)+"~ "+status);
            tv_good_startprice.setText(AppResourceMgr.getString(this,R.string.gooddetail_startprice)+" "+now_price+" LAP");
            tv_good_startprice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gooddetail_startprice,0,0,0);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(mGoodData.getImage()));
        if(mGoodData.getImage()!=null&&mGoodData.getImage().size()>0){
            img_url =mGoodData.getImage().get(0);
        }

        if("1".equals(data.getPosttype())){
            tv_good_deliverymode.setText(AppResourceMgr.getString(this,R.string.gooddetail_dilivermoder)+" "+AppResourceMgr.getString(this,R.string.issue_delivery_mode_free));
        }else{
            tv_good_deliverymode.setText(AppResourceMgr.getString(this,R.string.gooddetail_dilivermoder)+" "+AppResourceMgr.getString(this,R.string.issue_delivery_mode_cash));
        }

        dialog_price.setText(now_price);
        tv_now_r.setText(AppResourceMgr.getString(this,R.string.gooddetail_dialog_nowbid)+data.getPrice());
        tv_good_price.setText("LAP "+data.getPrice()+" ");
        tv_good_author.setText(AppResourceMgr.getString(this,R.string.gooddetail_author)+" "+data.getNickname());

        tv_good_area.setText(AppResourceMgr.getString(this,R.string.gooddetail_goodarea)+" "+data.getAddress());

        tv_good_desc.setText(data.getDescription());
    }

    /**
     * 倒计时
     * @param down_time
     */
    private void initCountDownTimer(String down_time) {
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        long time = Long.parseLong(down_time)*1000 - System.currentTimeMillis();
        timer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_good_endtime.setText(AppResourceMgr.getString(GoodsDetailActivity.this,R.string.gooddetail_endtime)+" "+DateTimerUtils.long2hour(millisUntilFinished));

            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }


    /***
     * ViewPagerAdapter
     */
    private class MyViewPagerAdapter extends PagerAdapter{
        List<String> list ;

        public MyViewPagerAdapter(List<String> list) {
            this.list = list;
        }



        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView view = new ImageView(GoodsDetailActivity.this);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            AppImageLoadHttp.getInstance().loadImage(GoodsDetailActivity.this,list.get(position)).into(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoDialog = new BigPhotoDialog(GoodsDetailActivity.this,position,list);
                    photoDialog.showDialog();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return list.size();
        }


    }



    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{
        private List<GoodDetailBean.SendGood> mList;
        public MyRecyclerAdapter(List<GoodDetailBean.SendGood> list){
            this.mList = list;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GoodsDetailActivity.this).inflate(R.layout.item_good,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final GoodDetailBean.SendGood bean = mList.get(position);
            if(!TextUtils.isEmpty(bean.getTitle()))
                holder.tv_title.setText(bean.getTitle());
            if(!TextUtils.isEmpty(bean.getPrice()))
                holder.tv_price.setText("LAP: "+bean.getPrice());
            AppImageLoadHttp.getInstance().loadImage(GoodsDetailActivity.this,bean.getCover()).into(holder.img_good);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GoodsDetailActivity.this,GoodsDetailActivity.class).putExtra("gid",bean.getGid()));
                }
            });

        }

        @Override
        public int getItemCount() {
            if(mList==null)
                return 0;

            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv_title,tv_price;
            ImageView img_good;
            public ViewHolder(View itemView) {
                super(itemView);
                img_good = (ImageView) itemView.findViewById(R.id.img_good);
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            }
        }


    }
    private void showBindGoogleDialog(){

            DialogUtils.getInstance().showAllDialog(this, getResources().getString(R.string.tip), getResources().getString(R.string.bindGoogleTip),
                    getResources().getString(R.string.cancel),  getResources().getString(R.string.ok), new DialogUtils.CallBack() {
                        @Override
                        public void onSure() {
                            DialogUtils.getInstance().dismiss();
                            startActivity(new Intent(GoodsDetailActivity.this,MeGoogleBindActivity.class));
                        }

                        @Override
                        public void onCancle() {
                            DialogUtils.getInstance().dismiss();
                        }
                    });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Util.isOnMainThread()&&!this.isFinishing()){
            Glide.with(this).pauseRequests();
        }
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);//完成回调
        if(resultCode==RESULT_OK&&requestCode==66){
            reQuestHttp();
//                tv_good_price.setText(AppResourceMgr.getString(this,R.string.gooddetail_price)+" "+dialog_price.getText().toString());
//                tv_buy.setEnabled(false);
//                tv_buy.setBackgroundResource(R.drawable.grey_circle_5_solid);
        }
    }
}
