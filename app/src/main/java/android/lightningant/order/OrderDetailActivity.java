package android.lightningant.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.base.ConfigYibaisong;
import inter.baisong.base.URLs;
import inter.baisong.bean.MeOrderDetailBean;
import inter.baisong.bean.OrderItemBean;
import inter.baisong.utils.AppResourceMgr;
import inter.baisong.utils.AppToastMgr;
import inter.baisong.utils.BaiSongHttpManager;
import inter.baisong.utils.DateTimerUtils;
import inter.baisong.utils.DialogUtils;
import inter.baisong.widgets.CircleImageView;

/**
 * Created by ${刘全伦} on 2017/9/11.
 * 名称:
 */

public class MeOrderDetailActivity extends BaseActivity {
    private TextView order_type,order_tel,order_name,order_address
            ,order_pay,goods_text,wuliu_company,wuliu_number,order_number,tv_user_name,
    create_time,delivery_time,closing_time,goods_confirm,goods_return;
    private ImageView goods_img,back_black;
    private CircleImageView order_face;
    private String oid;
    private String url;
    private String controller;
    private int ordertype;//1 Send   0 Rob
    private OrderItemBean bean;
    @Override
    public void initParms(Bundle parms) {
        try {
            oid = getIntent().getStringExtra("oid");
            ordertype = parms.getInt("type",0);
            if(ordertype==1){//Send
                url = URLs.ME_ORDER_DETAIL+oid;
                controller ="Order,orderSendDetail";
            }else{//Rob
                url = URLs.ME_ORDER_ROB_DETAIL+oid;
                controller ="Order,orderDetailByGet";
            }
        } catch (Exception e) {
            e.printStackTrace();
            url = URLs.ME_ORDER_ROB_DETAIL+oid;
            controller ="Order,orderDetailByGet";
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.me_order_detail;
    }

    @Override
    public void initView() {
        back_black = (ImageView) findViewById(R.id.back_black);
        order_type = (TextView) findViewById(R.id.order_type);
        order_tel = (TextView) findViewById(R.id.order_tel);
        order_name = (TextView) findViewById(R.id.order_name);
        order_address = (TextView) findViewById(R.id.order_address);
        order_pay = (TextView) findViewById(R.id.order_pay);
        goods_text = (TextView) findViewById(R.id.goods_text);
        wuliu_company = (TextView) findViewById(R.id.wuliu_company);
        wuliu_number = (TextView) findViewById(R.id.wuliu_number);
        order_number = (TextView) findViewById(R.id.order_number);
        create_time = (TextView) findViewById(R.id.create_time);
        delivery_time = (TextView) findViewById(R.id.delivery_time);
        closing_time = (TextView) findViewById(R.id.closing_time);
        goods_confirm = (TextView) findViewById(R.id.goods_confirm);
        goods_return = (TextView) findViewById(R.id.goods_return);
        goods_img= (ImageView) findViewById(R.id.goods_img);
        order_face= (CircleImageView) findViewById(R.id.order_face);
        tv_user_name=find(R.id.tv_user_name);
    }

    @Override
    public void initData() {

        requestHttp();
    }


    private void requestHttp() {
        BaiSongHttpManager.getInstance().getRequest(url,controller, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                MeOrderDetailBean bean = ConfigYibaisong.jsonToBean(result,MeOrderDetailBean.class);
                if(bean.getStatus()==1){
                    setView(bean.getData());
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {

            }
        });
    }

    private void setView(OrderItemBean data){
        if(data==null){
            return;
        }
        bean = data;
        int ostate = Integer.valueOf(data.getOstate());
            switch (ostate){
                case 1:
                    order_type.setText(AppResourceMgr.getString(this,R.string.ostate_1));
                    if(ordertype==1){
                        goods_confirm.setVisibility(View.VISIBLE);
                    }else{
                        goods_return.setVisibility(View.VISIBLE);
                        goods_return.setText(AppResourceMgr.getString(this,R.string.order_cancel));
                    }
                    break;
                case 3:
                    order_type.setText(AppResourceMgr.getString(this,R.string.ostate_3));
                    if(ordertype==1){
                        goods_confirm.setVisibility(View.VISIBLE);
                    }else{
                        goods_return.setVisibility(View.VISIBLE);
                        goods_confirm.setVisibility(View.VISIBLE);
                    }
                    break;
                case 5:
                    order_type.setText(AppResourceMgr.getString(this,R.string.ostate_5));
                    if(ordertype==1){
                        goods_confirm.setVisibility(View.VISIBLE);
                        goods_return.setVisibility(View.VISIBLE);
                    }else{
                        goods_return.setVisibility(View.VISIBLE);
                        goods_confirm.setVisibility(View.VISIBLE);
                    }
                    break;
                case 7:
                    order_type.setText(AppResourceMgr.getString(this,R.string.ostate_7));
                    if(ordertype==1){
                        goods_confirm.setVisibility(View.VISIBLE);
                    }else{
                    }
                    break;
                case 9:
                    order_type.setText(AppResourceMgr.getString(this,R.string.ostate_9));
                    if(ordertype==1){
                        goods_confirm.setVisibility(View.VISIBLE);
                    }else{
                    }
                    break;
            }

        if(!TextUtils.isEmpty(data.getNickname())){
            tv_user_name.setText(data.getNickname());
        }
        if(!TextUtils.isEmpty(data.getConsignee())){
            order_name.setText(data.getConsignee());
        }else{
            order_name.setText("null");
        }
        if(!TextUtils.isEmpty(data.getTitle())){
            goods_text.setText(data.getTitle());
        }else{
            goods_text.setText("null");
        }

        if(!TextUtils.isEmpty(data.getMoney())){
            order_pay.setText("LAP: "+data.getMoney());
        }else{
            order_pay.setText("null");
        }
        if(!TextUtils.isEmpty(data.getAddress())){
            order_address.setText(data.getCity()+data.getAddress());
        }else{
            order_address.setText("null");
        }
        if(!TextUtils.isEmpty(data.getExpress_company())){
            wuliu_company.setText(data.getExpress_company());
        }else{
            wuliu_company.setText("null");
        }

        if(!TextUtils.isEmpty(data.getTime())){
            create_time.setText(DateTimerUtils.strToDateLong(data.getTime(),"yyyy-MM-dd"));
        }else{
            create_time.setText("null");
        }
        if(!TextUtils.isEmpty(data.getSendtime())){
            delivery_time.setText(DateTimerUtils.strToDateLong(data.getSendtime(),"yyyy-MM-dd"));
        }else{
            delivery_time.setText("null");
        }
        if(!TextUtils.isEmpty(data.getExpress_number())){
            wuliu_number.setText(data.getExpress_number());
        }
        if(!TextUtils.isEmpty(data.getConfirmtime())){
            closing_time.setText(data.getConfirmtime());
        }else{
            closing_time.setText("null");
        }

        if(!TextUtils.isEmpty(data.getMobile())){
            order_tel.setText(data.getMobile());
        }else{
            order_tel.setText("null");
        }
        Glide.with(this).load(data.getFace()).error(R.drawable.normal_head_img).into(order_face);
        Glide.with(this).load(data.getCover()).error(R.drawable.normal_head_img).into(goods_img);
    }
    @Override
    public void setListener() {
        back_black.setOnClickListener(this);
        goods_confirm.setOnClickListener(this);
        goods_return.setOnClickListener(this);
        order_face.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_face:
                startActivity(new Intent(this,PersonalInfoActivity.class).putExtra("uid",bean.getUid()));
                break;
            case R.id.back_black:
                finish();
                break;
            case R.id.goods_confirm:
                if(ordertype==1){
                    if("1".equals(bean.getOstate())){//未发货
                        startActivity(new Intent(this,MeDeliverGoodsActivity.class)
                                .putExtra("oid",bean.getOid())
                                .putExtra("type",1)
                        );
                    }else {//已发货
                        startActivity(new Intent(this,MeDeliverGoodsActivity.class)
                                .putExtra("oid",bean.getOid())
                                .putExtra("type",0)
                        );
                    }
                }else {
                    sure();
                }

                break;
            case R.id.goods_return:
                if(ordertype==1){//送过的  同意退币
                    DialogUtils.getInstance().initView(this).setBottonNums(DialogUtils.BtnNumTwo).addCallListener(new DialogUtils.CallBack() {
                        @Override
                        public void onSure() {
                            requestReturn();
                        }

                        @Override
                        public void onCancle() {
                            DialogUtils.getInstance().dismiss();
                        }
                    }).show(AppResourceMgr.getString(this,R.string.agree_tuibi_request));
                }else {//抢到的
                    if("1".equals(bean.getOstate())){
                        DialogUtils.getInstance().initView(this).setTitle(AppResourceMgr.getString(this,R.string.tip)).setBottonNums(DialogUtils.BtnNumTwo)
                                .addCallListener(new DialogUtils.CallBack() {
                                    @Override
                                    public void onSure() {
                                        DialogUtils.getInstance().dismiss();
                                        cancelOrder();
                                    }

                                    @Override
                                    public void onCancle() {
                                        DialogUtils.getInstance().dismiss();
                                    }
                                }).show(AppResourceMgr.getString(this,R.string.dialog_order_cancel));
                    }else
                    startActivity(new Intent(this,MeFeedBackActivity.class).putExtra("type",1).putExtra("uid",bean.getUid()).putExtra("oid",bean.getOid()));
                }

                break;
        }
    }
    private void sure(){
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("oid",oid);
        map.put("privkey",ConfigYibaisong.USER_KEY);
        BaiSongHttpManager.getInstance().postRequest(URLs.ME_ORDER_SURE, "TakeDelivery,index",map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
                        AppToastMgr.showToast(getResources().getString(R.string.success));
                        order_type.setText(AppResourceMgr.getString(MeOrderDetailActivity.this,R.string.ostate_9));
                        goods_confirm.setVisibility(View.GONE);
                    }else{
                        AppToastMgr.showToast(json.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.getInstances().cancel();
                AppToastMgr.showError();
            }
        });
    }

    /***
     * 同意申诉
     */
    private void requestReturn() {
        AppToastMgr.getInstances().showLoading(this);
        Map<String,String> map = new HashMap<>();
        map.put("oid",oid);
        map.put("privkey",ConfigYibaisong.USER_KEY);
        BaiSongHttpManager.getInstance().postRequest(URLs.ORDER_SHENSU_SUCCESS, "GoodsBack,index",map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                DialogUtils.getInstance().dismiss();
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){
                        goods_return.setVisibility(View.GONE);
                        bean.setOstate("7");
                        order_type.setText(AppResourceMgr.getString(MeOrderDetailActivity.this,R.string.ostate_7));
                    }else{
                        AppToastMgr.showToast(json.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.showError();
                AppToastMgr.getInstances().cancel();
                DialogUtils.getInstance().dismiss();
            }
        });
    }


    /***
     * 取消订单
     * @param
     */
    private void cancelOrder() {
        AppToastMgr.getInstances().showLoading(this);
        Map map = new HashMap();
        map.put("oid",bean.getOid());
        map.put("privkey",ConfigYibaisong.USER_KEY);
        BaiSongHttpManager.getInstance().postRequest(URLs.ORDER_CANCEL, "Order,applyCancleOrder", map, new BaiSongHttpManager.HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                AppToastMgr.getInstances().cancel();
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.optInt("status")==1){

                    }else{
                    }
                    AppToastMgr.showToast(json.optString("msg"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error_msg, Throwable e) {
                AppToastMgr.showError();
                AppToastMgr.getInstances().cancel();
            }
        });

    }
}
