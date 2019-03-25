package com.inso.entity.http;

import java.util.List;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/24
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Information {

    /**
     * errcode : 0
     * errmsg : ok
     * items : [{"cover":"http://106.14.205.6/storage/upload/20190121/Blp1akxZt0CyTXvWEREPaFPAelj52CRXSRfme2a1.jpg","title":"彭博社：苹果高通分道扬镳或因软件权限问题起冲突","description":"彭博社获得的邮件显示，苹果与高通之间高达数十亿美元的芯片供应协议可能并非因为专利费而取消，真正的影响原因可能是他们在软件权限问题上发生冲突。当苹果高管杰夫·威廉姆斯（Jeff Williams）对法院表示高通取消iPhone芯片供应时，对美国联邦贸易委员会（FTC）起诉这家半导体巨头提供了有利的证词。","link":"https://www.cnbeta.com/articles/tech/810617.htm","created_at":"1548038062"},{"cover":"http://106.14.205.6/storage/upload/20190121/QOnvuTDvE78oqjViCCPCBMGwVHD_Dik2zmPn5UfA.jpg","title":"华为Mate 20 X登陆台湾市场：上千人排队抢购","description":"1月19日，配备7.2寸巨屏的华为Mate 20 X首次在台湾市场上开卖，引发抢购热潮，台北微风南山、台中文心秀泰、高雄梦时代三地有上千人排队购买，其中微风南山就有超过500人，现场热闹非凡。其中，在微风南山排队第一位的林先生，当天早上5点半就到了现场，同时他也是长期使用华为手机的忠实花粉，尤其是对Mate系列情有独钟，排队时手上还带着Mate 10、Mate 9。","link":"https://www.cnbeta.com/articles/tech/810511.htm","created_at":"1548038002"}]
     * _links : {"self":{"href":"http://api.inshowlife.cn/v1/data/information?page=1"}}
     * _meta : {"totalCount":2,"pageCount":1,"currentPage":1,"perPage":20}
     */

    private int errcode;
    private String errmsg;
    private LinksBean _links;
    private MetaBean _meta;
    private List<ItemsBean> items;

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

    public LinksBean get_links() {
        return _links;
    }

    public void set_links(LinksBean _links) {
        this._links = _links;
    }

    public MetaBean get_meta() {
        return _meta;
    }

    public void set_meta(MetaBean _meta) {
        this._meta = _meta;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class LinksBean {
        /**
         * self : {"href":"http://api.inshowlife.cn/v1/data/information?page=1"}
         */

        private SelfBean self;

        public SelfBean getSelf() {
            return self;
        }

        public void setSelf(SelfBean self) {
            this.self = self;
        }

        public static class SelfBean {
            /**
             * href : http://api.inshowlife.cn/v1/data/information?page=1
             */

            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }
    }

    public static class MetaBean {
        /**
         * totalCount : 2
         * pageCount : 1
         * currentPage : 1
         * perPage : 20
         */

        private int totalCount;
        private int pageCount;
        private int currentPage;
        private int perPage;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }
    }

    public static class ItemsBean {
        /**
         * cover : http://106.14.205.6/storage/upload/20190121/Blp1akxZt0CyTXvWEREPaFPAelj52CRXSRfme2a1.jpg
         * title : 彭博社：苹果高通分道扬镳或因软件权限问题起冲突
         * description : 彭博社获得的邮件显示，苹果与高通之间高达数十亿美元的芯片供应协议可能并非因为专利费而取消，真正的影响原因可能是他们在软件权限问题上发生冲突。当苹果高管杰夫·威廉姆斯（Jeff Williams）对法院表示高通取消iPhone芯片供应时，对美国联邦贸易委员会（FTC）起诉这家半导体巨头提供了有利的证词。
         * link : https://www.cnbeta.com/articles/tech/810617.htm
         * created_at : 1548038062
         */

        private String cover;
        private String title;
        private String description;
        private String link;
        private long created_at;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }
    }
}
