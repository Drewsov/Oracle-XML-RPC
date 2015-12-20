package com.nnetworks.xmlrpc;

import java.io.ByteArrayInputStream;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.sql.NUMBER;

import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcStruct;

import com.nnetworks.xmlrpc.Weblog;
import com.nnetworks.xmlrpc.FileManagerAPI;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import oracle.jdbc.OracleResultSet;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcFault;

/**
 * Weblogger XML-RPC Handler for the MetaWeblog API.
 * 
 * MetaWeblog API spec can be found at http://www.xmlrpc.com/metaWeblogApi
 * 
 * @author Andrew A. Toropov
 */
public class MetaWeblogAPIHandler extends BaseAPIHandler {

    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    /***
    mt.setPostCategories:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcArray 
     ***/
    public
    //mt.setPostCategories:[100131, drew, xxi90yarnet, [{categoryId=1}]]
    boolean setPostCategories(String postid, String userid, String password, 
                              XmlRpcArray content) throws Exception {
        logger.log(LINFO, 
                   "setPostCategories() Called =====[ SUPPORTED ]====="); //TODO
        logg(content.toString());
        if (validateUser(userid, password)) {
            try {
                String sCats = "";
                for (Iterator wbcItr = content.iterator(); wbcItr.hasNext(); 
                ) {
                    XmlRpcStruct category = (XmlRpcStruct)wbcItr.next();
                    String categoryId = (String)category.get("categoryId");
                    categoryId = getLookup(Integer.valueOf(categoryId));
                    if (!categoryId.equalsIgnoreCase("-1")) {
                        logg("setPostCategories:createCategory:" + 
                             categoryId); // TODO 
                    }
                    categoryId = categoryId + ",";
                    sCats = sCats + categoryId;
                }
                sCats = sCats.substring(0, sCats.length() - 1);
                //CATEGORIES   
                if (!sCats.equalsIgnoreCase("-1")) {
                    logg("setPostCategories:" + sCats);
                    setUnitAttr(postid, DCATEGORIES, sCats);
                }
            } catch (Exception e) {
            }
        }
        return true;
    }

    public boolean getPostCategories(String blogid, String userid, 
                                     String password, 
                                     XmlRpcArray content) throws Exception {
        logger.log(LINFO, 
                   "getPostCategories() Called =====[ SUPPORTED ]====="); //TODO
        logg(content.toString());
        return true;
    }

    /************    getPostCategories:!java.lang.String java.lang.String java.lang.String
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
<methodCall>
<methodName>mt.getPostCategories</methodName>
<params>
	<param>
		<value><string>18</string></value>
	</param>
	<param>
		<value><string>admin</string></value>
	</param>
	<param>
		<value><string>******</string></value>
	</param>
</params>
</methodCall>
     *******************/
    public Object getPostCategories(String posid, String userid, 
                                    String password) throws Exception {

        logger.log(LINFO, 
                   "getPostCategories() Called =====[ SUPPORTED wp.getPostCategories]=====");
        logger.log(LINFO, "     PosId: " + posid);
        logger.log(LINFO, "     UserId: " + userid);
        Vector results = new Vector();
        Weblog website = validate(posid, userid, password);
        if (website != null) {
            try {
                String sCats = fetchAttr(posid, "CATEGORIES");
                List<WeblogCategory> cats = getPostCategories(website, sCats);
                for (Iterator wbcItr = cats.iterator(); wbcItr.hasNext(); ) {
                    Hashtable result = new Hashtable();
                    boolean isPrimary = false;
                    WeblogCategory category = (WeblogCategory)wbcItr.next();
                    result.put("categoryName", sXML(category.getTitle()));
                    result.put("categoryId", category.getId());
                    if (category.getPos() == 0) {
                        isPrimary = true;
                    }
                    result.put("isPrimary", isPrimary); /// ? isPrimary //TODO 
                    results.add(result);
                }
            } catch (Exception e) {
                String msg = "ERROR in MetaWeblogAPIHandler.getPostCategories";
                logger.log(LINFO, msg, e);
                throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
            }
        }
        logger.log(LINFO, "getPostCategories:" + results);
        return results;
    }
    /**********************
Response:
<?xml version="1.0"?>
<methodResponse>
  <params>
    <param>
      <value>
        <array><data>
  <value><struct>
  <member><name>categoryName</name><value><string>uncategorized</string></value></member>
  <member><name>categoryId</name><value><string>1</string></value></member>
  <member><name>isPrimary</name><value><boolean>1</boolean></value></member>
</struct></value>
</data></array>
      </value>
    </param>
  </params>
</methodResponse>
 * ****************/

