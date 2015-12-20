/*
    Copyright (c) 2007 Redstone Handelsbolag

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

import com.nnetworks.jopa.PathInfo;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URL;

import java.util.StringTokenizer;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.nnetworks.resources.ResourcePool;
import com.nnetworks.xmlrpc.*;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;

import java.util.Enumeration;

import java.util.Iterator;
import java.util.Properties;

import java.util.TreeMap;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;


/**
 *  Servlet that publishes an XmlRpcServer in a web server.
 *
 *  @author Greger Olsson
 */

public class XmlRpcServlet extends HttpServlet
{
    public final static String version =
      "Dynamic PSP/XMLRPC Gateway Java Servlet 2010-2015/Oracle/DPSP/Tomcat";
    private File            fileCfg;    // Config file
    private String          cfgFile;
    private String          logLevel;
    
   /**
     *  Initializes the servlet by instantiating the XmlRpcServer that will
     *  hold all invocation handlers and processors. The servlet configuration
     *  is read to see if the XML-RPC responses generated should be streamed
     *  immediately to the resonse (non-compliant) or if they should be buffered
     *  before being sent (compliant, since then the Content-Length header may
     *  be set, as stipulated by the XML-RPC specification).<p>
     *  
     *  Further, the configuration is read to see if any services are mentioned
     *  declaratively that are to be instantiated and published. An alternative
     *  to this is to create a new servlet class extending from XmlRpcServlet
     *  that publishes services programmatically.
     */
   private HttpServletResponse res;
   
    public void init( ServletConfig config ) throws ServletException
    {
    
        // Init SQL-code depot:
        String services       = config.getInitParameter("services" );
        String contentType    = config.getInitParameter("contentType" );
        String streamMessages = config.getInitParameter("streamMessages" );
                 this.cfgFile = config.getInitParameter("cfgfile");
                this.logLevel = config.getInitParameter("loglevel");
        if ( streamMessages != null && streamMessages.equals( "1" ) )
        {
            this.streamMessages = true;
        }
        
        if ( contentType != null && contentType.startsWith( "text/javascript+json" ) )
        {
            this.contentType = "text/javascript+json";
            server = new XmlRpcServer( new XmlRpcJsonSerializer() );
        }
        else
        {
            this.contentType = "text/xml";
            server = new XmlRpcServer();
        }
        
         //  this.contentType += "; charset=" + XmlRpcMessages.getString( "XmlRpcServlet.Encoding" );
         this.contentType += "; charset=" + XmlRpcMessages.getString( "XmlRpcClient.Encoding" );
   
        
        if ( services != null )
        {
            addInvocationHandlers( services );
        }
    }

    
    /**
     *  Returns the XmlRpcServer that is backing the servlet.
     * 
     *  @return The XmlRpcServer backing the servlet.
     */
    
    public XmlRpcServer getXmlRpcServer()
    {
        return server;
    }

    
    /**
     *  Indicates whether or not messages are streamed or if they are built in memory
     *  to be able to calculate the HTTP Content-Length.
     * 
     *  @return True if messages are streamed directly over the socket as it is built.
     */
    
    public boolean getStreamMessages()
    {
        return streamMessages;
    }

    
    /**
     *  Returns the content type of the messages returned from the servlet which is
     *  text/xml for XML-RPC messages and text/javascript+json for JSON messages.
     * 
     *  @return The content type of the messages returned from this servlet.
     */
    
    public String getContentType()
    {
        return contentType;
    }
    
   
   protected void getStream (HttpServletRequest in) throws ServletException, IOException{
   
       ServletInputStream bin =  in.getInputStream();
       int b;
       while ( ( b = bin.read() ) != -1 )
       {

           char c = (char)b;         

           System.out.print(""+(char)b); //This prints out content that is unreadable.
                                         //Isn't it supposed to print out html tag?
       }
                                          System.out.println("");
   }
   public void log (int level,String s1,String s2 ) {
        logger.log(BaseAPIHandler.LINFO, s1+s2);
      }

