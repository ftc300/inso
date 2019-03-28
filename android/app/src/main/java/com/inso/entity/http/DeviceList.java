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
     * result : [{"user_id":"lj50mGr7EJkn9vNp","product_model":"inso_watch2","mac":"12:12:12:12:12:15","sn":"123456789","created_at":"1553227830","product_name":"隐秀石英表二代","description":"隐秀石英表二代，艺术与科技的结合","logo":"http://localhost/INSOCMS/inso_cms/inso_master/web/storage/upload/avatar/1/tiN2HE9eeixG4QZNpQ9C9vExuKOFjWZnyIfrIfYw.png","firmware":"1.0.0"}]
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
         * product_model : inso_watch2
         * mac : 12:12:12:12:12:15
         * sn : 123456789
         * created_at : 1553227830
         * product_name : 隐秀石英表二代
         * description : 隐秀石英表二代，艺术与科技的结合
         * logo : http://localhost/INSOCMS/inso_cms/inso_master/web/storage/upload/avatar/1/tiN2HE9eeixG4QZNpQ9C9vExuKOFjWZnyIfrIfYw.png
         * firmware : 1.0.0
         */

        private String user_id;
        private String product_model;
        private String mac;
        private String sn;
        private String created_at;
        private String product_name;
        private String description;
        private String logo;
        private String firmware;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
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

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getFirmware() {
            return firmware;
        }

        public void setFirmware(String firmware) {
            this.firmware = firmware;
        }
    }
}
