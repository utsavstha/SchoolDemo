package com.example.utsav.schooldemo.Utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.utsav.schooldemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 1/20/2016.
 */
public class CustomPagerAdapter extends PagerAdapter {

    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    List<String> imageList = new ArrayList<>();
    Context mContext;
    LayoutInflater mLayoutInflater;

    public CustomPagerAdapter(Context context, List<String > x) {
        mContext = context;
        imageList = x;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        mNetworkImageView = (NetworkImageView) itemView.findViewById(R.id
                .img_pager_item);

        mImageLoader = CustomVolleyRequestQueue.getInstance(mContext)
                .getImageLoader();
        //Image URL - This can point to any image file supported by Android
        final String url = imageList.get(position);
        mImageLoader.get(url, ImageLoader.getImageListener(mNetworkImageView,
                android.R.drawable.arrow_down_float, android.R.drawable
                        .ic_dialog_alert));
        mNetworkImageView.setImageUrl(url, mImageLoader);

        /*ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        imageView.setImageResource(mResources[position]);*/

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