    /***
        getCategoryList:!java.lang.String java.lang.String java.lang.String r 
     ***/
    public Object getCategoryList(String blogid, String userid, 
                                  String password) throws Exception {
        logger.log(LINFO, "getCategoryList() Called =====[ SUPPORTED ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        return getCategories(blogid, userid, password);
    }
 
    /**
     * Authenticates a user and returns the categories available in the website
     * 
     * @param blogid Dummy Value for Weblogger
     * @param userid Login for a MetaWeblog user who has permission to post to the blog
     * @param password Password for said username
     * @return 
     * @throws Exception
     * 
     wp.getCategories
     Get an array of available categories on a blog.

     Parameters
     int blog_id
     string username
     string password
     
     Return Values
     array
     
     struct
     int categoryId
     int parentId
     string description
     string categoryName
     string htmlUrl
     string rssUrl
     ...
     */
    public Object getCategories(String blogid, String userid, 
                                String password) throws Exception {
        logg("\n ============== getCategories() Called =====[ SUPPORTED wp.getCategories]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        Vector results = new Vector();
        Weblog website = validate(blogid, userid, password);
        try {
            List<WeblogCategory> cats = getWeblogCategories(website, false);
            for (Iterator wbcItr = cats.iterator(); wbcItr.hasNext(); ) {
                Hashtable result = new Hashtable();
                WeblogCategory category = (WeblogCategory)wbcItr.next();
                result.put("categoryId", category.getId());
                /*result.put("title", 
                           (category.getTitle())); //new String(category.getTitle().getBytes(XmlRpcClientcharset))*/
                result.put("description", 
                           (category.getDescription())); //new String(category.getDescription().getBytes(XmlRpcClientcharset))
                result.put("categoryName", (category.getTitle()));
                result.put("parentId", (category.getparent()));
                result.put("htmlUrl", DPERMALINK +category.getTitle());
                result.put("rssUrl",  DPERMALINK +category.getTitle());
                results.add(result);
            }
        } catch (Exception e) {
            String msg = "ERROR in MetaWeblogAPIHandler.getCategories";
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
        }
        logg("getCategories ->" + results);
        return results;
    }


    /*********
     * mt.getRecentPostTitles=>[200125, drew, xxi90yarnet, 20] 
    ReflectiveInvocationHandler.MethodDontExist:getRecentPostTitles:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer  
    * ***********/
     public Object getRecentPostTitles(int blogint, String userid, String password, int numposts) throws Exception {
         logger.log(LINFO, 
                    "getRecentPostTitles() Called ===========[ SUPPORTED v.2013 changed 10/12/2013 ]=====");
         Weblog website; 
         Vector PostsStructs = new Vector(); 
         Vector results      = new Vector();
         String blogid = Integer.toString(blogint);
         website = validate(blogid, userid, password);
         logg("getRecentPostTitles.validate");
         try {
             if (website != null) {  
             logg("getRecentPostTitles:");
                 List<WeblogEntry> entries = // website
                     getWeblogEntries(website, null, null, null, null, null, 
                                      null, null, "time_stamp", "DESC", DUPATH, 
                                      0, numposts);
                 Iterator iter = entries.iterator();
                 int i = 0;
                 while (iter.hasNext()) {
                       WeblogEntry entry = (WeblogEntry)iter.next();
                       entry = (WeblogEntry)entries.get(i++);
                       results.addElement(createPostStructTitles(entry, userid));
                       logg("getRecentPostTitles:" + i + ":" + entry.getId());
                 }
             }
            // results.add(PostsStructs);
             return results;
         } catch (NullPointerException e) {
             System.out.println("NullPointerException @ getRecentPosts =>"+e.getMessage()); 
             return results;
         } catch (Exception e) {
             String msg = "ERROR in MetaWeblogAPIHandler.getRecentPosts";
             logger.log(Level.WARNING, msg + e); 
             System.out.println(msg+" @ getRecentPosts =>"+e.getMessage());
             throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);            
         }       
     }

    /**
     * This method was added to the Blogger 1.0 API via an Email from Evan
     * Williams to the Yahoo Group bloggerDev, see the email message for details -
     * http://groups.yahoo.com/group/bloggerDev/message/225
     *
     * @param appkey Unique identifier/passcode of the application sending the post
     * @param blogid Unique identifier of the blog the post will be added to
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @param numposts Number of Posts to Retrieve
     * @throws XmlRpcException
     * @return Vector of Hashtables, each containing dateCreated, userid, postid, content
     */

    /****
     * metaWeblog.getRecentPosts : !ReflectiveInvocationHandler.MethodDontExist:getRecentPosts:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer 
 WARNING: Dynamic PSP/XMLRPC : metaWeblog.getRecentPostsWpIphoneClient : !ReflectiveInvocationHandler.MethodDontExist:getRecentPostsWpIphoneClient:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer 
 FRAME_PAGE=>100016

     * ****/
    public  Object getRecentPostsWpIphoneClient(String blogid, String userid,  String password, String numposts) throws Exception {
      //logger.log(LINFO, "getRecentPostsWpIphoneClient() Called ===========[ SUPPORTED USER-AGENT: wp-iphone/3.2]=====");
      return getRecentPosts( blogid , userid, password, Integer.valueOf(numposts));
    }

/**
 * getRecentPostsWpIphoneClient
 * **/                                         
    public  Object getRecentPostsWpIphoneClient(int blogid, String userid, 
                                        String password, 
                                        int numposts) throws Exception {
        logger.log(LINFO, 
                   "getRecentPostsWpIphoneClient() Called ===========[ SUPPORTED USER-AGENT: wp-iphone/2.7.1]=====");
        return getRecentPosts(Integer.toString(blogid), userid, password, 
                              numposts);
    }

    /**
     * getRecentPosts
     * **/                                         
        public  Object getRecentPosts(int blogid, String userid, 
                                            String password, 
                                            int numposts) throws Exception {
            logger.log(LINFO, 
                       "getRecentPostsWpIphoneClient() Called ===========[ SUPPORTED USER-AGENT: wp-iphone/2.7.1]=====");
            return getRecentPosts(Integer.toString(blogid), userid, password, 
                                  numposts);
        }
         
    /***
    public Object getRecentPosts(String appkey, String blogid, String userid, String password, int numposts)
            throws Exception {
            return  getRecentPosts(blogid, userid,  password, numposts);
    }
 ****/
     public Object getRecentPostsWindowsLiveWriter (String blogid, String userid, String password,  int numposts) throws Exception {
            logger.log(LINFO, 
                       "getRecentPostsWindowsLiveWriter() Called ===========[ SUPPORTED USER-AGENT: Mozilla/4.0 (compatible; MSIE 9.11; Windows NT 6.1; Windows Live Writer 1.0)]=====");
            return getRecentPosts( blogid , userid, password,  numposts);
        }
    /**
     * Get a list of recent posts for a category
     *
     * @param blogid Unique identifier of the blog the post will be added to
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @param numposts Number of Posts to Retrieve
     * @throws XmlRpcException
     * @return
     */
    public Object getRecentPosts(String blogid, String userid, String password, 
                                 int numposts) throws Exception {

        logger.log(LINFO, 
                   "getRecentPosts() Called ===========[ SUPPORTED v.2013 (Users) changed 22/12/2013]=====");
        Weblog website = validate(blogid, userid, password);
        Vector PostsStructs = new Vector(); 
        Vector results      = new Vector();
        try {
            if (website != null) {
                List<WeblogEntry> entries = // website
                    // startDate
                    // endDate
                    // catName
                    // tags
                    // status 
                    // text
                    // sortby
                    // sortOrder
                    getWeblogEntries(website, null, null, null, null, null, 
                                     null, null, "time_stamp", "DESC", DUPATH, 
                                     0, numposts);
                logg("<========== getRecentPosts ==========>" + numposts + ":");                     
                Iterator iter = entries.iterator();
                int i = 0;
                while (iter.hasNext()) {
                      WeblogEntry entry = (WeblogEntry)iter.next();
                      entry = (WeblogEntry)entries.get(i++);
                      /* LinkedHashMap struct = new LinkedHashMap();
                      struct = createPostStruct(entry, userid);
                      PostsStructs.addElement(struct);*/
                      results.addElement(createPostStruct(entry, userid,password));
                      //logg("\n<========== getRecentPosts:" + i + ":" + entry.getId()+" ==========> ");
                      logg(results.toString());
                      //logg("==========>");
                }
            }
           // results.add(PostsStructs);
           // return results;
        } catch (NullPointerException e) {
            System.out.println("Here is NullPointerException @ getRecentPosts =>"+e.getMessage());
            e.printStackTrace();
            //return results;
        } catch (Exception e) {
            String msg = "ERROR in MetaWeblogAPIHandler.getRecentPosts";
            logger.log(Level.WARNING, msg + e); 
            System.out.println(msg+" @ getRecentPosts =>"+e.getMessage());
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
           
        }
        return results;
    }

    /**
     * Get a list of pages
     *
     * @param blogid Unique identifier of the blog the post will be added to
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @param numposts Number of Posts to Retrieve
     * @throws XmlRpcException
     * @return
     */
    public Object getPages(String blogid, String userid, String password, 
                           int numposts) throws Exception {

        logger.log(LINFO, "getPages() Called ===========[ SUPPORTED ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     Number: " + numposts);
        Weblog website = validate(blogid, userid, password);
        Vector results = new Vector();
        try {
            if (website != null) {
                List<WeblogEntry> entries = // website
                    // startDate
                    // endDate
                    // catName
                    // tags
                    // status
                    // text
                    // sortby
                    // sortOrder
                    getWeblogEntries(website, null, null, null, null, null, 
                                     null, null, "time_stamp", "DESC", DPPATH, 
                                     0, numposts);

                Iterator iter = entries.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    WeblogEntry entry = (WeblogEntry)iter.next();
                    entry = (WeblogEntry)entries.get(i++);
                    results.addElement(createPostStruct(entry, userid,password));
                    //System.out.println(i+":"+entry.getTitle());
                }
            }
            return results;
        } catch (NullPointerException e) {
            return results;
        } catch (Exception e) {
            String msg = "ERROR in MetaWeblogAPIHandler.getRecentPosts";
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }
    }

