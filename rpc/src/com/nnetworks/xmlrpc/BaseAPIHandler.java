package com.nnetworks.xmlrpc;

import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;

import org.jsoup.Jsoup;

import redstone.xmlrpc.XmlRpcDispatcher;
import redstone.xmlrpc.XmlRpcException;

import oracle.jdbc.driver.*;

import com.nnetworks.jopa.*;
import com.nnetworks.resources.ResourcePool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.net.URLEncoder;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import java.util.regex.Pattern;

import oracle.jdbc.OracleTypes;
import oracle.jdbc.pool.OracleDataSource;

import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.DATE;
import oracle.sql.NUMBER;

import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcMessages;
import redstone.xmlrpc.XmlRpcStruct;
import redstone.xmlrpc.util.Base64;
import java.util.regex.Matcher;

/**
 * Base API handler does user validation, provides exception types, etc.
 * @author Andrew A. Toropov
 */
public class BaseAPIHandler {
    //static final String BUNDLE_NAME = "com.nnetworks.xmlrpc.jxmlrpc";
    //static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );
    public static boolean toUTF8 = false;
    public static boolean toCp1251 = false;
    public static boolean toCp866 = false;

    /**
     * Params for Oracle connections
     * 
     **/
    public static boolean authenticated = false;
    public static  WeblogEntry runEntry;
    protected HashMap<String, String> parmsMap;
    protected List<LinkedHashMap> CustomParams;
    protected Properties props;
    protected String sUser, sPasw, sLink;
    protected Connection m_conn;
    protected CharacterSet m_cset;
    ServletConfig gconfig;
    public static PathInfo pi;
    public static String UserAgent;
    protected String serverCharset;
    protected String effCharset;
    public static Level LINFO = Level.INFO;
    public static Level FINER = Level.FINER;
    public static Properties uProps;
    public static ResourcePool codes; // SQL statments
    public static ResourcePool cfg; // DAD pool
    public static String DADNAME = "Default"; // DAD Name

    // static final  String    ID                   = RESOURCE_BUNDLE.getString( "ID");
    public static int nposts = 100;
    public static String DUSERID;
    public static String DNAME;
    public static String weblogName;
    static String DGRANTS;
    static String DDESCRIPTION;
    static String DPNAME;
    static String DPAGENAME;
    static String DCATEGORIES;
    static String DTAGS;
    static String DFRAME;
    static String DCOMMENT;
    static String DANNOUNCEMENT;
    static String DTITLE;
    static String DPROCESSING_MODE;
    static String DPERMISSIONS;
    static String DDEBUG_LEVEL;
    static String DPARAMS;  
    static String UPARAMS;
    static String LOOKUP_CATEGORY;
    static String LOOKUP_COMMENTS;
    static String LOOKUP_RPCPING;
    static String DGETPOSTSRULE;
    static String DGETPAGESRULE;
    static String DADMINUSER;
    static String DWEBLOGPING;
    static String enumItemsCode = "EnumItems";
    // static String DDOMAIN = RESOURCE_BUNDLE.getString("DOMAIN");
    // static String DUNIT = RESOURCE_BUNDLE.getString("UNIT");
    static String DMODULE;
    static String DLOCAL_PATH;
    static String DUPATH;
    static String DPPATH;
    static String DPERMALINK;
    public static String DBLOGPATH;
    static String DBLOGID;
    public static String DBLOGNAME;
    static String DBXMLRPC;
    static String DBRSS;
    static String fileURL;
    static String dirPath;
    static String dusername;
    static String dpassword;
    static String ddriver;
    static String dhost;
    static String dport;
    static String dsid;

    public static String XmlRpcClientcharset;
    public static String XmlRpcServletEncoding;
    public static String XmlRpcCharacterEncoding;
    public static String XmlRpcClientEncoding;
    public int iAccl = 0;

    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    public static final int AUTHORIZATION_EXCEPTION = 0001;
    public static final String AUTHORIZATION_EXCEPTION_MSG = 
        "Invalid Username and/or Password";

    public static final int UNKNOWN_EXCEPTION = 1000;
    public static final String UNKNOWN_EXCEPTION_MSG = 
        "An error occured processing your request";

    public static final int UNSUPPORTED_EXCEPTION = 1001;
    public static final String UNSUPPORTED_EXCEPTION_MSG = 
        "Unsupported method - Roller does not support this method";

    public static final int USER_DISABLED = 1002;
    public static final String USER_DISABLED_MSG = "User is disabled";

    public static final int WEBLOG_NOT_FOUND = 1003;
    public static final String WEBLOG_NOT_FOUND_MSG = 
        "Weblog is not found or is disabled";

    public static final int WEBLOG_DISABLED = 1004;
    public static final String WEBLOG_DISABLED_MSG = 
        "Weblog is not found or is disabled";

    public static final int BLOGGERAPI_DISABLED = 1005;
    public static final String BLOGGERAPI_DISABLED_MSG = 
        "Weblog does not exist or XML-RPC disabled in web";

    public static final int BLOGGERAPI_INCOMPLETE_POST = 1006;
    public static final String BLOGGERAPI_INCOMPLETE_POST_MSG = 
        "Incomplete weblog entry";

    public static final int INVALID_POSTID = 2000;
    public static final String INVALID_POSTID_MSG = 
        "The entry postid you submitted is invalid";


    public static final int UPLOAD_DENIED_EXCEPTION = 4000;
    public static final String UPLOAD_DENIED_EXCEPTION_MSG = "Upload denied";

    //------------------------------------------------------------------------

    public BaseAPIHandler() {
    }

    /** 
     *  public static void initDAD(String DadName)
     * @param DadName
     */
    public static void initDAD(String DadName) {
        BaseAPIHandler.uProps = 
                BaseAPIHandler.cfg.getProperties(BaseAPIHandler.DADNAME.toUpperCase());
        XmlRpcClientcharset = 
                XmlRpcMessages.getString("XmlRpcClient.Encoding");
        XmlRpcServletEncoding = 
                XmlRpcMessages.getString("XmlRpcServlet.Encoding");
        XmlRpcCharacterEncoding = 
                XmlRpcMessages.getString("XmlRpcCharacter.Encoding");
        XmlRpcClientEncoding = 
                XmlRpcMessages.getString("XmlRpcClient.Encoding");
        System.setProperty("file.encoding", XmlRpcCharacterEncoding);
        System.setProperty("console.encoding", XmlRpcCharacterEncoding);
        try {
            System.setOut(new java.io.PrintStream(System.out, true, 
                                                  XmlRpcCharacterEncoding));
        } catch (Exception e) {
        }
        dusername = uProps.getProperty("username");
        dpassword = uProps.getProperty("password");
        ddriver = uProps.getProperty("driver");
        dhost = uProps.getProperty("host");
        dport = uProps.getProperty("port");
        dsid = uProps.getProperty("sid");
        DNAME = uProps.getProperty("NAME");
        DDESCRIPTION = uProps.getProperty("DESCRIPTION");
        DCOMMENT = uProps.getProperty("COMMENT");
        DANNOUNCEMENT = uProps.getProperty("ANNOUNCEMENT");
        DPNAME = uProps.getProperty("permaname");
        DPAGENAME = uProps.getProperty("pagename");
        DCATEGORIES = uProps.getProperty("CATEGORIES");
        DTAGS = uProps.getProperty("TAGS");
        DMODULE = uProps.getProperty("MODULE");
        DLOCAL_PATH = uProps.getProperty("LOCAL_PATH");
        DTITLE = uProps.getProperty("TITLE");
        DFRAME = uProps.getProperty("FRAME_PAGE");
        DPROCESSING_MODE = uProps.getProperty("PROCESSING_MODE","1");
        DPERMISSIONS     = uProps.getProperty("PERMISSIONS");
        logg("PROCESSING_MODE=>" + DPROCESSING_MODE);
        DDEBUG_LEVEL = uProps.getProperty("DEBUG_LEVEL","0");
        DPARAMS = uProps.getProperty("PARAMS");
        UPARAMS = uProps.getProperty("UPARAMS");
        //logg("FRAME_PAGE=>" + DFRAME);
        DUPATH = uProps.getProperty("postpath");
        DPPATH = uProps.getProperty("pagepath");
        DPERMALINK       = uProps.getProperty("permalink");
        DBLOGPATH        = uProps.getProperty("blogpath");
        DBLOGID          = uProps.getProperty("blogid");
        DBLOGNAME        = uProps.getProperty("blogname");
        DBXMLRPC         = uProps.getProperty("bxmlrpc");
        DBRSS            = uProps.getProperty("blogrss");
        fileURL          = uProps.getProperty("permafile");
        dirPath          = uProps.getProperty("localbase");
        LOOKUP_CATEGORY  = uProps.getProperty("LOOKUP_CATEGORY","CATEGORY");
        LOOKUP_COMMENTS  = uProps.getProperty("LOOKUP_COMMENTS","COMMENTS");
        LOOKUP_RPCPING   = uProps.getProperty("LOOKUP_RPCPING","RPC-PING");
        DADMINUSER       = uProps.getProperty("ADMINUSER","SUPERUSER");
        DGETPOSTSRULE    = uProps.getProperty("GETPOSTSRULE","ANYUSER");
        DGETPAGESRULE    = uProps.getProperty("GETPAGESRULE","ANYUSER");
        DWEBLOGPING      = uProps.getProperty("WEBLOGPING","false");
        logg("GETPAGESRULE=>" + DGETPAGESRULE);
        DGRANTS         = null;
        logg("PERMISSIONS=>" + DPERMISSIONS);
        //logg("LOOKUP_TYPE=>" + LOOKUP_CATEGORY);
    }
    //------------------------------------------------------------------------

    protected String getProperties(String pname, String fname) {
        // Read properties file.
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fname);
            props.load(fis);
            String str = props.getProperty(pname);
            fis.close();
            return str;

        } catch (IOException e) {
            logger.log(Level.WARNING, fname);
            throw new XmlRpcException(AUTHORIZATION_EXCEPTION_MSG + ":" + e);
        } finally {
            if (null != fis)
                try {
                    fis.close();
                } catch (IOException e) {
                }
        }

    }

    /**
     * Returns website, but only if user authenticates and is authorized to edit.
     * @param blogid   Blogid sent in request (used as website's hanldle)
     * @param username Username sent in request
     * @param password Password sent in requeset
     */
    public Weblog validate(int blogid, String username, 
                           String password) throws Exception {
        return validate(blogid, username, password);
    }

    public Weblog validate(String blogid, String username, 
                           String password) throws Exception {
        Weblog website = null;
        DUSERID = username;
        if (validateUser(username, password)) {
            website = 
                    new Weblog(blogid, username, "name", "desc", "email", "emailFrom", 
                               "editorTheme", "locale", "timeZone");
        }
        return website;
    }

    /**
     * Returns true if username/password are valid and user is not disabled.
     * @param username Username sent in request
     * @param password Password sent in requeset
     */
    protected boolean validateUser(String username, 
                                   String password) throws Exception {
        boolean enabled = false;
        try {

            //logger.log(LINFO,  "DAD:" + BaseAPIHandler.DADNAME + ":" + username + ":" +  password);
            establishConnection();
            authenticated = true;
            if (!authorize(username, password)) {
                authenticated = false;
                logg( "not authorized by Oracle:" + username + "/" + 
                           password);
            } else {
                logg("authorized by Oracle");
            }
            if (iAccl < 3) {
                enabled = false;
            } else {
                enabled = true;
                //logger.log(LINFO, "perms level:" + iAccl);
            }

        } catch (Exception e) {
            //"jdbc:oracle:thin:@212.232.33.114:1521:bm","sizak","sizak"
            logger.log(Level.INFO, 
                       "\n ERROR validating  user by connection string \n\n " + 
                       ddriver + ":@" + dhost + ":" + dport + ":" + dsid + 
                       "," + dusername + "," + dpassword + " \n\n " + e);
            throw new XmlRpcException("\n ERROR validating  user by connection string \n\n " + 
                                      ddriver + ":@" + dhost + ":" + dport + 
                                      ":" + dsid + "," + dusername + "," + 
                                      dpassword + " \n\n " + e);
        }

        if (!enabled) {
            throw new XmlRpcException(USER_DISABLED_MSG);
        }

        if (!authenticated) {
            throw new XmlRpcException(AUTHORIZATION_EXCEPTION_MSG);
        }
        return authenticated;
    }

    protected String escapeHTML(String s) {
        return escape(s, this.effCharset);
    }
    //----------------------------------------------------------------------

    protected String escape(String s) {
        return escape(s, this.effCharset);
    }

/*************/
     public static String wSpaceChars () {
         String whitespace_chars =  ""       /* dummy empty string for homogeneity */
                                 + "\\u0009" // CHARACTER TABULATION
                                 + "\\u000A" // LINE FEED (LF)
                                 + "\\u000B" // LINE TABULATION
                                 + "\\u000C" // FORM FEED (FF)
                                 + "\\u000D" // CARRIAGE RETURN (CR)
                                 + "\\u0020" // SPACE
                                 + "\\u0085" // NEXT LINE (NEL) 
                                 + "\\u00A0" // NO-BREAK SPACE
                                 + "\\u1680" // OGHAM SPACE MARK
                                 + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
                                 + "\\u2000" // EN QUAD 
                                 + "\\u2001" // EM QUAD 
                                 + "\\u2002" // EN SPACE
                                 + "\\u2003" // EM SPACE
                                 + "\\u2004" // THREE-PER-EM SPACE
                                 + "\\u2005" // FOUR-PER-EM SPACE
                                 + "\\u2006" // SIX-PER-EM SPACE
                                 + "\\u2007" // FIGURE SPACE
                                 + "\\u2008" // PUNCTUATION SPACE
                                 + "\\u2009" // THIN SPACE
                                 + "\\u200A" // HAIR SPACE
                                 + "\\u2028" // LINE SEPARATOR
                                 + "\\u2029" // PARAGRAPH SEPARATOR
                                 + "\\u202F" // NARROW NO-BREAK SPACE
                                 + "\\u205F" // MEDIUM MATHEMATICAL SPACE
                                 + "\\u3000" // IDEOGRAPHIC SPACE
                                 ;        
         /* A \s that actually works for JavaТs native character set: Unicode */
         String     whitespace_charclass = "["  + whitespace_chars + "]";    
         /* A \S that actually works for  JavaТs native character set: Unicode */
         String not_whitespace_charclass = "[^" + whitespace_chars + "]";
         
         return whitespace_charclass;
     }
