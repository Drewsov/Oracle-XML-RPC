package com.nnetworks.xmlrpc;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeblogCategory implements Serializable, Comparable {

    // attributes
    //         private String id = UUIDGenerator.generateUUID();
    private String id = null;
    private String title = null;
    private String name = null;
    private String description = null;
    private String image = null;
    private String path = null;
    private int pos = 0;
    private int iparent = 0;
    private List cats = null;

    // associations
    private Weblog website = null;
    private WeblogCategory parentCategory = null;
    private Set childCategories = new HashSet();


    public WeblogCategory() {
    }

    public WeblogCategory(Weblog website, WeblogCategory parent, String name, 
                          String description, String image, int iparent, 
                          int pos) {

        this.name = name;
        this.description = description;
        this.image = image;
        this.pos = pos;
        this.website = website;
        this.parentCategory = parent;
        this.iparent = iparent;

        // calculate path
        if (parent == null) {
            this.path = "/";
        } else if ("/".equals(parent.getPath())) {
            this.path = "/" + name;
        } else {
            this.path = parent.getPath() + "/" + name;
        }
    }

    public int compareTo(Object o) {
        WeblogCategory other = (WeblogCategory)o;
        return getName().compareTo(other.getName());
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPos() {
        return this.pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setParent(int parent) {
        this.iparent = parent;
    }

    public int getparent() {
        return this.iparent;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public List getCats() {
        return this.cats;
    }

    public void setCats(List cats) {
        this.cats = cats;
    }

}