    public int loadCfg ()
    {
      if (!this.fileCfg.exists()) {
         log(0, "Config file does not exists: ",this.fileCfg.getAbsolutePath() +":"+ this.fileCfg.getPath());
        return 0;
      }   
        BaseAPIHandler.cfg = new ResourcePool();
        BaseAPIHandler.cfg.setBaseProperties("Default");
        BaseAPIHandler.cfg.setSkipChars("\r\0");
      try {
        BaseAPIHandler.cfg.consultPropertiesPool(this.fileCfg);
      }
      catch (IOException e) {
         log(0, "Failed to load config file", e.getMessage());
        return -1;
      }
     return 1;
    }
 
    //----------------------------------------------------------------------
    // Debugging:
    //----------------------------------------------------------------------
     protected String        effCharset;
     public static void logg (int lvl, String prn) {
         if(BaseAPIHandler.LINFO == Level.INFO)    System.out.println(prn);
         if(BaseAPIHandler.LINFO == Level.WARNING) System.out.println(prn);
         if(BaseAPIHandler.LINFO == Level.SEVERE)  System.out.println(prn);
     }
     protected void prepareParams( ServletRequest sr, String sPath )
     {
       String sLogname = null;
       TreeMap params = new TreeMap();
       // sort the names - it appears that we have to do it for some silly
       // applications like WebRecruiter, which uses arguments positions to
       // determine their meaning
       logg(3,"=> prepareParams: "+sPath);
       for ( Enumeration e = sr.getParameterNames(); e.hasMoreElements(); )
         params.put(e.nextElement(), "");

       for ( Iterator keys = params.keySet().iterator(); keys.hasNext(); )
       {
         String sKey = (String)keys.next();
         String[] asVal = sr.getParameterValues(sKey);
         logg(3,"=> sKey: "+sKey);      
         if (asVal != null) 
         {
           if ((sKey.equalsIgnoreCase("ln")||sKey.equalsIgnoreCase("id_")) && sPath != null)
           {
             for (int i = 0; i < asVal.length; i++)
             {
     //           sLogname = (asVal[i].startsWith("/") ? sPath + asVal[i] : sPath + '/' + asVal[i]);
                sLogname = (asVal[i].startsWith("/") ? asVal[i] :  asVal[i]);
                logg(3,"=> sLogname: "+sLogname);
                //appPar(sKey, unescape(sLogname, this.effCharset));
             }
           }
           else 
           {
             for (int i = 0; i < asVal.length; i++)
               //appPar(sKey, unescape(asVal[i], this.effCharset));
               logg(3,"=> 2sKey: "+sKey);  
           }
         }
       }
       if (sPath != null && sLogname == null){
         //if(pi.iCount < 3) sPath = this.pi.getItem(pi.iCount);
         logg(3,"=> prepareParams: ln="+sPath);
         //appPar("ln", sPath);
         }

     }