    /**
     * Edits a given post. Optionally, will publish the blog after making the edit
     *
     * @param appkey Unique identifier/passcode of the application sending the post
     * @param postid Unique identifier of the post to be changed
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @param content Contents of the post
     * @param publish If true, the blog will be published immediately after the post is made
     * @throws XmlRpcException
     * @return
     */

    
    /**
     * 11.01.2012 17:29:47 redstone.xmlrpc.XmlRpcDispatcher dispatch
INFO: redstone.xmlrpc.XmlRpcException: 
!ReflectiveInvocationHandler.MethodDontExist:editPost:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
11.01.2012 17:29:47 redstone.xmlrpc.XmlRpcDispatcher writeError
WARNING: Dynamic PSP/XMLRPC ...... Version 2.12.20...
    Copyright(c) 2001-2012 by Hit-Media LLC.
                        All rights reserved.
Author: andrew.toropov@gmail.com 
 Ìethod Name: metaWeblog.editPost 
 !ReflectiveInvocationHandler.MethodDontExist:editPost:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct  
 wp-iphone/3.2
***/
    public boolean editPost32(String postid, String userid, String password, XmlRpcStruct content) throws Exception {
    logg("====================== editPost32() Called ========[ SUPPORTED v.2012 ]=====");                            
    boolean publish = true;
    return editThePost(postid, userid, password, content, publish);
    }
 
   public boolean editPost(String postid, String userid, String password, XmlRpcStruct content) throws Exception {
     logg("====================== editPost() Called ========[ SUPPORTED v.2012 ]=====");                            
     boolean publish = true;
     return editThePost(postid, userid, password, content, publish);
     }

    /**
     * WARNING: Dynamic PSP/XMLRPC : metaWeblog.editPost : !ReflectiveInvocationHandler.MethodDontExist:editPost:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct java.lang.Boolean 

     * **/
    public boolean editPost(String postid, String userid, String password, 
                            XmlRpcStruct content, 
                            Boolean publish) throws Exception {
        return editThePost(postid, userid, password, content, publish);
    }

