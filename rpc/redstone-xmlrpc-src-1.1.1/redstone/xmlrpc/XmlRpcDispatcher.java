/*
    Copyright (c) 2005 Redstone Handelsbolag

    This library is free software; you can redistribute it and/or modify it under the terms
    of the GNU Lesser General Public License as published by the Free Software Foundation;
    either version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License along with this
    library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
    Boston, MA  02111-1307  USA
*/

package redstone.xmlrpc;

import com.nnetworks.xmlrpc.BaseAPIHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;


/**
 *  Objects of the XmlRpcDispather class perform the parsing of inbound XML-RPC
 *  messages received by an XmlRpcServer and are responsible for invoking handlers and
 *  dealing with their return values and exceptions.
 *
 *  @author  Greger Olsson
 */

public class XmlRpcDispatcher extends XmlRpcParser
{
    public static String DEFAULT_HANDLER_NAME = "__default__";

    /**
     *  Creates a dispatcher and associates it with an XmlRpcServer and the IP address
     *  of the calling client.
     *
     *  @param server
     */
    
    public XmlRpcDispatcher( XmlRpcServer server, String callerIp )
    {
        this.server = server;
        this.callerIp = callerIp;
    }
    
    
    /**
     *  Returns the IP adress of the client being dispatched.
     * 
     *  @return The IP adress of the client being dispatched.
     */

    public String getCallerIp()
    {
        return callerIp;
    }
    

    /**
     *  Inbound XML-RPC messages to a server are delegated to this method. It
     *  performs the parsing of the message, through the inherited parse() method,
     *  and locates and invokes the appropriate invocation handlers.
     *
     *  @throws XmlRpcException When the inbound XML message cannot be parsed due to no
     *                   available SAX driver, or when an invalid message was received.
     *                   All other exceptions are caught and encoded within the
     *                   XML-RPC writer.
     */

