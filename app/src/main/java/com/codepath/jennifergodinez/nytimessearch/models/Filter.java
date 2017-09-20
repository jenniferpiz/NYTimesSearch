package com.codepath.jennifergodinez.nytimessearch.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jennifergodinez on 9/20/17.
 */

public class Filter {
    String mDate, mSortOrder;
    ArrayList<String> mNewsDeskList;

    public Filter() {
        mDate = "";
        mSortOrder = "";
        mNewsDeskList = new ArrayList<>();
    }

    public Filter(String mDate, String mSortOrder, ArrayList<String> mNewsDesk) {
        this.mDate = mDate;
        this.mSortOrder = mSortOrder;
        this.mNewsDeskList = mNewsDesk;
    }

    public String getDate() {
        return mDate;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public ArrayList<String> getNewsDeskList() {
        return mNewsDeskList;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setSortOrder(String mSortOrder) {
        this.mSortOrder = mSortOrder;
    }

    public void setNewsDeskList(ArrayList<String> mNewsDesk) {
        this.mNewsDeskList = mNewsDesk;
    }

    //prepare the filter for http request
    public HashMap<String,String> toMap() {
        HashMap<String, String> filterMap = new HashMap<>();

        if (!"".equals(this.getDate())) {
            filterMap.put("begin_date", this.getDate().replaceAll("-",""));
        }

        if (!"".equals(this.getSortOrder())) {
            filterMap.put("sort", this.getSortOrder());
        }

        if (this.getNewsDeskList().size()>0) {
            String sNewsDesk= "news_desk:(";
            for (String newsDesk : this.getNewsDeskList()) {
                sNewsDesk += "\""+newsDesk+"\"%20";
            }
            sNewsDesk += ")";
            filterMap.put("fq", sNewsDesk);
        }
        return filterMap;
    }
}