    public boolean editPage(String appid, String postid, String userid, 
                            String password, 
                            XmlRpcStruct content) throws Exception {
        boolean publish = true;
        return editThePost(postid, userid, password, content, publish);
    }

    public boolean editPage(String appid, String postid, String userid, 
                            String password, XmlRpcStruct content, 
                            boolean publish) throws Exception {
        return editThePost(postid, userid, password, content, publish);
    }
  /**
   *  Ìethod Name metaWeblog.editPostCocoaRPC 
 !ReflectiveInvocationHandler.MethodDontExist:editPostCocoaRPC:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct java.lang.String  
 CocoaRPC
   * **/
   
   public boolean editPostCocoaRPC(String postid, String userid, 
                                   String password, 
                                   XmlRpcStruct content, String draft) throws Exception {
       boolean publish = true;
       content.put("post_status", "draft");
       return editThePost(postid, userid, password, content, publish);
   }
    /***  !ReflectiveInvocationHandler.MethodDontExist:editPostCocoaRPC:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     *********/
    public boolean editPostCocoaRPC(String postid, String userid, 
                                    String password, 
                                    XmlRpcStruct content) throws Exception {
        boolean publish = true;
        return editThePost(postid, userid, password, content, publish);
    }
    /*********************/

    /**
     * metaWeblog.editPost : !ReflectiveInvocationHandler.MethodDontExist:editPost:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     ***/
    public boolean editPost(int postid, String userid, String password, 
                            XmlRpcStruct content) throws Exception {
        boolean publish = true;
        return editThePost(Integer.toString(postid), userid, password, content, 
                           publish);
    }

