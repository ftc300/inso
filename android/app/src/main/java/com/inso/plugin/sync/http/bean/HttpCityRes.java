package com.inso.plugin.sync.http.bean;

import java.util.List;

/**
 * Created by chendong on 2017/4/12.
 */

public class HttpCityRes {

    /**
     * version : 1
     * city_list : [{"id":"1","en":"Beijing","zh_cn":"北京","zh_hk":"北京","zh_tw":"北京","zone":"GMT+8:00"},{"id":"2","en":"New York","zh_cn":"纽约","zh_hk":"紐約","zh_tw":"紐約","zone":"GMT-5:00"},{"id":"3","en":"Tokyo","zh_cn":"东京","zh_hk":"東京","zh_tw":"東京","zone":"GMT+9:00"},{"id":"4","en":"Sydney","zh_cn":"悉尼","zh_hk":"悉尼","zh_tw":"悉尼","zone":"GMT+10:00"},{"id":"5","en":"Singapore","zh_cn":"新加坡","zh_hk":"新加坡","zh_tw":"新加坡","zone":"GMT+8:00"},{"id":"6","en":"Macao","zh_cn":"澳门","zh_hk":"澳門","zh_tw":"澳門","zone":"GMT+8:00"},{"id":"7","en":"Moscow","zh_cn":"莫斯科","zh_hk":"莫斯科","zh_tw":"莫斯科","zone":"GMT+3:00"},{"id":"8","en":"Abidjan","zh_cn":"阿比让","zh_hk":"阿比让","zh_tw":"阿比让","zone":"GMT+0:00"},{"id":"9","en":"Frankfurt","zh_cn":"法兰克福","zh_hk":"法兰克福","zh_tw":"法兰克福","zone":"GMT+1:00"},{"id":"10","en":"Darwin","zh_cn":"达尔文","zh_hk":"达尔文","zh_tw":"达尔文","zone":"GMT+9:00"},{"id":"11","en":"Harare","zh_cn":"哈拉雷","zh_hk":"哈拉雷","zh_tw":"哈拉雷","zone":"GMT+2:00"},{"id":"12","en":"Copenhagen","zh_cn":"哥本哈根","zh_hk":"哥本哈根","zh_tw":"哥本哈根","zone":"GMT+1:00"},{"id":"13","en":"Adamstown","zh_cn":"亚当","zh_hk":"亚当","zh_tw":"亚当","zone":"GMT-8:00"},{"id":"14","en":"The Valley","zh_cn":"山谷","zh_hk":"山谷","zh_tw":"山谷","zone":"GMT-4:00"},{"id":"15","en":"Bern","zh_cn":"伯恩","zh_hk":"伯恩","zh_tw":"伯恩","zone":"GMT+1:00"},{"id":"16","en":"Cali","zh_cn":"卡利","zh_hk":"卡利","zh_tw":"卡利","zone":"GMT-5:00"},{"id":"17","en":"Tsingtao","zh_cn":"青岛","zh_hk":"青岛","zh_tw":"青岛","zone":"GMT+8:00"}]
     */

    private String version;
    private List<CityListBean> city_list;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CityListBean> getCity_list() {
        return city_list;
    }

    public void setCity_list(List<CityListBean> city_list) {
        this.city_list = city_list;
    }

    public static class CityListBean {
        /**
         * id : 1
         * en : Beijing
         * zh_cn : 北京
         * zh_hk : 北京
         * zh_tw : 北京
         * zone : GMT+8:00
         */

        private String id;
        private String zh_cn;
        private String en;
        private String zh_hk;
        private String zh_tw;
        private String zone;

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getZh_hk() {
            return zh_hk;
        }

        public void setZh_hk(String zh_hk) {
            this.zh_hk = zh_hk;
        }

        public String getZh_tw() {
            return zh_tw;
        }

        public void setZh_tw(String zh_tw) {
            this.zh_tw = zh_tw;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getZh_cn() {
            return zh_cn;
        }

        public void setZh_cn(String zh_cn) {
            this.zh_cn = zh_cn;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }
    }
}
