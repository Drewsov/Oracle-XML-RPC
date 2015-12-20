package com.nnetworks.xmlrpc;

import java.io.ByteArrayInputStream;

import java.io.File;

import java.sql.Timestamp;

import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.jdbc.OracleResultSet;

import oracle.sql.NUMBER;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcMessages;
import redstone.xmlrpc.XmlRpcStruct;

/**
 * Weblogger XML-RPC Handler for the Blogger v1 API.
 * 
 * Blogger API spec can be found at http://plant.blogger.com/api/index.html
 * See also http://xmlrpc.free-conversant.com/docs/bloggerAPI
 * 
 * @author Andrew A. Toropov
 */
public class BloggerAPIHandler extends BaseAPIHandler {
    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    public BloggerAPIHandler() {
    }

    /**
     * Authenticates a user and returns the categories available in the website
     * 
     * @param blogid Dummy Value for Weblogger
     * @param userid Login for a MetaWeblog user who has permission to post to the blog
     * @param password Password for said username
     * @return 
     * @throws Exception
     */
  /*  public

    Object getIphoneCategories(int blog_id, String userid, 
                               String password) throws Exception {
        return getCategories(Integer.toString(blog_id), userid, password);
    }*/
 
    public Object getCategoriesWindowsLiveWriter(String blogid, String userid,  String password) throws Exception {
                                    logger.log(LINFO, 
                                               "getCategoriesWindowsLiveWriter() Called =====[ SUPPORTED wp.getCategories]=====");  
            return    getCategories(  blogid,   userid,  password)  ;                              
                                }
    /******
     * wp.getCategories
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
    ...******/
    public Object getCategories(String blogid, String userid, 
                                String password) throws Exception {
        logger.log(LINFO, 
                   "getCategories() Called =====[ SUPPORTED wp.getCategories]=====");
        //logger.log(LINFO, "     BlogId: " + blogid);
        // logger.log(LINFO, "     UserId: " + userid);
        Vector structs = new Vector();
        Vector results = new Vector();
        Weblog website = validate(blogid, userid, password);
        if (website != null)
            try {
                List<WeblogCategory> cats = 
                    getWeblogCategories(website, false);
                for (Iterator wbcItr = cats.iterator(); wbcItr.hasNext(); ) {
                    LinkedHashMap result = new LinkedHashMap();
                    WeblogCategory category = (WeblogCategory)wbcItr.next();
                    try {
                        result.put("categoryId", category.getId());
                        result.put("parentId", 
                                   Integer.toString(category.getparent()));
                        result.put("description", category.getDescription());
                        result.put("categoryDescription", category.getTitle());
                        result.put("categoryName", category.getTitle());
                        result.put("htmlUrl", 
                                   DPERMALINK + category.getTitle());
                        result.put("rssUrl", DPERMALINK + category.getTitle());
                        //result.put("parentId", category.getparent());
                        //int catId = Integer.valueOf(category.getId());
                        //result.put("categoryId", catId);
                    } catch (Exception e) {
                        result.put("categoryId", category.getId());
                    }
                    results.add(result);
                }
            } catch (Exception e) {
                String msg = "ERROR in MetaWeblogAPIHandler.getCategories";
                logger.log(LINFO, msg, e);
                throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
            }
        structs.add(results);
        return results;
    }
    /****
     * 
     * 
     * 30.03.2013 10:03:52 redstone.xmlrpc.XmlRpcDispatcher writeError
    WARNING: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:getCategories:!java.lang.Integer java.lang.String java.lang.String  No such method system.getCategories

     * 
     * ****/
      public Object getCategories(int blog_id, String userid, String password) throws Exception {
         return getCategories(Integer.toString(blog_id), userid, password);
     } 
    /**
     * deleteCategory:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     **/
    public

    boolean deleteCategory(String blogid, String userid, String password, 
                           int catid) throws Exception {
        logger.log(LINFO, 
                   "deleteCategory() Called =====[ SUPPORTED wp.deleteCategory]=====");
        logger.log(LINFO, "     catid: " + catid);
        logger.log(LINFO, "deleteCategory:" + userid);
        logger.log(LINFO, "password:" + password);
        Weblog website = validate(blogid, userid, password);
        setLookup("deleteCategory", userid, password, Integer.toString(catid), LOOKUP_CATEGORY);
        return true;
    }

    /**
     * newCategory:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     **/
    public boolean newCategory32 (String blogid, String userid, String password, XmlRpcStruct content) throws Exception {
        logger.log(LINFO, "newCategory32() Called =====[ SUPPORTED ]=====");
        Weblog website = validate(blogid, userid, password);
        String cat = (String)content.get("name");
        setLookup("newCategory", cat, cat, blogid, LOOKUP_CATEGORY);
        return true;
    }
    /**
     * newCategory:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     **/
    public boolean newCategory(String blogid, String userid, String password, 
                               XmlRpcStruct content) throws Exception {
        logger.log(LINFO, "newCategory() Called =====[ SUPPORTED ]=====");
        Weblog website = validate(blogid, userid, password);
        String cat = (String)content.get("name");
        setLookup("newCategory", cat, cat, blogid, LOOKUP_CATEGORY);
        return true;
    }

    /****
     * wp.newCategory : !ReflectiveInvocationHandler.MethodDontExist:newCategory:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     * */
     public  boolean newCategory     (Integer catid, String userid, 
                                       String password, 
                                       XmlRpcStruct content) throws Exception {
     return newCategoryWpIphoneClient(catid,userid,password,content);
                                       }  
    public  boolean newCategoryWpIphoneClient(Integer catid, String userid, 
                                      String password, 
                                      XmlRpcStruct content) throws Exception {
        logger.log(LINFO, "newCategory() Called =====[ SUPPORTED ]=====");
        //Weblog website = validate(blogid, userid,password);
        if (validateUser(userid, password)) {
            logger.log(LINFO, "newCategory() Called =====[ Connected ]=====");
            int parentId = content.getInteger("parent_id");
            String catname = (String)content.get("name");
            logger.log(LINFO, 
                       catname + ":" + catid.toString() + ":" + parentId);
            setLookupParent("newParentCategory", catname, catname, 
                            catid.toString(), parentId);
        }
        return true;
    }
    /**
     * Authenticates a user and returns the categories available in the website
     *
     * @param blogid Dummy Value for Weblogger
     * @param userid Login for a MetaWeblog user who has permission to post to the blog
     * @param password Password for said username
     * @return
     * @throws Exception
     */

    /**wp.getTags
        Get list of all tags.
        
        Parameters
        int blog_id
        string username
        string password
        
        Return Values
        array
        struct
        int tag_id
        string name
        int count
        string slug
        string html_url
        string rss_url
     **/
    /*******
     *  wp.getTags 
 !ReflectiveInvocationHandler.MethodDontExist:getTags:!java.lang.Integer java.lang.String java.lang.String  

     * *******/ 
    public Object getTags(Integer blogid, String userid, String password) throws Exception {  
        return getTags(Integer.toString(blogid), userid, password);
    }
                           