    /****** editPost *******/
    public boolean editThePost(String postid, String userid, String password, 
                               XmlRpcStruct content, 
                               boolean publish) throws Exception {

        logger.log(LINFO, "editPost() Called ========[ SUPPORTED v.2014.05.01 ]=====");
        logger.log(Level.FINE, "     PostId: " + postid);
        logger.log(Level.FINE, "     UserId: " + userid);
        logger.log(Level.FINE, "    Publish: " + publish);
        logger.log(Level.FINE, "     Content:\n " + content);
        if (validateUser(userid, password)) {
            try {
                logger.log(LINFO, "     PostId: " + postid);
                String title = (String)content.get("title");
                String description = (String)content.get("description");
                BaseAPIHandler.logg(content.toString());
                /*** 11/03/21 19:30:22 {
                               * mt_keywords=ï¿½ï¿½ï¿½ï¿½ï¿½,
                               * title=111111,
                               * mt_allow_pings=1,
                               * mt_text_more=,
                               * mt_allow_comments=1,
                               * description=1111,
                               * categories=[ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½],
                               * mt_excerpt=ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½,
                               * mt_convert_breaks=,
                               * dateCreated=Mon Mar 21 22:30:28 GMT 2011
                               * wp_slug=Ïîëå äàííûõ
                               * {
                               * wp_author_id=200006,
                               * mt_keywords=Drew, drew,
                               * title=Drew,
                               * wp_slug=ïîëå äàííûõ,
                               * mt_allow_pings=1,
                               * mt_basename=ïîëå äàííûõ,
                               * mt_allow_comments=1,
                               * mt_text_more=,
                               * description=<p>Drew. <s>Õæûàîëääüüòìñ÷÷âïðîëëÑìèîîßîîëèî</s></p>,
                               * categories=[aaa, Knife, wewqeqwe],
                               * mt_excerpt=îòðûâîê,
                               * wp_password=gfjhkm}

                               * }
                             * ***/
                             /******
                              * wp_post_thumbnail add 05.2014 
                              * 
                              * ********/
                        
                WeblogEntry entry = getWeblogEntry(postid);
                entry.setTitle(title);
                entry.setText(description);

                Date dateCreated = (Date)content.get("dateCreated");
                if (dateCreated == null)
                    dateCreated = (Date)content.get("pubDate");
                if (dateCreated == null)
                    dateCreated = new Date();
                entry.setPubTime(new Timestamp(dateCreated.getTime()));
                Timestamp now = new Timestamp(System.currentTimeMillis());
                entry.setUpdateTime(now);
                Timestamp date_created_gmt = now;
                try {
                    Date Ddate_created_gmt = 
                        (Date)content.get("date_created_gmt");
                    date_created_gmt = 
                            new Timestamp(Ddate_created_gmt.getTime());
                } catch (Exception e) {
                    logg("date_created_gmt:" + e.getMessage());
                }
                entry.setDateCreatedGmt(date_created_gmt); //TODO 
                if ((Date)content.get("date_created_gmt") == null) {
                    date_created_gmt = 
                            new Timestamp(dateCreated.getTime()- 4 * 60 * 60 * 1000);
                    entry.setDateCreatedGmt(date_created_gmt);
                }
                /** categories=[qweqwe],**/
                List<WeblogCategory> wCats = new ArrayList<WeblogCategory>();
                try {
                    wCats = entry.setCategory((List)content.get("categories"));
                } catch (Exception e) {
                }
                
                /****  v.2013 add custom_fields ***/  //TODO 
                
               logg("Called ========[ custom_fields ]=====");
               List<WeblogCategory> custom_fields = new ArrayList<WeblogCategory>();
                try { 
                custom_fields        = (List)content.get("custom_fields");
                logg("custom_fields:" + custom_fields);
                    DPARAMS = "PARAMS";
                    UPARAMS = "&custom_fields="+custom_fields;
                } catch (Exception e) {
                }
                
                /**********************************/
                
                String mt_keywords = null;
                String mt_text_more = null;
                String mt_allow_pings = null;
                String mt_allow_comments = null;
                mt_keywords = (String)content.get("mt_keywords");
                mt_text_more = (String)content.get("mt_text_more");
                try {
                    mt_allow_pings = 
                            Integer.toString((Integer)content.get("mt_allow_pings"));
                    mt_allow_comments = 
                            Integer.toString((Integer)content.get("mt_allow_comments"));
                } catch (Exception e) {
                }

                String mt_excerpt = (String)content.get("mt_excerpt");
                String mt_convert_breaks = 
                    (String)content.get("mt_convert_breaks");
                String mt_tb_ping_url = (String)content.get("mt_tb_ping_urls");
                String wp_slug =  get(content.get("wp_slug"));
                String page_status = (String)content.get("page_status");
                if (page_status==null) page_status="Published"; 
                String mt_basename = (String)content.get("mt_basename");
                String wp_author_id = (String)content.get("wp_author_id");
                String wp_password = (String)content.get("wp_password");
                // if(wp_password!=null){DPERMISSIONS=wp_password;} // TODO
                String wp_page_parent_id =  (String)content.get("wp_page_parent_id");
                String wp_author_display_name   =  (String)content.get("wp_author_display_name");
                String wp_page_order = (String)content.get("wp_page_order");
                String post_status = (String)content.get("post_status");
                if (post_status==null) post_status="Published"; 
                String wp_post_format = (String)content.get("wp_post_format");
                
                //String wp_author = getLookup(wp_author_id);
                String creator = DGRANTS.toUpperCase();
                if (creator==null) creator="creator"; 
                if (wp_author_id==null) wp_author_id=creator; 
                if (wp_author_display_name==null) wp_author_display_name=getPersonInfo(wp_author_id);
                // 2013.13 AAT
                String sticky = "0";
                 try {
                     int isticky =  content.getInteger("sticky");
                          sticky =  String.valueOf(isticky);
                    }
                catch (Exception e) {}
                // 2014.04 AAT
                 String wp_post_thumbnail ="null";
                out("================================ get wp_post_thumbnail ================================");
                    if (content.containsKey("wp_post_thumbnail")){
                    logg("get wp_post_thumbnail Exists and  wp_post_thumbnail:"+content.get("wp_post_thumbnail").toString());
                    }
                       
                try {
                    if (content.containsKey("wp_post_thumbnail")){
                      if (content.get("wp_post_thumbnail").toString().length()>0)
                       wp_post_thumbnail =  Integer.toString((Integer)content.get("wp_post_thumbnail"));
                      else 
                       wp_post_thumbnail ="null";
                    }
                    else
                    {  wp_post_thumbnail = checkthumbnail(entry.getLocalPath());
                        }
                    
                } catch (Exception e) {
                       wp_post_thumbnail = checkthumbnail(entry.getLocalPath());
                    // out(entry.getLocalPath());
                }
                out("================================ end wp_post_thumbnail ================================"); 
                // 
                String wp_author = creator;
                String mt_content = 
                    "&post_status=" + post_status + 
                    "&wp_author_display_name=" + wp_author_display_name + 
                    "&wp_author=" + wp_author + 
                    "&wp_author_id=" +  wp_author_id + 
                    "&wp_password=" + wp_password + 
                    "&wp_page_parent_id=" + wp_page_parent_id + 
                    "&wp_page_order=" + wp_page_order +
                    "&wp_post_format="+ wp_post_format+
                    "&post_format="+ wp_post_format+
                    "&sticky="+sticky+
                    "&wp_post_thumbnail="+ wp_post_thumbnail;
                     
                    //"&excerpt="+mt_excerpt;

                entry.SetWeblogEntry(entry.getId(), wCats, creator, 
                                     entry.getTitle(), postid, entry.getText(), 
                                     uProps.getProperty("postpath"), 
                                     entry.getPubTime(), entry.getPubTime(), 
                                     page_status, mt_keywords, mt_allow_pings, 
                                     mt_text_more, mt_allow_comments, 
                                     mt_excerpt, mt_convert_breaks, 
                                     mt_tb_ping_url, wp_slug, date_created_gmt, 
                                     mt_basename, mt_content);
                saveWeblogEntry(entry);

                Weblog weblog = new Weblog(); //TODO 


                Thread.sleep(50);
                return true;
            } catch (Exception e) {
                String msg = "ERROR in BlooggerAPIHander.editPost:";
                logger.log(Level.WARNING, msg, e);
                throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
            }
        }
        return true;

    }
    /***
     * ***/
     public String checkthumbnail(String LocalPath){
     String wp_post_thumbnail = "null";
         try {
             String get$wp_post_thumbnail = LocalPath;
             get$wp_post_thumbnail        = getStringByValue("wp_post_thumbnail", get$wp_post_thumbnail);
             wp_post_thumbnail            = get$wp_post_thumbnail;
             wp_post_thumbnail            = checkLookupExistsByID(wp_post_thumbnail);
             out("wp_post_thumbnail:" + wp_post_thumbnail);
         } catch (Exception e1) {
         }
     return wp_post_thumbnail;
     }
    /**
     * deletePost:!java.lang.String java.lang.Integer java.lang.String java.lang.String 

     * metaWeblog.deletePost 
     * **/
    public