     protected void prepareParams( ServletRequest sr )
     {
       prepareParams(sr, null);
     }
    protected void spyRequest
    ( StringBuffer sb
    , String  sMode
    , String  sMethod
    , String  sReqURI
    , HttpServletRequest request
    )
    { 
      sb.setLength(0);
      sb.append("----- JXML.spyRequest ------------------------------------------- ");
      sb.append(sMode);
     // System.out.println(  sb.toString());

      sb.setLength(0);
      sb.append("=> ");
      sb.append(sMethod);
      sb.append(' ');
      sb.append(sReqURI);
      sb.append(" HTTP/1.1");
     // System.out.println( sb.toString());

      String sName;
      Enumeration en;
      en = request.getHeaderNames();
      while (en.hasMoreElements()) 
      {
        sName = (String)en.nextElement();
        if (sName.equalsIgnoreCase("USER-AGENT")){
            BaseAPIHandler.UserAgent = request.getHeader(sName);             
        }
        sb.setLength(0);
        sb.append("=> ");
        sb.append(sName);
        sb.append(": ");
        sb.append(request.getHeader(sName));
       // System.out.println( sb.toString());
      }

      sb.setLength(0);
        //prepareParams(request);
    }
         public void doScreen(
             HttpServletRequest req,
             HttpServletResponse res,
             String inresponse)
             throws ServletException, IOException {
                 Writer responseWriter = new StringWriter( 2048 );                   
                 String response =   
                 "<body>" +
                 inresponse+
                 "</body>";
                 res.setContentLength( response.length() );
                 responseWriter = res.getWriter();
                 responseWriter.write( response );
                 responseWriter.flush();                 
             }
/****************************************/
    public void readConfigFile(HttpServletRequest req, 
                         PathInfo pi) throws ServletException {
        StringBuffer sb = new StringBuffer(128);
        String sMethod = req.getMethod();
        String sReqURI = req.getRequestURI();
        String sPathInfo = req.getPathInfo();

        if (pi.iCount < 1) {
            logger.log(BaseAPIHandler.LINFO, "getRequestURI() =>" + sReqURI);
            logger.log(BaseAPIHandler.LINFO, "No DAD specified:" + sPathInfo);
        }
        // DAD init
        BaseAPIHandler.DADNAME = pi.getItem(1);
        logger.log(BaseAPIHandler.LINFO, 
                   "BaseAPIHandler.DADNAME:" + BaseAPIHandler.DADNAME);
        if (BaseAPIHandler.LINFO == Level.INFO) {
            System.out.println("DAD specified:" + 
                               BaseAPIHandler.pi.getItem(1));
            spyRequest(sb, null, sMethod, sReqURI, req);
        }

        BaseAPIHandler.UserAgent = req.getHeader("USER-AGENT"); //TODO 

        /****/
        int i = -1;

        //s = getInitParameter("cfgfile");
        String s = this.cfgFile;
        this.cfgFile = s;
        if (s == null)
            s = "/jxml/cfg/jxml.conf";
        this.fileCfg = new File(s);
        logger.log(BaseAPIHandler.LINFO, 
                   "Loading resources:" + fileCfg.getAbsolutePath());
        i = loadCfg();
        if (i == 0) {
            try {
                PrintWriter out = res.getWriter();
                logger.log(BaseAPIHandler.LINFO, 
                           "Loading resources:" + fileCfg.getAbsolutePath());
            } catch (java.lang.NullPointerException e) {
                logger.log(BaseAPIHandler.LINFO, 
                           "NullPointerException:Loading resources:" + 
                           fileCfg.getAbsolutePath());
            } catch (java.io.IOException e) {
            }
            throw new ServletException("Config file does not exist:" + 
                                       fileCfg.getAbsolutePath());
        } else if (i < 0) {
            throw new ServletException("Failed to load config file " + 
                                       fileCfg.getAbsolutePath());
        }
        try {
            BaseAPIHandler.DADNAME = BaseAPIHandler.pi.getItem(1);
            BaseAPIHandler.uProps = 
                    BaseAPIHandler.cfg.getProperties(BaseAPIHandler.DADNAME.toUpperCase());
            BaseAPIHandler.initDAD(BaseAPIHandler.DADNAME);
        } catch (Exception e) {
            System.out.println("Failed load config for DAD ->" + 
                               BaseAPIHandler.DADNAME);
        }
    }

