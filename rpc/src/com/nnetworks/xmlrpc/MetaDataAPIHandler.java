package com.nnetworks.xmlrpc;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import java.util.logging.Level;

import java.util.logging.Logger;

import org.apache.xmlrpc.metadata.XmlRpcSystemImpl;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcInvocation;
import redstone.xmlrpc.XmlRpcStruct;

public class MetaDataAPIHandler extends BaseAPIHandler {
    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    public MetaDataAPIHandler() {
    }

    /** listMethods() 
              Implements the "system.listMethods" call.
     **/
    public static
    /*
                      * Added introspection - http://ws.apache.org/xmlrpc/introspection.html
                      *
                      * Introspection is the servers ability to provide metadata to the client. The client may ask "What method
                      * names does the server offer?", "How do I invoke method 'foo'?", or "Can you give me help on method
                      * 'foo'?".
                      *
                      * The client does so by invoking the special methods "system.listMethods", "system.methodSignature" and
                      * "system.methodHelp". These are described in detail in the non-official specification for XML-RPC
                      * introspection, which you'll find at http://scripts.incutio.com/xmlrpc/introspection.html.
                      *
                      */
    Object listMethods() throws XmlRpcException {
        logger.log(LINFO, 
                   "listMethods() Called ===[ SUPPORTED v.2013 ]=======");
        // Hashtable returnStruct = new Hashtable();
        // returnStruct.elements() .put("system.listMethods", "system.listMethods");
        Vector results = new Vector();
        // results.addElement("system.multicall");
         results.addElement("wp.getUsersBlogs");
         results.addElement("wp.newPost");
         results.addElement("wp.editPost");
         results.addElement("wp.deletePost");
         results.addElement("wp.getPost");
         results.addElement("wp.getPosts");
         results.addElement("wp.newTerm");
         results.addElement("wp.editTerm");
         results.addElement("wp.deleteTerm");
         results.addElement("wp.getTerm");
         results.addElement("wp.getTerms");
         results.addElement("wp.getTaxonomy");
         results.addElement("wp.getTaxonomies");
         results.addElement("wp.getUser");
         results.addElement("wp.getUsers");
         results.addElement("wp.getProfile");
         results.addElement("wp.editProfile");
         results.addElement("wp.getPage");
         results.addElement("wp.getPages");
         results.addElement("wp.newPage");
         results.addElement("wp.deletePage");
         results.addElement("wp.editPage");
         results.addElement("wp.getPageList");
         results.addElement("wp.getAuthors");
         results.addElement("wp.getCategories");
         results.addElement("wp.getTags");
         results.addElement("wp.newCategory");
         results.addElement("wp.deleteCategory");
         results.addElement("wp.suggestCategories");
         results.addElement("wp.uploadFile");
         results.addElement("wp.getCommentCount");
         results.addElement("wp.getPostStatusList");
         results.addElement("wp.getPageStatusList");
         results.addElement("wp.getPageTemplates");
         results.addElement("wp.getOptions");
         results.addElement("wp.setOptions");
         results.addElement("wp.getComment");
         results.addElement("wp.getComments");
         results.addElement("wp.deleteComment");
         results.addElement("wp.editComment");
         results.addElement("wp.newComment");
         results.addElement("wp.getCommentStatusList");
         results.addElement("wp.getMediaItem");
         results.addElement("wp.getMediaLibrary");
         results.addElement("wp.getPostFormats");
         results.addElement("wp.getPostType");
         results.addElement("wp.getPostTypes");
         results.addElement("wp.getRevisions");
         results.addElement("wp.restoreRevision");
         results.addElement("blogger.getUsersBlogs");
         results.addElement("blogger.getUserInfo");
         results.addElement("blogger.getPost");
         results.addElement("blogger.getRecentPosts");
         results.addElement("blogger.newPost");
         results.addElement("blogger.editPost");
         results.addElement("blogger.deletePost");
         results.addElement("metaWeblog.newPost");
         results.addElement("metaWeblog.editPost");
         results.addElement("metaWeblog.getPost");
         results.addElement("metaWeblog.getRecentPosts");
         results.addElement("metaWeblog.getCategories");
         results.addElement("metaWeblog.newMediaObject");
         results.addElement("metaWeblog.deletePost");
         results.addElement("metaWeblog.getUsersBlogs");
         results.addElement("mt.getCategoryList");
         results.addElement("mt.getRecentPostTitles");
         results.addElement("mt.getPostCategories");
         results.addElement("mt.setPostCategories");
         results.addElement("mt.supportedMethods");
         results.addElement("mt.supportedTextFilters");
         results.addElement("mt.getTrackbackPings");
         results.addElement("mt.publishPost");
         results.addElement("pingback.ping");
         results.addElement("pingback.extensions.getPingbacks");
         results.addElement("demo.sayHello");
         results.addElement("demo.addTwoNumbers");
         results.addElement("system.getCapabilities"); //# Incutio XML-RPC Library call - added 17.06.2013
         results.addElement("system.listMethods"); //# Incutio XML-RPC Library call - added 17.06.2013
         results.addElement("system.multicall");// # Incutio XML-RPC Library call - added 17.06.2013
         
       
        return results;
    }


    /** java.lang.String       methodHelp(java.lang.String methodName)
              Implements the "system.methodHelp" call.
     java.lang.String[][]   methodSignature(java.lang.String methodName)
              Implements the "system.methodSignature" call.    
     INFO: system.multicall:[{params=[200125, drew, xxi90yarnet], methodName=wp.getOptions}, {params=[200125, drew, xxi90yarnet], methodName=wp.getPostFormats}, {params=[200125, drew, xxi90yarnet], methodName=wp.getCategories}, {params=[200125, drew, xxi90yarnet, {number=100}], methodName=wp.getComments}, {params=[200125, drew, xxi90yarnet, 40], methodName=metaWeblog.getRecentPosts}, {params=[200125, drew, xxi90yarnet, 40], methodName=wp.getPages}]

              **/
              
     private static String fileRevision = "$Revision: 2013.05 $";
}