    boolean deletePost(String appkey, int postid, String userid, 
                       String pass) throws Exception {

        logger.log(LINFO, "deletePost() Called =====[ SUPPORTED ]=====");
        logger.log(LINFO, "     Appkey: " + appkey);
        logger.log(LINFO, "     PostId: " + postid);
        logger.log(LINFO, "     UserId: " + userid);

        WeblogEntry entry = new WeblogEntry();

        try {
            if (validateUser(userid, pass)) {
                entry = deleteWeblogEntry(Integer.toString(postid));
                if (entry == null)
                    return false;
            }

        } catch (Exception e) {
            String msg = 
                "ERROR in blogger.deletePost: " + e.getClass().getName();
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }

        return true;
    } /**
     * Makes a new post to a designated blog. Optionally, will publish the blog after making the post
     *
     * @param blogid Unique identifier of the blog the post will be added to
     * @param userid Login for a MetaWeblog user who has permission to post to the blog
     * @param password Password for said username
     * @param struct Contents of the post
     * @param publish If true, the blog will be published immediately after the post is made
     * @throws org.apache.xmlrpc.XmlRpcException
     * @return
     */

    /**
     * java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     metaWeblog.newPost 
     * **/
    public
    //  WARNING: Dynamic PSP/XMLRPC : metaWeblog.newPost : !ReflectiveInvocationHandler.MethodDontExist:newPost:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 

    String newPost(int blogid, String userid, String password, XmlRpcStruct struct) throws Exception {
        boolean publish = true;
        return newThePost(Integer.toString(blogid), userid, password, struct, 
                          publish);
    }
/**
 * INFO: wp-iphone/3.2:metaWeblog.newPost:
 *  newPost32
 * **/
 
 public String newPost32(String blogid, String userid, String password,XmlRpcStruct struct) throws Exception {
     boolean publish = true;
     return newThePost(blogid, userid, password, struct, publish);
 }
    /***
    @metaWeblog.newPost : !ReflectiveInvocationHandler.MethodDontExist:newPost:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct java.lang.String 
     ****/
    public String newPostCocoaRPC(String blogid, String userid, 
                                  String password, 
                                  XmlRpcStruct struct) throws Exception {
        boolean publish = true;
        return newThePost(blogid, userid, password, struct, publish);
    }
    
    public String newPost(String blogid, String userid, String password, 
                          XmlRpcStruct struct, 
                          String post_id) throws Exception {
        logger.log(LINFO, 
                   "newPost() Called ===========[ SUPPORTED metaWeblog.newPost ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     postid: " + post_id);
        boolean publish = true;
        return newThePost(blogid, userid, password, struct, publish);
    }

    public String newPost(String blogid, String userid, String password, 
                          XmlRpcStruct struct) throws Exception {
        boolean publish = true;
        return newThePost(blogid, userid, password, struct, publish);
    }


    public String newPost(String blogid, String userid, String password, 
                          XmlRpcStruct struct, 
                          boolean publish) throws Exception {
        return newThePost(blogid, userid, password, struct, publish);
    }