    /****************************************/
    public void doGet(HttpServletRequest req, 
                      HttpServletResponse res) throws ServletException, 
                                                      IOException {
        /***  reading config from cfgfile ***/
        String sReqURI = req.getRequestURI();
        PathInfo pi = new PathInfo(sReqURI);
        BaseAPIHandler.pi = pi;
        readConfigFile(req, BaseAPIHandler.pi);
        /*****/
        int loglevel = Integer.parseInt(this.logLevel);
        if (loglevel > 0) {
            System.out.println("==========================  doGet =============================" + 
                               "\n" + req.getRequestURL());
        }
        /*   String response =
            "<html><head><title>dynamicpsp.com</title></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">" +
            "<a href=\"http://www.dynamicpsp.com\">Dynamic PSP/XMLRPC</a> ...... Version 2.12.20...\n" +
            "    Copyright(c) 2001-2013 by <a href=\"http://www.hitmedia.ru\">Hit-Media</a>  LLC.\n" +
            "                        All rights reserved.\n" +
            "Author: andrew.toropov@gmail.com \n" + "</pre>" +
            "<img src=\"http://www.gravatar.com/avatar/a3589f5499ec09c78548ff4c1954cc77?s=80\" />" +
            "</body></html>";
                 res.setContentType("text/html");
                 res.setContentLength( response.length() );
                 Writer responseWriter = new StringWriter(2048);
                 responseWriter = res.getWriter();
                 responseWriter.write( response );
                 responseWriter.flush(); */
                 
        res.sendRedirect(BaseAPIHandler.DBLOGPATH);
        if (loglevel > 0) {
            System.out.println("" + BaseAPIHandler.DBLOGPATH);
        }

    }
    /**
     *  Handles reception of XML-RPC messages.
     * 
     *  @param req
     *  @param res
     *  @throws ServletException
     *  @throws IOException
     */
    
    public void doPost(
        HttpServletRequest req,
        HttpServletResponse res)
        throws ServletException, IOException
    {   int loglevel = Integer.parseInt(this.logLevel);
         if (loglevel>0){System.out.println("==========================  doPost =============================");}
        String CharacterEncoding = req.getCharacterEncoding(); 
        BaseAPIHandler.toUTF8 = true;
        try { 
        if(CharacterEncoding.equalsIgnoreCase("utf-8")){
        BaseAPIHandler.toUTF8 = false;
            if (loglevel>0) System.out.println("BaseAPIHandler.toUTF8:"+CharacterEncoding);
        }
        else if (CharacterEncoding.equalsIgnoreCase("windows-1251")) {
        BaseAPIHandler.toCp1251 = true;
             if (loglevel>0) System.out.println("BaseAPIHandler.toCp1251:"+CharacterEncoding);
        }
        else if (CharacterEncoding.equalsIgnoreCase("cp866")) {
        BaseAPIHandler.toCp866 = true; 
             if (loglevel>0) System.out.println("BaseAPIHandler.cp866:"+CharacterEncoding);
        }   
        }
        catch (Exception e) {BaseAPIHandler.toUTF8 = true;}
        res.setCharacterEncoding( XmlRpcMessages.getString( "XmlRpcCharacter.Encoding" ) );
        res.setContentType( contentType );
        
        BaseAPIHandler.LINFO = Level.FINE;
        if(loglevel==1)  BaseAPIHandler.LINFO = Level.FINE;
        if(loglevel==2)  BaseAPIHandler.LINFO = Level.INFO;
        if(loglevel==3)  BaseAPIHandler.LINFO = Level.WARNING;
        if(loglevel==4)  BaseAPIHandler.LINFO = Level.SEVERE;
        
       if (loglevel>0){
        System.out.println("req.getCharacterEncoding:"+CharacterEncoding);
        System.out.println("XmlRpcCharacter.Encoding:"+XmlRpcMessages.getString( "XmlRpcCharacter.Encoding" ));
        System.out.println("contentType:"+contentType);
       }
        
        logger.log(BaseAPIHandler.LINFO,"setCharacterEncoding:"+XmlRpcMessages.getString( "XmlRpcCharacter.Encoding" ));
        logger.log(BaseAPIHandler.LINFO,"setContentType:"+contentType);
        
 
        /***  reading config from cfgfile ***/
        String sReqURI = req.getRequestURI();
        PathInfo pi = new PathInfo(sReqURI);
        BaseAPIHandler.pi = pi;
        readConfigFile(req, BaseAPIHandler.pi);
        /*****/
   
        if (  streamMessages  )
        {   
            server.execute( req.getInputStream(), res.getWriter() );
            res.getWriter().flush();
            System.out.println(streamMessages);
        }
        else
        {
            try{
            Writer responseWriter = new StringWriter( 2048 );
            server.execute( req.getInputStream(), responseWriter );
            String response = responseWriter.toString(); // TODO EXPENSIVE!!
            res.setContentLength( response.length() );
            responseWriter = res.getWriter();
            responseWriter.write( response );
            responseWriter.flush();
            }
            catch (Exception e){
            System.out.println("doPost ->"+req.getRequestURL()+" ("+e.getMessage() +") ");
            res.sendRedirect(BaseAPIHandler.DBLOGPATH);
            //doScreen(req,res,e.getMessage());
            }
        }
    }

    
    /**
     *  Adds invocation handlers based on the service classes mentioned in the supplied
     *  services string.
     * 
     *  @param services List of services specified as fully qualified class names separated
     *                  by whitespace (tabs, spaces, newlines). Each class name is also prefixed
     *                  by the name of the service to use for the class:
     *                  <pre>
     *    <init-param>
     *        <param-name>services</param-name>
     *        <param-value>
     *            SimpleDatabase:java.util.HashMap
     *            RandomNumberGenerator:java.util.Random
     *        </param-value>
     *    </init-param>
     *                  </pre>
     *                  
     *  @throws ServletException If the services argument is invalid or contains names of
     *                           classes that cannot be loaded and instantiated.
     */

