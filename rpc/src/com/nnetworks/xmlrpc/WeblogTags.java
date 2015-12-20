package com.nnetworks.xmlrpc;

import java.io.Serializable;

public class WeblogTags implements Serializable {

    private String tag_id = null;
    private String name = null;
    private int count = 1;
    private String html_url = null;
    private String slug = null;
    private String rss_url = null;

    public WeblogTags() {
    }

    public String getId() {
        return this.tag_id;
    }

    public void setId(String id) {
        this.tag_id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int id) {
        this.count = id;
    }

    public String getHtmlUrl() {
        return this.html_url;
    }

    public void setHtmlUrl(String id) {
        this.html_url = id;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String id) {
        this.slug = id;
    }

    public String getRssUrl() {
        return this.rss_url;
    }

    public void setRssUrl(String id) {
        this.rss_url = id;
    }
}