    public String newThePost(String blogid, String userid, String password, 
                             XmlRpcStruct struct, 
                             boolean publish) throws Exception {
        logger.log(LINFO, 
                   "newThePost() Called ===========[ SUPPORTED metaWeblog.newPost ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "    Publish: " + publish);

        Weblog website = validate(blogid, userid, password);

        XmlRpcStruct content = struct;
        String description = "";
        description  =  defs(content.get("description"),content.get("post_content")).toString();
        String title = "";
        title        = defs(content.get("title"),content.get("post_title")).toString();
        if (title == null) {
               throw new XmlRpcException(BLOGGERAPI_INCOMPLETE_POST + 
                                      "Must specify title or description");
         }

        WeblogEntry entry = new WeblogEntry();
        NUMBER uId = getFreeUnitID();
        DUPATH = uProps.getProperty("postpath");
        uId = createUnit(uId.stringValue() + DPNAME, uId);
        entry.setId(uId.stringValue());
        entry.setLink(uId.stringValue());

        if (title != null)
            entry.setTitle(title);
        else
            entry.setTitle(uId.stringValue());


        if (description.length() > 0) {
            entry.setText(description);
        } else {
            description = " ";
            entry.setText(description);
        }


        Date dateCreated = (Date)content.get("dateCreated");
        if (dateCreated == null)
            dateCreated = (Date)content.get("pubDate");
        if (dateCreated == null)
            dateCreated = new Date();
        entry.setPubTime(new Timestamp(dateCreated.getTime()));
        entry.setUpdateTime(new Timestamp(dateCreated.getTime()));
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp date_created_gmt = now;
        try {
            Date Ddate_created_gmt = (Date)content.get("date_created_gmt");
            date_created_gmt = new Timestamp(Ddate_created_gmt.getTime());

        } catch (Exception e) {
        }
        entry.setDateCreatedGmt(date_created_gmt); //TODO 


        /**        INFO:     postcontent: {
 * mt_keywords=, title=111111111111,
 * mt_tb_ping_urls=11111111111,
 * mt_allow_pings=0, mt_text_more=,
 * mt_allow_comments=0,
 * description=1111111111111,
 * categories=[],
 * mt_excerpt=11111111111111,
 * mt_convert_breaks=,
 * dateCreated=Mon Mar 21 12:42:12 MSK 2011
 * date_created_gmt
 * }
 * {title=ÓùùçççææÈÈØØø,
 * date_created_gmt=Tue Apr 05 15:32:00 GMT 2011, mt_text_more=, mt_allow_comments=1, categories=[Firearm, 1111, 222],
 * dateCreated=Tue Apr 05 15:32:00 GMT 2011, mt_keywords=Êëþ÷åâîå ñëîâî, mt_allow_pings=1, wp_slug=Ïîëå äàííûõ, description=<p>ËËÒÒòòÜáÁÈÌìÌÌÌìÈÈèÈØØø</p> <p>@author</p>, mt_basename=Ïîëå äàííûõ, mt_excerpt=, wp_password=}
mt_keywords=Êëþ÷åâîå ñëîâî&mt_allow_pings=1&mt_text_more=&mt_allow_comments=1&mt_excerpt=&mt_convert_breaks=null&mt_tb_ping_url=null&wp_slug=Ïîëå äàííûõ

**/
        /*** categories=[],*/
        try {
            entry.setCategory((List)content.get("categories"));
        } catch (Exception e) {
        }

        String mt_keywords = null;
        String mt_text_more = null;
        String mt_excerpt = null;
        String mt_convert_breaks = null;
        String mt_allow_pings = null;
        String mt_allow_comments = null;
        String mt_tb_ping_url = null;
        String wp_slug = null;
        String page_status = null;

        try {
            mt_allow_pings = 
                    Integer.toString((Integer)content.get("mt_allow_pings"));
            mt_allow_comments = 
                    Integer.toString((Integer)content.get("mt_allow_comments"));

        } catch (Exception e) {
        }
        mt_keywords         = get(content.get("mt_keywords"));
        mt_text_more        = get(content.get("mt_text_more"));
        mt_excerpt          = get(content.get("mt_excerpt"));
        mt_convert_breaks   = get(content.get("mt_convert_breaks"));
        mt_tb_ping_url      = get(content.get("mt_tb_ping_urls"));
        wp_slug             = get(content.get("wp_slug"));
        page_status         = get(content.get("page_status"));
        if (page_status==null) page_status="publish"; 
        String mt_basename  = get(content.get("mt_basename"));
        String wp_author_display_name  =  get(content.get("wp_author_display_name"));
        String wp_author_id = get(content.get("wp_author_id"));
        String wp_password  = get(content.get("wp_password"));
        // if(wp_password!=null){DPERMISSIONS=wp_password;} // TODO
        if (wp_author_display_name==null) wp_author_display_name=getPersonInfo(wp_author_id);
        String wp_page_parent_id    = get(content.get("wp_page_parent_id"));
        String wp_page_order        = get(content.get("wp_page_order"));
        String post_status          = get(content.get("post_status"));
        if (post_status==null) post_status="publish"; 
        //String wp_author = getLookup(wp_author_id);
        String creator = DGRANTS.toUpperCase();
        if (creator==null) creator="creator"; 
        if (wp_author_id==null) wp_author_id=creator; 
        if (wp_author_display_name==null) wp_author_display_name=getPersonInfo(wp_author_id);
        // 2013.13 AAT
        String sticky = "0";
         try {
             int isticky =  content.getInteger("sticky");
                  sticky =  String.valueOf(isticky);
            }
        catch (Exception e) {}
        // 2014.04 AAT
        
        String wp_post_thumbnail = "";
        try {
                 wp_post_thumbnail =  content.get("wp_post_thumbnail").toString();
           }
        catch (Exception e) {}
        // 
        String wp_author = creator;
        String mt_content = 
            "&post_status=" + post_status + "&wp_author_display_name=" + 
            wp_author_display_name + "&wp_author=" + wp_author + "&wp_author_id=" + 
            wp_author_id + "&wp_password=" + wp_password + 
            "&wp_page_parent_id=" + wp_page_parent_id + 
            "&wp_page_order=" +  wp_page_order + 
            "&sticky="+sticky+
            "&wp_post_thumbnail="+ wp_post_thumbnail;

        entry.SetWeblogEntry(entry.getId(), entry.getSetCategories(), 
                             creator, entry.getTitle(), entry.getLink(), 
                             entry.getText(), uProps.getProperty("postpath"), 
                             entry.getPubTime(), entry.getPubTime(), 
                             page_status, mt_keywords, mt_allow_pings, 
                             mt_text_more, mt_allow_comments, mt_excerpt, 
                             mt_convert_breaks, mt_tb_ping_url, wp_slug, 
                             date_created_gmt, mt_basename, mt_content);
        createWeblogEntry(entry);

        return entry.getId();
    }


    /**
     *
     * @param postid
     * @param userid
     * @param password
     * @return
     * @throws Exception
     */
     /** Method:metaWeblog.getPost 
      !ReflectiveInvocationHandler.MethodDontExist:getPost:!java.lang.Integer java.lang.String java.lang.String  
      wp-iphone/3.0 **/ 
   
         
    public Object getPostMozilla(String postid, String userid, String password) throws Exception {
              return getPost(postid, userid, password);
    }
              
  /*  public Object getPost(int postid, String userid, String password) throws Exception {
          return getPost(Integer.toString(postid), userid, password);
      } 
      */
    public Object getPost(int postid, String userid, String password) throws Exception {
          return getPost(Integer.toString(postid), userid, password);
      }   
    /**java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct**/
    public Object getPost(int postid, String userid, String password, 
                          XmlRpcStruct content) throws Exception {
        logger.log(LINFO, 
                   "getPost() Called =========[ SUPPORTED metaWeblog.getPost]=====");
        //System.out.println("metaWeblog.getPost:"+content);
        return getPost(Integer.toString(postid), userid, password);
    }
   
    public Object getPost(String postid, String userid, 
                          String password) throws Exception {

        logger.log(LINFO, 
                   "getPost() Called =========[ SUPPORTED metaWeblog.getPost v.2013]=====");
        logg( "     PostId: " + postid);
        logg( "     UserId: " + userid);
        Weblog website = validate(postid, userid, password);
        logg("=========[ getWeblogEntry ]=====");
        if (ensureUnit(postid)){
        WeblogEntry entry = getWeblogEntry(postid);
        logg("=========[ createPostStruct ]=====");
        try {
            return createPostStruct(entry, userid,password);
        } catch (Exception e) {
            String msg = "ERROR in MetaWeblogAPIHandler.getPost :";
            logger.log(Level.WARNING, msg + ":" + e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }
        }
        else {
            LinkedHashMap struct = new LinkedHashMap();
            struct.put("faultCode",404);
            struct.put("faultString","Post not found");
            // throw new XmlRpcFault( 404, "Post not found" );
            return struct;
            
        }
    }


    public Object getUsersBlogs(String appkey, String userid, 
                                String password) throws Exception {

        logger.log(LINFO, "getUsersBlogs() Called ===[ SUPPORTED ]=======");
        logger.log(LINFO, "     Appkey: " + appkey);
        logger.log(LINFO, "     UserId: " + userid);

        Vector result = new Vector();
        if (validateUser(userid, password)) {
            try {
                String contextUrl = DBLOGPATH;
                // get list of user's enabled websites
                Hashtable blog = new Hashtable(3);
                blog.put("url", contextUrl);
                blog.put("blogid", DBLOGID);
                blog.put("blogName", DBLOGNAME);
                blog.put("xmlrpc", DBXMLRPC);
                result.add(blog);

            } catch (Exception e) {
                String msg = "ERROR in BlooggerAPIHander.getUsersBlogs:";
                logger.log(Level.WARNING, msg, e);
                throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
            }
        }
        logger.log(LINFO, "getUsersBlogs" + result);
        return result;
    }
 /**
  * Method:metaWeblog.newMediaObject 
 !ReflectiveInvocationHandler.MethodDontExist:newMediaObject:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct  
 wp-iphone/3.0
  * 
  * **/
  public Object newMediaObject(int  blogid, String userid, String password, XmlRpcStruct struct ) throws Exception {
      return newMediaObject(Integer.toString(blogid), userid, password, struct);
  }
    /**
     * Allows user to post a binary object, a file, to Weblogger. If the file is
     * allowed by the RollerConfig file-upload settings, then the file will be
     * placed in the user's upload directory.
     */
    public Object newMediaObject(String blogid, String userid, String password, 
                          XmlRpcStruct struct, boolean pub) throws Exception {
        return newMediaObject(blogid, userid, password, struct);
    }

   
    /**
     * Allows user to post a binary object, a file, to Weblogger. If the file is
     * allowed by the RollerConfig file-upload settings, then the file will be
     * placed in the user's upload directory.
     */
    public Object newMediaObject(String blogid, String userid, String password, 
                                 XmlRpcStruct struct) throws Exception {

        logger.log(LINFO, " newMediaObject() Called =[ SUPPORTED ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "   Password: *********");
        logger.log(LINFO, "   struct:"+struct);

        Weblog website = validate(blogid, userid, password);

        try {
            String name = (String)struct.get("name");
            name = name.replaceAll("/", "_");
            String iname = name;
            String type = (String)struct.get("type");
            try {
            int dotPos = name.lastIndexOf(".");
            String  extension = name.substring(dotPos); 
            Date now = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS"); 
                TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-3")); 
                sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT-3"));
                String strDate = sdf.format(now);              
            name = strDate+extension;
            
            }
            catch (Exception e) { }
            logger.log(LINFO, "newMediaObject name: " + name);
            logger.log(LINFO, "newMediaObject type: " + type);

            byte[] bits = (byte[])struct.get("bits"); 
            
            String fileLink = 
                fileURL + name; // WebloggerFactory.getWeblogger().getUrlStrategy().getWeblogResourceURL(website, name, true);
            // Try to save file
            Hashtable fileStruct = FileManagerAPI.saveFile(website, name, type, bits.length, 
                                    new ByteArrayInputStream(bits),fileLink);
           
            Hashtable returnStruct = new Hashtable(1);
            fileStruct.put("file",name);
            fileStruct.put("url",fileLink);
            
            returnStruct.put("url",  fileLink);
            returnStruct.put("file", name);
            try
            { returnStruct.put("type", type);
                fileStruct.put("type", type);
            } catch (Exception e) {
              returnStruct.put("type", "image/jpeg");
                fileStruct.put("type", "image/jpeg");
            }
            logg("newMediaObject return  ->"+returnStruct);
            // insert media into database
            try{
                   String media$id  = setAnyLookup("setAnyLookup","MEDIA", name, fileLink,0);
                   out("setAnyLookup:"+name+":"+fileLink);
                   returnStruct.put("id", media$id);
                     fileStruct.put("id", media$id);
                   try{  
                   /*** read fileStruct Hashtable ***/
                        String FileParams ="";
                        Map.Entry            entry;
                        Iterator<Map.Entry> it = fileStruct.entrySet().iterator();
                            while (it.hasNext()) {
                               entry = it.next();
                               FileParams = FileParams+"&"+entry.getKey().toString()+"="+entry.getValue().toString();
                            }
                 // out("<--- "+FileParams+"--->");        
                    out("link:"+getStringByValue("link",FileParams)); 
                    String parent$id = setAnyLookup("setAnyLookup","MEDIA", name, FileParams,Integer.parseInt(media$id));
                    }
                   catch(Exception f){logg("newMediaObject Exception setAnyLookup -> "+f);}
                }
            
            catch(Exception e){
                returnStruct.put("id",0);
                logg("newMediaObject Exception media into database -> "+e);
            }
            
            return returnStruct;

        } catch (Exception e) {
            String msg = "ERROR in MetaWeblogAPIHandler.newMediaObject";
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
        }
    }

     
                               
    private static String fileRevision = "$Revision: 2014.06 $";
}
