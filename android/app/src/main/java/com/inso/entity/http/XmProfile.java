package com.inso.entity.http;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/12
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class XmProfile {

    /**
     * result : ok
     * code : 0
     * data : {"miliaoNick":"陈栋","unionId":"TGIsaVC_10gI9yQwwn3swNz8IHMKiJ8m8O5qASSA","miliaoIcon_75":"https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_75.jpg","miliaoIcon_orig":"https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_orig.jpg","miliaoIcon":"https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T.jpg","miliaoIcon_320":"https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_320.jpg","miliaoIcon_90":"https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_90.jpg","miliaoIcon_120":"https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_120.jpg"}
     * description : no error
     */

    private String result;
    private int code;
    private DataBean data;
    private String description;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class DataBean {
        /**
         * miliaoNick : 陈栋
         * unionId : TGIsaVC_10gI9yQwwn3swNz8IHMKiJ8m8O5qASSA
         * miliaoIcon_75 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_75.jpg
         * miliaoIcon_orig : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_orig.jpg
         * miliaoIcon : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T.jpg
         * miliaoIcon_320 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_320.jpg
         * miliaoIcon_90 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_90.jpg
         * miliaoIcon_120 : https://s1.mi-img.com/mfsv2/avatar/fdsc3/p012g2fu1k5v/30PbHky05MZF0T_120.jpg
         */

        private String miliaoNick;
        private String unionId;
        private String miliaoIcon_75;
        private String miliaoIcon_orig;
        private String miliaoIcon;
        private String miliaoIcon_320;
        private String miliaoIcon_90;
        private String miliaoIcon_120;

        public String getMiliaoNick() {
            return miliaoNick;
        }

        public void setMiliaoNick(String miliaoNick) {
            this.miliaoNick = miliaoNick;
        }

        public String getUnionId() {
            return unionId;
        }

        public void setUnionId(String unionId) {
            this.unionId = unionId;
        }

        public String getMiliaoIcon_75() {
            return miliaoIcon_75;
        }

        public void setMiliaoIcon_75(String miliaoIcon_75) {
            this.miliaoIcon_75 = miliaoIcon_75;
        }

        public String getMiliaoIcon_orig() {
            return miliaoIcon_orig;
        }

        public void setMiliaoIcon_orig(String miliaoIcon_orig) {
            this.miliaoIcon_orig = miliaoIcon_orig;
        }

        public String getMiliaoIcon() {
            return miliaoIcon;
        }

        public void setMiliaoIcon(String miliaoIcon) {
            this.miliaoIcon = miliaoIcon;
        }

        public String getMiliaoIcon_320() {
            return miliaoIcon_320;
        }

        public void setMiliaoIcon_320(String miliaoIcon_320) {
            this.miliaoIcon_320 = miliaoIcon_320;
        }

        public String getMiliaoIcon_90() {
            return miliaoIcon_90;
        }

        public void setMiliaoIcon_90(String miliaoIcon_90) {
            this.miliaoIcon_90 = miliaoIcon_90;
        }

        public String getMiliaoIcon_120() {
            return miliaoIcon_120;
        }

        public void setMiliaoIcon_120(String miliaoIcon_120) {
            this.miliaoIcon_120 = miliaoIcon_120;
        }
    }
}
