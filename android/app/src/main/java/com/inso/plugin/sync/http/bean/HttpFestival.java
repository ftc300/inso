package com.inso.plugin.sync.http.bean;

import java.util.List;

/**
 * Created by chendong on 2017/4/12.
 */

public class HttpFestival {

    /**
     * version : 1
     * list : [{"type":1,"date":"2017-01-02"},{"type":2,"date":"2017-01-22"},{"type":1,"date":"2017-01-27"},{"type":1,"date":"2017-01-30"},{"type":1,"date":"2017-01-31"},{"type":1,"date":"2017-02-01"},{"type":1,"date":"2017-02-02"},{"type":2,"date":"2017-02-04"},{"type":2,"date":"2017-04-01"},{"type":1,"date":"2017-04-03"},{"type":1,"date":"2017-04-04"},{"type":1,"date":"2017-05-01"},{"type":2,"date":"2017-05-27"},{"type":1,"date":"2017-05-29"},{"type":1,"date":"2017-05-30"},{"type":2,"date":"2017-09-30"},{"type":1,"date":"2017-10-02"},{"type":1,"date":"2017-10-03"},{"type":1,"date":"2017-10-04"},{"type":1,"date":"2017-10-05"},{"type":1,"date":"2017-10-06"}]
     */
    private String version;
    private List<Festival> list;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Festival> getList() {
        return list;
    }

    public void setList(List<Festival> list) {
        this.list = list;
    }

    public static class Festival {
        /**
         * type : 1
         * date : 2017-01-02
         */
        private int type;
        private String date;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