/*************/


    /**
     * **
     * @param prn
     */
      public static void out(String prn) {
         //System.out.println(prn);
         // if (BaseAPIHandler.FINER == Level.FINER)System.out.println(prn);
         if (BaseAPIHandler.LINFO == Level.INFO) System.out.println(prn);
         if (BaseAPIHandler.LINFO == Level.WARNING) System.out.println(prn);
     } 
    public static

    void logg(String prn) {
        if (BaseAPIHandler.LINFO == Level.INFO)
            System.out.println(prn);
        if (BaseAPIHandler.LINFO == Level.WARNING)
            System.out.println(prn);
        if (BaseAPIHandler.LINFO == Level.SEVERE)
            System.out.println(prn);
    }

    /**
     * @param s
     * @param enc
     * @return
     */
    protected String escape(String s, String enc) {
        if (s == null)
            return "";
        if (s.length() > 0) {
            try {
                return URLEncoder.encode(s, enc);
            } catch (UnsupportedEncodingException uee) {
                logger.log(LINFO, "escape: ", uee.getMessage());
            }
        }
        return s;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    protected String unescape(String s) {
        return unescape(s, this.effCharset);
    }

    protected String unescape(String s, String enc) {
        if (s == null)
            return "";
        if (s.length() > 0) {
            try {
                return URLDecoder.decode(s, enc);
            } catch (UnsupportedEncodingException uee) {
                logger.log(LINFO, "unescape: ", uee.getMessage());
            } catch (IllegalArgumentException iae) {
                logger.log(LINFO, "unescape: ", iae.getMessage());
            }
        }
        return s;
    }


    /**
     * replace(String name)
     *  
     */
    public static String replace(String name, String from, String to) {
        return name.replaceAll(from, to);
    }
    /**
     * matchString (String searchMe, String findMe)
     * 
     * **/
     public static boolean matchString (String searchMe, String findMe){
         int searchMeLength = searchMe.length();
         int findMeLength = findMe.length();
         searchMe = searchMe.toUpperCase();
         findMe   = findMe.toUpperCase();
         boolean foundIt = false;
         for (int i = 0; i <= (searchMeLength - findMeLength);  i++) {
           if (searchMe.regionMatches(i, findMe, 0, findMeLength)) {
                    foundIt = true;
                    logg("matchString ->"+searchMe.substring(i, i + findMeLength));
                    break;
                    }
                 }
         if (!foundIt)
                    logg("matchString ->"+"No match found.");
       return foundIt;
     }
     
     
    /**
     * freeTemporary (BLOB blob)
     *  
     */
    protected static void freeTemporary(CLOB clob) {
        try {
            if (clob != null && clob.isTemporary())
                clob.freeTemporary();
        } catch (SQLException e) {
        }
    }

    /**
     * freeTemporary (BLOB blob)
     *  
     */
    protected static void freeTemporary(BLOB blob) {
        try {
            if (blob != null && blob.isTemporary())
                blob.freeTemporary();
        } catch (SQLException e) {
        }
    }
    /***
    *
    *
    * **/

    //----------------------------------------------------------------------

    /***
     *  protected NUMBER getFreeUnitID()
     * 
     * **/
    protected NUMBER getFreeUnitID() {
        NUMBER nRef = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin ? := NN$PSP_UNIT.getFreeUnitID; end;");
            cs.registerOutParameter(1, OracleTypes.NUMBER);
            cs.execute();
            nRef = cs.getNUMBER(1);
            if (cs.wasNull())
                nRef = null;
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, "getFreeUnitID/SQL: ", e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return nRef;
    }

    /**
     * 
     * 
     * **/
    public static String c2s(CLOB clob) throws JOPAException {
        try {
            int offset = -1;
            int chunkSize = 2048;
            long clobLength = clob.length();
            out("c2s.clobLength:"+clobLength);
            if (chunkSize > clobLength) {
                chunkSize = (int)clobLength;
            }
            char buffer[] = new char[chunkSize];
            StringBuilder stringBuffer = new StringBuilder();
            Reader reader =  clob.getCharacterStream();

            while ((offset = reader.read(buffer)) != -1) {
                stringBuffer.append(buffer, 0, offset);
            }
            return stringBuffer.toString();
        } catch (SQLException e) {
            logger.log(LINFO, "respUnitCLOB/SQL: " + e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            logger.log(LINFO, "respUnitCLOB/IO: " + e.getMessage());
            return e.getMessage();
        } finally {
            freeTemporary(clob);
        }
    }

    /**
     * 
     * 
     * **/
    public static String b2s(BLOB blob) throws JOPAException {
        try {
            int offset = -1;
            int chunkSize = 2048;
            long blobLength = blob.length();
            if (chunkSize > blobLength) {
                chunkSize = (int)blobLength;
            }
            char buffer[] = new char[chunkSize];
            StringBuilder stringBuffer = new StringBuilder();
            Reader reader = new InputStreamReader(blob.getBinaryStream());

            while ((offset = reader.read(buffer)) != -1) {
                stringBuffer.append(buffer, 0, offset);
            }
            return stringBuffer.toString();
        } catch (SQLException e) {
            logger.log(LINFO, "respUnitBLOB/SQL: " + e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            logger.log(LINFO, "respUnitBLOB/IO: " + e.getMessage());
            return e.getMessage();
        } finally {
            freeTemporary(blob);
        }
    }

    /**
     *   protected CHAR encode (String z)
     * @
     * **/
    protected CHAR encode(String z) {
        int n = z.length();
        byte[] au = new byte[n];
        int c;
        for (int i = 0; i < n; i++) {
            c = (int)z.charAt(i);
            if (c > 0xFF)
                // possibly Unicode string, need not be converted
                try {
                    return new CHAR(z, this.m_cset);
                } catch (SQLException e) {
                    au[i] = (byte)(c & 0xFF);
                }
            else
                au[i] = (byte)(c & 0xFF);
        }
        return new CHAR(au, this.m_cset);
    }

    /**
     * ----------------------------------------------------------------------
     * impersonate (int iAccl)
     *---------------------------------------------------------------------- 
     **/
    protected void impersonate(int iAccl) {
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("declare n number; begin n := NN$NTS.impersonate(?); end;");
            cs.setInt(1, iAccl);
            cs.execute();
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "impersonate/SQL:" + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }
    }

    /**
     * ----------------------------------------------------------------------
     * convert (int iAccl)
     *---------------------------------------------------------------------- 
     **/
    protected String convert(String text, String cto, String cfrom) {
        String oblob = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("declare n number; begin ? := convert(?,?,?); end;");
            cs.registerOutParameter(1, OracleTypes.VARCHAR);
            cs.setString(2, text);
            cs.setString(3, cto);
            cs.setString(4, cfrom);
            cs.execute();
            oblob = cs.getString(1);
            if (cs.wasNull())
                oblob = null;
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "convert/SQL:" + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }
        return oblob;
    }

    /**
     *  prepare for XML conversion rev. 10.02.2012
     * **/
    public String sXML(String tXML) throws Exception {
     String cXML = tXML;
     try {
        //tXML = new String(tXML.getBytes(XmlRpcCharacterEncoding),XmlRpcClientEncoding);
        //cXML = new String(cXML.getBytes("Cp866"),XmlRpcClientEncoding); 
         // logg("String sXML : " + tXML); 
        if (BaseAPIHandler.toUTF8) {
            //logg("String sXML.toUTF8 :" + XmlRpcClientEncoding);
            cXML = new String(tXML.getBytes("utf8"), XmlRpcClientEncoding);
        } else if (BaseAPIHandler.toCp1251) {
            //logg("String sXML.toCp1251 :" + XmlRpcClientEncoding);
            cXML = new String(tXML.getBytes("Cp866"), XmlRpcClientEncoding);
        } else if (BaseAPIHandler.toCp866) {
            //logg("String sXML.toCp866 :" + XmlRpcClientEncoding);
            //cXML = new String(tXML.getBytes("Cp866"),"Cp866");
        }
         else {
             logg("String sXML.ISO-8859-1 :" + XmlRpcClientEncoding);
             //cXML = new String(cXML.getBytes("ISO-8859-1"),"Cp1251");
         }
        //cXML= cXML.replaceAll("<br>\n","\n");                
        // cXML= cXML.replaceAll("<br />\n","\n");
     }
     catch  (Exception je) {logger.log(Level.INFO, "sXML is null:",tXML+":"+je.getMessage());cXML="-1";}
        return cXML;
    }

    /**
     * Authentication support:
     * @param username Username sent in request
     * @param password Password sent in requeset
     */
    protected boolean authorize(String username, 
                                String password) throws Exception {
        int i = 0;
        // Check username/password;
        // System.out.println(username+"/"+password);
        // System.out.println(getCode("authenticateDAV").toString()); 
         DGRANTS = username;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("authenticateDAV"));
            cs.registerOutParameter(3, OracleTypes.INTEGER);
            cs.setString(1, username);
            cs.setString(2, password);
            cs.execute();
            i = cs.getInt(3);
            if (!cs.wasNull())
                iAccl = i;
            cs.close();
            if (iAccl <= 0) {
                return false;
            } else {
                return true;
            }

        } catch (Exception je) {
            logger.log(Level.WARNING, "authorize/SQL: ", je.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }


        //------------------------------------------------------------------------
        return false;
    }
    //----------------------------------------------------------------------

    /**
     * String getCode
     * @param username Username sent in request
     * @param password Password sent in requeset
     */
    public String getCode(String sName) throws JOPAException {
        String s = codes.getText(sName);
        if (s == null)
            throw (new JOPAException("JOPA.getCode", 
                                     "No such code: " + sName));
        return s;
    }

    public String getCode(String sName, String sPar1, 
                          String sVal1) throws JOPAException {
        String s = this.codes.getTextWithSubst(sName, sPar1, sVal1);
        if (s == null)
            throw (new JOPAException("JOPA.getCode", 
                                     "No such code: " + sName));
        return s;
    }

    public String getCode(String sName, String sPar1, String sVal1, 
                          String sPar2, String sVal2) throws JOPAException {
        String s = 
            this.codes.getTextWithSubst(sName, sPar1, sVal1, sPar2, sVal2);
        if (s == null)
            throw (new JOPAException("JOPA.getCode", 
                                     "No such code: " + sName));
        return s;
    }

    public String getCode(String sName, String sPar1, String sVal1, 
                          String sPar2, String sVal2, String sPar3, 
                          String sVal3) throws JOPAException {
        String s = 
            this.codes.getTextWithSubst(sName, sPar1, sVal1, sPar2, sVal2, 
                                        sPar3, sVal3);
        if (s == null)
            throw (new JOPAException("JOPA.getCode", 
                                     "No such code: " + sName));
        return s;
    }
    //----------------------------------------------------------------------
    // Connection support:
    //----------------------------------------------------------------------

    /**
     * Connection support:
     * establishConnection 
     * @param username Username sent in request
     * @param password Password sent in requeset
     */
    public void establishConnection() throws JOPAException {
        String sDriver, sHost, sPort, sSID;
        this.sUser = dusername.trim();
        this.sPasw = dpassword.trim();
        sDriver = ddriver;
        sHost = dhost;
        sPort = dport;
        sSID = dsid;
        this.sLink = 
                ConnectionPool.makeDatabaseString(sDriver, sHost, sPort, sSID);
        //System.out.println(this.sLink );
        try {

            // Start v.1.8.1
            try {
                OracleDataSource ods = new OracleDataSource();
                ods.setUser(this.sUser);
                ods.setPassword(this.sPasw);
                ods.setURL(this.sLink);
                if (m_conn == null) {
                   // logger.log(LINFO, "not connected");
                } else {
                   // logger.log(LINFO, "already connected");
                    try {
                       // AAT 05-2012 (!m_conn.isClosed())
                        while (!m_conn.isClosed())
                            try {
                                m_conn.close();
                                //logger.log(LINFO, "try to disconnect");
                            } catch (SQLException e) {
                               // logger.log(LINFO, "disconnected");
                            } catch (NullPointerException e) {
                                logger.log(LINFO, 
                                           "disconnected:NullPointerException");
                            }
                    } catch (NullPointerException e) {
                        logger.log(LINFO, "disconnected:!m_conn.isClosed()");
                    }
                }
                // get Connection
                m_conn = ods.getConnection();
                m_conn.setAutoCommit(true);
            } catch (java.lang.NullPointerException ex) {
                logger.log(LINFO, "NullPointerException");
            } catch (SQLException e) {
                //e.printStackTrace(shell.getLogStream(0));
                throw (new JOPAException("establishConnection/SQL", 
                                         e.getMessage()));
                //out.println("go.java:"+ i +":ORA-"+e.getErrorCode()+":"+e.getLocalizedMessage());
            }

            // end  v.1.8.1

            if (m_conn instanceof OracleConnection)
                if (!((OracleConnection)m_conn).getImplicitCachingEnabled() && 
                    !((OracleConnection)m_conn).isLogicalConnection())
                    ((OracleConnection)m_conn).setImplicitCachingEnabled(true);
            String sKey = 
                (String)((OracleConnection)m_conn).getClientData("Key");
            String sStat = 
                (String)((OracleConnection)m_conn).getClientData("Stat");
            //logger.log(LINFO, "Connected."+ sStat  + " conn: " + sKey);

            initSession();
        }

        catch (SQLException e) {
            //e.printStackTrace(shell.getLogStream(0));
            throw (new JOPAException("establishConnection/SQL", 
                                     e.getMessage()));
        }
    }

    /**
     * Encoding support:
     * 
     * obtainCharset ()
     * 
     * **/
    protected void obtainCharset() {
        if (m_cset == null) {
            try {
                //logger.log(LINFO, "obtainCharset: getting DB charset...");
                m_cset = 
                        CharacterSet.make(((OracleConnection)m_conn).getDbCsId());
            } catch (SQLException e) {
               // logger.log(LINFO,  "obtainCharset: creating default charset...");
                m_cset = CharacterSet.make(CharacterSet.DEFAULT_CHARSET);
            }
        }
    }

    //----------------------------------------------------------------------

    /**
     * Encoding support:
     * initSession ()
     * 
     * 
     * **/
    protected void initSession() {
        resetSession(m_conn);
        obtainCharset();
        this.serverCharset = getIANACharsetName();
        this.effCharset = uProps.getProperty("charset");
        if (this.serverCharset == null)
            this.serverCharset = this.effCharset;
        //logger.log(LINFO, "Server charset: " + this.serverCharset);
        //logger.log(LINFO, "Effective charset: " + this.effCharset);
    }

    //----------------------------------------------------------------------

    /**
     * resetSession (Connection conn)
     * 
     */
    protected void resetSession(Connection conn) {
        OracleCallableStatement cs;
        try {
            cs = 
 (OracleCallableStatement)conn.prepareCall(uProps.getProperty("reset_mode").equalsIgnoreCase("fast") ? 
                                           "begin DBMS_SESSION.MODIFY_PACKAGE_STATE(2); end;" : 
                                           "begin DBMS_SESSION.RESET_PACKAGE; end;");
            cs.executeUpdate();
            cs.close();
        } catch (SQLException e) {
            try {
                cs = 
 (OracleCallableStatement)conn.prepareCall("begin DBMS_SESSION.RESET_PACKAGE; end;");
                cs.executeUpdate();
                cs.close();
            } catch (SQLException e1) {
                logger.log(LINFO, "resetSession/SQL: ", e1.getMessage());
            }
        }
    }

    /**
     * attempts to convert current Oracle database charset ID to IANA charset name
     * using UTL_GDK or, if unavailable, UTL_I18N packages.
     *
     * @return      Oracle charset name if mapping succeeded, <code>ianacs</code> if not
     */
    protected String getIANACharsetName() {
        String s = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin ? := SYS.UTL_GDK.charset_map(NLS_CHARSET_NAME(?), SYS.UTL_GDK.ORACLE_TO_IANA); end;");
            cs.registerOutParameter(1, OracleTypes.VARCHAR);
            cs.setInt(2, ((OracleConnection)m_conn).getDbCsId());
            cs.executeUpdate();
            s = cs.getString(1);
            if (cs.wasNull())
                s = null;
            cs.close();
        } catch (SQLException egdk) {
            try {
                // try 10g package if the above failed
                OracleCallableStatement cs = 
                    (OracleCallableStatement)m_conn.prepareCall("begin ? := SYS.UTL_I18N.map_charset(NLS_CHARSET_NAME(?)); end;");
                cs.registerOutParameter(1, OracleTypes.VARCHAR);
                cs.setInt(2, ((OracleConnection)m_conn).getDbCsId());
                cs.executeUpdate();
                s = cs.getString(1);
                if (cs.wasNull())
                    s = null;
                cs.close();
            } catch (SQLException ei18n) {
            }
        }
        return s;
    }
    //----------------------------------------------------------------------

    /**
     * NUMBER locatePath (String sPath)
     * @param sPath   sPath return ID of Path
     */
    protected NUMBER locatePath(String sPath) {
        NUMBER nKey = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin ? := NN$NTS.locatePath(?); end;");
            cs.registerOutParameter(1, OracleTypes.NUMBER);
            cs.setCHAR(2, encode(sPath));
            cs.execute();
            nKey = cs.getNUMBER(1);
            if (cs.wasNull())
                nKey = null;
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "locatePath/SQL:", e.getMessage());
        }
        return nKey;
    }

    //----------------------------------------------------------------------

    /**
     * Returns OracleResultSet
     * @param blogid   Blogid sent in request (used as website's hanldle)
     * @param username Username sent in request
     * @param password Password sent in requeset
     */
    protected

    OracleResultSet enumItems(NUMBER nFolder, 
                              String sSort) throws JOPAException {
        OracleResultSet rs = null;
        String stmt = getCode(enumItemsCode);
        if (nFolder != null) {
            stmt = getCode(enumItemsCode);
        } else if (nFolder == null) {
            stmt = getCode("EnumAllItems");
        }
        // enumItems(?, ?, null,?);
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setNUMBER(2, nFolder);
            cs.setString(3, sSort);
            if (enumItemsCode.compareToIgnoreCase("EnumByUserItems")==0) {cs.setString(4, DGRANTS.toUpperCase());}
            cs.execute();
            rs = (OracleResultSet)cs.getCursor(1);
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, 
                       "enumItems/SQL: " + e.getErrorCode() + ":" + stmt + 
                       ":" + e.getMessage());
        }
        return rs;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    /**
     *  protected CHAR fetchProfileCHAR (NUMBER nRef)
     * **/
    protected
    //-
    CHAR fetchProfileCHAR(NUMBER nRef) {
        CHAR chProfile = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin NN$PSP_UNIT.fetchProfile(?, ?, 'I'); end;");
            cs.setNUMBER(1, nRef);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.execute();
            chProfile = cs.getCHAR(2);
            if (cs.wasNull())
                chProfile = null;
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "fetchProfileCHAR/SQL: " + e.getMessage());
        }
        return chProfile;
    }
    //----------------------------------------------------------------------
     
     
    /**
     * 
     *  setName (NUMBER nKey, String sName)
     * 
     * **/
    private boolean setUnitName(String nKey, String sName) {
        boolean b = false;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin ? := NN$NTS.setName(?, ?); end;");
            cs.registerOutParameter(1, OracleTypes.NUMBER);
            cs.setInt(2, Integer.parseInt(nKey));
            cs.setCHAR(3, encode(sName));
            cs.executeUpdate();
            NUMBER n = cs.getNUMBER(1);
            if (!cs.wasNull())
                b = (n.intValue() > 0);
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, "setUnitName/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return b;
    }
    /********
     * ensureUnit
     * *********/
     
     public boolean ensureUnit(String nKey) throws JOPAException {
         boolean b = false;
         String code = getCode("ensureUnit");
    try {
        OracleCallableStatement cs = (OracleCallableStatement)m_conn.prepareCall(code);  
        cs.setInt(1, Integer.parseInt(nKey));
        cs.registerOutParameter(2, OracleTypes.NUMBER);
        cs.execute();
        NUMBER n = cs.getNUMBER(2);
        
        if (!cs.wasNull())
            b = (n.intValue() > 0);
        cs.close();
    } catch (SQLException e) {
        logger.log(LINFO, "ensureUnit/SQL: " + e.getMessage());
        try {
            m_conn.clearWarnings();
        } catch (SQLException e1) {
        }

    }
    return b;
    }
     
    /**
     * private void setUnitAttr (int nRef, String sName, String sData)
     * 
     * **/
    public void setUnitAttr(String nRef, String sName, String sData) {
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin NN$PSP_UNIT.storeAttr(?, ?, ?); commit; end;");
            cs.setInt(1, Integer.parseInt(nRef));
            cs.setString(2, sName);
            cs.setCHAR(3, encode(sData));
            cs.execute();
            cs.close();
            logg("setUnitAttr/SQL:" + nRef + ":" + sName + ":" + sData);
        } catch (SQLException e) {
            logger.log(LINFO, "setUnitAttr/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
    }
    //----------------------------------------------------------------------

    /****
     * 
     *    public NUMBER createUnit (String sName) throws JOPAException
     * 
     * ****/
    public

    NUMBER createUnit(String sName, NUMBER uId) throws JOPAException {
        NUMBER unidId = null;
        String stmt = getCode("createUnitDAV");
        StringBuffer prof = new StringBuffer(256);
        prof.append(" NAME=\"");
        prof.append(sName);
        prof.append("\"\r\n");
        prof.append(" DOMAIN=\"");
        prof.append(dusername.toUpperCase().trim());
        prof.append("\"\r\n");
        prof.append(" CODE_TYPE=\"");
        prof.append("PSP");
        prof.append("\"\r\n");
        prof.append(" PROCESSOR=\"");
        prof.append("DPSP");
        prof.append("\"\r\n");
        prof.append(" FRAME_PAGE=\"");
        prof.append(DFRAME.trim());
        prof.append("\"\r\n");
        prof.append(" PROCESSING_MODE=\"");
        prof.append(DPROCESSING_MODE.trim());
        prof.append("\"\r\n"); 
        prof.append(" DEBUG_LEVEL=\"");
        prof.append(DDEBUG_LEVEL.trim());
        prof.append("\"\r\n"); 
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setNUMBER(1, locatePath(DUPATH));
            cs.setCHAR(2, encode(sName));
            cs.setCHAR(3, encode(prof.toString()));
            cs.setNUMBER(4, uId);
            cs.registerOutParameter(5, OracleTypes.NUMBER);
            cs.execute();
            unidId = cs.getNUMBER(5);
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, "createUnit/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return unidId;
    }
    //----------------------------------------------------------------------

    /**
     * 
     * String mergeAttr (Properties prop)
     * **/
    private

    //----------------------------------------------------------------------

    String mergeAttr(Properties prop) {
        String sName, sData;
        int i, iLen;
        char ch;
        StringBuffer sb = new StringBuffer(4096);
        Enumeration en = prop.propertyNames();
        while (en.hasMoreElements()) {
            sName = (String)en.nextElement();
            sData = prop.getProperty(sName);
            if (sData == null) {
                sData = "";
            }
            sb.append(' ');
            sb.append(sName);
            sb.append("=\"");
            iLen = sData.length();
            for (i = 0; i < iLen; i++) {
                ch = sData.charAt(i);
                if (ch == '\"')
                    sb.append('\\');
                sb.append(ch);
            }
            //sb.append(sData);
            sb.append("\"\r\n");
        }
        return sb.toString();
    }

    //----------------------------------------------------------------------

    /****
     *  updateUnitBLOB (NUMBER nRef, BLOB blob, Properties prop)
     **/
    private

    //----------------------------------------------------------------------

    void updateUnitBLOB(NUMBER nRef, BLOB blob) throws JOPAException {
        String stmt = getCode("updateUnitBlobDAV");
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("declare " + 
                                                            "  nRef  number := ?; " + 
                                                            "begin " + 
                                                            "  NN$PSP_UNIT.storeCodeBlob(nRef, ?); " + 
                                                            "  commit; " + 
                                                            "end;");
            cs.setNUMBER(1, nRef);
            cs.setBLOB(2, blob);
            cs.executeUpdate();
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "updateUnitBLOB/SQL: " + e.getMessage());
        }
    }

    /****
     *  updateUnitBLOB (NUMBER nRef, BLOB blob, Properties prop)
     **/
    private

    //----------------------------------------------------------------------

    void updateUnitBLOB(String nRef, BLOB blob) throws JOPAException {
        String stmt = getCode("updateUnitBlobDAV");
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin " + 
                                                            "  NN$PSP_UNIT.storeCodeBlob(?, ?); " + 
                                                            "  commit; " + 
                                                            "end;");
            cs.setInt(1, Integer.parseInt(nRef));
            cs.setBLOB(2, blob);
            cs.executeUpdate();
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, "updateUnitBLOB/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
    }

    //----------------------------------------------------------------------

    /**
     *  protected BLOB fetchProfileBLOB (NUMBER nRef)
     * **/
    protected
    //----------------------------------------------------------------------

    BLOB fetchProfileBLOB(NUMBER nRef) {
        BLOB blobProfile = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("declare b blob; s varchar2(32767); begin" + 
                                                            " NN$PSP_UNIT.fetchProfile(?, s, 'I');" + 
                                                            " sys.dbms_lob.createTemporary(b, false, dbms_lob.CALL);" + 
                                                            " sys.dbms_lob.writeAppend(b, length(s), utl_raw.cast_to_raw(s));" + 
                                                            " ? := b; end;");
            cs.setNUMBER(1, nRef);
            cs.registerOutParameter(2, OracleTypes.BLOB);
            cs.execute();
            blobProfile = cs.getBLOB(2);
            if (cs.wasNull())
                blobProfile = null;
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "fetchProfileCHAR/SQL: " + e.getMessage());
        }
        return blobProfile;
    }

    /***
     *   protected CLOB fetchCodeCLOB (NUMBER nRef)
     * **/
    protected CLOB fetchCodeCLOB(String nRef) throws JOPAException {
        long nSize = -1;
        CLOB clob = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("fetchCodeCLOB"));
            cs.setInt(1, Integer.parseInt(nRef));
            cs.registerOutParameter(3, OracleTypes.CLOB);
            cs.registerOutParameter(2, OracleTypes.INTEGER);
            cs.execute();
            nSize = cs.getLong(2);
            if (cs.wasNull())
                nSize = -1;
            if (nSize >= 0) {
                clob = cs.getCLOB(3);
                //out("fetchCodeCLOB:"+cs.getCLOB(3).length());
                if (cs.wasNull()) {
                    clob = null;
                    nSize = -1;
                }
            }
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "fetchCodeCLOB/SQL: " + e.getMessage());
        }
        return clob;
    }
    //----------------------------------------------------------------------

    /***
     *   protected BLOB fetchCodeBLOB (NUMBER nRef)
     * **/
    protected BLOB fetchCodeBLOB(String nRef) throws JOPAException {
        long nSize = -1;
        BLOB blob = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("fetchCodeBLOB"));
            cs.setInt(1, Integer.parseInt(nRef));
            cs.registerOutParameter(3, OracleTypes.BLOB);
            cs.registerOutParameter(2, OracleTypes.INTEGER);
            cs.execute();
            nSize = cs.getLong(2);
            if (cs.wasNull())
                nSize = -1;
            if (nSize >= 0) {
                blob = cs.getBLOB(3);
                if (cs.wasNull()) {
                    blob = null;
                    nSize = -1;
                }
            }
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "fetchCodeBLOB/SQL: " + e.getMessage());
        }
        return blob;
    }
    //----------------------------------------------------------------------

    protected NUMBER getNumber(String nKey) {
        NUMBER nRef = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin ? := to_number(" + 
                                                            nKey + "); end;");
            cs.registerOutParameter(1, OracleTypes.NUMBER);
            cs.execute();
            nRef = cs.getNUMBER(1);
            if (cs.wasNull())
                nRef = null;
            cs.close();
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
            logger.log(LINFO, "getNumber/SQL: " + e.getMessage());
        }
        return nRef;
    }
    //----------------------------------------------------------------------
    /*
 * NUMBER getRef (NUMBER nKey)
 * */
    //------------------------------------------------------------------------

    protected NUMBER getRef(String nKey) {
        NUMBER nRef = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin ? := NN$NTS.getRef(?); end;");
            cs.registerOutParameter(1, OracleTypes.NUMBER);
            cs.setInt(2, Integer.parseInt(nKey));
            cs.execute();
            nRef = cs.getNUMBER(1);
            if (cs.wasNull())
                nRef = null;
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, "getRef/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return nRef;
    }
    // 
    //----------------------------------------------------------------------

    /*****
     * private int deleteNode (NUMBER nKey)
     * 
     * ****/
    private String deleteNode(String nKey) throws JOPAException {
        NUMBER nRes = null;
        String stmt = getCode("deleteNodeDAV");
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setInt(1, Integer.parseInt(nKey));
            cs.registerOutParameter(2, OracleTypes.NUMBER);
            cs.executeUpdate();
            nRes = cs.getNUMBER(2);
            if (cs.wasNull())
                nRes = null;
            cs.close();
            if (nRes != null)
                return nRes.stringValue();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "deleteNode/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return null;
    }
    //----------------------------------------------------------------------

    /**
     * protected String fetchAttr(String nKey,String attr)       
     * begin NN$PSP_ADM.fetchAttr(?, 'LOCKED_AT', dAt) ; end;");
     * **/
    protected byte[] fetchBAttr(String nKey, String attr) {
        byte[] nRef = null;
        try {
            OracleCallableStatement cs =
                //"begin nn$psp_unit.fetchAttr(?, ?, sAttr); ? :=convert(sAttr,'RU8PC866','CL8MSWIN1251'); end;");
                (OracleCallableStatement)m_conn.prepareCall("" + "declare " + 
                                                            "sAttr varchar2(4000);" + 
                                                            "begin nn$psp_unit.fetchAttr(?, ?, ?); end;");
            cs.setInt(1, Integer.valueOf(nKey));
            cs.setString(2, attr);
            cs.registerOutParameter(3, OracleTypes.VARCHAR);
            cs.execute();
            nRef = cs.getBytes(3);
            if (cs.wasNull())
                nRef = null;
            cs.close();
            return nRef;
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
                logger.log(Level.WARNING, "fetchAttr/SQL: " + e.getMessage());
            } catch (SQLException e1) {
            }

        }
        return nRef;
    }

    /**
     * protected String fetchAttr(String nKey,String attr)       
     * begin NN$PSP_ADM.fetchAttr(?, 'LOCKED_AT', dAt) ; end;");
     * **/
    protected

    String fetchAttr(String nKey, String attr) {
        String nRef = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("" + "declare " + 
                                                            "sAttr varchar2(4000);" + 
                                                            "begin nn$psp_unit.fetchAttr(?, ?, ?); end;");
            // "begin nn$psp_unit.fetchAttr(?, ?, sAttr); ? :=convert(sAttr,'CL8MSWIN1251','CL8MSWIN1251'); end;");
            cs.setInt(1, Integer.valueOf(nKey));
            cs.setString(2, attr);
            cs.registerOutParameter(3, OracleTypes.VARCHAR);
            cs.execute();
            nRef = cs.getString(3);
            if (cs.wasNull())
                nRef = null;
            cs.close();
            return nRef;
        } catch (Exception e) {
            try {
                m_conn.clearWarnings();
                logger.log(Level.WARNING, "fetchAttr/SQL: " + e.getMessage());
            } catch (SQLException e1) {
            }

        }
        return nRef;
    }

    /**
     *
  NUMBER getUnitAttr (NUMBER nKey,NUMBER attr)
  NN$PSP_ADM.getUnitAttr(nUnitID, 3, sUnitDescr);
  set_param('description',nvl(sUnitDescr,param('description')));
     * */
    protected

    //----------------------------------------------------------------------
    String getUnitAttr(String nKey, String attr) {
        String nRef = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin NN$PSP_ADM.getUnitAttr(" + 
                                                            nKey + ", " + 
                                                            attr + 
                                                            ", ?); end;");
            cs.registerOutParameter(1, OracleTypes.VARCHAR);
            cs.execute();
            nRef = cs.getString(1);
            if (cs.wasNull())
                nRef = null;
            cs.close();
            return nRef;
        } catch (SQLException e) {
            try {
                m_conn.clearWarnings();
                logger.log(Level.WARNING, 
                           "getUnitAttr/SQL: " + e.getMessage());
            } catch (SQLException e1) {
            }

        }
        return nRef;
    }

    /**
     *   function wrapBuffer
     *   protected BLOB wrapBuffer(String buf )
     *   
     * **/
    protected BLOB wrapBuffer(String buf) throws JOPAException {
        BLOB blob = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("StoreWeblogCode"));
            cs.setString(1, buf);
            cs.registerOutParameter(2, OracleTypes.BLOB);
            cs.execute();
            blob = cs.getBLOB(2);
            if (cs.wasNull())
                blob = null;
            cs.close();
            return blob;
        } catch (SQLException e) {
            logger.log(Level.WARNING, 
                       "wrapBuffer/SQL:\n " + e.getMessage() + "\n" + 
                       getCode("StoreWeblogCode"));
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return blob;

    }

    /**
     * Lookup section
     * **/
    public String getLookup(String sId) throws JOPAException {
        if (sId != null) {
            try {
                int nId = Integer.valueOf(sId);
                return getLookup(nId);
            } catch (Exception e) {
            }
        }
        return null;
    }
    /******************************/
    /**  get Translit for slug ****/
    /** ***************************/
    
     public String getTranslitInfo(String nUnitId) throws JOPAException {
         String nInfo = "";
         try {
             OracleCallableStatement cs = 
                 (OracleCallableStatement)m_conn.prepareCall(getCode("getSlug"));
             cs.setString(1, nUnitId);
             cs.registerOutParameter(2, OracleTypes.VARCHAR);
             cs.execute();
             nInfo = cs.getString(2);
             cs.close();
         } catch (SQLException e) {
            System.out.println( getCode("getSlug")+"\nBaseAPIHandler.getTranslitInfo ==>\n" + 
                                nUnitId + ":" + 
                                e.getMessage() + "\n");
            logger.log(LINFO, 
                       "BaseAPIHandler.getTranslitInfo/SQL: " + e.getErrorCode() + 
                       ":" + nUnitId + ":" + e.getMessage());
             nInfo = "";
             try {
                 m_conn.clearWarnings();
             } catch (SQLException e1) {
             }

         }
         return nInfo;
     }
     
     
    /** get Person Name getPersonInfo **/
    public String getPersonInfo(String nName) throws JOPAException {
        String nInfo = "";
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("getUserInfo"));
            cs.setString(1, nName);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.execute();
            nInfo = cs.getString(2);
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, 
                       "getPersonInfo/SQL: " + e.getErrorCode() + ":" + nName + ":" + 
                       e.getMessage());
            nInfo = "";
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return nInfo;
    }
 
    /** get lookup name**/
    public String getLookup(int nId) throws JOPAException {
        String nDescription = "";
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("getLookupDescById"));
            cs.setInt(1, nId);
            cs.setString(2,DBLOGNAME);  
            cs.registerOutParameter(3, OracleTypes.VARCHAR);
            cs.execute();
            nDescription = cs.getString(3);
            cs.close();
           logger.log(LINFO, "" +
           "getLookupDescById:     nID => " + nId + "\n " +
           "                       BlogName => "+DBLOGNAME+"\n");
        } catch (SQLException e) {
            
            logger.log(LINFO, 
                       "getLookupDescById/SQL: \n"+ getCode("getLookupDescById") +"\n"+ e.getErrorCode() + ":\n nID=>" + nId + "\n BlogName=>"+DBLOGNAME+"\n" + 
                       e.getMessage());
            //e.printStackTrace();           
            nDescription = "NoName";
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return nDescription;
    }
    /**set setRpcPing**/
    protected String setRpcPing(String sCode, String sType) throws JOPAException {
        String stmt = getCode(sCode);
        String nOut = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setString(1, sType);  
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.execute();
            nOut = cs.getString(2);
            cs.close();
            
        } catch (SQLException e) {
            logger.log(Level.WARNING, "setRpcPing/SQL:\n " + stmt + "\n" + e+"\n->" +sCode+
            "\n->"+sType+"\n");
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {  }
        }
      return nOut;}
      
    /*** get checkLookupExistsByID ****/
     protected String checkLookupExistsByID(String nCode) throws JOPAException {
        String stmt = getCode("checkLookupExistsByID");
       //  out("checkLookupExistsByID:"+stmt);
        String nOut = "1";
        try {
            OracleCallableStatement cs = (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setString(1, nCode);  
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.execute();
            nOut = cs.getString(2);
            cs.close();
             
        } catch (SQLException e) {
            logger.log(Level.WARNING, "checkLookupExistsByID/SQL:\n " + stmt + "\n" + e+"\n->" +
            "\n->"+nCode+"\n");
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) { logger.log(Level.WARNING,""+e1 );  }
        }
      out(nOut);
      return nOut;
      }
      
    /**** get getAnyLookupArrayByType ***************************/
     protected OracleResultSet getAnyLookupArrayByType(String nType,String code, int first_in_set,int last_in_set) throws JOPAException {
         OracleResultSet rs = null;
         String nCode = getCode(code);
         //logg(nCode);
         try {            
            OracleCallableStatement cs = 
                 (OracleCallableStatement)m_conn.prepareCall(nCode);
             cs.setString(1, nType);
             cs.setInt(2, first_in_set);
             cs.setInt(3, last_in_set);
             cs.registerOutParameter(4, OracleTypes.CURSOR);
             cs.execute();
              rs = (OracleResultSet)cs.getCursor(4); 
             cs.close();             
         } 
        
         catch (SQLException e) {
             logger.log(LINFO, 
                        "getAnyLookupArrayByType/SQL: " + nCode + ":" + e.getErrorCode() + 
                        ":" + e.getMessage());
             try {
                 m_conn.clearWarnings();
             } catch (SQLException e1) {
             }

         }
         catch (Exception e) { e.printStackTrace(); logg("Exception:getAnyLookupArrayByType:"+e.getMessage());}
         return rs;
     }
    /*******************************/
    /** get getAnyLookupByID by id**/
    protected OracleResultSet getAnyLookupByID(int nCode, String nPos, String code) throws JOPAException {
        OracleResultSet rs = null;
        try {
           OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode(code));
            cs.setInt(1, nCode);
            cs.setString(2, nPos);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();
             rs = (OracleResultSet)cs.getCursor(3); 
            cs.close();             
        } 
       
        catch (SQLException e) {
            logger.log(LINFO, 
                       "getAnyLookupByID/SQL: " + nCode + ":" + e.getErrorCode() + 
                       ":" + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        catch (Exception e) { out ("getAnyLookupByID:"+e.getMessage());}
        return rs;
    }
 
    /** get lookup by id**/
    protected OracleResultSet getCommentbyId(String nCode, 
                                             String nDesc) throws JOPAException {
        OracleResultSet rs = null;
        try {
           OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("getCommentbyId"));
            cs.setString(1, nCode);
            cs.setString(2, nDesc);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();
            rs = (OracleResultSet)cs.getCursor(3); 
            cs.close();             
        } 
       
        catch (SQLException e) {
            logger.log(LINFO, 
                       "getCommentbyId/SQL: " + nCode + ":" + e.getErrorCode() + 
                       ":" + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        catch (Exception e) { out ("getCommentbyId:"+e.getMessage());}
        return rs;
    }

    /** get lookup**/
    protected OracleResultSet getLookup(String nCode, 
                                        String nDesc) throws JOPAException {
        OracleResultSet rs = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(getCode("getPostLookups"));
            cs.setString(1, nCode);
            cs.setString(2, nDesc);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();
            rs = (OracleResultSet)cs.getCursor(3);
            cs.close();
        } catch (SQLException e) {
            logger.log(LINFO, 
                       "getPostLookups/SQL:getPostLookups:" + "1:"+nCode + "\n2:"+nDesc+"\n" + e.getErrorCode() + 
                       ":" + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return rs;
    }

    /**set lookup**/
    protected void setLookup(String sCode, String nCode, String nDesc, 
                             String nKey, String lookup_type) throws JOPAException {
        String stmt = getCode(sCode);
        String nErr = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setString(1, nCode);
            cs.setString(2, nDesc);
            cs.setInt(3, Integer.parseInt(nKey));
            cs.setString(4, lookup_type);
            cs.execute();
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "setLookup/SQL: " + stmt + "\n" + e+":"+sCode+"\n"+ nCode+":"+ nDesc+":"+ nKey+":"+lookup_type);
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }
    }

    /**set lookup with parent**/
    protected void setLookupParent(String sCode, String nCode, String nDesc, 
                                   String nKey, 
                                   int nParent) throws JOPAException {
        String stmt = getCode(sCode);
        String nErr = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setString(1, nCode);
            cs.setString(2, nDesc);
            cs.setInt(3, Integer.parseInt(nKey));
            cs.setInt(4, nParent);
            cs.setString(5, LOOKUP_CATEGORY);
            cs.execute();
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, 
                       "setLookupParent/SQL: " + stmt + "\n" + e);
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }
    }
    /**set lookup with parent**/
    protected String setLookupWithParent(String sCode, String nCode, String nDesc,String lookup_type, int nParent) throws JOPAException {
        String stmt = getCode(sCode);
        String nOut = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt);
            cs.setString(1, nCode);
            cs.setString(2, nDesc);
            cs.setInt(3, nParent);
            cs.setString(4, lookup_type);
            cs.registerOutParameter(5, OracleTypes.VARCHAR);
            cs.execute();
            nOut = cs.getString(5);
            cs.close();
            return nOut;
        } catch (SQLException e) {
            logger.log(Level.WARNING, 
                       "setLookupWithParent/SQL: " + stmt + "\n"+"1:"+nCode+"\n"+"2:"+nDesc+"\n"+"3:"+nParent+"\n"+ "4:"+lookup_type+"\n" + e);
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }
        return nOut;
    } 
    
    /**set anylookup with parent**/
    protected String setAnyLookup(String sCode,String lookup_type, String nCode, String nDesc, int nParent) throws JOPAException {
        String stmt = getCode(sCode);
        String nOut = null;
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall(stmt); 
            cs.setString(1, lookup_type);
            cs.setString(2, nCode);
            cs.setString(3, nDesc);
               cs.setInt(4, nParent);
           
            cs.registerOutParameter(5, OracleTypes.VARCHAR);
            cs.execute();
            nOut = cs.getString(5);
            cs.close();
            return nOut;
        } catch (SQLException e) {
            logger.log(Level.WARNING, 
                       "setLookupWithParent/SQL: " + stmt + "\n"+"1:"+nCode+"\n"+"2:"+nDesc+"\n"+"3:"+nParent+"\n"+ "4:"+lookup_type+"\n" + e);
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }
        }
        return nOut;
    } 
    /**
 * End of Lookup section
 * **/
    /*******************************************/
    //----------------------------------------------------------------------

    /*****
     * 
     *  protected DATE getTimeStamp (NUMBER nKey)
     * *****/
    protected DATE getTimeStamp(String nKey) {
        DATE d = null;
          try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("declare " + 
                                                            "nKey number;" + 
                                                            "begin " + 
                                                            " SELECT id into nKey FROM nn$t_nts_tree where id_ref=?;" + 
                                                            "? := nvl(NN$NTS.getTimeStamp(nKey),sysdate); " + 
                                                            "end;");
            cs.registerOutParameter(2, OracleTypes.DATE);
            cs.setInt(1, Integer.valueOf(nKey));
            cs.execute();
            d = cs.getDATE(2);
            if (cs.wasNull())
                d = null;
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "getTimeStamp/SQL: unitID =>"+nKey+"<=" + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return d;
    }

    /**
     * 
     * 
     protected String ensureLink (
     String sPath 
    ,String sDisp  
    ,String nRef
    ,String nSize
    ,String sData
    ,String nAccl
    ,String dStamp
 )
     * */
    protected

    String ensureLink(String sPath, String sDisp, NUMBER nRef, int nSize, 
                      String sData, int nAccl, DATE dStamp) {
        String sErr = "";
        try {
            /** *
    * ensureLink
( sErr    out varchar2
, sPath   in  varchar2
, sDisp   in  varchar2
, nRef    in  number    := null
, nSize   in  number    := null
, sData   in  varchar2  := null
, nAccl   in  number    := null
, dStamp  in  date      := null
)**/
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin NN$NTS.ensureLink(?,?,?,?,?,?,?,?); end;");
            cs.registerOutParameter(1, OracleTypes.VARCHAR);
            cs.setString(2, sPath);
            cs.setString(3, sDisp);
            cs.setNUMBER(4, nRef);
            cs.setInt(5, nSize);
            cs.setString(6, sData);
            cs.setInt(7, nAccl);
            cs.setDATE(8, dStamp);
            cs.execute();
            sErr = cs.getString(1);
            if (cs.wasNull())
                sErr = null;
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ensureLink/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return sErr;
    }

    /*******
     * 
     * *********/
    protected String setLink(int nRef, int nStat, DATE dStamp) { /*** SELECT a.id, a.id_folder, a.hash, a.name, a.accl, a.disp, a.id_ref,
                 a.ref_size, a.data, a.status, a.pos, a.param, a.time_stamp
            FROM nn$t_nts_tree a
            **/
        String sErr = "";
        try {
            OracleCallableStatement cs = 
                (OracleCallableStatement)m_conn.prepareCall("begin update nn$t_nts_tree set status=?,time_stamp=? where id_ref=?;commit; end;");
            cs.setInt(1, nStat);
            cs.setDATE(2, dStamp);
            cs.setInt(3, nRef);
            cs.executeUpdate();
            cs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "setLink/SQL: " + e.getMessage());
            try {
                m_conn.clearWarnings();
            } catch (SQLException e1) {
            }

        }
        return sErr;
    }

    /**
     *  2013.12
     * Parse params 
     * ************
     * @param search
     * @throws UnsupportedEncodingException
     */
    public void initPARAMS(String search) throws Exception {
         LinkedHashMap custom_fields = new LinkedHashMap(); 
                   this.CustomParams = new ArrayList<LinkedHashMap>();
        String params[] = search.split("&");
        for (int i = 0; i < params.length; i++) {
            if (params[i].length() > 0) {
                //out("initPARAMS params["+i+"]:"+params[i].length()+ ":=>"+ params[i].toString());
                int startIndex = params[i].indexOf("[{");
                if (startIndex < 0) {
                } else if (startIndex > 0) {
                    int endIndex = params[i].indexOf("}]");
                    //out("name       =>"+params[i].substring(0, startIndex - 1));
                    //out("value      =>"+params[i].substring(startIndex+1,endIndex+1));
                    String name = params[i].substring(0, startIndex - 1);
                    String value = 
                        params[i].substring(startIndex + 2, endIndex + 1 - 1);
                    String[] split = value.split("\\}(.*?),(.*?)\\{");
                    for (int s = 0; s < split.length; s++) {
                        try {
                            out("\ninitPARAMS split[" + s + "] => " + split[s]);
                            String[] arrvalue = split[s].split(","); 
                            custom_fields = new LinkedHashMap<String,String>();
                            
                            for (int r = 0; r < arrvalue.length; r++)
                            {
                                   //out("initPARAMS arrvalue[" + r + "] => " + arrvalue[r].trim());
                                   String[] vals = arrvalue[r].split("=");
                                   //out("initPARAMS vals[] =>" + vals[0].trim()+":"+vals[1].trim());
                                   custom_fields.put(vals[0].trim(),vals[1].trim());
                           }
                                   this.CustomParams.add(custom_fields);

                        } catch (Exception e) {
                        }
                    }
                }
                    
            }

        }
    }

    /**
     * 
     * Parse params 
     * ************
     * @param search
     * @throws UnsupportedEncodingException
     */
    public void initMap(String search) throws Exception {
        this.parmsMap = new HashMap<String, String>();
        String params[] = search.split("&");
        for (String param: params) {
            String temp[] = param.split("=");
            try {
                this.parmsMap.put(temp[0], 
                                  java.net.URLDecoder.decode(temp[1], "UTF-8"));
            } catch (Exception e) {
                this.parmsMap.put(temp[0], "");
            }
        }
    }
    
    /**
     * @param bd
     * @return
     */
    public boolean isIntegerValue(String bd) {
        int i;
        try {
            i = Integer.valueOf(bd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /*******
    *
    * *******/

    /**
     * @param entry
     * @throws JOPAException
     */
    public void updateWeblogEntry(WeblogEntry entry) throws JOPAException {
        setUnitAttr(entry.getId(), DDESCRIPTION, entry.getTitle());
        logg("updateWeblogEntry->  DESCRIPTION:"+DDESCRIPTION);
        if (DPERMISSIONS != null) { 
            setUnitAttr(entry.getId(),"PERMISSIONS", DPERMISSIONS.toUpperCase());
        }
        // установим юзера, если не админ юзер, админ не мен€ет, если уже установлено
        
         if (DADMINUSER!=null)if(DGRANTS.compareToIgnoreCase(DADMINUSER)==0){
             logg("updateWeblogEntry-> ADMINUSER: "+DADMINUSER);
             String mGRANTS = fetchAttr(entry.getId(),"GRANTS");
             if(mGRANTS==null){setUnitAttr(entry.getId(),"GRANTS", DGRANTS.toUpperCase());}
         }
         else { 
         if (DGRANTS != null) { 
             setUnitAttr(entry.getId(),"GRANTS", DGRANTS.toUpperCase());
         }}
        //  
        if (entry.getmt_excerpt() != null) {
            setUnitAttr(entry.getId(), DTITLE, entry.getmt_excerpt());
        } else {
            setUnitAttr(entry.getId(), DTITLE, entry.getTitle());
        }
        if (entry.getmt_basename() != null)
            if (entry.getmt_basename().length() > 0) {
                //logg("entry.getmt_basename:"+entry.getmt_basename());
                setUnitAttr(entry.getId(), DANNOUNCEMENT, 
                            entry.getmt_basename());
            } else {
                setUnitAttr(entry.getId(), DANNOUNCEMENT, entry.getTitle());
            }

        try {
            setUnitAttr(entry.getId(), DCOMMENT, entry.getTitle());
            setUnitAttr(entry.getId(), DPARAMS, UPARAMS);
            logg("==============PARAMS===============");
            logg("DPARAMS:"+DPARAMS+":"+UPARAMS);
            logg("===================================");

        } catch (Exception e) {
        }
        setUnitAttr(entry.getId(), DMODULE, entry.getAnchor());
        String unitText = entry.getText();
       // logg("updateWeblogEntry:unitText:"+unitText);
        // updateUnitBLOB(entry.getId(), wrapBuffer(unitText.replaceAll("\n","<br />\n")));
        updateUnitBLOB(entry.getId(), wrapBuffer(unitText));
        /**Category**/
        List<WeblogCategory> cats = new ArrayList<WeblogCategory>();
        cats = entry.getSetCategories();
        String sCats = "";
        int i = 0;
        for (Iterator wbcItr = cats.iterator(); wbcItr.hasNext(); ) {
            WeblogCategory wCat = new WeblogCategory();
            wCat = cats.get(i++);
            if (i < cats.size()) {
                sCats = sCats + wCat.getTitle() + ",";
            } else {
                sCats = sCats + wCat.getTitle();
            }
            if (i == cats.size())
                break;
        }
        logg("updateWeblogEntry:sCats:" + sCats + ":unitId:" + entry.getId());
        if (sCats != null) {
            setUnitAttr(entry.getId(), DCATEGORIES, sCats);
        }
        if (entry.getmtContent() != null) {
            setUnitAttr(entry.getId(), DLOCAL_PATH, entry.getmtContent());
        }
        if (entry.getmt_keywords() != null) {
            setUnitAttr(entry.getId(), DTAGS, entry.getmt_keywords());

            try {
                String tags = entry.getmt_keywords();
                String[] tagarr = new String[100];
                StringTokenizer tokens = new StringTokenizer(tags, ",");
                int q = 0;
                while (tokens.hasMoreTokens()) {
                    tagarr[q++] = tokens.nextToken();
                    logg(tagarr[q]);
                }
            } catch (Exception e) {
                logg("DTAGS" + e.getMessage());
            }

        }
        //MODIFIED_AT
        if (entry.getUpdateTime() != null) {
            setUnitAttr(entry.getId(), "MODIFIED_AT", 
                        entry.getUpdateTime().toString());
        }
        //COMPILED_AT
        if (entry.getDateCreatedGmt() != null) {
            setUnitAttr(entry.getId(), "COMPILED_AT", 
                        entry.getDateCreatedGmt().toString());
        }

        logger.log(LINFO, 
                   "updateWeblogEntry.getmtContent:" + entry.getmtContent());
    }

    protected void WeblogPingBack(String pingTitle, 
                                  String pingText) throws Exception {
        setLookup("newComment", pingTitle, pingText, "1", LOOKUP_COMMENTS);
    }
    /********************************************/
    /**
     * WeblogPing
     *
     * **
     * @param weblog
     * @return
     */
    // weblogUpdates.extendedPing   
    // X-Pingback: http://charlie.example.com/pingback/xmlrpc 

    /******
     @INFO: weblogUpdates.extendedPing
      @13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcDispatcher dispatch
      @INFO: weblogUpdates.extendedPing:[—оздать и оптимизировать сайт, http://www.hitmedia.ru/b/, http://www.hitmedia.ru/b/?feed=rss2]
      @INFO: redstone.xmlrpc.XmlRpcException: !ReflectiveInvocationHandler.MethodDontExist:extendedPing:!java.lang.String java.lang.String java.lang.String 
      @WARNING: Dynamic PSP/XMLRPC : weblogUpdates.extendedPing : !ReflectiveInvocationHandler.MethodDontExist:extendedPing:!java.lang.String java.lang.String java.lang.String 

      13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcServlet doPost
      INFO: setCharacterEncoding:Cp1251
      13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcServlet doPost
      INFO: setContentType:text/xml; charset=Cp866
      13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcServlet doPost
      INFO: BaseAPIHandler.DADNAME:media
      13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcServlet doPost
      INFO: Loading resources:C:\jxml\cfg\jxml.conf
      13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcDispatcher dispatch
      @INFO: weblogUpdates.ping
      13.04.2011 17:06:38 redstone.xmlrpc.XmlRpcDispatcher dispatch

      @INFO: weblogUpdates.ping:[—оздать и оптимизировать сайт, http://www.hitmedia.ru/b/]
      @13.04.2011 17:06:38 com.nnetworks.xmlrpc.RPCping ping
      @INFO: ping() Called =====[ SUPPORTED  weblogUpdates.ping ]=====
      @13.04.2011 17:06:38 com.nnetworks.xmlrpc.RPCping ping
      @INFO:      weblogName: —оздать и оптимизировать сайт
      @13.04.2011 17:06:38 com.nnetworks.xmlrpc.RPCping ping
      @INFO:      weblogUrl: http://www.hitmedia.ru/b/

     * *******/
    public String WeblogPing(WeblogEntry weblog) throws JOPAException {
        logg("========================== WeblogPing="+DWEBLOGPING+" =============================");
        String weblogUrl = DBLOGPATH;
        if(DWEBLOGPING.compareToIgnoreCase("true")==0) {
        
        String content = "\n";
        String uRpcLinks = null;
        //weblogUrl  = DPERMALINK +weblog.getLink();// "http://www.hitmedia.ru/media/!go?id_=100178";
        String weblogName = getLookup(Integer.valueOf(DBLOGID));
        String weblogRss = DBRSS;
        //weblogName = weblog.getTitle();
        String[] WeblogPingArray = new String[] { weblogName, weblogUrl };
        List<String> WeblogPingList = Arrays.asList(WeblogPingArray);
        try {
            // Ping weblogs.com RPC-PING LOOKUP_RPCPING   = uProps.getProperty("LOOKUP_RPCPING","RPC-PING");
            String PingName =  setRpcPing("getRpcPing",LOOKUP_RPCPING);
            logg("WeblogPing.getRpcPing->"+PingName);
            OracleResultSet rs = getLookup(LOOKUP_RPCPING, "POS");
            List<WeblogCategory> rets = new ArrayList<WeblogCategory>();
            // select  id,lookup_type,code,description,pos,parent_id,owner_id,status from t$lookup 
            //         :1  :2          :3    :4         :5   :6        :7        :8
            try {
                // weblogName = sXML(weblogName);
                if (rs != null) {
                    while (rs.next()) {
                        uRpcLinks = rs.getString(4);
                        try {
                            String tags = uRpcLinks.replaceAll("\n", ",");
                            String[] tagarr = new String[100];
                            StringTokenizer tokens = 
                                new StringTokenizer(tags, ",");
                            int q = 0;
                            while (tokens.hasMoreTokens()) {
                                tagarr[q] = tokens.nextToken();
                                //tagarr[q] = "http://192.168.1.227:8990/r/media/";

                                XmlRpcClient client = 
                                    new XmlRpcClient(tagarr[q], true);
                                client.setRequestProperty("X-Pingback", 
                                                          DBXMLRPC);
                                Object token;
                                try {
                                    /*** @INFO: weblogUpdates.extendedPing:[—оздать и оптимизировать сайт, http://www.hitmedia.ru/b/, http://www.hitmedia.ru/b/?feed=rss2] ***/
                                    try {
                                    
                                       RPCping.weblogUpdates_extendedPing( weblogName,  weblogRss, weblogUrl,PingName); //TODO 
                                        
                                        token = 
                                                client.invoke("weblogUpdates.extendedPing", 
                                                              new Object[] { sXML(weblogName), 
                                                                             weblogUrl, 
                                                                             weblogRss });
                                                                              
                                                                             
                                     logg("===============>\n weblogUpdates.extendedPing: " + weblogName + "," + weblogUrl + "," + weblogRss+ " to "+ PingName +
                                        "\n<==============");
                                     
                                    } catch (Exception e) {
                                        logg("====>\n Exception:"+e.getMessage());
                                        token = 
                                                client.invoke("weblogUpdates.ping", 
                                                              new Object[] { sXML(weblogName), 
                                                                             weblogUrl });
                                        logg("====>\n weblogUpdates.ping: " + weblogName + "," + weblogUrl + "," + weblogRss);                                          
                                    }
                                    content = 
                                            content + "\n\n" + tagarr[q] + "\n" + 
                                            weblogName + " " + weblogUrl + 
                                            "\n" + token.toString();
                                } catch (Exception e) {
                                    content = 
                                            content + "\n" + tagarr[q] + ", " + 
                                            weblogName + ", " + weblogUrl + 
                                            "," + weblogRss + "\n" + 
                                            e.getMessage();
                                }


                                logg("WeblogPing =>"+q + ":" + tagarr[q]+"\n");
                                q++;
                            }
                        } catch (Exception e) {
                            logg("WeblogPing:tokens:" + e.getMessage());
                        }
                    }
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                String msg = "Exception.WeblogPing:";
                logger.log(Level.WARNING, msg + e.getMessage());
            }
            logg("WeblogPing:" + weblogName + ", " + weblogUrl + ", " + 
                 weblogRss);
            setLookup("newComment", weblogName, weblogUrl + content, weblog.getId(), LOOKUP_COMMENTS);
        } catch (Exception e) {
            logger.log(LINFO, "WeblogPing: " + weblogUrl, e);
        }
        }
        return weblogUrl;

        //XmlRpcClient client = new XmlRpcClient("http://ping.blogs.yandex.ru/RPC2", true);
        //Object token = client.invoke("weblogUpdates.ping", new Object[] { weblogName, weblogUrl} );
        //System.out.println("WeblogPing:"+token);
        //logg("WeblogPing:"+DPERMALINK +weblog.getLink());
    }

         /*************************************/        
          public String WeblogPings(WeblogEntry entry) throws  Exception {
              String[] ExtendedPing     = uProps.getProperty("ExtendedPing").split(",");               
              String weblogUrl          = uProps.getProperty("blogpath");
              String weblogName         = BaseAPIHandler.weblogName;
              String weblogRss          = uProps.getProperty("blogrss"); 
              
              logg("WeblogPings:"+BaseAPIHandler.DADNAME+"  weblogName: " + weblogName + " weblogUrl: " + weblogUrl + " weblogRss: " +  weblogRss+": ExtendedPing:"+ExtendedPing.toString());
              for (String ExtendedPingTo: ExtendedPing) {
                   logg( "\n"+ "ExtendedPingTo   => "+ExtendedPingTo); 
                try {
                   RPCping.weblogUpdatesExtendedPing(weblogName,weblogUrl,weblogRss,ExtendedPingTo);
                   System.out.println("\nJXML WeblogPings:" + weblogName + " : " + weblogUrl + " : " +  weblogRss+":"+ExtendedPingTo+"\n");
                } catch(Exception ww){System.out.println("\nException -> JXML WeblogPings =>"+ExtendedPingTo+":"+ww.getMessage());}
              }
              return weblogUrl;
          }
         /**************************************/
    /**
     * Create weblog entry.
     * @param entry
     * @throws JOPAException
     */
    public void createWeblogEntry(WeblogEntry entry) throws JOPAException {
        logger.log(LINFO, "createWeblogEntry:" + entry.getId());
        BaseAPIHandler.runEntry = entry;  
        updateWeblogEntry(entry); 
        //String weblogUrl = WeblogPing(entry); // TODO 
         BaseAPIHandler.weblogName = getLookup(Integer.valueOf(DBLOGID));
         Runnable r = new Runnable() {
                     public void run() { 
                      
                       try {
                       BaseAPIHandler.logg("**  ========================== Start Background Ping **");
                       String weblogUrl = WeblogPings(BaseAPIHandler.runEntry);}
                       catch(Exception ee){
                           BaseAPIHandler.logg("Exception Background Ping =>"+ee.getMessage());
                           ee.printStackTrace();
                       }
                   }
               };
        new Thread(r).start();     
               

    }
    //----------------------------------------------------------------------          

    /**
     * Save weblog entry.
     */
    public void saveWeblogEntry(WeblogEntry entry) throws JOPAException {
        logg("\n========================== Called BaseAPIHandler.saveWeblogEntry() =============================");
        
        logger.log(LINFO, "saveWeblogEntry:" + entry.getId());
        updateWeblogEntry(entry);
        DATE dStamp = new DATE(entry.getPubTime()); // TODO getPubTime
        String uNode = setLink(Integer.parseInt(entry.getId()), 1, dStamp);
        if(DWEBLOGPING.compareToIgnoreCase("true")==0) {
        BaseAPIHandler.runEntry = entry;
        logg("========================== WeblogPing="+DWEBLOGPING+" =============================");
        // String weblogUrl = WeblogPing(entry); // TODO  comment it
         BaseAPIHandler.weblogName = getLookup(Integer.valueOf(DBLOGID));
         Runnable r = new Runnable() {
               public void run() { 
                
                 try {
                 BaseAPIHandler.logg("**  ========================== Start Background Ping **");
                 String weblogUrl = WeblogPings(BaseAPIHandler.runEntry);}
                 catch(Exception ee){}
             }
         };
         new Thread(r).start();
         
        }
        else {
        logg("========================== WeblogPing="+DWEBLOGPING+" =============================");
        }
       /* try {
            logger.log(LINFO, 
                       "saveWeblogEntry:" + uNode + ":" + Integer.parseInt(entry.getId()) + 
                       ":" + entry.getStatus() + ":" + 
                       dStamp.toText("Dy, DD Mon YYYY HH24:MI:SS", "ENGLISH"));
        } catch (SQLException e) {
            logg("\nsaveWeblogEntry -> "+e.getMessage());
        }*/
        logg("========================== End Called BaseAPIHandler.saveWeblogEntry() =============================\n");
    }
    //----------------------------------------------------------------------  

    /**
     *  public WeblogEntry deleteWeblogEntry (String postid) throws JOPAException
     * **/
    public WeblogEntry deleteWeblogEntry(String postid) throws JOPAException {
        WeblogEntry entry = new WeblogEntry();
        entry.setId(deleteNode(postid));
        logger.log(LINFO, "deleteWeblogEntry:deleteNode:" + entry.getId());
        return entry;
    }

    /**
     * Get Weblog Entries grouped by day. This method returns a Map that
     * contains Lists, each List contains WeblogEntryData objects, and the
     * Lists are keyed by Date objects.
     * @param website    Weblog or null to get for all weblogs.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.
     * @param catName    Category path or null for all categories.
     * @param status     Status of DRAFT, PENDING, PUBLISHED or null for all
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @return Map of Lists, keyed by Date, and containing WeblogEntryData.
     * @throws WebloggerException
     */
    public WeblogEntry getWeblogEntry(String postid) throws Exception {

        WeblogEntry entry = new WeblogEntry();
        WeblogCategory category = new WeblogCategory();
        if (authenticated)
        try {
            entry.setId(postid);
            try {
                //BLOB blob = fetchCodeBLOB(postid);
                //entry.setText((sXML(b2s(blob))));
                CLOB clob = fetchCodeCLOB(postid);
                entry.setText(sXML(c2s(clob)));
            } catch (Exception e) {
                entry.setText("");
            }
            entry.setLink(postid);

            try {
                Timestamp PubTime = getTimeStamp(postid).timestampValue();
                entry.setPubTime(PubTime);
                Timestamp now = new Timestamp(System.currentTimeMillis());
                entry.setUpdateTime(PubTime);
            } catch (Exception e) {
                logg("getWeblogEntry:" + e);
            }
            try {
                String lPath = null;
                lPath = fetchAttr(postid, "COMPILED_AT");
                if (lPath != null) {
                    Timestamp gStamp = Timestamp.valueOf(lPath);
                    entry.setDateCreatedGmt(gStamp); } 
                 else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    entry.setDateCreatedGmt(now);
                logg( "BaseAPIHandler.getWeblogEntries.COMPILED_AT : now 2.->" + now);
                }
                //logg("setDateCreatedGmt:" + lPath);
            } catch (Exception e) {
                logger.log(LINFO, "getWeblogEntries.setDateCreatedGmt:" + e);
            }

            try {
                entry.setTitle(sXML(fetchAttr(postid, "DESCRIPTION")));
                if (entry.getTitle() == null) {
                    entry.setTitle(sXML(fetchAttr(postid, "NAME")));
                }
            } catch (Exception e) {
                entry.setTitle(e.getMessage());
            }
            /**** other tags ****/
            // LOCAL_PATH 
            String lPath = null;
                   lPath = fetchAttr(postid, "LOCAL_PATH"); //TODO
            logg(lPath);
            try {
                if (lPath != null) {
                    try{initMap(sXML(lPath));} 
                    catch (Exception e) {logg("initMap.getWeblogEntries:" + e);}
                    entry.setmtContent(lPath);
                    entry.setLocalPath(sXML(lPath));
                }
            } catch (Exception e) {
                logg("initMap.getWeblogEntries:" + e);
            }
            /********** MODIFIED_AT ***************/
             try {
                  String PubTime = fetchAttr(postid, "MODIFIED_AT");
                  logg( "BaseAPIHandler.getWeblogEntries.MODIFIED_AT : PubTime 1.->" + PubTime);
                  if (PubTime != null) {
                      Timestamp PubStamp = Timestamp.valueOf(PubTime);
                      entry.setUpdateTime(PubStamp);    
                  }
                  else {
                      Timestamp now = new Timestamp(System.currentTimeMillis());
                      entry.setUpdateTime(now);
                  logg( "BaseAPIHandler.getWeblogEntries.MODIFIED_AT : PubTime 2.->" + now);
                  }
                
              } catch (Exception e) {
                  logger.log(LINFO, 
                             "getWeblogEntry.MODIFIED_AT : " + e);
              }
              
             /************************/
             /** Tags **/
             String sTags = null;
             try {
                 sTags = 
                         fetchAttr(postid, "TAGS");
                 if (sTags != null) {
                     entry.setmt_keywords(sXML(sTags)); //(new String(sTags.getBytes(XmlRpcClientcharset)))
                     logg("TAGS:" + sXML(sTags));
                 }
             } catch (Exception e) {
                 logger.log(LINFO, 
                            "Tags.getWeblogEntry:" + sTags + ":" + e);
                 //entry.setText(e.getMessage());
             }
            /** CATEGORIES **/
            try {
                String cStr = null;
                cStr = fetchAttr(postid, "CATEGORIES");
                Vector catArray = new Vector();
                if (cStr != null) {
                    StringTokenizer st = new StringTokenizer(cStr, ",");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        catArray.addElement(sXML(token)); ////(new String(token.getBytes(XmlRpcClientcharset)))
                    }
                    entry.sCategories(catArray);
                }

            } catch (Exception e) {
                out("CATEGORIES.getWeblogEntries:" + e);
                entry.setText(e.getMessage());
            }

            entry.setStatus("Published");
            
            /*********** custom_fields **************/
             /********     
              * @array custom_fields
                  struct
                  string id
                  string key
                  string value 
                  ********/ 
              // PARAMS  &custom_fields=[{key=custom, value=Custom}] 
             String lPARAMS = null;
             try
             {   lPARAMS =  fetchAttr(postid, "PARAMS"); 
                 out("getWeblogEntry.PARAMS.0 => "+lPARAMS);
                try
                 {
                   initPARAMS(sXML(lPARAMS)); //TODO finished 2013.12
                   entry.setCustom(this.CustomParams);
                 } 
                 catch (Exception e) {logg("initPARAMS.getWeblogEntries:" + e);}
             }
                  
             catch (Exception e) {
                 logg("PARAMS.getWeblogEntry :" +lPARAMS + ":" +  e);
             }
             
            /******* wp_slug ********/
             try {
                 String wp_slug = null;
                 wp_slug = getTranslitInfo(postid);
                 entry.setSlug(wp_slug);
                 out( "getWeblogEntry:wp_slug=>" + wp_slug);
              }
             catch (Exception e) {System.out.println(e);}
            /************************/
        } catch (java.lang.NullPointerException e) {
            logger.log(LINFO, "getWeblogEntry:" + e.getStackTrace());
            return entry;
        }


        return entry;
    }

    /**
     * Get Weblog Entries grouped by day. This method returns a Map that
     * contains Lists, each List contains WeblogEntryData objects, and the
     * Lists are keyed by Date objects.
     * @param website    Weblog or null to get for all weblogs.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.
     * @param catName    Category path or null for all categories.
     * @param status     Status of DRAFT, PENDING, PUBLISHED or null for all
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @return Map of Lists, keyed by Date, and containing WeblogEntryData.
     * @throws WebloggerException
     */
    public Map getWeblogEntryObjectMap(Weblog website, Date startDate, 
                                       Date endDate, String catName, List tags, 
                                       String status, String locale, 
                                       int offset, 
                                       int range) throws JOPAException {
        Map entries = null;
        return entries;

    }

    /**
     * Get WeblogEntries by offset/length as list in reverse chronological order.
     * The range offset and list arguments enable paging through query results.
     * @param website    Weblog or null to get for all weblogs.
     * @param user       User or null to get for all users.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.
     * @param catName    Category path or null for all categories.
     * @param status     Status of DRAFT, PENDING, PUBLISHED or null for all
     * @param text       Text appearing in the text or summary, or null for all
     * @param sortBy     Sort by either 'pubTime' or 'updateTime' (null for pubTime)
     * @param sortOrder  Sort order of ASCENDING or DESCENDING (null for DESCENDING)
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @return List of WeblogEntryData objects in reverse chrono order.
     * @throws WebloggerException
     */
    public

    List getWeblogEntries(Weblog website, String user, Date startDate, 
                          Date endDate, String catName, List tags, 
                          String status, String text, String sortBy, 
                          String sortOrder, String locale, int offset, 
                          int range) throws JOPAException {
        /**
 * collection of oracle
 * */   logg("\ngetWeblogEntries () Called ===========[ SUPPORTED v.2012 ]=====");
        //"1",category,website,"creator","title","link","text","anchor",entry.getPubTime(),entry.getUpdateTime(),"status"
        if      (locale.compareToIgnoreCase(DPPATH)==0 & DGETPAGESRULE.compareToIgnoreCase("USER")==0){ enumItemsCode = "EnumByUserItems";}
        else if (locale.compareToIgnoreCase(DUPATH)==0 & DGETPOSTSRULE.compareToIgnoreCase("USER")==0){ enumItemsCode = "EnumByUserItems";}
        else enumItemsCode = "EnumItems";
 
        if (DADMINUSER!=null)if(matchString (DADMINUSER,DGRANTS)){enumItemsCode = "EnumItems";
            logg("getWeblogEntries.AdminUser        -> " + DADMINUSER);
        }
        NUMBER nFolder = locatePath(locale);
        //logg("getWeblogEntries:" + "\'" + sortBy + " " + sortOrder + "\'");
        // logger.log(BaseAPIHandler.LINFO,"nFolder:"+nFolder);
        
        logg("getWeblogEntries.locale           -> " + locale);
        logg("getWeblogEntries.DUPATH           -> " + DUPATH+":"+locale.compareToIgnoreCase(DUPATH));
        logg("getWeblogEntries.DPPATH           -> " + DPPATH+":"+locale.compareToIgnoreCase(DPPATH));
        logg("getWeblogEntries.user             -> " + DGRANTS);
        logg("getWeblogEntries.GETPAGESRULE     -> " + DGETPAGESRULE+":"+DGETPAGESRULE.compareToIgnoreCase("USER"));
        logg("getWeblogEntries.GETPOSTSRULE     -> " + DGETPOSTSRULE+":"+DGETPOSTSRULE.compareToIgnoreCase("USER")); 
        logg("getWeblogEntries.enumItems        -> " + enumItemsCode); 
        logg("\n");
        OracleResultSet rs =  enumItems(nFolder, "\'" + sortBy + " " + sortOrder +   "\'"); //TODO make to fetch all pages with star *
        // ( 1:ID, 2:ID_FOLDER, 3:PATH, 4:NAME, 5:ACCL, 6:DISP, 7:ID_REF, 8:REF_SIZE, 9:DATA, 10:TIME_STAMP )
        
        List<WeblogEntry> entries = new ArrayList<WeblogEntry>();
        System.setProperty("console.encoding", "Cp1251"); 
        System.setProperty("file.encoding", "Cp1251");
        try {
            if (rs != null) {
                int i = 1;
                while (rs.next()) {
                    i++;
                    WeblogEntry entry = new WeblogEntry();
                    WeblogCategory category = new WeblogCategory();
                    String getId = rs.getNUMBER(1).stringValue();
                   // BLOB blob = 
                   //     fetchCodeBLOB(getRef(rs.getNUMBER(1).stringValue()).stringValue());
                    CLOB clob = fetchCodeCLOB(getRef(rs.getNUMBER(1).stringValue()).stringValue());
                    entry.setId(rs.getNUMBER(7).stringValue());
                    entry.setLink(rs.getString(4));
                    entry.setDisplayTitle(rs.getString(4));
                    try {
                        entry.setTitle(sXML(fetchAttr(rs.getNUMBER(7).stringValue(), 
                                                      "DESCRIPTION")));
                        if (entry.getTitle() == "-1") {
                            entry.setTitle(rs.getString(4));
                        }
                    } catch (Exception e) {
                        logg("Exception.getWeblogEntries:entry.setTitle:" + 
                             getId + ":" + e);
                        entry.setTitle(getId);
                     }
                    logg("getWeblogEntries.Title        -> " + entry.getTitle()); 
                    try {
                        //logg(System.getProperty("console.encoding"));
                        //System.setOut(new java.io.PrintStream(System.out, true,         "Cp866"));
                        String sText = "ЎјЅ»¬\u0410\u0411\u0412";
                        logg("sText:" + sText);
                        //logg("System.getenv(\"NLS_LANG\")="+System.getenv("NLS_LANG"));
                         //logg("clob: "  + clob.toString()); 
                         String cXML = c2s(clob);
                         // logg("clob: "  + cXML);
                        entry.setText(sXML(cXML));
                        // logg("clob: "  + new String(cXML.getBytes("ISO-8859-1"),"Cp1251"));
                          //  entry.setText(new String(cXML.getBytes("ISO-8859-1"),"Cp1251"));
                       // entry.setText( new String(cXML.getBytes("ISO-8859-1"),"Cp1251"));
                        //entry.setText((sXML(b2s(blob))));
                          
                        // String cXML = b2s(blob);
                            //  cXML = new String(cXML.getBytes("utf-8"),"Cp1251");  
                          // entry.setText(blob.getBytes() getBytes("utf-8"));
                          // entry.setText((sXML(b2s(blob))));
                        
                    } catch (Exception e) {
                        logg("Exception.getWeblogEntries:entry.setText:" + 
                             getId + ":" + e);
                        entry.setText(e.getMessage());
                    }
                    /****/
                    //COMPILED_AT

                    /**** other tags ****/
                    try {
                        // LOCAL_PATH 
                        String lPath = null;
                        lPath = 
                                    fetchAttr(rs.getNUMBER(7).stringValue(), "LOCAL_PATH");
                         logg("getWeblogEntries.LOCAL_PATH:"+lPath); 
                        if (lPath != null) {
                            entry.setLocalPath(sXML(lPath));
                              try{initMap(sXML(lPath));} 
                              catch (Exception e) {logg("initMap.getWeblogEntries:" + e);}
                        }
                                            
                    } catch (Exception e) {
                        logg("LOCAL_PATH.getWeblogEntries:" + e);
                    }
                    /************************/
                    entry.setStatus(rs.getNUMBER(5).stringValue());
                    //Timestamp dStamp = rs.getDATE(10).timestampValue();
                   //Timestamp now = new Timestamp(System.currentTimeMillis());
                    //entry.setUpdateTime(dStamp);      
                    // entry.setPubTime(dStamp); 
                  /** MODIFIED_AT **/  
                   try {
                        String PubTime = fetchAttr(rs.getNUMBER(7).stringValue(), "MODIFIED_AT");
                        logg("getWeblogEntries.MODIFIED_AT : PubTime =>" + PubTime);
                        if (PubTime != null) {
                            Timestamp PubStamp = Timestamp.valueOf(PubTime);
                            entry.setUpdateTime(PubStamp);    
                        }
                        else {
                            Timestamp dStamp = rs.getDATE(10).timestampValue();
                            entry.setUpdateTime(dStamp);      
                            }
                    } catch (Exception e) {
                        logger.log(LINFO, 
                                   "getWeblogEntries.setPubTime:" + e);
                    }
                    
                    try {
                        String PubTime = null;
                        PubTime = fetchAttr(rs.getNUMBER(7).stringValue(), "COMPILED_AT");
                        logg("getWeblogEntries.COMPILED_AT : PubTime =>" + PubTime);        
                        if (PubTime != null) {
                            Timestamp PubStamp = Timestamp.valueOf(PubTime);
                            Timestamp date_created_gmt = new Timestamp(PubStamp.getTime()- 0 * 60 * 60 * 1000);
                            logg("getWeblogEntries.date_created_gmt =>" + date_created_gmt); 
                            entry.setDateCreatedGmt(date_created_gmt); //TODO
                            entry.setPubTime(PubStamp);
                        }
                        else {
                            Timestamp dStamp = rs.getDATE(10).timestampValue();
                            entry.setDateCreatedGmt(dStamp);  
                            entry.setPubTime(dStamp);
                            }
                        //logg("setDateCreatedGmt:"+lPath);
                    } catch (Exception e) {
                        logger.log(LINFO, 
                                   "getWeblogEntries.setDateCreatedGmt:" + e);
                    }
                    /** Tags **/
                    String sTags = null;
                    try {
                        sTags = 
                                fetchAttr(rs.getNUMBER(7).stringValue(), "TAGS");
                        if (sTags != null) {
                            entry.setmt_keywords(sXML(sTags)); //(new String(sTags.getBytes(XmlRpcClientcharset)))
                            logg("TAGS:" + sXML(sTags));
                        }
                    } catch (Exception e) {
                        logger.log(LINFO, 
                                   "Tags.getWeblogEntries:" + sTags + ":" + e);
                        //entry.setText(e.getMessage());
                    }
                    /** CATEGORIES **/
                    try {
                        String cStr = null;
                        cStr = fetchAttr(rs.getNUMBER(7).stringValue(), "CATEGORIES");
                        Vector catArray = new Vector();
                        if (cStr != null) {
                            StringTokenizer st = 
                                new StringTokenizer(cStr, ",");
                            while (st.hasMoreTokens()) {
                                String token = st.nextToken();
                                catArray.addElement(sXML(token)); ////(new String(token.getBytes(XmlRpcClientcharset)))
                            }
                            entry.sCategories(catArray);
                        }
                    } catch (Exception e) {
                        logger.log(LINFO, "getWeblogEntriess:" + e);
                        out("getWeblogEntriess:"+e);
                        //entry.setText(e.getMessage());
                    }

                    entries.add(entry);
                    if (i > range) {
                        break;
                    }
                    //logg("getWeblogEntries:"+entry.getText());
                }
                rs.close();
            }
        } catch (SQLException e) {
            String msg = "getWeblogEntries\n";
            logger.log(Level.WARNING, msg + e.getMessage());
            return entries;
        }
        return entries;
    }

    /******
     * 
     * *******/
    public static

    Calendar convertToGmt(Calendar cal) {
        Date date = cal.getTime();
        TimeZone tz = cal.getTimeZone();
        logg("input calendar has date [" + date + "]");
        //Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT  
        long msFromEpochGmt = date.getTime();
        //gives you the current offset in ms from GMT at the current date 
        int offsetFromUTC = tz.getOffset(msFromEpochGmt);
        logg("offset is " + offsetFromUTC);
        //create a new calendar in GMT timezone, set to this date and add the offset 
        Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtCal.setTime(date);
        gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);
        logg("Created GMT cal with date [" + gmtCal.getTime() + "]");
        return gmtCal;
    }
    
    private HashMap setCustomFields(String fId, String fKey,String fValue)
                              {
         HashMap result  = new HashMap(); 
                                  result.put("id", fId);  
                                  result.put("key", fKey); 
                                  result.put("value",fValue); 
                                 
         return result;
                              }
    protected  Hashtable getMediaItems(String blogid, String userid, String password, int attachment_id) throws Exception {
            Hashtable MediaItem = new Hashtable();
            //out("getMediaItems.attachment_id=>"+attachment_id);
           /* select id,lookup_type, code, description,pos,parent_id,owner_id,status,timestamp */
           OracleResultSet rs = getAnyLookupByID(attachment_id, "ID","getAnyLookupByParentID");
        if (rs != null) {
            int i = 0;
            while (rs.next()) {
           out("getMediaItems.attachment_id=>"+attachment_id);
           String description = rs.getString(4);
           if (description!=null){
           // out("getMediaItems.description=>"+description);
           MediaItem.put("attachment_id", rs.getString(6)); 
           Timestamp current = rs.getDATE(9).timestampValue();
           MediaItem.put("date_created_gmt",  current);
           MediaItem.put("parent",  rs.getString(1));
           MediaItem.put("link", getStringByValue("link",description));
           MediaItem.put("title",getStringByValue("title",description)); 
           MediaItem.put("caption",getStringByValue("caption",description));
           MediaItem.put("description", rs.getString(3)); 
           MediaItem.put("thumbnail", getStringByValue("link",description)); 
             /***** MediaItemMetadata metadata ****/
            LinkedHashMap metadata = new LinkedHashMap();
            metadata.put("width",  getStringByValue("width",description));
            metadata.put("height", getStringByValue("height",description));
            metadata.put("file",rs.getString(3));
                LinkedHashMap sizes = new LinkedHashMap(); //TODO 
                sizes.put("file"  , rs.getString(3));
                sizes.put("width" , getStringByValue("width",description));
                sizes.put("height", getStringByValue("height",description));
                sizes.put("mime-type",getStringByValue("type",description));
            metadata.put("post-thumbnail",sizes);
                
          MediaItem.put("metadata", metadata); 
             /****** PostThumbnailImageMeta image_meta ************/ 
           /* LinkedHashMap thumbnail = new LinkedHashMap();
            thumbnail.put("file", rs.getString(3));
            thumbnail.put("width", "150");
            thumbnail.put("height", "150");
            thumbnail.put("mime-type", "image/jpg");
            MediaItem.put("thumbnail", thumbnail);*/
            i++;
           }
         }}
         // MediaItemMetadata 
         // PostThumbnailImageMeta
      return MediaItem;   
    
    }                          
    /*********    
     * 
     * createPostStructTitles for 
     * 
     * 
     * mt.getRecentPostTitles
     * 
     * ***********/
     public LinkedHashMap createPostStructTitles(WeblogEntry entry, String userid) {
         out("---------------  createPostStruct step.0 ----------------------");
         LinkedHashMap struct = new LinkedHashMap(); 
         struct.put("postid",   entry.getId()  );
         try {
             struct.put("title", entry.getTitle());
         } catch (Exception e) {
             struct.put("title", "");
         }
         try {
             struct.put("description", entry.getText());
          } catch (Exception e) {
             struct.put("description", "");
         }
         out("---------------  createPostStructTitles step.1 ----------------------");
             struct.put("userid", DUSERID);
             struct.put("dateCreated", entry.getPubTime());
             struct.put("date_created_gmt", entry.getDateCreatedGmt());
             struct.put("post_status", "publish");
          out("--------------- End createPostStructTitles --------------------");
           return struct;
     }
    /**
     *  Hashtable createPostStruct(WeblogEntry entry, String userid)
     * v.2013.12
     * */
    public LinkedHashMap createPostStruct(WeblogEntry entry, String userid, String password) {
        out("---------------  createPostStruct step.0 ----------------------");
        LinkedHashMap struct = new LinkedHashMap(); 
        struct.put("postid",   entry.getId()  );
        try {
            struct.put("title", entry.getTitle());
        } catch (Exception e) {
            struct.put("title", "");
        }
        try {
            struct.put("description", entry.getText());
         } catch (Exception e) {
            struct.put("description", "");
        }
        out("---------------  createPostStruct step.1 ----------------------");
        struct.put("link", DPERMALINK + entry.getLink());
        struct.put("userid", DUSERID);
        struct.put("dateCreated", entry.getPubTime());
        struct.put("date_created_gmt", entry.getDateCreatedGmt());
        struct.put("date_modified", entry.getPubTime());
        struct.put("date_modified_gmt", entry.getDateCreatedGmt());
        struct.put("permaLink", DPERMALINK + entry.getLink());
         /*********************************************************/
         out("---------------  createPostStruct step.2 ----------------------");
         out("createPostStruct.postid=>" +  entry.getId().toString());
          /** CATEGORIES **/
         try {
            out("createPostStruct.Categories=>" +  entry.gCategories().toString());
            struct.put("categories", entry.gCategories());
        } catch (Exception e) {
            List<String> cStr = new ArrayList<String>();
            cStr.add("Uncategorised");
            entry.sCategories(cStr); 
            struct.put("categories", cStr);
        }
        /** tags **/
        if (entry.getmt_keywords() != null){
            struct.put("mt_keywords", entry.getmt_keywords()); 
            out("createPostStruct.mt_keywords=>" +  entry.getmt_keywords().toString());}  
        else
            struct.put("mt_keywords", "");  
        
          try {
              String wp_slug = null;
              wp_slug = getTranslitInfo(entry.getId());
              entry.setSlug(wp_slug);
              if (wp_slug != null) {
                    struct.put("wp_slug", wp_slug);}
              else {struct.put("wp_slug", "");}
              out("createPostStruct.wp_slug =>"+wp_slug);
           }  catch (Exception e) {
              out("createPostStruct.wp_slug=>"+e);
                }
          try {
            // LOCAL_PATH 
            String lPath = null;
            //lPath = fetchAttr(entry.getId(), "LOCAL_PATH");  
            lPath = entry.getLocalPath();
        } catch (Exception e) {
            logg("initMap.createPostStruct :" + entry.getLocalPath() + ":" + 
                 e);
        }
 
        /***** 2013 v.2013.12 *************/ //TODO custom_fields
        ArrayList<LinkedHashMap> custom_fields = new ArrayList<LinkedHashMap>();
        /* LinkedHashMap addcustom_fields_struct = new LinkedHashMap();
          addcustom_fields_struct.put("id","_thumbnail_id");
          addcustom_fields_struct.put("key","_thumbnail_id");
          addcustom_fields_struct.put("value","224309");
          custom_fields.add(addcustom_fields_struct);*/
   // PARAMS:&custom_fields=[{key=geo_latitude, value=57.622693}, {key=geo_longitude, value=39.856856}, {key=geo_public, value=1}]
         String lPARAMS = fetchAttr(entry.getId(), "PARAMS"); 
         logg("createPostStruct => PARAMS:"+lPARAMS);       
         if(lPARAMS!=null)
         if(lPARAMS.indexOf("custom_fields=")>0)
          try {
             String CustomPARAMS = lPARAMS; 
             LinkedHashMap custom_fields_struct = new LinkedHashMap();
                    CustomPARAMS = CustomPARAMS.replace("&custom_fields=[","");
                    CustomPARAMS = CustomPARAMS.replace("]","");
             ArrayList<String> custom_arr = new ArrayList<String>(Arrays.asList(CustomPARAMS.split(",")));
             int i=0;
             for(String element: custom_arr){
                         element = element.replace("{","");
                         element = element.replace("}","");
                          String temp[] = element.split("=");
                        // out(temp[0]+":"+temp[1]);
                         custom_fields_struct.put(temp[0],temp[1]); 
                         if(temp[0].indexOf("value")>0){
                         custom_fields.add(i,custom_fields_struct); 
                         //System.out.println(i+":"+custom_fields_struct);
                         custom_fields_struct = new LinkedHashMap();
                         i++;
                         }                       
                     }    
                          struct.put("custom_fields",custom_fields);  //TODO Finished 05.04.2014      
           }
           catch (Exception e) {} 
        
         /****** wp.getTerm *******/ //TODO v.2014
        /*      Map<String,String> taxonomy = new HashMap<String,String>();
              taxonomy.put("term_id", "1");
              taxonomy.put("name","taxonomy_name");
              taxonomy.put("slug","title"); 
              taxonomy.put("term_group","group");
              taxonomy.put("term_taxonomy_id","1");
              taxonomy.put("taxonomy","taxonomy");
              taxonomy.put("description","taxonomy");
              taxonomy.put("parent","0");
              struct.put("post_thumbnail", taxonomy); */   
          /****************/
        
        try {
            /**** other tags ****/
            /************************/
            Map<?, ?> map = this.parmsMap;
             out("--------------- Begin Map ----------------------");
            if (this.parmsMap != null)
                for (Map.Entry<?, ?> lentry: map.entrySet()) {
                    String gValue = lentry.getValue().toString();
                    String gKey   = lentry.getKey().toString();
                    try {
                        if (!gValue.equalsIgnoreCase("null") && 
                            !gValue.equalsIgnoreCase("")&&!gKey.equalsIgnoreCase("mt_keywords")&&!gKey.equalsIgnoreCase("wp_slug")) {
                            //System.out.println("createPostStruct=> "+lentry.getKey() + ":" + lentry.getValue() );
                            out("createPostStruct=> "+lentry.getKey() + ":" + lentry.getValue() );
                            
                            /******** wp_post_thumbnail ********/ 
                            if (lentry.getKey().toString().indexOf("post_thumbnail")>0) {
                            //out("--------------- createPostStruct post_thumbnail ----------------------\n"+post_thumbnail);
                            String wp_post_thumbnail = lentry.getValue().toString();
                                   wp_post_thumbnail = checkLookupExistsByID(wp_post_thumbnail);
                             if (wp_post_thumbnail!=null) 
                             struct.put("wp_post_thumbnail", checkLookupExistsByID(wp_post_thumbnail));
                             else 
                             struct.put("wp_post_thumbnail", "");
                             /***** 2014 getMediaItem *****/
                           /* try{
                             //int thumbnailID =  Integer.valueOf(lentry.getValue().toString());
                            // struct.put("post_thumbnail", getMediaItems(entry.getId(),userid,password,thumbnailID) );
                            }
                            catch(Exception e) {out("wp_post_thumbnail:"+e.getMessage());}
                            */
                            }
                             
                            else {
                            
                            struct.put(lentry.getKey(), lentry.getValue());}
                        }
                    } catch (Exception e) {
                        logg("createPostStruct /**** other tags ****/ :" + e + 
                             ":" + this.parmsMap);
                    }
                }


        } catch (Exception e) {
            out("createPostStruct /**** other tags ****/ :" + e + ":" + 
                this.parmsMap);
        }
        out("--------------- End createPostStruct --------------------");
        
         out("=============== Structs createPostStruct for out ================");
         //out(struct.toString()); 
         out("=============== End     createPostStruct for out ================");
        
        return struct;
    }
    public static String getAbsoluteContextURL() {
        return DPERMALINK;
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
     @struct
     datetime dateCreated (ISO.8601)
     int userid
     int page_id
     string page_status
     string description
     string title
     string link
     string permaLink
     @array categories
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
     @array custom_fields
     struct
     string id
     string key
     string value
     ...
     string wp_page_template

     * ****/
    public LinkedHashMap createPageStruct(WeblogEntry entry, String userid) {
        String contextUrl = getAbsoluteContextURL();
        LinkedHashMap struct = new LinkedHashMap();
        struct.put("dateCreated", entry.getPubTime());
        struct.put("userid", userid);
        struct.put("page_id", entry.getId());
        struct.put("page_status", entry.getStatus());
        if (entry.getText() != null) {
            struct.put("description", entry.getText());
        } else {
            struct.put("description", "");
        }
        struct.put("title", entry.getTitle());
        struct.put("permaLink", DPERMALINK + entry.getLink());
        struct.put("link", DPERMALINK + entry.getLink());
        if (entry.getDateCreatedGmt() != null) {
            struct.put("date_created_gmt", entry.getDateCreatedGmt());
        }
        if (entry.getmt_keywords() != null)
            struct.put("mt_keywords", entry.getmt_keywords());
       
       /** CATEGORIES **/
        try {
            struct.put("categories", entry.gCategories());
        } catch (Exception e) {
            List<String> cStr = new ArrayList<String>();
            struct.put("categories", cStr);
        }
        
        // logg("init4Map:" + entry.getLocalPath());
        boolean Mapped = false;
        try {
            initMap(entry.getLocalPath());
            Mapped = true;
        } catch (Exception e) {
            logg("initMap.createPageStruct:" + e);
        }
        if (Mapped) {
            try {

                Map<?, ?> map = this.parmsMap;
                // logg("--------------- Begin Map ----------------------");
                for (Map.Entry<?, ?> lentry: map.entrySet()) {
                    String sValue = lentry.getValue().toString();
                    if (lentry.getValue() != null) {
                        //  logg("createPageStruct:"+lentry.getKey() + ": " + lentry.getValue());
                        if (isIntegerValue(lentry.getValue().toString()) && 
                            !lentry.getKey().toString().equalsIgnoreCase("page_status")) {
                            int eval = 
                                Integer.valueOf(lentry.getValue().toString());
                            //logg("createPageStruct:integer.put:"+lentry.getKey() + ": " + eval);
                            struct.put(lentry.getKey(), eval);
                        } else {

                            if (!sValue.equalsIgnoreCase("null") && 
                                sValue.length() > 0) {
                                //logg("createPageStruct:string.put:"+sValue.length()+":"+lentry.getKey() + ": " + lentry.getValue());
                                struct.put(lentry.getKey(), lentry.getValue());
                            }
                        }
                    }
                }
                // logg("----------------- End Map --------------------");
            } catch (Exception e) {
                logg("createPageStruct.initMap:" + e);
            }
        }

        return struct;
    }

    /*********** createCategoryStruct ****************/
    public static Hashtable createCategoryStruct(WeblogCategory category, 
                                                 String userid) {

        String contextUrl = getAbsoluteContextURL();

        Hashtable struct = new Hashtable();
        struct.put("categoryid", category.getPath());
        struct.put("title", category.getName());
        struct.put("description", category.getDescription());
        /*
        struct.put("description", category.getPath());

        String catUrl = contextUrl+"/page/"+userid+"?catname="+category.getPath();
        catUrl = replace(catUrl," ","%20");
        struct.put("htmlUrl", catUrl);

        String rssUrl = contextUrl+"/rss/"+userid+"?catname="+category.getPath();
        rssUrl = replace(catUrl," ","%20");
        struct.put("rssUrl",rssUrl);
        */
        return struct;
    }

    /**
     *  Hashtable createPageStruct(WeblogEntry entry, String userid)
     * @Return Values
    @array struct
    int page_id
    string page_title
    int page_parent_id
    datetime dateCreated
    ... 
     * */
    public Hashtable createPageListStruct(WeblogEntry entry, String userid) {
        Hashtable struct = new Hashtable();
        struct.put("page_id", Integer.valueOf(entry.getId()));
        struct.put("page_title", entry.getTitle());
        struct.put("page_parent_id", 0); //TODO
        struct.put("dateCreated", entry.getPubTime());
        return struct;
    }

    /**
     * 
     * 
     * @roller.wrapPojoMethod type="pojo-collection" class="org.apache.roller.weblogger.pojosWeblogCategorya"
     */
    public

    List<WeblogCategory> getPostCategories(Weblog categoryPath, 
                                           String sCats) throws JOPAException {
        OracleResultSet rs = getLookup(LOOKUP_CATEGORY, "POS");
        List<WeblogCategory> rets = new ArrayList<WeblogCategory>();
        // select  id,lookup_type,code,description,pos,parent_id,owner_id,status from t$lookup 
        //         :1  :2          :3    :4         :5   :6        :7        :8
        try {
            if (rs != null) {
                int i = 1;
                while (rs.next()) {
                    i++;
                    rs.getNUMBER(8).stringValue();
                    WeblogCategory ret = new WeblogCategory();
                    String uCats = rs.getString(4);
                    //logg("getPostCategories"+":"+sCats.indexOf(uCats)+":"+sCats+":"+uCats);
                    if (sCats.indexOf(uCats) > -1) {
                        ret.setId(rs.getNUMBER(1).stringValue());
                        ret.setTitle(sXML(rs.getString(3)));
                        ret.setDescription(sXML(rs.getString(4)));
                        ret.setPath(sXML(rs.getString(4)));
                        ret.setName(sXML(rs.getString(4)));
                        ret.setImage(rs.getString(4));
                        ret.setPos(rs.getInt(5));
                        ret.setParent(rs.getInt(6));
                        rets.add(ret);
                    }
                }
                rs.close();
            }
        } catch (Exception e) {
            String msg = "getPostCategories:";
            logger.log(Level.WARNING, msg + e.getMessage());
        }
        return rets;
    }

    /*******
     * 
     * ************/
    public List<WeblogCategory> getWeblogCategories(Weblog categoryPath, 
                                                    boolean publish) throws JOPAException {
        OracleResultSet rs = getLookup(LOOKUP_CATEGORY, "POS");
        List<WeblogCategory> rets = new ArrayList<WeblogCategory>();
        // select  id,lookup_type,code,description,pos,parent_id,owner_id,status from t$lookup 
        //         :1  :2          :3    :4         :5   :6        :7        :8
        try {
            if (rs != null) {
                int i = 1;
                while (rs.next()) {
                    i++;
                    rs.getNUMBER(8).stringValue();
                    WeblogCategory ret = new WeblogCategory();
                    ret.setId(rs.getNUMBER(1).stringValue());
                    ret.setTitle(sXML(rs.getString(3)));
                    ret.setDescription(sXML(rs.getString(4)));
                    ret.setPath(sXML(rs.getString(4)));
                    ret.setName(sXML(rs.getString(4)));
                    ret.setImage(rs.getString(4));
                    ret.setPos(rs.getInt(5));
                    ret.setParent(rs.getInt(6));
                    rets.add(ret);
                }
                rs.close();
            }
        } catch (Exception e) {
            String msg = "getWeblogCategories";
            logger.log(Level.WARNING, msg + e.getMessage());
        }
        return rets;
    }

    /** getWeblogTags Tags **/
    public List getWeblogTags(Weblog tagPath, 
                              boolean publish) throws JOPAException {
        // id,lookup_type,code,description,pos,parent_id,owner_id,status
        OracleResultSet rs = getLookup("TAGS", "POS");
        List<WeblogTags> rets = new ArrayList<WeblogTags>();

        try {
            if (rs != null) {
                int i = 1;
                while (rs.next()) {
                    i++;
                    WeblogTags tag = new WeblogTags();
                    tag.setId(rs.getNUMBER(1).stringValue());
                    tag.setName(rs.getString(3));
                    tag.setCount(rs.getInt(1));
                    tag.setSlug(rs.getString(4));
                    tag.setHtmlUrl(rs.getString(4));
                    tag.setRssUrl(rs.getString(4));
                    rets.add(tag);
                }
                rs.close();
            }
        } catch (SQLException e) {
            String msg = "getWeblogTags";
            logger.log(Level.WARNING, msg + e.getMessage());
        }
        return rets;
    }
    /** getStringByValue **/
    public String getStringByValue(String key,String sparams) throws JOPAException {
        String rvalue = "";
        String params[] = sparams.split("&");
        for (String param: params) {
           String temp[] = param.split("=");
        try{  
           //out("getStringByValue: "+key+" =>"+temp[0]+":"+temp[1]);
           if (temp[0].equalsIgnoreCase(key)){
               rvalue = temp[1];
           }
           }
        catch(Exception p){ logger.log(Level.WARNING,p.getMessage());}
        }
      return rvalue;
    };
    
    /***********************/
     public static String get(Object obj) {
     String val = null;
      if (obj!=null) {
                 if (obj instanceof String)  val = obj.toString();
            else if (obj instanceof Number)  val = obj.toString();
            else if (obj instanceof Boolean) {
               boolean b = ((Boolean) obj).booleanValue();
               if(b) return "true";
               else  return "false";
            }
        }       
      return val;
      
     }
    /*************************/
     public static void printObject (Object obj){
          if (obj instanceof Collection) {
              System.out.println("Start Collection ==>");
              for (Object objs : (Collection)obj) {
                    System.out.println(objs);
                 }
              System.out.println("<==== End Collection");    
          }
         else if (obj.getClass().isArray()){
             Object[] objects = (Object[]) obj;
             for (Object o : objects)
                     System.out.println(o);
             
              System.out.println("Object  - isArray");
            
         } 
         else if (obj instanceof Map) {
             HashMap map  = (HashMap) obj;
            
             Set set = map.entrySet(); 
             Iterator i = set.iterator(); 
             while(i.hasNext()) { 
                 Map.Entry me = (Map.Entry)i.next();
                 String   key = me.getKey().toString();
                 String value = me.getValue().toString();
                 
                  if (me.getValue().getClass().isArray())
                  {
                      System.out.println(key+":=>");
                      printObject(me.getValue());
                      System.out.println("<=");
                       
                  }
                  else  System.out.println(key+" : "+value);
             }
              
              System.out.println("Object is Map");
              System.out.println(map.toString());
              
     
         }
         else if (obj instanceof Number)   System.out.println(obj.toString());
         else if (obj instanceof Boolean)  System.out.println(obj.toString());
         else if (obj instanceof String)   System.out.println(obj.toString());
         
     }
    /***********
     * 
     * Object defs(Object in,Object val)
     * 
     * ************/
     public static Object defs(Object in,Object val) {
      if (in!=null) return in;
      else return val;
     }
    
    /***  *************/
    public static String defS(Object in,Object val) {
     if (in!=null) return in.toString();
     else return val.toString();
    }   
    /***//****************/
    
    public static LinkedHashMap repsLHM (LinkedHashMap struct,String fromName,String toName){
       try {
        Object value =  struct.get(fromName);
                        struct.put(toName,value);
                        struct.remove(fromName);
       }
       catch(Exception ee){}
       return  struct;                
    }
    private static String fileRevision = "$Revision: 2014.06 $";
} // end of class