    private void addInvocationHandlers( String services ) throws ServletException
    {
    
        URL urlCfg =
        this.getClass().getClassLoader().getResource("com/nnetworks/xmlrpc/sqlcodes.properties");
        //logger.log( LINFO, urlCfg.toString());  
        if ( urlCfg == null ) {
          logger.log(BaseAPIHandler.LINFO,  "Failed to load resources");
          throw new ServletException("Failed to load resources");
        }

        try {        
        BaseAPIHandler.codes = new ResourcePool();
        BaseAPIHandler.codes.setSkipChars("\r\0");
        BaseAPIHandler.codes.consultTextPool(urlCfg);
        }
        catch(IOException e) {
          logger.log(BaseAPIHandler.LINFO, "Failed to load resources");
          throw new ServletException("Failed to load resources");
        }

        StringTokenizer tokenizer = new StringTokenizer( services );
       // logger.log( LINFO, "Read from <param-name>services</param-name>: " + services );
        
        while ( tokenizer.hasMoreTokens() )
        {
            String service = tokenizer.nextToken();
            int separatorIndex = service.indexOf( ':' );

            if ( separatorIndex > -1 )
            {
                String serviceName = service.substring( 0, separatorIndex );
                String className = service.substring( separatorIndex + 1 );
                
                try
                {
                    Class serviceClass = Class.forName( className );
                    Object invocationHandler = serviceClass.newInstance();
                    server.addInvocationHandler( serviceName, invocationHandler );
                }
                catch ( ClassNotFoundException e )
                {
                    throw new ServletException(
                        XmlRpcMessages.getString(
                            "XmlRpcServlet.ServiceClassNotFound" ) + className, e );
                }
                catch ( InstantiationException e )
                {
                    throw new ServletException(
                        XmlRpcMessages.getString(
                            "XmlRpcServlet.ServiceClassNotInstantiable" ) + className, e );
                }
                catch ( IllegalAccessException e )
                {
                    throw new ServletException(
                        XmlRpcMessages.getString(
                            "XmlRpcServlet.ServiceClassNotAccessible" ) + className, e );
                }
            }
            else
            {
                throw new ServletException(
                    XmlRpcMessages.getString(
                        "XmlRpcServlet.InvalidServicesFormat" ) + services );
            }
        }
    }
    

    /** The XmlRpcServer containing the handlers and processors. */
    private XmlRpcServer server;
    
    /** Indicates whether or not response messages should be streamed. */
    private boolean streamMessages;

    /** <describe> */
    private String contentType;
    
    /** Serial Version UID. */
    private static Logger logger = Logger.getLogger( XmlRpcDispatcher.class.getName() );
    private static String fileRevision = "$Revision: 2012.02 $"; 
    
}
