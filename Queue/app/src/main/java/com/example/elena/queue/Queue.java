package com.example.elena.queue;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elena on 9/12/2017.
 */

public class Queue implements Parcelable{
    private List<String> mItems;
    private int mLastItemIndex;

    private Queue(Parcel in){
        in.readStringList(mItems);
        mLastItemIndex = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mItems);
        dest.writeInt(mLastItemIndex);
    }

    public final Parcelable.Creator<Queue> CREATOR  = new Parcelable.Creator<Queue>(){
        @Override
        public Queue createFromParcel(Parcel source) {
            return new Queue(source);
        }

        @Override
        public Queue[] newArray(int size) {
            return new Queue[size];
        }
    };


    public Queue(){
        mItems = new ArrayList<>();
        mLastItemIndex = -1;
    }

    public List<String> getmItems() {
        return mItems;
    }

    public int getCount(){
        return mItems.size();
    }

    public void put(String item){
        mLastItemIndex++;
        mItems.add(mLastItemIndex, item);

    }

    public String get(){
        if (getCount() != 0){
            String item = mItems.get(0);

            mLastItemIndex--;
            mItems.remove(0);
            return item;
        }
        return null;
    }

    public String peek(){
        return mItems.get(0);
    }
}

