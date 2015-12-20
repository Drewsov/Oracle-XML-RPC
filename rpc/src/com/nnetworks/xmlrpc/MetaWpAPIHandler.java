package com.nnetworks.xmlrpc;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;

import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;

public class MetaWpAPIHandler extends BaseAPIHandler {
    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    public MetaWpAPIHandler() {
    }
    /**
     * wp.getPages : !ReflectiveInvocationHandler.MethodDontExist:getPages:!java.lang.Integer java.lang.String java.lang.String java.lang.Integer
     * **/

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
    public
    /*
     * wp.getPages : !ReflectiveInvocationHandler.MethodDontExist:getPages:!java.lang.String java.lang.String java.lang.String java.lang.Integer
     * */
    Object getPages(int blog_id, String userid, String password, 
                    int numposts) throws Exception {
        logger.log(LINFO, 
                   "MetaWpAPIHandler.getPages() Called ===========[ SUPPORTED wp.getPages ]=====");
        logger.log(LINFO, "     BlogId: " + blog_id);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     password: " + password);
        logger.log(LINFO, "     numposts: " + numposts);
        BaseAPIHandler.nposts = numposts;
        return getPages(Integer.toString(blog_id), userid, password, numposts);
    }

    public Object getPages(String blog_id, String userid, String password, 
                           int numposts) throws Exception {
        logger.log(LINFO, 
                   "MetaWpAPIHandler.getPages() Called ===========[ SUPPORTED wp.getPages ]=====");

        logger.log(LINFO, "     BlogId: " + blog_id);
        logger.log(LINFO, "     UserId: " + userid);
        logger.log(LINFO, "     password: " + password);
        logger.log(LINFO, "     Number: " + numposts);
        Vector results = new Vector();
        if (validateUser(userid, password)) {
            Weblog website = validate(blog_id, userid, password);

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
                                         null, null, "TIME_STAMP", "DESC", 
                                         DPPATH, 0, numposts);

                    Iterator iter = entries.iterator();
                    int i = 0;
                    while (iter.hasNext()) {
                        WeblogEntry entry = (WeblogEntry)iter.next();
                        entry = (WeblogEntry)entries.get(i++);
                        results.addElement(createPageStruct(entry, userid));
                        //System.out.println(i+":"+entry.getTitle());
                        //System.out.println(i+":"+entry.getId());
                    }
                }


            } catch (Exception e) {
                String msg = "ERROR in BlooggerAPIHander.getPages";
                logger.log(LINFO, msg, e);
                throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
            }
        }
        return results;
    }

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
   /* public  Object getPage(String blog_id, String pageid, String userid, 
                   String password) throws Exception {
        logger.log(LINFO, 
                   "wp.getPage() Called ===========[ SUPPORTED wp.getPage v.2 ]=====");
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
            logger.log(Level.WARNING, msg + ":" + e);
            throw new XmlRpcException(UNKNOWN_EXCEPTION + ":" + msg);
        }
    }*/
}
