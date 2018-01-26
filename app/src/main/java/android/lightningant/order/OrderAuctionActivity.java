package android.lightningant.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inter.baisong.R;
import inter.baisong.base.BaseActivity;
import inter.baisong.fragment.AuctionOrderFailedFragment;
import inter.baisong.fragment.AuctionOrderProgressFragment;
import inter.baisong.fragment.AuctionOrderSuccessFragment;
import inter.baisong.utils.AppResourceMgr;

/**
 * Created by ${刘全伦} on 2017/9/6.
 * 名称:
 */

public class MeOrderAuctionActivity extends BaseActivity  {
    private ViewPager viewpager;
    private TextView tv_success,tv_participateIn,tv_failed,tv_title;
    private ImageView img_left;
    private Fragment frags[];
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.me_order_auction;
    }

    @Override
    public void initView() {
        viewpager =find(R.id.viewpager);
        tv_success = find(R.id.tv_success);
        tv_participateIn = find(R.id.tv_participateIn);
        tv_title = find(R.id.tv_title);
        tv_failed = find(R.id.tv_failed);
        img_left = find(R.id.img_left);
        frags = new Fragment[3];
        frags[0] = new AuctionOrderSuccessFragment();
        frags[1] = new AuctionOrderProgressFragment();
        frags[2] = new AuctionOrderFailedFragment();
    }

    @Override
    public void initData() {
        tv_title.setText(AppResourceMgr.getString(this,R.string.orderAuction));
        viewpager.setOffscreenPageLimit(3);
        viewpager.setAdapter(new MyAdapter(getSupportFragmentManager()));

    }



    @Override
    public void setListener() {
        img_left.setOnClickListener(this);
        tv_success.setOnClickListener(this);
        tv_participateIn.setOnClickListener(this);
        tv_failed.setOnClickListener(this);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        changeView(0);
                        break;
                    case 1:
                        changeView(1);
                        break;
                    case 2:
                        changeView(2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /***
     * 改变tab状态
     * @param position
     */
    private void changeView(int position){
        //重置
        tv_success.setTextColor(AppResourceMgr.getColor(MeOrderAuctionActivity.this,R.color.tv_most_black));
        tv_participateIn.setTextColor(AppResourceMgr.getColor(MeOrderAuctionActivity.this,R.color.tv_most_black));
        tv_failed.setTextColor(AppResourceMgr.getColor(MeOrderAuctionActivity.this,R.color.tv_most_black));
        tv_success.setBackgroundResource(R.color.white);
        tv_participateIn.setBackgroundResource(R.color.white);
        tv_failed.setBackgroundResource(R.color.white);
        //填充
        switch (position){
            case 0:
                tv_success.setTextColor(AppResourceMgr.getColor(MeOrderAuctionActivity.this,R.color.app_color_theme));
                tv_success.setBackgroundResource(R.drawable.rectangle_bottom_line_blue);
                break;
            case 1:
                tv_participateIn.setTextColor(AppResourceMgr.getColor(MeOrderAuctionActivity.this,R.color.app_color_theme));
                tv_participateIn.setBackgroundResource(R.drawable.rectangle_bottom_line_blue);
                break;
            case 2:
                tv_failed.setTextColor(AppResourceMgr.getColor(MeOrderAuctionActivity.this,R.color.app_color_theme));
                tv_failed.setBackgroundResource(R.drawable.rectangle_bottom_line_blue);
                break;
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_left:
                this.finish();
                break;
            case R.id.tv_success:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_participateIn:
                viewpager.setCurrentItem(1);
                break;
            case R.id.tv_failed:
                viewpager.setCurrentItem(2);
                break;
        }
    }


    private class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==200 &&data.getIntExtra("position",-1)!=-1){

            ((AuctionOrderSuccessFragment)frags[0]).pingjiaRefresh(data.getIntExtra("position",-1));
        }
    }
}
