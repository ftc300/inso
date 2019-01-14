package com.inso.plugin.sync.http;

/**
 * Created by chendong on 2017/5/5.
 */

public class RequestParams {
        public  String model;
        public  String uid;
        public String did;
        public String type;
        public String key;
        public String  value;
        public int time;
        public int  limit;
        public int  timeStart;
        public int  timeEnd;
    // follow is appconfig
        public String name;
        public int version;
        public String lang;
        public String app_id;


    public RequestParams(String model,String name, int version) {
        this.model = model;
        this.name = name;
        this.version = version;
    }

    public RequestParams(String name, int version, String lang, String app_id) {
        this.name = name;
        this.version = version;
        this.lang = lang;
        this.app_id = app_id;
    }

    public RequestParams(String model, String uid, String did, String type, String key, String value , int time) {
            this.model = model;
            this.uid = uid;
            this.did = did;
            this.type = type;
            this.key = key;
            this.time = time;
            this.value = value;
        }

        public RequestParams(String model,String uid, String did, String type, String key, int time, int limit, int timeStart, int timeEnd) {
            this.model = model;
            this.uid = uid;
            this.did = did;
            this.type = type;
            this.key = key;
            this.time = time;
            this.limit = limit;
            this.timeStart = timeStart;
            this.timeEnd = timeEnd;
        }
    }
