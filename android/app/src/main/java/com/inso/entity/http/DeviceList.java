package com.inso.entity.http;

import java.util.List;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/22
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class DeviceList {


    /**
     * errcode : 0
     * errmsg : ok
     * result : [{"user_id":"lj50mGr7EJkn9vNp","device_id":"asdfasdfasdfasdf","product_model":"mi_watch2","mac":"12:12:12:12:12:12","created_at":"1552645881"},{"user_id":"lj50mGr7EJkn9vNp","device_id":"asdfasdfasdfasdf2224","product_model":"mi_watch2","mac":"12:12:12:12:12:14","created_at":"1552876327"}]
     */

    private int errcode;
    private String errmsg;
    private List<ResultBean> result;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * user_id : lj50mGr7EJkn9vNp
         * device_id : asdfasdfasdfasdf
         * product_model : mi_watch2
         * mac : 12:12:12:12:12:12
         * created_at : 1552645881
         */

        private String user_id;
        private String device_id;
        private String product_model;
        private String mac;
        private String created_at;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getProduct_model() {
            return product_model;
        }

        public void setProduct_model(String product_model) {
            this.product_model = product_model;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
