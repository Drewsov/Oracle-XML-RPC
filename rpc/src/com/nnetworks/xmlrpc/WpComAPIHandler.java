package com.nnetworks.xmlrpc;

import java.util.Hashtable;
 
import java.util.Vector;
import java.util.logging.Logger;

import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcStruct;

public class WpComAPIHandler extends BaseAPIHandler {
    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());
    public WpComAPIHandler() {
    }
    /***
     INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:getFeatures:!java.lang.Integer java.lang.String java.lang.String 
 
     ****/
    public Object getFeatures(Integer blogid, String userid, 
                              String password) throws Exception {
        logger.log(LINFO, 
                   "wpcom.getFeatures() Called =========[ SUPPORTED wpcom.getFeatures]=====");
        /*
                                                                       * The method call  wpcom.getFeatures  ( blog_id, username, password ) which returns a struct.
                                                                       * Just a simple way to expose data on WPCOM specific features (VideoPress or space upgrade for instance).
                                                                       *
                                                                       * Right now the only field in the struct is "videopress_enabled", with a boolean value.
                                                                       *
                                                                       */
        Hashtable struct = new Hashtable();
        struct.put("videopress_enabled", true);
        return struct;
                                  }
    /*
             * The method call  wpcom.getFeatures  ( blog_id, username, password ) which returns a struct.
             * Just a simple way to expose data on WPCOM specific features (VideoPress or space upgrade for istance).
             *
             * Right now the only field in the struct is "videopress_enabled", with a boolean value.
             *
             */
/*
          protected synchronized Hashtable getFeatures(Blog blog) throws Exception {
                    try {
                            logg(">>> reading WP.COM Blog Features : " + blog.getName());

                            Vector args = new Vector(3);
                            args.addElement(String.valueOf(blog.getId()));
                            args.addElement(mUsername);
                            args.addElement(mPassword);

                            Object response = execute("wpcom.getFeatures", args);
                            if (connResponse.isError()) {
                                    return null;
                            }

                            Hashtable features = (Hashtable) response;
                            Enumeration elements = features.keys();
                            for (; elements.hasMoreElements();) {
                                    String key = (String) elements.nextElement();
                                    Log.trace("key: " + key);
                                    Log.trace("value: " + features.get(key));
                            }
                            Log.debug("<<< reading WP.COM Blog Features : " + blog.getName());
                            return features;
                    } catch (ClassCastException cce) {
                            throw new Exception ("Error while reading blog Features");
                    }
            }
 */            
}