    public void dispatch( InputStream xmlInput, Writer xmlOutput ) throws XmlRpcException
    {
        // Parse the inbound XML-RPC message. May throw an exception.
        //System.out.println("Parse the inbound XML-RPC message. May throw an exception"); 
        parse( xmlInput );
        //System.out.println("Response is written directly to the Writer supplied by the XmlRpcServer"); 
        // Response is written directly to the Writer supplied by the XmlRpcServer.
         
        this.writer = xmlOutput;

        // Exceptions will from hereon be encoded in the XML-RPC response.

        int separator = methodName.lastIndexOf( "." );

        if ( separator == -1 )
        {
            methodName = DEFAULT_HANDLER_NAME + "." + methodName;
            separator  = DEFAULT_HANDLER_NAME.length();
        }

        final String handlerName = methodName.substring( 0, separator );
                      methodName = methodName.substring( separator + 1 );
       
       // System.out.println("=> "+ handlerName+"."+methodName+arguments );
          BaseAPIHandler.out("=> "+ handlerName+"."+methodName+arguments );
     
        XmlRpcInvocationHandler handler = server.getInvocationHandler( handlerName );
      
        if ( handler != null )
        {
            final int callId = ++callSequence;
            XmlRpcInvocation invocation = null;
            
            if( server.getInvocationInterceptors().size() > 0 )
            {
                invocation = new XmlRpcInvocation(
                    callId,
                    handlerName,
                    methodName,
                    handler,
                    arguments,
                    writer );
            }
           logger.log( BaseAPIHandler.LINFO,"=> "+ handlerName+"."+methodName+arguments );

            if (handlerName.equalsIgnoreCase("system")&& methodName.equalsIgnoreCase("multicall")){                      
            String handlerN  = null;
            String methodN   = null;
              try{
                      Vector requests = new Vector(arguments);
                      Vector ret      = new Vector();
                      List   mArgs     = null;              
                      for (int i = 0; i < requests.size(); i++){
                     XmlRpcStruct call = (XmlRpcStruct) requests.elementAt(i);
                         String mName  =  (String)call.get("methodName");
                                mArgs  =  (List)  call.get("params");
                               int sep =  mName.lastIndexOf( "." ); 
                            handlerN   =  mName.substring( 0, sep );
                            methodN    =  mName.substring( sep + 1 );
                            methodName =  methodN;
                           //if (BaseAPIHandler.FINER == Level.FINER) System.out.println("==> " + handlerN + "." +  methodN + mArgs + " <==");
    try {
                            handler = server.getInvocationHandler(handlerN);
                            invocation = 
                                    new XmlRpcInvocation(callId, handlerN, methodN, 
                                                         handler, mArgs, 
                                                         writer);
                            logger.log(BaseAPIHandler.LINFO, 
                                       "==> " + handlerN + "." + methodN + 
                                       mArgs + " <==");
    
                            Vector v = new Vector();
                            Object returnValue =  handler.invoke(methodN, mArgs);
                            
                            BaseAPIHandler.logg("===========> multicall @ "+methodN+ " v.20.12.2013 <============");
                            if (methodN.equalsIgnoreCase("getRecentPosts")) {
                                BaseAPIHandler.logg("===========> multicall.getRecentPosts v.2013 <============");
                                 v.addElement(postProcess(invocation, returnValue));
                                 ret.addElement(v);
                                  
                            } 
                            else if (methodN.equalsIgnoreCase("getCategories")) { 
                                    BaseAPIHandler.logg("===========> multicall.getCategories  v.20.12.2013 <============");
                                    v.addElement(postProcess(invocation, returnValue));
                                    ret.addElement(v);
                                
                            }
                            else if (methodN.equalsIgnoreCase("multiCall")) {
                                writeError(-32600, 
                                           " recurse method multiCall not supported");
                                           return;
                            } else {                     
                               BaseAPIHandler.logg("===========> multicall."+methodN+" v.2013 <============");
                                ret.addElement(postProcess(invocation,  returnValue)); //TODO 
                               // System.out.println(returnValue);
                             }
                        }

                        catch (Throwable x) {
                            String message = x.toString();
                            writeError(-32600, 
                                       message + " No such method " + handlerN + 
                                       "." + methodN);
                        }
                    }
                    try {
                        BaseAPIHandler.logg("===========> multicall @ "+ " v.20.12.2013 <============");
                        writeValue(ret);
                        BaseAPIHandler.logg("===========>\n"+ ret + "\n<============");
                        

                    } catch (Exception e) {
                        System.out.println("==> " + handlerN + "." + methodN + 
                                           " ==> " + e.toString() + 
                                           "\n   <==");
                        Writer writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        e.printStackTrace(printWriter);
                        String s = writer.toString();
                        System.out.println(s);

                    }
                           
                        // if (BaseAPIHandler.FINER == Level.FINER)  System.out.println("==> \n " + ret + "\n<==");                
                }
             catch (Exception e) { 
              String message = e.toString();
              writeError( -1, message + " <= system.multicall => " + handlerN+"."+methodN  );}
             return;
            }
            
            if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("getRecentPosts")){
                 logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                /* if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                  methodName  = "getRecentPostsWpIphoneClient";}*/
             }
             
            if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("getRecentPosts")){
                      logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                      if(BaseAPIHandler.UserAgent.startsWith("Mozilla")){
                       methodName  = "getRecentPostsWindowsLiveWriter";} 
              }
            
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getPages")){
                      logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                      if(BaseAPIHandler.UserAgent.startsWith("Mozilla")){
                       methodName  = "getPagesWindowsLiveWriter";} 
              } 
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getCategories")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("Mozilla")){
                 methodName  = "getCategoriesWindowsLiveWriter";} 
            }
            
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getAuthors")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("Mozilla")){
                 methodName  = "getAuthorsWindowsLiveWriter";} 
            }
            if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("getPost")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                  if(BaseAPIHandler.UserAgent.startsWith("Mozilla")){
                      methodName  = "getPostMozilla";
                  }               
            }  
            
            if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("editPost")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                /*if(BaseAPIHandler.UserAgent.startsWith("wp-iphone/3.5")){
                 methodName  = "editPost32";}*/
            }
            if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("newPost")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                /*if(BaseAPIHandler.UserAgent.startsWith("wp-iphone/3.5")){
                 methodName  = "newPost32";}*/
            }
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("editPage")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                /*if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "editPage32";}*/
            }
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("newPage")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               /* if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "newPage32";}*/
            }          
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getPages")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                /* if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "getPages32";} */
            }   
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("newCategory")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               /* if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "newCategory32";}*/
            } 
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getPages")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               /*if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "getPagesWpIphoneClient";}*/
            }
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("deletePage")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                /*if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "deletePageWpIphoneClient";}*/
            }
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getCategories")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               /* if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "getIphoneCategories";}*/
            }
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("newCategory")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               /* if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "newCategoryWpIphoneClient";}*/
            }            
 
             if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("editPost")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
                 methodName  = "editPostCocoaRPC";}
            } 
            
            if (handlerName.equalsIgnoreCase("metaWeblog")&& methodName.equalsIgnoreCase("newPost")){
               logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
                methodName  = "newPostCocoaRPC";}
            }
           
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("editPage")){
               logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
                methodName  = "editPageCocoaRPC";}
            }
           if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("editComment")){
               logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
               if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
                methodName  = "editCommentCocoaRPC";}
            }          
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("newPage")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
                 methodName  = "newPageCocoaRPC";}
            }                
              if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("deleteComment")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
                 methodName  = "deleteCommentCocoaRPC";} 
            }   
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getComments")){
              logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
              if(BaseAPIHandler.UserAgent.startsWith("CocoaRPC")){
               methodName  = "getCommentsCocoaRPC";} 
            }           
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getPage")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("wp-iphone")){
                 methodName  = "getPageWpIphoneClient";}
            } 
            if (handlerName.equalsIgnoreCase("wp")&& methodName.equalsIgnoreCase("getPage")){
                logger.log( BaseAPIHandler.LINFO, BaseAPIHandler.UserAgent+":" + handlerName+"."+methodName+":"+arguments  );
                if(BaseAPIHandler.UserAgent.startsWith("Mozilla/4.0")){
                 methodName  = "getPageMozilla";}
            } 
            try
            {
                // Invoke the method, which may throw any kind of exception. If any of the
                // preProcess calls thinks the invocation should be cancelled, we do so.
                 BaseAPIHandler.logg("=> XmlRpcDispatcher.preProcess "+methodName);
                if ( !preProcess( invocation ) )
                {   
                    BaseAPIHandler.logg("=> XmlRpcDispatcher.InvocationCancelled "+methodName);
                    writeError( -1, XmlRpcMessages.getString( "XmlRpcDispatcher.InvocationCancelled" ) );
                }
                else
                {   BaseAPIHandler.logg("=> XmlRpcDispatcher handler.invoke the method "+methodName);
                    Object returnValue = handler.invoke( methodName, arguments );
                    returnValue = postProcess( invocation, returnValue );
                    
                    // If the return value wasn't intercepted by any of the interceptors,
                    // write the response using the current serlialization mechanism.
                    
                    if ( returnValue != null )
                    {
                      // 
                         BaseAPIHandler.logg("\n writeValue\n ==> \n "+returnValue+"\n<=="); // Выход на клиента  // TODO 
                        //logger.log(BaseAPIHandler.LINFO,"==> \n "+returnValue+"\n<==");  
                        writeValue( returnValue );
                        
                    }
                    else {System.out.println("\n writeValue is null ");}
                }
            }
            catch ( Throwable t )
            {
        
                processException( invocation, t );
                int code = -1;
                if ( t instanceof XmlRpcFault )
                {
                    code = ( (XmlRpcFault) t ).getErrorCode();
                }
                
               // writeError( code, t.getClass().getName() + ": " + t.getMessage() );
                 logger.log( BaseAPIHandler.LINFO,t.getClass().getName()+ ": " + t.getMessage());
                  writeError( code,  
                  "Dynamic PSP/XMLRPC\n  " + 
                  " ver 1.02.2015 \n " + 
                  " Copyright(c) 2001-2015 by HitMedia LLC  www.hitmedia.ru \n " +
                  " All rights reserved.\n" +
                  " Author:\n" +
                  " andrew.toropov@gmail.com \n" +
                  " " + handlerName+"."+methodName+"=>"+arguments+" \n " + 
                  t.getMessage()+" \n " + 
                  BaseAPIHandler.UserAgent+"\n"
                  );
                  t.printStackTrace();
                  // AAT 16.10.2011
                  // AAT 26.04.2013
                  // AAT 12.12.2013
            }
        }
        else
        {
            writeError( -1, XmlRpcMessages.getString( "XmlRpcDispatcher.HandlerNotFound" )+":"+handlerName+"."+methodName  );
        }
    }


    /**
     *  Override the endElement() method of the XmlRpcParser class, and catch
     *  the method name element. The method name element is unique for XML-RPC
     *  calls, and belongs here in the server.
     */

    public void endElement(
        String uri,
        String name,
        String qualifiedName )
        throws SAXException
    {
        if ( name.equals( "methodName" ) )
        {
            methodName = this.consumeCharData();
        }
        else
        {
            super.endElement( uri, name, qualifiedName );
        }
    }


    /**
     *  Implementation of abstract method introduced in XmlRpcParser. It will
     *  be called whenever a value is parsed during a parse() call. In this
     *  case, the parsed values represent arguments to be sent to the invocation
     *  handler of the call.
     */

    protected void handleParsedValue( Object value )
    {
        arguments.add( value );
    }


    /**
     *  Invokes all processor objects registered with the XmlRpcServer this dispatcher is
     *  working for.
     *
     *  @todo Determine a way for a preProcess call to indicate the reason for cancelling
     *        the invocation.
     *
     *  @return true if the invocation should continue, or false if the invocation should
     *          be cancelled for some reason.
     */

    private boolean preProcess( XmlRpcInvocation invocation )
    {
        XmlRpcInvocationInterceptor p;

        for ( int i = 0; i < server.getInvocationInterceptors().size(); ++i )
        {
            p = ( XmlRpcInvocationInterceptor ) server.getInvocationInterceptors().get( i );

            if ( !p.before( invocation ) )
            {
                return false;
            }
        }

        return true;
    }

    
    /**
     *  Invokes all interceptor objects registered with the XmlRpcServer this dispatcher is
     *  working for.
     */

    private Object postProcess( XmlRpcInvocation invocation, Object returnValue )
    {
        XmlRpcInvocationInterceptor p;

        for ( int i = 0; i < server.getInvocationInterceptors().size(); ++i )
        {
            p = ( XmlRpcInvocationInterceptor ) server.getInvocationInterceptors().get( i );
            returnValue = p.after( invocation, returnValue );
            
            // If the interceptor intercepts the return value completely and takes
            // responsibility for writing a response directly to the client, break
            // the interceptor chain and return immediately.
            
            if ( returnValue == null )
            {
                return null;
            }
        }
        
        return returnValue;
    }


    /**
     *  Invokes all processor objects registered with the XmlRpcServer this dispatcher is
     *  working for.
     */

    private void processException(
        XmlRpcInvocation invocation,
        Throwable exception )
    {
        XmlRpcInvocationInterceptor p;

        for ( int i = 0; i < server.getInvocationInterceptors().size(); ++i )
        {
            p = ( XmlRpcInvocationInterceptor ) server.getInvocationInterceptors().get( i );

            p.onException( invocation, exception );
        }
    }


    /**
     *  Writes a return value to the XML-RPC writer.
     *
     *  @param value The value to be encoded into the writer.
     * @throws IOException 
     */

    private void writeValue( Object value ) throws IOException
    {
        server.getSerializer().writeEnvelopeHeader( value, writer );
        
        if ( value != null )
        {
            server.getSerializer().serialize( value , writer );
        }
         
        server.getSerializer().writeEnvelopeFooter( value, writer );
    }


    /**
     *  Creates an XML-RPC fault struct and puts it into the writer buffer.
     *
     *  @param code The fault code.
     *  @param message The fault string.
     */

    private void writeError( int code, String message )
    {
        try
        {
            logger.log( Level.WARNING, message );
            this.server.getSerializer().writeError( code, message, writer );
        }
        catch ( IOException ignore )
        {
            // If an exception occurs at this point there is no way to recover.
            // We are already trying to send a fault to the client. We swallow
            // the exception and trace it to the console.
            
            logger.log(
                Level.SEVERE,
                XmlRpcMessages.getString( "XmlRpcDispatcher.ErrorSendingFault" ),
                ignore );
        }
    }


    /** The XmlRpcServer this dispatcher is working for */
    private XmlRpcServer server;

    /** The IP address of the caller */
    private String callerIp;

    /** The name of the method the client wishes to call */
    private String methodName;

    /** The arguments for the method */
    private List arguments = new ArrayList( 6 );

    /** Holds the XML-RPC repsonse as it is built up */
    private Writer writer;
    
    /** The current call sequence for traceability */
    private static int callSequence;
    
    /** Logger used to log problems an exceptions. */
    private static Logger logger = Logger.getLogger( XmlRpcDispatcher.class.getName() );
}