    public Object getTags(String blogid, String userid, 
                          String password) throws Exception {

        logger.log(LINFO, "getTags() Called =====[ SUPPORTED ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        Vector results = new Vector();

        Weblog website = validate(blogid, userid, password);

        try {
            List tags = getWeblogTags(website, false);

            for (Iterator wbcItr = tags.iterator(); wbcItr.hasNext(); ) {
                Hashtable result = new Hashtable();
                WeblogTags tag = (WeblogTags)wbcItr.next();
                //System.out.println("tag.getId():"+tag.getId());
                result.put("tag_id", tag.getId());
                result.put("name", 
                           sXML(tag.getName())); // new String(tag.getName().getBytes(XmlRpcClientcharset)))
                result.put("count", tag.getCount());
                result.put("slug", 
                           sXML(tag.getSlug())); // new String(tag.getSlug().getBytes(XmlRpcClientcharset)))
                result.put("html_url", 
                           sXML(tag.getHtmlUrl())); // new String(tag.getHtmlUrl().getBytes(XmlRpcClientcharset)))
                result.put("rss_url", 
                           sXML(tag.getRssUrl())); // new String(tag.getRssUrl().getBytes(XmlRpcClientcharset)))
                results.add(result);
            }
        } catch (Exception e) {
            Hashtable result = new Hashtable();
            result.put("tag_id", "1");
            result.put("name", "none tags found");
            results.add(result);
        }
        //System.out.println("getTags:"+results);
        return results;
    }


    /**
     * Delete a Post
     *
     * @param appkey Unique identifier/passcode of the application sending the post
     * @param postid Unique identifier of the post to be changed
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @param publish Ignored
     * @throws XmlRpcException
     * @return
     */
    /**
 * WARNING: Dynamic PSP/XMLRPC : wp.deletePage : !ReflectiveInvocationHandler.MethodDontExist:deletePage:!java.lang.String java.lang.String java.lang.String java.lang.Integer

 * **/
    /* public boolean deletePage(String blog_id, String userid, String password, int page_id) throws Exception {
         return deletePost(blog_id,Integer.toString(page_id),userid,password,true);
         }*/

    /**
     * wp.deletePage : !ReflectiveInvocationHandler.MethodDontExist:deletePage:!java.lang.String java.lang.String java.lang.String java.lang.Integer 
     * **/
  /* 
 */

    
    public  boolean deletePage(int blog_id, String userid, 
                                     String password, 
                                     int page_id) throws Exception {
        return deletePost(Integer.toString(blog_id), Integer.toString(page_id), 
                          userid, password, true);
    }
    
     public boolean deletePage(String blog_id, String userid, String password, 
                              String postid) throws Exception {
        return deletePost(blog_id, postid, userid, password, true);
    }
    public boolean deletePost(String blog_id, String postid, String userid, 
                              String password) throws Exception {
        return deletePost(blog_id, postid, userid, password, true);
    } 

    public boolean deletePost(String appkey, String postid, String userid, 
                              String pass, boolean publish) throws Exception {

        logger.log(LINFO, "deletePost() Called =====[ SUPPORTED ]=====");
        logger.log(LINFO, "     Appkey: " + appkey);
        logger.log(LINFO, "     PostId: " + postid);
        logger.log(LINFO, "     UserId: " + userid);

        WeblogEntry entry = new WeblogEntry();

        try {
            if (validateUser(userid, pass)) {
                entry = deleteWeblogEntry(postid);
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
    }

    /**
     * Returns information on all the blogs a given user is a member of
     *
     * @param appkey Unique identifier/passcode of the application sending the post
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @throws XmlRpcException
     * @return
     */
    public Object getUsersBlogs(String userid, 
                                String password) throws Exception {
        String appkey = null;
        return getUsersBlogs(appkey, userid, password);
    }

    /**
     * Returns information on all the blogs a given user is a member of
     *
     * @param appkey Unique identifier/passcode of the application sending the post
     * @param userid Login for a Blogger user who has permission to post to the blog
     * @param password Password for said username
     * @throws XmlRpcException
     * @return
     */
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
    
    /***
     * wp.newPage : wp.newPageCocoaRPC :!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     * ****/
     public Object newPageCocoaRPC(String pageid, String userid, String password,  XmlRpcStruct content ) throws Exception {
         logger.log(LINFO, 
                    "wp.newPageCocoaRPC() Called ===========[ SUPPORTED wp.newPage v.2 ]=====");
         content.put("page_status","publish");         
         return newPage( pageid,  userid,  password, content, true) ;                 
                    }
  /**
   * INFO: wp-iphone/3.2:wp.newPage:
   * ***/
    public Object newPage32(String pageid, String userid, String password,  XmlRpcStruct content) throws Exception {
        logger.log(LINFO, 
                   "wp.newPage32() Called ===========[ SUPPORTED wp.newPage v.2 ]=====");
        content.put("page_status","publish");         
        return newPage( pageid,  userid,  password, content, true) ;                 
                   } 
    /****
     *
     *  wp.newPage : !ReflectiveInvocationHandler.MethodDontExist:newPage:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct java.lang.Boolean
     * *****/

    /*****
     * wp.newPage
                Create a new page. Similar to metaWeblog.newPost.
                
                @@Parameters
                @int blog_id
                @string username
                @string password
                @struct content
                    string wp_slug
                    string wp_password
                    int wp_page_parent_id
                    int wp_page_order
                    int wp_author_id
                    string title
                    string description (content of post)
                    string mt_excerpt
                    string mt_text_more
                    int mt_allow_comments (0 = closed, 1 = open)
                    int mt_allow_pings (0 = closed, 1 = open)
                    datetime dateCreated
                    array custom_fields
                    struct
                @Same struct data as custom_fields in wp.getPage
                bool publish
                
                @@Return Values
                int page_id
     ********/
    public Object newPage(String pageid, String userid, String password, 
                          XmlRpcStruct content, 
                          boolean publish) throws Exception {
        logger.log(LINFO, 
                   "newPage() Called ===========[ SUPPORTED wp.newPage v.2 ]=====");
        logg("pageid:" + pageid);
        logg("userid:" + userid);
        logg("password:" + password);
        logg("publish:" + publish);
        logg(content.toString());
        // {mt_keywords=, title=asdasd, wp_slug=aaa, mt_allow_pings=1, mt_basename=aaa, mt_allow_comments=1, description=<p>asdasdasdasd</p>, wp_page_parent_id=100103, wp_page_order=1, wp_password=aaaa}
        int newPageId = 0;
        if (publish)
            newPageId = 
                    Integer.valueOf(newPage(Integer.valueOf(pageid), userid, 
                                            password, content));
        return newPageId;
    }
    
/**
 * WARNING: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:getUsers:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct redstone.xmlrpc.XmlRpcArray 
 wp.getUsers 
 !ReflectiveInvocationHandler.MethodDontExist:getUsers:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct redstone.xmlrpc.XmlRpcArray  
 wp.getUser
 Retrieve a user.

 Added in WordPress 3.5.

 Parameters
 int blog_id
 string username
 string password
 int user_id
 array fields: Optional. List of field or meta-field names to include in response.
 Return Values
 struct: Note that the exact fields returned depends on the fields parameter.
 string user_id
 string username
 string first_name
 string last_name
 string bio
 string email1
 string nickname
 string nicename1
 string url
 string display_name1
 datetime registered1
 roles
 **/
 
 public Object getUsers(int blog_id, int blogid, String userid, String password,XmlRpcStruct filter, XmlRpcArray fields ) throws Exception {
     LinkedHashMap struct = new LinkedHashMap();
     logger.log(LINFO, 
                "wp.getUsers() Called ===========[ SUPPORTED wp.getUsers v.2013 ]=====");
 
     logg("userid:" + userid);
     logg("filter:" + filter);
     logg("fields:" + filter);
     
     struct.put("user_id",userid);
     struct.put("user_id",userid);
     struct.put("username",userid);
     struct.put("first_name",userid);
     struct.put("last_name",userid);
     struct.put("bio",userid);
     struct.put("email1",userid);
     return struct;
                
 }
 
 
 
/**
 * Мethod Name wp.getPage 
 !ReflectiveInvocationHandler.MethodDontExist:getPage:!java.lang.String java.lang.Integer java.lang.String java.lang.String  
 CocoaRPC
 * ***/
 /*   public Object getPage(String blog_id, int pageid, String userid, String password) throws Exception {
        logger.log(LINFO, 
                   "getPage() Called ===========[ SUPPORTED wp.getPage (java.lang.String java.lang.Integer java.lang.String java.lang.String)   ]=====");
        return getPage(blog_id, Integer.toString(pageid),userid, password);
    }*/

    /***
     * 
     * wp.getPage : !ReflectiveInvocationHandler.MethodDontExist:getPage:!java.lang.String java.lang.String java.lang.String java.lang.String 
      wp.getPage
      Get the page identified by the page id.

      @Parameters
      int blog_id
      int page_id
      string username
      string password
      
      @Return Values
      struct
      datetime dateCreated (ISO.8601)
      int userid
      int page_id
      string page_status
      string description
      string title
      string link
      string permaLink
      array categories
      string Category Name
      ...
      string excerpt
      string text_more
      int mt_allow_comments
      int mt_allow_pings
      string wp_slug
      string wp_password
      string wp_author
      int wp_author_id
      int wp_page_parent_id
      string wp_page_parent_title
      int wp_page_order
      int wp_author_id
      string wp_author_display_name
      datetime date_created_gmt
      array custom_fields
      struct
      string id
      string key
      string value
      ...
      string wp_page_template

     * ****/
     
     /**
      * getPage:!java.lang.Integer java.lang.Integer java.lang.String java.lang.String 
      * **/
     public Object getPage(int blog_id, int pageid, String userid, String password) throws Exception {
         return getPage(Integer.toString(blog_id), Integer.toString(pageid), 
                        userid, password);
     }
     
    public Object getPage(int blog_id, int pageid, String userid, String password, 
                   XmlRpcStruct content) throws Exception {
        return getPage(Integer.toString(blog_id), Integer.toString(pageid), 
                       userid, password);
    }
    
     
    /****
     * 
     * 23.01.2012 19:08:07 redstone.xmlrpc.XmlRpcDispatcher dispatch
INFO: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Windows Live Writer 1.0):wp.getPage:[200125, 100090, drew, xxi90yarnet]
23.01.2012 19:08:07 redstone.xmlrpc.XmlRpcDispatcher dispatch
INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:getPageWpIphoneClient1:!java.lang.String java.lang.String java.lang.String java.lang.String 
     * *****/
    public Object getPageMozilla(String blog_id, String pageid, String userid, String password) throws Exception {
        return getPage(blog_id, pageid, userid, password);
    }
    
    public Object getPage(String blog_id, String pageid, String userid, String password) throws Exception {
        logger.log(LINFO, 
                   "wp.getPage() Called ===========[ SUPPORTED wp.getPage v.2013 ]===== @"+this.getClass().getName());
        logg("blog_id:" + blog_id);
        logg("pageid:" + pageid);
        logg("userid:" + userid);
        logg("password:" + password);
        Weblog website = validate(pageid, userid, password);
        WeblogEntry entry = getWeblogEntry(pageid);
        try {
            return createPageStruct(entry, userid);
        } catch (Exception e) {
            String msg = "ERROR in  BloggerAPIHandler.getPage :";
            logger.log(Level.WARNING, msg + ":" + e.getMessage());
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }
    }

    /****
    wp.newPage :!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     ****/
    public String newPage(int pageid, String userid, String password, 
                          XmlRpcStruct content) throws Exception {
        logger.log(LINFO, 
                   "newPage() Called ===========[ SUPPORTED wp.newPage ]=====");
        logger.log(LINFO, "     BlogId: " + pageid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "    content: " + content);
        Weblog website = validate(Integer.toString(pageid), userid, password);
        String description = "";
        description = (String)content.get("description");
        String title = "";
        title = (String)content.get("title");
        if (title == null) {
            throw new XmlRpcException(BLOGGERAPI_INCOMPLETE_POST + 
                                      "Must specify title or description");
        }
        WeblogEntry entry = new WeblogEntry();
        NUMBER uId = getFreeUnitID();
        DUPATH = uProps.getProperty("pagepath");
        uId = createUnit(uId.stringValue() + DPAGENAME, uId);
        entry.setId(uId.stringValue());


        if (title.length() > 0)
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
        Timestamp now = new Timestamp(System.currentTimeMillis());
        entry.setUpdateTime(now);
        Timestamp date_created_gmt = now;
        try {
            Date Ddate_created_gmt = (Date)content.get("date_created_gmt");
            date_created_gmt = new Timestamp(Ddate_created_gmt.getTime());

        } catch (Exception e) {
        }
        entry.setDateCreatedGmt(date_created_gmt); //TODO 
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
        try {
            mt_allow_pings = 
                    Integer.toString((Integer)content.get("mt_allow_pings"));
            mt_allow_comments = 
                    Integer.toString((Integer)content.get("mt_allow_comments"));
        } catch (Exception e) {
        }

        String mt_basename = (String)content.get("mt_basename");
        mt_keywords = (String)content.get("mt_keywords");
        mt_text_more = (String)content.get("mt_text_more");
        mt_excerpt = (String)content.get("mt_excerpt");
        mt_convert_breaks = (String)content.get("mt_convert_breaks");
        mt_tb_ping_url = (String)content.get("mt_tb_ping_urls");
        wp_slug = (String)content.get("wp_slug");
        String mt_content = "";
        String page_status = (String)content.get("page_status");
        if (page_status==null) page_status="publish"; 
        String creator = DGRANTS.toUpperCase();
        if (creator==null) creator="creator"; 
        entry.SetWeblogEntry(entry.getId(), entry.getSetCategories(), 
                             creator, entry.getTitle(), "link", 
                             entry.getText(), uProps.getProperty("pagepath"), 
                             entry.getPubTime(), entry.getPubTime(), page_status, 
                             mt_keywords, mt_allow_pings, mt_text_more, 
                             mt_allow_comments, mt_excerpt, mt_convert_breaks, 
                             mt_tb_ping_url, wp_slug, date_created_gmt, 
                             mt_basename, mt_content);
        createWeblogEntry(entry);

        return entry.getId();
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

    /******
     *  wp.getPageList : !ReflectiveInvocationHandler.MethodDontExist:getPageList:!java.lang.String java.lang.String java.lang.String 
    @Parameters
    int blog_id
    string username
    string password
    
    @Return Values
    array struct
    int page_id
    string page_title
    int page_parent_id
    datetime dateCreated
    ... 
     ******/
    public Object getPageList(String blog_id, String userid, 
                              String password) throws Exception {
        logger.log(LINFO, 
                   "getPageList() Called ===========[ SUPPORTED wp.getPageList ]=====");
        logger.log(LINFO, "     blog_id: " + blog_id);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     Number: " + password);
        Weblog website = validate(blog_id, userid, password);
        int numposts = 100;
        try {
            Vector results = new Vector();
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
                                     null, null, "TIME_STAMP", "DESC", DPPATH, 
                                     0, numposts);

                Iterator iter = entries.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    WeblogEntry entry = (WeblogEntry)iter.next();
                    entry = (WeblogEntry)entries.get(i++);
                    results.addElement(createPageListStruct(entry, userid));
                }
            }
            return results;

        } catch (Exception e) {
            String msg = "ERROR in BlooggerAPIHander.getPages";
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }
    }
    /***********************/
    /*****
      * wp.getPages
        Get an array of all the pages on a blog.

        @Parameters
        int blog_id
        string username
        string password
        @Return Values
        array
        struct Same as wp.getPage
        ...
********/
     public Object getPagesWindowsLiveWriter (String blogid, String userid, String password, 
                             int numposts) throws Exception {
            logger.log(LINFO, "getPagesWindowsLiveWriter Called ===========[ SUPPORTED ]=====");                 
            return getPages32(blogid,  userid , password, Integer.toString(numposts));         
                           } 
    /**
     * wp.getPages : !ReflectiveInvocationHandler.MethodDontExist:getPages:!java.lang.String java.lang.String java.lang.String 
     * **/
  
     public Object getPages(String pageid, String userid, 
                            String password) throws Exception {
         logger.log(LINFO, 
                    "wp.getPages() Called ===========[ SUPPORTED wp.getPages (Users) changed 10/01/2012 ]=====");
         logger.log(LINFO, "     BlogId: " + pageid);
         logger.log(LINFO, "     UserId: " + userid);
         logger.log(LINFO, "     password: " + password);
         return getPages(pageid, userid, password, BaseAPIHandler.nposts);
     }
     
   /*************
    *   wp.getPages 
        !ReflectiveInvocationHandler.MethodDontExist:getPages:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer  
        wp-iphone/3.5.2 (iPhone OS 6.1.3, iPhone) Mobile
    * */
      public Object getPages(int pageid, String userid, String password, 
                             int numposts) throws Exception {
            logger.log(LINFO, "getPages(int,String,String,int) Called ===========[ SUPPORTED ]=====");                 
            return getPages32(Integer.toString(pageid),  userid , password, Integer.toString(numposts));         
                           } 
    /* 
     * Method:wp.getPages32 
     * 
     * !ReflectiveInvocationHandler.MethodDontExist:getPages32:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer    */ 
     
    public Object getPages32(int blogid, String userid, String password,int numposts)throws Exception {
         logger.log(LINFO, "getPages(int,String,String,int) Called ===========[ SUPPORTED ]=====");                 
         return   getPages32(Integer.toString(blogid),userid,password,Integer.toString(numposts));    
     }
    public Object getPages32(String pageid, String userid, String password,String numpages) throws Exception {
        logger.log(LINFO, 
                   "wp.getPages32() Called ===========[ SUPPORTED wp.getPages (Users) changed 10/01/2012 ]=====");
        logger.log(LINFO, "     BlogId: " + pageid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     password: " + password);
        return getPages(pageid, userid, password, Integer.valueOf(numpages));
    }
    
    /**
     * Method:wp.getPageWpIphoneClient 
    !ReflectiveInvocationHandler.MethodDontExist:getPageWpIphoneClient:!java.lang.Integer java.lang.Integer java.lang.String java.lang.String
     * **/
     public Object getPageWpIphoneClient(int blogid, int pageid, String userid,String password)  throws Exception {
         return getPage(Integer.toString(blogid),  Integer.toString(pageid),  userid, password);
     }
     
    /** INFO: wp-iphone/2.9.3:wp.getPage:
     * Мethod Name wp.getPageWpIphoneClient 
     !ReflectiveInvocationHandler.MethodDontExist:getPageWpIphoneClient:!java.lang.Integer java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct  
     wp-iphone/2.9.3
     * **/
     public Object getPageWpIphoneClient(int blogid, int pageid, String userid,String password, 
                                          XmlRpcStruct content) throws Exception {
     logger.log(LINFO, 
                "wp.getPageWpIphoneClient() Called ===========[ SUPPORTED wp.getPage ]=====");
     logger.log(LINFO, "     BlogId: " + blogid);
     logger.log(LINFO, "     pageid: " + pageid);
     logger.log(LINFO, "     UserId: " + userid);
     logger.log(LINFO, "     password: " + password);
     return getPage(Integer.toString(blogid),  Integer.toString(pageid),  userid, password);
 }
    /**
     * wp.getPages : !ReflectiveInvocationHandler.MethodDontExist:getPages:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer 
     **/
    public Object getPagesWpIphoneClient(String pageid, String userid, 
                                         String password, 
                                         String numposts) throws Exception {
        logger.log(LINFO, 
                   "getPages() Called ===========[ SUPPORTED wp.getPages ]=====");
        logger.log(LINFO, "     BlogId: " + pageid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     Number: " + password);
        logger.log(LINFO, "     Number: " + numposts);
        return getPages(pageid, userid, password, Integer.valueOf(numposts));
    }
    /**
     * wp.getPages : !ReflectiveInvocationHandler.MethodDontExist:getPages:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer 
     **/
    public Object getPagesWpIphoneClient(int pageid, String userid, 
                                         String password, 
                                         int numposts) throws Exception {
        logger.log(LINFO, 
                   "getPages() Called ===========[ SUPPORTED wp.getPages ]=====");
        logger.log(LINFO, "     BlogId: " + pageid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     Number: " + password);
        logger.log(LINFO, "     Number: " + numposts);
        return getPages(Integer.toString(pageid), userid, password, numposts);
    }

    public Object getPages(String blogid, String userid, String password, 
                           int numposts) throws Exception {
        logger.log(LINFO, "getPages() Called ===========[ SUPPORTED v.2013-2 ]===== @"+this.getClass().getName());
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     Number: " + numposts);

        Weblog website = validate(blogid, userid, password);

        try {
            Vector results = new Vector();
            if (authenticated) {
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
                                     null, null, "TIME_STAMP", "DESC", DPPATH, 
                                     0, numposts);

                Iterator iter = entries.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    WeblogEntry entry = (WeblogEntry)iter.next();
                    entry = (WeblogEntry)entries.get(i++);
                    results.addElement(createPageStruct(entry, userid));
                }
            }
            return results;

        } catch (Exception e) {
            String msg = "ERROR in BlooggerAPIHander.getPages";
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }
    }

    /** wp.editPageCocoaRPC
     * java.lang.String java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
     * **/
    public boolean editPageCocoaRPC(String blogid, String postid, String userid, 
                            String password, XmlRpcStruct content) throws Exception {
        logger.log(LINFO, "wp.editPageCocoaRPC() Called ===========[  wp.editPage. SUPPORTED ]=====");
        logger.log(LINFO, "     blogid: "  + blogid);
        logger.log(LINFO, "     postid: "  + postid); 
        content.put("page_status", "publish");
        logger.log(LINFO, "     content: " + content);  
        return editPage(blogid, postid, userid, password, content, true);
    }
    
    /** wp.editPage editPage32 **/
     public boolean editPage32(String blogid, String postid, String userid,  String password, XmlRpcStruct content ) throws Exception {
         logger.log(LINFO, "wp.editPage32() Called ===========[  wp.editPage. SUPPORTED ]=====");
         logger.log(LINFO, "     blogid: "  + blogid);
         logger.log(LINFO, "     postid: "  + postid);
         logger.log(LINFO, "      draft: "  + postid);
         logger.log(LINFO, "     content: " + content);  
         return editPage(blogid, postid, userid, password, content, true);
     }
    /** wp.editPage
     editPageCocoaRPC
     * !java.lang.String java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct java.lang.String  
     * **/
    public boolean editPageCocoaRPC(String blogid, String postid, String userid, 
                            String password, XmlRpcStruct content, String draft ) throws Exception {
        logger.log(LINFO, "wp.editPageCocoaRPC() Called ===========[  wp.editPage. SUPPORTED ]=====");
        logger.log(LINFO, "     blogid: "  + blogid);
        logger.log(LINFO, "     postid: "  + postid);
        logger.log(LINFO, "      draft: "  + postid);
        content.put("page_status", "draft");
        logger.log(LINFO, "     content: " + content);  
        return editPage(blogid, postid, userid, password, content, true);
    }
    /** wp.editPage
     * java.lang.String java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct
     * **/
    public boolean editPage(int blogid, int postid, String userid, 
                            String password, 
                            XmlRpcStruct content) throws Exception {
        return editPage(Integer.toString(blogid), Integer.toString(postid), 
                        userid, password, content, true);
    }

    /** wp.editPage
     * java.lang.String java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct java.lang.Boolean 
     * **/
    public boolean editPage(String blogid, String postid, String userid, 
                            String password, XmlRpcStruct content, 
                            boolean publish) throws Exception {
        logger.log(LINFO, 
                   "BloggerAPIHandler editPage() Called ========[ SUPPORTED wp.editPage ]=====");
        logger.log(Level.FINE, "     PostId: " + postid);
        logger.log(Level.FINE, "     UserId: " + userid);
        logger.log(Level.FINE, "    Publish: " + publish);
        logger.log(Level.FINE, "  Content:\n " + content);
        if (validateUser(userid, password)) {
            try {
                logger.log(LINFO, "     PostId: " + postid);
                String title = (String)content.get("title");
                String description = (String)content.get("description");
                 WeblogEntry entry = getWeblogEntry(postid);
                entry.setTitle(title);
                entry.setText(description);

                Date dateCreated = (Date)content.get("date_created_gmt");
                if (dateCreated == null) dateCreated = (Date)content.get("dateCreated");
                if (dateCreated == null) dateCreated = (Date)content.get("pubDate");    
                if (dateCreated == null) dateCreated = new Date(); 
                Timestamp date_created_gmt = new Timestamp(dateCreated.getTime());
                if ((Date)content.get("date_created_gmt") ==null){
                  date_created_gmt = new Timestamp(dateCreated.getTime()- 4 * 60 * 60 * 1000);}
                entry.setPubTime(date_created_gmt);
                entry.setUpdateTime(date_created_gmt);
                entry.setDateCreatedGmt(date_created_gmt);
                  /** categories=[qweqwe],**/
                List<WeblogCategory> wCats = new ArrayList<WeblogCategory>();
                try {
                    wCats = entry.setCategory((List)content.get("categories"));
                } catch (Exception e) {
                }

                /**NOT Done: mt_tb_ping_url
                             *
                             * {title=Page-100048.dpsp, mt_tb_ping_urls=[http://rpc.pingomatic.com], description=rewerwerwer}

                             * **/

                String mt_keywords = (String)content.get("mt_keywords");
                String mt_text_more = (String)content.get("mt_text_more");

                String mt_allow_pings = null;
                String mt_allow_comments = null;
                try {
                    mt_allow_pings = 
                            Integer.toString((Integer)content.get("mt_allow_pings"));
                    mt_allow_comments = 
                            Integer.toString((Integer)content.get("mt_allow_comments"));
                } catch (Exception e) {
                }

                String mt_excerpt = (String)content.get("mt_excerpt");
                String mt_convert_breaks = (String)content.get("mt_convert_breaks");
                String mt_tb_ping_url = (String)content.get("mt_tb_ping_urls");
                String wp_slug = (String)content.get("wp_slug");
                String page_status = (String)content.get("page_status");
                if (page_status==null) page_status="Published"; 
                String mt_basename = (String)content.get("mt_basename");
                String wp_author_display_name = (String)content.get("wp_author_display_name");
                String wp_author_id = (String)content.get("wp_author_id");
                String wp_password  = (String)content.get("wp_password");
                //if(wp_password!=null){DPERMISSIONS=wp_password;} // TODO
                String wp_page_parent_id = (String)content.get("wp_page_parent_id");
                /*************************/ 
                // wp_page_parent_title                
                String wp_page_parent_title = (String)content.get("wp_page_parent_title");
                if (wp_page_parent_id!=null)
                   wp_page_parent_title =  fetchAttr(wp_page_parent_id, "TITLE");
                /*************************/  
                String wp_page_order = (String)content.get("wp_page_order");
                String post_status = (String)content.get("post_status");
                if (post_status==null) post_status="Published"; 
                String wp_post_thumbnail  = (String)content.get("wp_post_thumbnail");
                // String wp_author = getLookup(wp_author_id);
                String creator = DGRANTS.toUpperCase();
                if (creator==null) creator="creator"; 
                if (wp_author_id==null) wp_author_id=creator; 
                if (wp_author_display_name==null) wp_author_display_name=getPersonInfo(wp_author_id);
                String wp_author = creator;
                String mt_content = 
                    "&post_status=" + post_status + "&wp_author_display_name=" + 
                    wp_author_display_name + "&wp_author=" + wp_author + "&wp_author_id=" + 
                    wp_author_id + "&wp_password=" + wp_password + 
                    "&wp_page_parent_id=" + wp_page_parent_id + 
                    "&wp_post_thumbnail=" + wp_post_thumbnail + 
                    "&wp_page_order=" + wp_page_order +
                    "&wp_page_parent_title=" + wp_page_parent_title  
                    ;

                entry.SetWeblogEntry(entry.getId(), wCats, creator, 
                                     entry.getTitle(), postid, entry.getText(), 
                                     uProps.getProperty("pagepath"), 
                                     entry.getPubTime(), entry.getPubTime(), 
                                     page_status, mt_keywords, mt_allow_pings, 
                                     mt_text_more, mt_allow_comments, 
                                     mt_excerpt, mt_convert_breaks, 
                                     mt_tb_ping_url, wp_slug, date_created_gmt, 
                                     mt_basename, mt_content);

                saveWeblogEntry(entry);
                Thread.sleep(10);
                return true;
            } catch (Exception e) {
                String msg = "ERROR in BlooggerAPIHander.editPost:";
                logger.log(Level.WARNING, msg, e);
                throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
            }
        }
        return true;
    }

    /**
     * 
     * wp.getAuthors
            Get an array of users for the blog.            
            @@Parameters
            int blog_id
            string username
            string password
            
            @@Return Values
            array
            struct
            int user_id
            string user_login
            string display_name
            string user_email
            string meta_value (Serialized data)
            ...
     * ***/
    public Object getAuthorsWindowsLiveWriter (String blog_id, String userid, 
                             String password) throws Exception {
    logger.log(LINFO,  "getAuthorsWindowsLiveWriter() Called ===========[ SUPPORTED wp.getAuthors ]=====");   
    return   getAuthors( blog_id ,userid,password);
                             }
    public Object getAuthors(String blog_id, String userid, 
                             String password) throws Exception {
        logger.log(LINFO, 
                   "getAuthors() Called ===========[ SUPPORTED wp.getAuthors ]=====");
        logger.log(LINFO, "     blog_id: " + blog_id);
        logger.log(LINFO, "     userid: " + userid);
        logger.log(LINFO, "     password: " + password);
        Weblog website = validate(blog_id, userid, password);
        Vector results = new Vector();
        try {
            if (website != null) {
                OracleResultSet rs = getLookup("AUTHORS", "POS");
                // id,lookup_type, code, description,pos,parent_id,owner_id,status 
                if (rs != null) {
                    int i = 1;
                    while (rs.next()) {
                        i++;
                        Hashtable result = new Hashtable();
                        result.put("user_id", rs.getInt(1));
                        result.put("user_login", sXML(rs.getString(3)));
                        result.put("display_name", sXML(rs.getString(4)));
                        result.put("user_email", sXML(rs.getString(3)));
                        result.put("meta_value", sXML(rs.getString(5)));
                        results.add(result);
                    }
                    rs.close();
                }
            }
        } catch (Exception e) {
            String msg = "ERROR in wp.getAuthors:";
            logger.log(Level.WARNING, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
        }
        return results;

    }


    /**
     * WARNING: Dynamic PSP/XMLRPC : wp.newComment : !ReflectiveInvocationHandler.MethodDontExist:newComment:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer redstone.xmlrpc.XmlRpcStruct 
     * wp.newComment
Create new comment.
     INFO: wp.newComment:[200125, drew, xxi90yarnet, 0, {comment_parent=209624, post_id=0, content=Опросов, status=approve}]
If you want to send anonymous comments, leave the second and third parameter blank and install a filter to xmlrpc_allow_anonymous_comments to return true.
See this WordPress forum post for more details.
    @Parameters
    int blog_id
    string username
    string password
    int post_id
    @struct comment
        int comment_parent
        string content
        string author
        string author_url
        string author_email
    @Return Values
    int comment_id
     * **/
    public int newComment(int blog_id, String userid, String password, 
                          int post_id, XmlRpcStruct comment) throws Exception {
        int comment_id = 0;
        logger.log(LINFO, 
                   "newComment() Called ===========[ SUPPORTED wp.newComment v.2013 ]=====");
        logg("     blog_id: " + blog_id);
        logg("     userid: " + userid);
        logg("     password: " + password);
        logg("     post_id: " + post_id);
        logg("     comment: " + comment);

        Vector results = new Vector();
        if (validateUser(userid, password)) {
            int comment_parent =  (Integer)comment.get("comment_parent") ;
            String content      = (String)comment.get("content");
            logg("     content: " + content);
            String status       = (String)comment.get("status");
            String author       = (String)comment.get("author");
            String author_url   = (String)comment.get("author_url");
            String author_email = (String)comment.get("author_email");
            //setLookup("newComment",weblog.getTitle(),weblog.getLink(),weblog.getId(),);
            comment_id  = Integer.parseInt(setLookupWithParent("newParentComment", content/*status*/, content, LOOKUP_COMMENTS, comment_parent));
        }
        return comment_id;
    }


    /**
     * wp.editComment
Edit comment.

    @Parameters
    int blog_id
    string username
    string password
    int comment_id
    @struct comment
        string status
        date date_created_gmt
        string content
        string author
        string author_url
        string author_email
    @Return Values
    boolean status
     * ***/
  /**
       * INFO: wp.editComment:[200125, andrew, xxi90yarnet, 210035, {content=Jjggcv., status=hold}]
10.01.2012 3:42:52 redstone.xmlrpc.XmlRpcDispatcher dispatch
INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:editComment:!
java.lang.String java.lang.String java.lang.String java.lang.Integer redstone.xmlrpc.XmlRpcStruct 
**/
     public  boolean editCommentCocoaRPC(String blog_id, String userid, String password, int comment_id, 
                        XmlRpcStruct comment) throws Exception {
      return editComment(Integer.valueOf(blog_id),userid,password,Integer.valueOf(comment_id),comment);
      } 

     public  boolean editComment(int blog_id, String userid, String password, 
                        int comment_id, 
                        XmlRpcStruct comment) throws Exception {
        logg("\n=============  editComment() Called ===========[ SUPPORTED wp.editComment ]=====");
        logger.log(LINFO, "     blog_id: " + blog_id);
        logger.log(LINFO, "     userid: " + userid);
        logger.log(LINFO, "     password: " + password);
        logger.log(LINFO, "     comment_id: " + comment_id);
        logger.log(LINFO, "     comment: " + comment);
        WeblogEntry entry = new WeblogEntry();
        String content          = (String)comment.get("content");
        String status           = (String)comment.get("status");
        String author           = (String)comment.get("author");
        String author_url       = (String)comment.get("author_url");
        String author_email     = (String)comment.get("author_email");
        Date  date_created_gmt  = (Date)comment.get("date_created_gmt");
        Vector results = new Vector();
        if (validateUser(userid, password)) {
        logg("=============   editComment() entry.setTitle ================");
            //String  DESCRIPTION = fetchAttr(Integer.toString(comment_id), "DESCRIPTION");
        String  DESCRIPTION = getLookup(comment_id);
        logg("comment_id  -> "+comment_id);      
        logg("DESCRIPTION -> "+DESCRIPTION); 
        if (DESCRIPTION==null) DESCRIPTION="Комментарий";
        entry.setTitle(sXML(DESCRIPTION));
        logg("=============   editComment() setPostCategories ================");  
        if (status.compareToIgnoreCase("hold")==0)    status="2";
        if (status.compareToIgnoreCase("approve")==0) status="1";
        if (status.compareToIgnoreCase("spam")==0)    status="3";    
        
        setLookup("setCommentStatus", entry.getTitle(), content, Integer.toString(comment_id),status);
        }
       logg("=============   editComment() End Called ================\n");
        return true;
    } 
 
   /*****
    * INFO: wp.deleteComment
    INFO: wp.deleteComment:[200125, drew, xxi90yarnet, 209670]
    * ***/
    public  boolean deleteCommentCocoaRPC(String blog_id, String userid, String password, int comment_id) throws Exception {
    return deleteComment(Integer.valueOf(blog_id),userid,password,comment_id);           
                          }
    /*****
     *  wp.deleteComment
    Remove comment.

    @Parameters
    int blog_id
    string username
    string password
    int comment_id
    @Return Values
    boolean status
     ******/
    public  boolean deleteComment(int blog_id, String userid, String password, 
                          int comment_id) throws Exception {
        logger.log(LINFO, 
                   "deleteComment() Called ===========[ SUPPORTED wp.deleteComment ]=====");
        logger.log(LINFO, "     blog_id: " + blog_id);
        logger.log(LINFO, "     userid: " + userid);
        logger.log(LINFO, "     password: " + password);
        logger.log(LINFO, "     comment_id: " + comment_id);
        Vector results = new Vector();
        if (validateUser(userid, password)) {
            setLookup("deleteCategory", userid, password, Integer.toString(comment_id), LOOKUP_COMMENTS);
        }

        return true;
    }
    /**
   * WARNING: Dynamic PSP/XMLRPC : wp.getComment : !ReflectiveInvocationHandler.MethodDontExist:getComment:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer redstone.xmlrpc.XmlRpcStruct
   wp.getComment
   Gets a comment, given it's comment ID. Note that this isn't in 2.6.1, but is in the HEAD (so should be in anything newer than 2.6.1)

   @Parameters
   int blog_id
   string username
   string password
   int comment_id
       @Return Values
       @struct
       datetime dateCreated (ISO.8601, always GMT)
       string user_id
       string comment_id
       string parent
       string status
       string content
       string link
       string post_id
       string post_title
       string author
       string author_url
       string author_email
       string author_ip
   * **/
 
    /**
     * INFO: wp.getComment:[1, drew, xxi90yarnet, 0, {comment_parent=200042, post_id=0, content=Thghgh, status=approve}]
     * **/
    public Object getComment(int blog_id, String userid, String password, 
                             int comment_id, 
                             XmlRpcStruct comment) throws Exception {
        logg("===========  getComment() Called =====[ SUPPORTED  wp.getComment ]=====");
        logger.log(LINFO, "     BlogId: " + blog_id);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     comment_id: " + comment_id);
        logger.log(LINFO, "     comment: " + comment);

        int post_id = comment.getInteger("post_id");
        int comment_parent = comment.getInteger("comment_parent");
        String content = (String)comment.get("content");
        String status = (String)comment.get("status");

        Vector results = new Vector();
        Hashtable result = new Hashtable();
        Weblog website = validate(Integer.toString(blog_id), userid, password);
        if (website != null) {
            OracleResultSet rs = getCommentbyId(Integer.toString(comment_id), "POS");
            // id,lookup_type, code, description,pos,parent_id,owner_id,status 
            if (rs != null) {
                int i = 1;
                while (rs.next()) {
                    i++;
                    Timestamp current = 
                        new Timestamp(System.currentTimeMillis());
                    //result.put("dateCreated", current);
                    result.put("dateCreated", rs.getDATE(9));
                    result.put("user_id", "@author");
                    result.put("comment_id", rs.getInt(1));
                    result.put("status", sXML(rs.getString(3)));
                    result.put("content", sXML(rs.getString(4)));
                    result.put("link", "link");
                    result.put("post_id", "post_id");
                    result.put("post_title", "XML-RPC client for Dynamic PSP");
                    result.put("author", "@author");
                    result.put("author_url", "www.hitmedia.ru");
                    result.put("author_email", "andrew.toropov@gmail.com");
                    results.add(result);
                }
                rs.close();
            }
        }
        return results;
    }
    
    /****
     * wp.getComment=>[200125, drew, xxi90yarnet, 200606] 
     * getComment:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer  
     * *****/
     public Object getComment(int blog_id, String userid, String password, int comment_id) throws Exception {
             Weblog website;
             website = validate(Integer.toString(blog_id), userid, password);
         XmlRpcStruct comment = new XmlRpcStruct();
         Vector results = new Vector();
         Hashtable result = new Hashtable(); 
         logg("===========  getComment() Called =====[ SUPPORTED v.2013 wp.getComment ]=====");
         logg("getComment => ");
         if (website != null) {
             OracleResultSet rs = getCommentbyId(Integer.toString(comment_id), "POS");
             logg("getComment => connected");
             // id,lookup_type, code, description,pos,parent_id,owner_id,status 
             if (rs != null) {
                 int i = 1;
                 while (rs.next()) {
                     i++;
                     out("getComment =>"+comment_id);
                     Timestamp current = rs.getDATE(9).timestampValue();
                     result.put("date_created_gmt", current);
                     result.put("user_id", "@author");
                     result.put("comment_id",comment_id/* rs.getInt(1)*/);
                     
                     String status = sXML(rs.getString(8));
                     if (status.compareToIgnoreCase("1")==0)    status="approve";
                     if (status.compareToIgnoreCase("2")==0)    status="hold";
                     if (status.compareToIgnoreCase("3")==0)    status="spam";   
                     result.put("status", sXML(status));
                     result.put("content", sXML(rs.getString(4)));
                     result.put("link", "link");
                     result.put("post_id", "0");
                     result.put("post_title", "XML-RPC client for Dynamic PSP");
                     result.put("author", "@author");
                     result.put("author_url", "www.hitmedia.ru");
                     result.put("author_email", "andrew.toropov@gmail.com");
                     result.put("type", "Reply");
                     results.add(result);
                 }
                 rs.close();
             }
             else {
                   result.put("comment_id",0);
                   result.put("status", "deleted");
                   results.add(result);
             }
         }
         return results;
         }
    /**
     * wp.getComments:
     *   /***   getCommentsCocoaRPC:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct ***/
     
     public Object getCommentsCocoaRPC(String blogid, String userid, String password, 
                               XmlRpcStruct content) throws Exception {
     return  getComments(Integer.valueOf(blogid), userid,  password, content);             
                               }
    /***   getComments:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct ***/

    /**wp.getComments
    Gets a set of comments for a given post.

    @Parameters
    int blog_id
    string username
    string password
    struct
    post_id
    status (defaults to approve)
    offset
    number

    @Return Values
    Returns an array of the comment structure (see wp.getComment)

    @struct
    datetime dateCreated (ISO.8601, always GMT)
    string user_id
    string comment_id
    string parent
    string status
    string content
    string link
    string post_id
    string post_title
    string author
    string author_url
    string author_email
    string author_ip
     **/
    public Object getComments(int blogid, String userid, String password, 
                              XmlRpcStruct content) throws Exception {

        logger.log(LINFO, "getComments() Called =====[ SUPPORTED ]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        Vector results = new Vector();

        Weblog website = validate(Integer.toString(blogid), userid, password);
        if (website != null) {
            /** id,lookup_type, code, description,pos,parent_id,owner_id,status,timestamp 
             * getPostLookups
             * **/ 
         OracleResultSet rs = getLookup(LOOKUP_COMMENTS, "POS");
            if (rs != null) {
                int i = 1;
                while (rs.next()) {
                    i++;
                    Hashtable result = new Hashtable();
                    //Timestamp current = new Timestamp(System.currentTimeMillis());
                    //result.put("date_created_gmt", current);
                    //result.put("dateCreated", current);
                    Timestamp current = rs.getDATE(9).timestampValue();
                    result.put("date_created_gmt", current);
                    //result.put("user_id"  ,sXML(rs.getString(7)));
                    result.put("user_id", "@author");
                    result.put("comment_id", rs.getInt(1));
                    String status = sXML(rs.getString(8));
                    //logg("status -> "+status);
                    if (status.compareToIgnoreCase("1")==0)    status="approve";
                    if (status.compareToIgnoreCase("2")==0)    status="hold";
                    if (status.compareToIgnoreCase("3")==0)    status="spam";   
                    result.put("status", status);
                    result.put("content", sXML(rs.getString(4)));
                    result.put("link", "link");
                    result.put("post_id", "post_id");
                    result.put("post_title", "XML-RPC client for Dynamic PSP");
                    result.put("parent", rs.getString(5));
                    result.put("author", "@author");
                    result.put("author_url", "www.hitmedia.ru");
                    result.put("author_email", "andrew.toropov@gmail.com");
                    result.put("type", "Reply");
                    results.add(result);
                }
                rs.close();
            }
        }
        logg("==============================");
        logg("results -> "+results);
        logg("==============================");
        return results;
    }

    /**
     * wp.getCommentStatusList
        Retrieve all of the comment status.
        
        @Parameters
        int blog_id
        string username
        string password
        @Return Values
        @struct
                string hold
                string approve
                string spam
     * **/
    public Object getCommentStatusList(int blog_id, String userid, 
                                String password) throws Exception {
        logger.log(LINFO, 
                   "getCommentStatusList() Called ===========[ SUPPORTED wp.getCommentStatusList ]=====");
        logger.log(LINFO, "     blog_id: " + blog_id);
        logger.log(LINFO, "     userid: " + userid);
        logger.log(LINFO, "     password: " + password);
        Vector results = new Vector();
        if (validateUser(userid, password)) {
        }

        return results;
    }
    

  /**
   * INFO: wp.uploadFile:[200125, drew, xxi90yarnet, {overwrite=false, bits=[B@d083, type=image/jpeg, name=Photo 01.01.2012, 14:56.jpg}]
   * INFO: metaWeblog.newMediaObject:[200125, drew, xxi90yarnet, {bits=[B@166b0df, type=image/jpeg, name=20120114-230749.jpg}]

14.01.2012 18:20:57 redstone.xmlrpc.XmlRpcDispatcher dispatch
INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:uploadFile:!java.lang.String java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct 
   
   wp.uploadFile
   Upload a file.

   Parameters
   int blog_id
   string username
   string password
   struct data
   string name
   string type
   base64 bits
   bool overwrite
   
   Return Values
   struct
   string file
   string url
   string type
   * **/ 
    public Object uploadFile(int blogid, String userid, String password,  XmlRpcStruct struct) throws Exception {
       return  uploadFile(Integer.toString(blogid),  userid,  password,   struct);
    }
    public Object uploadFile(String blogid, String userid, String password,  XmlRpcStruct struct) throws Exception {
        logger.log(LINFO, "wp.uploadFile Called =[ SUPPORTED v.2014.06]=====");
        logger.log(LINFO, "     BlogId: " + blogid);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "   Password: *********");
        Hashtable Rstruct = new Hashtable();
        
        Weblog website = validate(blogid, userid, password);
        if (validateUser(userid, password))
        try {
            String name = (String)struct.get("name");
            name = name.replaceAll("/", "_");
            String type = (String)struct.get("type");
             int dotPos = name.lastIndexOf(".");
             String  extension = name.substring(dotPos);
             try {
             logg("extension  ->"+extension);  
             Date now = new Date(System.currentTimeMillis());
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
                 TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-3")); 
                 sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT-3"));
                 String strDate = sdf.format(now);              
            //logg("FileDateFormat  ->"+strDate);     
            //logg("TimeZone.getDefault  ->"+ TimeZone.getDefault());     
            name = strDate+extension;
             }
            catch (Exception e) { }
            logger.log(LINFO, "wp.uploadFile name: " + name);
            logger.log(LINFO, "wp.uploadFile type: " + type);
            byte[] bits = (byte[])struct.get("bits");
            String fileLink =  fileURL + name; // WebloggerFactory.getWeblogger().getUrlStrategy().getWeblogResourceURL(website, name, true);
             // Try to save file           
             Hashtable fileStruct = FileManagerAPI.saveFile(website, name, type, bits.length, 
                                    new ByteArrayInputStream(bits),fileLink);  
             try {
             String media$id  = setAnyLookup("setAnyLookup","MEDIA", name, fileLink,0);
             Rstruct.put("id", media$id);} 
             catch (Exception e) {logg("Exception uploadFile.setAnyLookup =>"+e.getMessage());}
            
            Rstruct.put("type", type);
            Rstruct.put("url", fileLink);
            Rstruct.put("file", name); 
            
            logg("wp.uploadFile results  ->"+Rstruct);
        } catch (Exception e) {
            String msg = "ERROR in BloggerAPIHandler.uploadFile";
            logger.log(LINFO, msg, e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + msg);
        } 
        return Rstruct;
    }
 
 /**
  * wp.getOptions
Retrieve blog options. If passing in a struct, search for options listed within it.

This call return also settings available in the Media Settings area of wp-admin. For example: If a user has specified properties for Image Sizes such as Thumbnail Size, Medium Size, and Large Size, this call would return those properties.

Parameters
int blog_id
string username
string password
struct
string option


Return Values
array
struct
string desc
string readonly
string option***/
    public Object  getOptions(int blog_id, String username, String password) throws Exception {
         XmlRpcStruct struct = null;
         return getOptions(blog_id,username,password, struct ); 
    }
    
    public HashMap setOptions(String oDesc, String oValue, boolean readOnly)
                              {
         LinkedHashMap result  = new LinkedHashMap(); 
                                   result.put("desc", oDesc); 
                                   result.put("readonly", readOnly);  
                                   result.put("value",oValue);
         return result;
                              }
    public Object  getOptions(int blog_id, String username, String password, XmlRpcStruct struct ) throws Exception {
        logger.log(LINFO, 
                   "getOptions() Called ===========[ SUPPORTED wp.getOptions ]=====");
                   
                   /**
                    * Return Values
                        array
                        struct
                            string desc
                            string readonly
                            string option
**/
        Vector results = new Vector();
        LinkedHashMap structs = new LinkedHashMap();
 
        if (validateUser(username, password)) {
        
            structs.put("software_name",         setOptions("Software Name","Dynamic XML-RPC",true));
            structs.put("software_version",      setOptions("Software version","3.8",true));           
            structs.put("blog_url",              setOptions("Site URL",DBLOGPATH,true));
   /// added 04.2014 
            structs.put("post_thumbnail",        setOptions("Post Thumbnail","true",true)); 
             
            structs.put("time_zone",             setOptions("Time Zone","3",false));
            structs.put("blog_title",            setOptions("Site Title",DBLOGNAME,false));
            structs.put("blog_tagline",          setOptions("Site Tagline",DBLOGNAME,false));
            structs.put("date_format",           setOptions("Date Format","d/m/Y",false));
            structs.put("time_format",           setOptions("Time Format","g:i a",false));
            
            structs.put("users_can_register",    setOptions("Allow new users to sign up","0",false));
            structs.put("thumbnail_size_w",      setOptions("Thumbnail Width","150",false));
            structs.put("thumbnail_size_h",      setOptions("Thumbnail Height","150",false));
            structs.put("thumbnail_crop",        setOptions("Crop thumbnail to exact dimensions","1",false));
            structs.put("medium_size_w",         setOptions("Medium size image width","300",false));
            structs.put("medium_size_h",         setOptions("Medium size image height","300",false));
            structs.put("large_size_w",          setOptions("Large size image width","1024",false));
            structs.put("large_size_h",          setOptions("Large size image height","1024",false)); 
             
         
        // results.add(structs);
      }

        return structs;
    }
    
    /**
     * wp.getPostFormats
Retrieves a list of post formats used by the site. A filter parameter could be provided that would allow the caller to filter the result. At this moment the only supported filter is 'show-supported' that enable the caller to retrieve post formats supported by the active theme.

Parameters
int blog_id
string username
string password
Struct
const string 'show-supported'
Return Values
When no filter is specified

struct
e.g. [standard] => Default [aside] => Aside [chat] => Chat [gallery] => Gallery [link] => Link [image] => Image [quote] => Quote [status] => Status [video] => Video

When a filter is specified

struct 'all'
struct
struct 'supported'
struct
     * 
     * **/
     public Object  getPostFormats(int blog_id, String username, String password) throws Exception {
          XmlRpcStruct struct = null;
          return getPostFormats(blog_id,username,password, struct ); 
     }
     public Object  getPostFormats(int blog_id, String username, String password, XmlRpcStruct struct ) throws Exception {
         logger.log(LINFO, 
                    "getPostFormats() Called ===========[ SUPPORTED wp.getPostFormats" +
                    "]=====");
         Vector results = new Vector();         
         LinkedHashMap structs = new LinkedHashMap();
         if (validateUser(username, password)) { 
             structs.put("standard", "Standard"); 
             structs.put("aside", "Aside"); 
             structs.put("chat", "Chat"); 
             structs.put("gallery", "Gallery"); 
             structs.put("link", "Link"); 
             structs.put("image", "Image"); 
             structs.put("quote", "Quote"); 
             structs.put("status", "Status"); 
             structs.put("video", "Video"); 
             structs.put("audio", "Audio"); 
             structs.put("code", "Code"); 
             results.add(structs);
         }
          return structs;
     }
  /********
   * wp.getMediaLibrary=>[200125, drew, xxi90yarnet, {offset=0, number=20}] 
 !ReflectiveInvocationHandler.MethodDontExist:getMediaLibrary:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct  
   struct
   string attachment_id (Added in WordPress 3.4)
   datetime date_created_gmt
   int parent: ID of the parent post.
   string link: URL to the media item itself (the actual .jpg/.pdf/etc file, eg http://domain.tld/wp-content/uploads/2013/09/foo.jpg)
   string title 
   string caption
   string description
   MediaItemMetadata metadata
   PostThumbnailImageMeta image_meta
   string thumbnail: URL to the media item thumbnail (eg http://domain.tld/wp-content/uploads/2013/09/foo-150x150.jpg)
   ***********/   

   public Object getMediaLibrary(String blog_id, String username, String password, XmlRpcStruct struct ) throws Exception {
         return getMediaLibrary(Integer.parseInt(blog_id),  username,  password,  struct );
   }
   
   
   /******
    * 
    * wp.getMediaLibrary
            Retrieve list of media items.
            
            Added in WordPress 3.1.
            
            Parameters
            int blog_id
            string username
            string password
            struct filter: Optional (and all members are optional).
            int number: Total number of media items to retrieve.
            int offset
            int parent_id: Limit to attachments on this post ID. 0 shows unattached media items. Empty string shows all media items.
            string mime_type
    * **********/
   
    public Object getMediaLibrary(int blogid, String username, String password, XmlRpcStruct struct ) throws Exception {
        logger.log(LINFO,"getMediaLibrary() Called ===========[ SUPPORTED wp.getMediaLibrary ]=====");

        Vector results    = new Vector(); 
        
        int    offset  = 0;
        int    snumber = 10;
        int   first_in_set = 0;
        int    last_in_set = 0;
        try {
             offset  = Integer.valueOf(struct.get("offset").toString());
             snumber = Integer.valueOf(struct.get("number").toString());
             first_in_set = offset;
              last_in_set = offset+snumber;
        }
        catch(Exception ee){}
       // LinkedHashMap structs = new LinkedHashMap();
      
      /*
        String offset  = (String)struct.get("offset");
        Number snumber = (Number)struct.get("number");
         int ioffset = Integer.parseInt(offset);
         int inumber = snumber.intValue();
         // 'MEDIAITEMS'
                try {
                  File folder = new File(dirPath);  // TODO 
                  File[] listOfFiles = folder.listFiles();
                  SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                  for (int i = ioffset; i < inumber  ; i++) {
                  
                    if (listOfFiles[i].isFile()) {
                        out(listOfFiles[i].getName()+":"+sdf.format(listOfFiles[i].lastModified()));
                        Timestamp date_created_gmt = new Timestamp(listOfFiles[i].lastModified());
                        structs.put("attachment_id", i);
                        structs.put("date_created_gmt", date_created_gmt);
                        structs.put("parent", 0);
                        structs.put("link",fileURL+listOfFiles[i].getName());
                        structs.put("title",listOfFiles[i].getName()); 
                        structs.put("caption","caption");
                        structs.put("description","description");
                        structs.put("thumbnail",fileURL+listOfFiles[i].getName());
                        results.add(structs);
                        structs = new LinkedHashMap(); 
                    }
                  }
              }
             catch (Exception e){}
             */
             
       if (validateUser(username, password)) { 
        OracleResultSet rs = getAnyLookupArrayByType("MEDIA","getAnyLookupArrayByType",first_in_set,last_in_set);
        if (rs != null) {
         int i = 0;
         while (rs.next()) {
             Hashtable structs = new Hashtable();
             //out("getMediaItems =>"+rs.getInt(1));
             structs = getMediaItems( Integer.toString(blogid),username,password,rs.getInt(1));
             results.add(structs);
             i++; 
         }
        }
       } 
        return results;  
        
        
    }
   /*******
     * blogger.getUserInfo 
 !ReflectiveInvocationHandler.MethodDontExist:getUserInfo:!java.lang.String java.lang.String java.lang.String  
 XML-RPC.NET********/
    public Object getUserInfo(String userid, String username, 
                       String password) throws Exception {
        logger.log(LINFO, 
                   "getUserInfo() Called ===========[ SUPPORTED blogger.getUserInfo " + 
                   "]=====");
        Vector results = new Vector();
        LinkedHashMap structs = new LinkedHashMap();

        if (validateUser(username, password)) {
        // getPersonInfo(username);
            String firstname = getPersonInfo(username);
            String lastname  = "";
            try {
            String commaSeparated = firstname;
            ArrayList<String> info = new  ArrayList<String>(Arrays.asList(commaSeparated.split(" ")));
            firstname  = info.get(0);
            lastname   = info.get(1);
            }
            catch (Exception e){}
            
            structs.put("userid", 1);
            structs.put("nickname", username);
            structs.put("firstname", firstname);
            structs.put("lastname", lastname);
            structs.put("url", "");
            results.add(structs);
        }
        return results;
    }
    
    /****
     * wp.getMediaItem
     * http://codex.wordpress.org/XML-RPC_WordPress_API/Media
     * ***/
     /****
      * string attachment_id (Added in WordPress 3.4)
       datetime date_created_gmt
       int parent: ID of the parent post.
       string link: URL to the media item itself (the actual .jpg/.pdf/etc file, eg http://domain.tld/wp-content/uploads/2013/09/foo.jpg)
       string title
       string caption
       string description
       MediaItemMetadata metadata
       PostThumbnailImageMeta image_meta
       string thumbnail: URL to the media item thumbnail (eg http://domain.tld/wp-content/uploads/2013/09/foo-150x150.jpg)
       *****/
       
    public  Object getMediaItem(int blogid, String username, String password, int attachment_id) throws Exception {
      Hashtable returnStruct = new Hashtable(1);
      if (validateUser(username, password)) {
     returnStruct =  getMediaItems( Integer.toString(blogid),  username,  password,  attachment_id);}
     return returnStruct;
      }
    
    /******
     *  wp.getPosts=>[200125, drew, xxi90yarnet, {post_type=page, number=20}] 
 !ReflectiveInvocationHandler.MethodDontExist:getPosts:!java.lang.Integer java.lang.String java.lang.String redstone.xmlrpc.XmlRpcStruct  
 BlogPadPro / 325
     wp.getPosts
     Retrieve list of posts of any registered post type.

     Added in WordPress 3.4.

     Parameters
     int blog_id
     string username
     string password
     struct filter: Optional.
     string post_type
     string post_status
     int number
     int offset
     string orderby
     string order
     array fields: Optional. See #wp.getPost.
     Return Values
     array
     struct: See #wp.getPost.
     Notes
     * ************/  
     public  Object getPosts(int blog_id, String username, String password, XmlRpcStruct filter) throws Exception {
     
         logger.log(LINFO, "getPosts() Called ===========[ wp.getPosts  ]=====");
         int    numposts  = 10;
         String  post_type = "post";
         boolean post = true;
         Object returnStruct = new Object();
         MetaWeblogAPIHandler mt = new MetaWeblogAPIHandler();
         /*** post_type=page, number=20 ****/
         try{
              numposts  = filter.getInteger("number");
              post_type = filter.getString("post_type");
              if (post_type.indexOf("page")>0) post = false;
          }
         catch(Exception ep){}
         
         if (post){
             returnStruct = mt.getRecentPosts(blog_id, username, password, numposts);
         }
         else{ 
         returnStruct = mt.getPages(Integer.toString(blog_id), username, password, numposts); 
         } 
         return returnStruct; 
     
     }  
     
/********
 * wp.getPost
            Retrieve a post of any registered post type.
            
            Added in WordPress 3.4.
            
            Parameters
            int blog_id
            string username
            string password
            int post_id
            array fields: Optional. List of field or meta-field names to include in response.
            
            Return Values
            struct: Note that the exact fields returned depends on the fields parameter.
            string post_id
            string post_title1
            datetime post_date1
            datetime post_date_gmt1
            datetime post_modified1
            datetime post_modified_gmt1
            string post_status1
            string post_type1
            string post_format1
            string post_name1
            string post_author1 author id
            string post_password1
            string post_excerpt1
            string post_content1
            string post_parent1
            string post_mime_type1
            string link1
            string guid1
            int menu_order1
            string comment_status1
            string ping_status1
            bool sticky1
            struct post_thumbnail1: See wp.getMediaItem.
            array terms
            struct: See wp.getTerm
            array custom_fields
            struct
            string id
            string key
            string value
            struct enclosure
            string url
            int length
            string type
            1 post meta-field
 * ********************/     
 
 
 public  Object getPost(int blog_id, String username, String password, int post_id ) throws Exception { // TODO 
     logger.log(LINFO, "wp.getPost() Called ===========[ wp.editPost v.2014  ]=====");
     MetaWeblogAPIHandler getThePost = new MetaWeblogAPIHandler();
     LinkedHashMap struct = (LinkedHashMap) getThePost.getPost(post_id,username,password);
     struct.remove("sticky");
     struct.put("sticky",false);
     struct.put("post_parent",0);
     repsLHM(struct,"title","post_title");
     repsLHM(struct,"description","post_content");   
     repsLHM(struct,"postid","post_id");
     repsLHM(struct,"dateCreated","post_date");
     repsLHM(struct,"date_created_gmt","post_date_gmt");
     repsLHM(struct,"date_modified_gmt","post_modified_gmt"); 
     repsLHM(struct,"date_modified","post_modified");  
     repsLHM(struct,"wp_author_id","post_author");
     repsLHM(struct,"page_status","post_status");
     
     repsLHM(struct,"wp_slug","post_name");
  
     return  struct;

    // => wp.newPost[0, drew, xxi90yarnet, {post_thumbnail=0, ping_status=open, terms_names={}, terms={post_tag=[], category=[1]}, sticky=0, post_date_gmt=Mon Jun 02 11:10:00 GMT 2014, post_author=1, post_content=via PressSync, post_format=standard, post_name=, comment_status=open, post_type=post, post_status=publish, post_title=Вылиться, custom_fields=[], post_parent=0}]

 
 }  
/***************************************/
/*****************
 * 02.05.2014 5:45:01 redstone.xmlrpc.XmlRpcDispatcher dispatch
INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:
editPost:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer redstone.xmlrpc.XmlRpcStruct 
02.05.2014 5:45:01 redstone.xmlrpc.XmlRpcDispatcher writeError
WARNING: Dynamic PSP/XMLRPC
************************/
 public  Object editPost(int blog_id, String userid, String password, int post_id, XmlRpcStruct content ) throws Exception {
     logger.log(LINFO, "wp.editPost() Called ===========[ wp.editPost v.2014  ]=====");
     MetaWeblogAPIHandler getThePost = new MetaWeblogAPIHandler();
     boolean publish = true;
     return getThePost.editThePost(Integer.toString(post_id), userid, password, content, publish);
 
 } 
/*****************************************/

/******
 * 
         * 27.05.2014 16:28:06 redstone.xmlrpc.XmlRpcDispatcher dispatch
        INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:getAuthors:!java.lang.Integer java.lang.String java.lang.String 
        27.05.2014 16:28:06 redstone.xmlrpc.XmlRpcDispatcher writeError
        WARNING: Dynamic PSP/XMLRPC
           ver 1.01.2014 
          Copyright(c) 2001-2014 by HitMedia LLC  www.hitmedia.ru 
          All rights reserved.
         Author:
         andrew.toropov@gmail.com 
         wp.getAuthors=>[0, drew, xxi90yarnet] 
         !ReflectiveInvocationHandler.MethodDontExist:getAuthors:!java.lang.Integer java.lang.String java.lang.String  
         
             wp.getAuthors
             Get an array of users for the blog.
            
             Parameters
             int blog_id
             string username
             string password
             Return Values
             array
             
             struct
             int user_id
             string user_login
             string display_name
             string meta_value (Serialized PHP data)
 ...
 * *********/
    public Object getAuthors (int blog_id, String userid, String password) throws Exception {
        logger.log(LINFO, "getAuthors() Called ===========[ wp.getAuthors  ]=====");
        out("blog_id => "+blog_id+": userid=> "+ userid );
        XmlRpcArray results = new XmlRpcArray();
        XmlRpcStruct struct = new XmlRpcStruct();
         // {display_name=Всеволод Шапошников, user_login=Sagoth-x, user_id=3}
        if (validateUser(userid, password)) {
            struct.put("user_id", 1);
            struct.put("user_login", userid);
            struct.put("display_name", "Andrew Toropov");
            results.add(struct);
        }
        return results;
    }
    
    
    /***********
     * wp.getUser
            Retrieve a user.

            Added in WordPress 3.5.
            
            Parameters
            int blog_id
            string username
            string password
            int user_id
            array fields: Optional. List of field or meta-field names to include in response.
            Return Values
            struct: Note that the exact fields returned depends on the fields parameter.
            string user_id
            string username1
            string first_name
            string last_name
            string bio
            string email1
            string nickname
            string nicename1
            string url
            string display_name1
            datetime registered1
            roles
            1 basic meta-field
            Errors
            401
            If user does not have permission to edit the user.
            404
            If no user with that user_id exists.
     * wp.getUser=>[0, drew, xxi90yarnet, 1] 
         !ReflectiveInvocationHandler.MethodDontExist:getUser:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer  
     * **************/
     /**
      * 
      * {display_name=imenno.ru, first_name=, username=imenno.ru, bio=, nickname=imenno.ru, email=andrew.toropov@gmail.com, registered=Wed Apr 23 08:46:06 GMT+04:00 2014, roles=[Ljava.lang.Object;@118a770, last_name=, nicename=imenno-ru, user_id=2, url=}

      * *******/
     public XmlRpcStruct getUser (int blog_id, String userid, String password, int user_id) throws Exception {
      logger.log(LINFO, "getUser() Called ===========[ wp.getUser  ]=====");
      XmlRpcStruct struct = new XmlRpcStruct();
         struct.put("display_name", "Andrew Toropov");
         struct.put("user_id",user_id);
         struct.put("username","Andrew Toropov");
         struct.put("email","andrew.toropov@gmail.com");
         Timestamp now = new Timestamp(System.currentTimeMillis());
         struct.put("registered",now);
         XmlRpcArray roles = new XmlRpcArray();
         roles.add("administrator");
         struct.put("roles",roles);
      return struct; 
     
     } 
     
     
     /********
      * 
      * wp.getPostTypes=>[0, drew, xxi90yarnet] 
             !ReflectiveInvocationHandler.MethodDontExist:getPostTypes:!java.lang.Integer java.lang.String java.lang.String  
      * *********/
      
      /*******{hierarchical=false, 
       * cap={publish_posts=publish_posts, 
       * edit_published_posts=edit_published_posts, 
       * read_private_posts=read_private_posts, 
       * delete_posts=delete_posts, 
       * delete_private_posts=delete_private_posts, 
       * edit_others_posts=edit_others_posts, 
       * read_post=read_post, 
       * delete_post=delete_post, 
       * delete_published_posts=delete_published_posts, 
       * create_posts=edit_posts, 
       * delete_others_posts=delete_others_posts, 
       * read=read, 
       * edit_private_posts=edit_private_posts, 
       * edit_post=edit_post, edit_posts=edit_posts}, 
       * labels={menu_name=Posts, name_admin_bar=Post, 
       * add_new_item=Add New Post, singular_name=Post, 
       * search_items=Search Posts, not_found=No posts found., 
       * name=Posts, not_found_in_trash=No posts found in Trash.,
       * all_items=All Posts, edit_item=Edit Post, add_new=Add New, 
       * new_item=New Post, view_item=View Post, parent_item_colon=}, 
       * _builtin=true, name=post, 
       * supports={custom-fields=true, author=true, editor=true, title=true, trackbacks=true, thumbnail=true, excerpt=true, revisions=true, post-formats=true, comments=true}, 
       * label=Posts, show_ui=true, public=true, has_archive=false, taxonomies=[Ljava.lang.Object;@18ddc48, map_meta_cap=true}
***/
      public XmlRpcStruct getPostTypes (int blog_id, String userid, String password) throws Exception {
          logger.log(LINFO, "getPostTypes() Called ===========[ wp.getPostTypes  ]=====");
          XmlRpcStruct struct = new XmlRpcStruct();
          struct.put("hierarchical",false);
          struct.put("public",true);
          struct.put("has_archive",false);
          struct.put("map_meta_cap",true);
          
          XmlRpcStruct supports = new XmlRpcStruct();
          supports.put("custom-fields",true);  
          supports.put("author",true);  
          supports.put("editor",true);  
          supports.put("title",true);  
          supports.put("trackbacks",true);  
          supports.put("thumbnail",true);  
          supports.put("excerpt",true);  
          supports.put("revisions",true);  
          supports.put("post-formats",true);  
          supports.put("comments",true);  
          struct.put("supports",supports);
          return struct; 
      }
      
      
      /*******
       * wp.newPost=>[0, drew, xxi90yarnet, {post_thumbnail=0, ping_status=open, terms_names={}, sticky=0, post_date_gmt=Mon Jun 02 10:22:00 GMT 2014, post_author=1, post_content=via PressSync, post_format=standard, post_name=, comment_status=open, post_type=page, post_status=publish, post_title=Kaka, custom_fields=[], post_parent=0}] 
       * 
       * string post_type
        string post_status
        string post_title
        int post_author
        string post_excerpt
        string post_content
        datetime post_date_gmt | post_date
        string post_format
        string post_name: Encoded URL (slug)
        string post_password
        string comment_status
        string ping_status
        bool sticky
        int post_thumbnail
        int post_parent
        array custom_fields
        struct
        string key
        string value
       * *************/
       public String newPost (int blog_id, String userid, String password,XmlRpcStruct struct) throws Exception {
               MetaWeblogAPIHandler  metaWeblogAPIHandler = new MetaWeblogAPIHandler();              
        return metaWeblogAPIHandler.newThePost(Integer.toString(blog_id),userid, password,struct,true);
       }


/**
     *
     * wp.getTerm
 
 Retrieve a taxonomy term.

 Added in WordPress 3.4.

 Parameters
 int blog_id
 string username
 string password
 string taxonomy
 int term_id

 Return Values
 struct: See get_term.
 string term_id
 string name
 string slug
 string term_group
 string term_taxonomy_id
 string taxonomy
 string description
 string parent
 int count

     * **/
    public XmlRpcStruct getTerm(int blog_id, String username, String password,
                                String taxonomy, int term_id) throws Exception {
        logger.log(LINFO, "getTerm() Called ===========[ wp.getTerm]=====");
        XmlRpcStruct struct = new XmlRpcStruct();
        struct.put("term_id", "");
        struct.put("name", "");
        struct.put("slug", "");
        struct.put("term_group", "");
        struct.put("term_taxonomy_id", "");
        struct.put("taxonomy", "");
        struct.put("description", "");
        struct.put("parent", "");
        struct.put("count", 0);
        return struct;
    }

  /*****
    wp.getTerms=>[0, drew, xxi90yarnet, category] 
    !ReflectiveInvocationHandler.MethodDontExist:getTerms:!java.lang.Integer java.lang.String java.lang.String java.lang.String  
    wp-iphone/4.6.1 (iPhone OS 8.1.2, iPhone) Mobile
***/
  
  /***
   * 
   * <?php 
// no default values. using these as examples
$taxonomies = array( 
    'post_tag',
    'my_tax',
);

$args = array(
    'orderby'           => 'name', 
    'order'             => 'ASC',
    'hide_empty'        => true, 
    'exclude'           => array(), 
    'exclude_tree'      => array(), 
    'include'           => array(),
    'number'            => '', 
    'fields'            => 'all', 
    'slug'              => '',
    'parent'            => '',
    'hierarchical'      => true, 
    'child_of'          => 0, 
    'get'               => '', 
    'name__like'        => '',
    'description__like' => '',
    'pad_counts'        => false, 
    'offset'            => '', 
    'search'            => '', 
    'cache_domain'      => 'core'
); 

$terms = get_terms($taxonomies, $args);
?>

   wp.getTerms
   Retrieve list of terms in a taxonomy.

   Added in WordPress 3.4.

   Parameters
   int blog_id
   string username
   string password
   string taxonomy
   struct filter: Optional.
       int number
       int offset
       string orderby
       string order
       bool hide_empty: Whether to return terms with count=0.
       string search: Restrict to terms with names that contain (case-insensitive) this value.

   Return Values
   array
   struct: See #wp.getTerm.


   * ***/
   public XmlRpcArray getTerms (int blog_id, String userid, String password,String taxonomy, XmlRpcStruct filter) throws Exception {
      return getTerms(blog_id,  userid,  password, taxonomy);
      }
  
   public XmlRpcArray getTerms (int blog_id, String username, String password,String taxonomy) throws Exception {
       logger.log(LINFO, "getTerms() Called ===========[ wp.getTerms  ]=====");
       XmlRpcStruct struct = new XmlRpcStruct();
       struct = getTerm( blog_id,  username,  password, taxonomy,  0); 
       XmlRpcArray terms = new XmlRpcArray();
       terms.add(struct); 
       return terms; 
   }
  

    private static String fileRevision = "$Revision: 2015.02 $";

}
