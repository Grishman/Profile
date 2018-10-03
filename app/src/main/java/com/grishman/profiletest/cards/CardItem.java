package com.grishman.profiletest.cards;


import org.jetbrains.annotations.NotNull;

public class CardItem {

    private int mTextResource;
    private int mTitleResource;
    private String imageUrl;

    public CardItem(int title, int text, String url) {
        mTitleResource = title;
        mTextResource = text;
        imageUrl = url;
    }

    public CardItem(@NotNull String s) {
        imageUrl = s;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
