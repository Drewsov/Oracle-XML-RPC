package com.nnetworks.xmlrpc;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import redstone.xmlrpc.XmlRpcDispatcher;


public class RPCping extends BaseAPIHandler {
    public String Url = "http://pulse/xmlrpc";
    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    public RPCping() {
    }


    /***********************/
    public static XmlRpcClient RPCConnect(String url) throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setEnabledForExtensions(true);
        config.setServerURL(new URL(url));
        config.setEncoding("UTF-8");
        config.setBasicEncoding("UTF-8");
        org.apache.xmlrpc.client.XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));  // http://ws.apache.org/xmlrpc/client.html "XmlRpcCommonsTransportFactory" "XmlRpcLocalTransportFactory"
        client.setConfig(config);
        return client;
    } 
  /******
   * 
           * <?xml version="1.0"?>
        <methodCall>
        <methodName>weblogUpdates.extendedPing</methodName>
        <params>
        <param><value><string>ярославские областные новости</string></value></param>
        <param><value><string>http://yaroblnews.ru/</string></value></param>
        <param><value><string>http://yaroblnews.ru/feed/</string></value></param>
        </params></methodCall>

   * ******/
  
   /*******************/
    public static Object weblogUpdatesExtendedPing (String weblogName,String weblogUrl,String weblogRss,String xmlRPCurl) throws Exception { 
        logger.log(LINFO,"** Start weblogUpdates_extendedPing => "+" weblogName=>"+weblogName+" weblogUrl=>"+weblogUrl+" weblogRss=>"+weblogRss);
        XmlRpcClient client = RPCConnect(xmlRPCurl);
        Vector params = new Vector();
        params.addElement(weblogName);
        params.addElement(weblogUrl);
        params.addElement(weblogRss); 
        
        BaseAPIHandler.logg("==========  weblogUpdates.extendedPing  ========== ");
        Object struct = (Object)client.execute("weblogUpdates.extendedPing", params);          
        printObject(struct);
        BaseAPIHandler.logg("======== end weblogUpdates.extendedPing ========== ");       
        return struct;
    } 
    
    
 
   
    public static Object weblogUpdates_extendedPing(String weblogName, String weblogUrl ,String weblogRss,String xmlRPCurl)  throws Exception { 
        
         logger.log(LINFO,"** Start weblogUpdates_extendedPing   => "+" weblogName=>"+weblogName+" weblogUrl=>"+weblogUrl+" weblogRss=>"+weblogRss);
         redstone.xmlrpc.XmlRpcClient client = new redstone.xmlrpc.XmlRpcClient(xmlRPCurl,true);
        // client.setTransportFactory(new XmlRpcCommonsTransportFactory(client)); 
         Object struct = client.invoke( "weblogUpdates.extendedPing", new Object[] { weblogName,weblogUrl, weblogRss } );
         BaseAPIHandler.printObject (xmlRPCurl);                
         BaseAPIHandler.printObject (struct);  
         logger.log(LINFO,"** End weblogUpdates_extendedPing");
        return struct;  
    } 
    public Hashtable extendedPing(String weblogName, String weblogUrl, 
                                  String weblogRss) {
        logger.log(LINFO, 
                   "extendedPing() Called =====[ SUPPORTED  weblogUpdates.extendedPing ]=====");
        logger.log(LINFO, "     weblogName: " + weblogName);
        logger.log(LINFO, "     weblogUrl: " + weblogUrl);
        logger.log(LINFO, "     weblogRss: " + weblogRss);
        Hashtable result = new Hashtable();
        result.put("flerror", false);
        result.put("message", "Thanks for pinging www.dynamicpsp.com");
        System.out.println("Execute extendedPing");
        try {
           // if (validateUser(dusername, dpassword)) // TODO 
                    WeblogPingBack(weblogName, weblogUrl + ", " + weblogRss);
            System.out.println("JXML-RPC Execute extendedPing:" + weblogName + ", " + 
                               weblogUrl + ", " + weblogRss);
        } catch (Exception e) {
            System.out.println("extendedPing:" + e);
        }
        return result;
    }

    public Hashtable ping(String weblogName, String weblogUrl) {
        logger.log(LINFO, 
                   "ping() Called =====[ SUPPORTED  weblogUpdates.ping ]=====");
        logger.log(LINFO, "     weblogName: " + weblogName);
        logger.log(LINFO, "     weblogUrl: " + weblogUrl);
        Hashtable result = new Hashtable();
        result.put("flerror", false);
        result.put("message", "Thanks for pinging www.dynamicpsp.com");
        try {
           // if (validateUser(dusername, dpassword))  // TODO 
            WeblogPingBack(weblogName, weblogUrl);
            System.out.println("JXML-RPC Execute ping:" + weblogName + ":" + weblogUrl);
        } catch (Exception e) {
            System.out.println("ping:" + e.getMessage());
        }
        return result;
    }
    private static String fileRevision = "$Revision: 2013.06 $";
    
    //Start weblogUpdates_extendedPing => :weblogName=>ISK.Local :weblogUrl=>http://192.168.0.103:8990/ohs_images/ :weblogRss=>http://www.sizak.ru/i/rss-100044.html
     public static void main(String[] args) throws Exception {
            System.setProperty("console.encoding", "Cp1251"); 
            System.setProperty("file.encoding", "Cp1251");
          // weblogUpdates_extendedPing("ISK.Local","http://www.sizak.ru/i/rss-100044.html","http://hitmedia.ru:8080/rpc2");
         /**************/
           // weblogUpdates_extendedPing("ISK.Local","http://www.sizak.ru/","http://www.sizak.ru/i/rss-100044.html","http://hitmedia.ru/jxml/media/");   // <--  это работает
           // weblogUpdatesExtendedPing("ISK.Local","http://www.sizak.ru/","http://www.sizak.ru/i/rss-100044.html","http://hitmedia.ru/jxml/media/");    // <--  это работает
         /************/
         //  weblogUpdates_extendedPing("ISK.Local","http://www.sizak.ru/i/rss-100044.html","http://hitmedia.ru/jxml/media/");
        // weblogUpdates_extendedPing("ISK.Local","http://www.sizak.ru","http://www.sizak.ru/i/rss-100044.html","http://192.168.0.103:8080/RPC2"); //           
          
         //  weblogUpdatesExtendedPing("ISK.Local","http://www.sizak.ru/","http://www.sizak.ru/i/rss-100044.html","http://192.168.0.103:8080/RPC2/");
         // weblogUpdatesExtendedPing("ISK.Local","http://www.sizak.ru/","http://www.sizak.ru/i/rss-100044.html","http://192.168.0.103:8990/RPC2");
          
          // weblogUpdates_extendedPing("ISK.Local","http://www.sizak.ru/","http://www.sizak.ru/i/rss-100044.html","http://hitmedia.ru:8080/RPC2");
          // weblogUpdatesExtendedPing("ISK.Local","http://www.sizak.ru/","http://www.sizak.ru/i/rss-100044.html","http://hitmedia.ru/jxml/media/"); 
             weblogUpdatesExtendedPing("“ехнологии создани€ сайта","http://hitmedia.ru/","http://www.hitmedia.ru/a/rss-100044.html","http://ping.blogs.yandex.ru/RPC2");
            weblogUpdatesExtendedPing("“ехнологии создани€ сайта","http://hitmedia.ru","http://www.hitmedia.ru/a/rss-100044.html","http://hitmedia.ru:8080/RPC2");
     }
}
/******
 * 
 * String weblogName = "Living Tao";
String weblogUrl = "http://livingtao.blogspot.com/";
//create a client pointing to the rpc service on javablogs
XmlRpcClient client = new XmlRpcClient("http://javablogs.com/xmlrpc", true);
//invoke the method to queue the blog for an update
Object token = client.invoke("weblogUpdates.ping", new Object[] { weblogName, weblogUrl} );
System.out.println(token);
 * ********//******
 * <?xml version="1.0"?>
<methodCall>
<methodName>weblogUpdates.ping</methodName>
<params>
<param>
<value>Someblog</value>
</param>
<param>
<value>http://spaces.msn.com/someblog</value>
</param>
</params>
</methodCall>

<methodCall> 
    <methodName>weblogUpdates.ping</methodName>  
<params> 
<param> 
            <value>'.$blogname.'</value> 
        </param> 
<param> 
            <value>'.$blogurl.'</value> 
        </param> 
    </params> 
</methodCall>';  
 * 
 * ********/