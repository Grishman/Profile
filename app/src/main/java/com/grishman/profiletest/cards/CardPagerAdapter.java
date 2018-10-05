package com.grishman.profiletest.cards;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.grishman.profiletest.R;
import com.grishman.profiletest.model.Card;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private List<Card> mData2;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mData2 = new ArrayList<>();
        mViews = new ArrayList<>();
    }

//    public void swapItems(List<CardItem> newDataold) {
//        this.mData = newDataold;
//        notifyDataSetChanged();
//    }

    public void swapItems(List<Card> newData) {
        this.mData2 = newData;
        notifyDataSetChanged();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }


    @Override
    public int getCount() {
        return mData2.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.card_item_layout, container, false);
        container.addView(view);
        //bind(mData.get(position), view);
//        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        Picasso.get()
                .load(mData2.get(position).getImageUrl())
                .placeholder(R.drawable.ic_mobile_info)
                .into(imageView);
        if (mBaseElevation == 0) {
            // mBaseElevation = cardView.getCardElevation();
        }

//        cardView.setMaxCardElevation(mBaseElevation *4);
//        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

//    private void bind(CardItem item, View view) {
//        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
//        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
//        titleTextView.setText(item.getTitle());
//        contentTextView.setText(item.getText());
//    }

}
