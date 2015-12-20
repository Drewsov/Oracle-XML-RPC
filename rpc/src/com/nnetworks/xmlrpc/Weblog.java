package com.nnetworks.xmlrpc;

import com.nnetworks.jopa.JOPAException;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;

import java.util.logging.Logger;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleTypes;

import com.nnetworks.jopa.*;

import redstone.xmlrpc.XmlRpcDispatcher;


public class Weblog extends BaseAPIHandler {
    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());
    // Simple properties
    //private String  id               = UUIDGenerator.generateUUID();
    private String handle = null;
    private String name = null;
    private String description = null;
    private String defaultPageId = "dummy";
    private String weblogDayPageId = "dummy";
    private Boolean enableBloggerApi = Boolean.TRUE;
    private String editorPage = null;
    private String blacklist = null;
    private Boolean allowComments = Boolean.TRUE;
    private Boolean emailComments = Boolean.FALSE;
    private String emailFromAddress = null;
    private String emailAddress = null;
    private String editorTheme = null;
    private String locale = null;
    private String timeZone = null;
    private String defaultPlugins = null;
    private Boolean enabled = Boolean.TRUE;
    private Boolean active = Boolean.TRUE;
    private Date dateCreated = new java.util.Date();
    private Boolean defaultAllowComments = Boolean.TRUE;
    private int defaultCommentDays = 0;
    private Boolean moderateComments = Boolean.FALSE;
    private int entryDisplayCount = 15;
    private Date lastModified = new Date();
    private String pageModels = new String();
    private boolean enableMultiLang = false;
    private boolean showAllLangs = true;
    private String customStylesheetPath = null;
    private String iconPath = null;
    private String about = null;


    // Associated objects
    // private User           creator = null; 
    private String creator = null;
    private List permissions = new ArrayList();
    private WeblogCategory bloggerCategory = null;
    private WeblogCategory defaultCategory = null;

    private Map initializedPlugins = null;

    public Weblog() {
    }

    public Weblog(String handle, String creator, String name, String desc, 
                  String email, String emailFrom, String editorTheme, 
                  String locale, String timeZone) {

        this.handle = handle;
        this.creator = creator;
        this.name = name;
        this.description = desc;
        this.emailAddress = email;
        this.emailFromAddress = emailFrom;
        this.editorTheme = editorTheme;
        this.locale = locale;
        this.timeZone = timeZone;
    }


}
