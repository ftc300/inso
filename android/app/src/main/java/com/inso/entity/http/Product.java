package com.inso.entity.http;

import java.util.List;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/24
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Product {

    /**
     * errcode : 0
     * errmsg : ok
     * items : [{"logo":"https://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_ff62fa69edab71f326831af5045c4113.png","name":"隐秀石英表二代","model":"inso_watch2","description":"隐秀石英表二代，艺术与科技的结合"},{"logo":"https://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_ff62fa69edab71f326831af5045c4113.png","name":"IBONZ","model":"inso_ibonz","description":""},{"logo":"https://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_ff62fa69edab71f326831af5045c4113.png","name":"隐秀AI翻译机","model":"inso_translate_robot","description":""}]
     * _links : {"self":{"href":"http://api.inshowlife.cn/v1/product/list?page=1"}}
     * _meta : {"totalCount":3,"pageCount":1,"currentPage":1,"perPage":20}
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
         * self : {"href":"http://api.inshowlife.cn/v1/product/list?page=1"}
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
             * href : http://api.inshowlife.cn/v1/product/list?page=1
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
         * totalCount : 3
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
         * logo : https://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_ff62fa69edab71f326831af5045c4113.png
         * name : 隐秀石英表二代
         * model : inso_watch2
         * description : 隐秀石英表二代，艺术与科技的结合
         */

        private String logo;
        private String name;
        private String model;
        private String description;

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
