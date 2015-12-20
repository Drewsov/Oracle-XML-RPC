package com.nnetworks.xmlrpc;

import com.nnetworks.jopa.JOPAException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import java.util.Vector;
import java.util.logging.Level;

import java.util.logging.Logger;

import oracle.sql.BLOB;

import redstone.xmlrpc.XmlRpcDispatcher;

public class WeblogEntry implements Serializable {


    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());


    public static final String DRAFT = "DRAFT";
    public static final String PUBLISHED = "PUBLISHED";
    public static final String PENDING = "PENDING";
    public static final String SCHEDULED = "SCHEDULED";

    // Simple properies
    private String id = null; // UUIDGenerator.generateUUID();
    private String title = null;
    private byte[] btitle;
    private byte[] btext;
    private char[] ctitle;
    private String link = null;
    private String summary = null;
    private String text = null;
    private String contentType = null;
    private String contentSrc = null;
    private String anchor = null;
    private Timestamp pubTime = null;
    private Timestamp updateTime = null;
    private Timestamp date_created_gmt = null;
    private String plugins = null;
    private Boolean allowComments = Boolean.TRUE;
    private Integer commentDays = new Integer(7);
    private Boolean rightToLeft = Boolean.FALSE;
    private Boolean pinnedToMain = Boolean.FALSE;
    private String status = DRAFT;
    private String locale = null;

    private String mt_keywords = null;
    private String mt_allow_pings = null;
    private String mt_text_more = null;
    private String mt_allow_comments = null;
    private String description = null;
    private String mt_excerpt = null;
    private String mt_convert_breaks = null;
    private String mt_content = null;
    private Timestamp dateCreated = null;
    private String mt_tb_ping_url = null;
    private String local_path = null;
    private String wp_slug = null;
    private String mt_basename = null;

    // Associated objects
    //  private User           creator  = null;
    private String creator = null;
    private Weblog website = null;
    private WeblogCategory category = null;

    // Collection of name/value entry attributes
    private Set attSet = new TreeSet();
    private List<WeblogCategory> catSet = new ArrayList<WeblogCategory>();
    private List<String> content = new ArrayList<String>();
    private List<String> cStr = new ArrayList<String>();
    private List<LinkedHashMap>  customSet = new ArrayList<LinkedHashMap>();
    private Set tagSet = new HashSet();
    private Set removedTags = new HashSet();
    private Set addedTags = new HashSet();

    //----------------------------------------------------------- Construction

    public WeblogEntry() {
    }

    public WeblogEntry(String id, WeblogCategory category, Weblog website, 
                       String creator, String title, String link, String text, 
                       String anchor, Timestamp pubTime, Timestamp updateTime, 
                       String status, String local_path, String wp_slug) {
        this.id = id;
        this.category = category;
        this.website = website;
        this.creator = creator;
        this.title = title;
        this.link = link;
        this.text = text;
        this.anchor = anchor;
        this.pubTime = pubTime;
        this.updateTime = updateTime;
        this.status = status;
        this.local_path = local_path;
        this.wp_slug = wp_slug;
    }

    public void SetWeblogEntry(String id, List category, String creator, 
                               String title, String link, String text, 
                               String anchor, Timestamp pubTime, 
                               Timestamp updateTime, String status, 
                               String mt_keywords, String mt_allow_pings, 
                               String mt_text_more, String mt_allow_comments, 
                               String mt_excerpt, String mt_convert_breaks, 
                               String mt_tb_ping_url, String wp_slug, 
                               Timestamp date_created_gmt, String mt_basename, 
                               String mt_content) {
        this.id = id;
        this.catSet = category;
        //this.setCategory(category); 
        this.creator = creator;
        this.title = title;
        this.link = link;
        this.text = text;
        this.anchor = anchor;
        this.pubTime = pubTime;
        this.updateTime = updateTime;
        this.status = status;
        this.mt_keywords = mt_keywords;
        this.mt_allow_pings = mt_allow_pings;
        this.mt_text_more = mt_text_more;
        this.mt_allow_comments = mt_allow_comments;
        this.mt_excerpt = mt_excerpt;
        this.mt_convert_breaks = mt_convert_breaks;
        this.mt_tb_ping_url = mt_tb_ping_url;
        this.wp_slug = wp_slug;
        this.date_created_gmt = date_created_gmt;
        this.mt_basename = mt_basename;
        this.mt_content = 
                mt_content + "&mt_keywords=" + mt_keywords + "&mt_allow_pings=" + 
                mt_allow_pings + "&mt_text_more=" + mt_text_more + 
                "&mt_allow_comments=" + mt_allow_comments + "&mt_excerpt=" + 
                mt_excerpt + "&mt_convert_breaks=" + mt_convert_breaks + 
                "&mt_tb_ping_urls=" + mt_tb_ping_url + "&wp_slug=" + wp_slug + 
                "&page_status=" + status + "&mt_basename=" + mt_basename+"&creator="+creator;
    }

    /**
     * String getmtContent()
     * **/
    public String getmt_basename() {
        return this.mt_basename;
    }

    public void setmt_basename(String text) {
        this.mt_basename = text;
    }

    public String getmt_excerpt() {
        return this.mt_excerpt;
    }

    public void setmt_excerpt(String text) {
        this.mt_excerpt = text;
    }

    /**
     * String getmtContent()
     * **/
    public String getmtContent() {
        return this.mt_content;
    }

    public void setmtContent(String text) {
        this.mt_content = text;
    }

    /**
     * String getmtContent()
     * **/
    public String getmt_keywords() {
        return this.mt_keywords;
    }

    public void setmt_keywords(String text) {
        this.mt_keywords = text;
    }

    /**
     * <p>Publish time is the time that an entry is to be (or was) made available
     * for viewing by newsfeed readers and visitors to the Roller site.</p>
     *
     * <p>Roller stores time using the timeZone of the server itself. When
     * times are displayed  in a user's weblog they must be translated
     * to the user's timeZone.</p>
     *
     * <p>NOTE: Times are stored using the SQL TIMESTAMP datatype, which on
     * MySQL has only a one-second resolution.</p>
     *
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="pubtime" non-null="true" unique="false"
     */
    public Timestamp getPubTime() {
        return this.pubTime;
    }


    /**
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.id column="id" generator-class="assigned"  
     */
    public String getId() {
        return this.id;
    }

    /** @ejb:persistent-field */
    public void setId(String id) {
        // Form bean workaround: empty string is never a valid id
        if (id != null && id.trim().length() == 0)
            return;
        this.id = id;
    }

    /**
     * Get content text for weblog entry (maps to RSS content:encoded and Atom content).
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="text" non-null="true" unique="false"
     */
    public String getText() {
        return this.text;
    }

    /**
     * Set content text for weblog entry (maps to RSS content:encoded and Atom content).
     * @ejb:persistent-field
     */
    public void setText(String text) {
        this.text = text;
    }

    /** @ejb:persistent-field */
    public void setSlug(String wp_slug) {
        this.wp_slug = wp_slug;
    }
    public String getSlug() {
        return this.wp_slug;
    }
    /**
     * Set content text for weblog entry (maps to RSS content:encoded and Atom content).
     * @ejb:persistent-field
     */
    protected static final int BUFFER_SIZE = 8192;

    /**
     * freeTemporary (BLOB blob)
     *  
     */
    protected void freeTemporary(BLOB blob) {
        try {
            if (blob != null && blob.isTemporary())
                blob.freeTemporary();
        } catch (SQLException e) {
        }
    }

    /**
     *  setText(BLOB blob) 
     *  
     */
 /*   public void setText(BLOB blob) throws JOPAException {
        this.text = BaseAPIHandler.b2s(blob);
    }*/

    /** @ejb:persistent-field */
    public void setPubTime(Timestamp pubTime) {
        this.pubTime = pubTime;
    }


    /**
     * <p>Update time is the last time that an weblog entry was saved in the
     * Roller weblog editor or via web services API (XML-RPC or Atom).</p>
     *
     * <p>Roller stores time using the timeZone of the server itself. When
     * times are displayed  in a user's weblog they must be translated
     * to the user's timeZone.</p>
     *
     * <p>NOTE: Times are stored using the SQL TIMESTAMP datatype, which on
     * MySQL has only a one-second resolution.</p>
     *
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="updatetime" non-null="true" unique="false"
     */
    public Timestamp getUpdateTime() {
        return this.updateTime;
    }

    /** @ejb:persistent-field */
    public void setDateCreatedGmt(Timestamp date_created_gmt) {
        this.date_created_gmt = date_created_gmt;
    }

    public Timestamp getDateCreatedGmt() {
        return this.date_created_gmt;
    }

    /** @ejb:persistent-field */
    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * A no-op. TODO: fix formbean generation so this is not needed.
     */
    public void setPermalink(String string) {
    }

    /**
     * A no-op. TODO: fix formbean generation so this is not needed.
     */
    public void setPermaLink(String string) {
    }

    /**
     * A no-op.
     * TODO: fix formbean generation so this is not needed.
     * @param string
     */
    public void setDisplayTitle(String string) {
    }

    /**
     * @roller.wrapPojoMethod type="pojo"
     * @ejb:persistent-field
     * @hibernate.many-to-one column="websiteid" cascade="none" not-null="true"
     */
    public Weblog getWebsite() {
        return this.website;
    }

    /** @ejb:persistent-field */
    public void setWebsite(Weblog website) {
        this.website = website;
    }

    /**
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.many-to-one column="userid" cascade="none" not-null="true"
     */
    public String getCreator() {
        return this.creator;
    }

    /** @ejb:persistent-field */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="status" non-null="true" unique="false"
     */
    public String getStatus() {
        return this.status;
    }

    /** @ejb:persistent-field */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Some weblog entries are about one specific link.
     * @return Returns the link.
     *
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="link" non-null="false" unique="false"
     */
    public String getLink() {
        return link;
    }

    /**
     * @ejb:persistent-field
     * @param link The link to set.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="anchor" non-null="true" unique="false"
     */
    public String getAnchor() {
        return this.anchor;
    }

    /** @ejb:persistent-field */
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getLocalPath() {
        return this.local_path;
    }

    /** @ejb:persistent-field */
    public void setLocalPath(String local_path) {
        this.local_path = local_path;
    }

    /**
     * @roller.wrapPojoMethod type="pojo"
     * @ejb:persistent-field
     * @hibernate.many-to-one column="categoryid" cascade="none" not-null="true"
     */
    public WeblogCategory getCategory() {
        return this.category;
    }

    /** @ejb:persistent-field */
    public List setCategory(List cats) {
        int i = 0;
        List<WeblogCategory> wCats = new ArrayList<WeblogCategory>();
        for (Iterator wbcItr = cats.iterator(); wbcItr.hasNext(); ) {
            String catName = cats.get(i).toString();
            WeblogCategory wCat = new WeblogCategory();
            wCat.setId(Integer.toString(i));
            wCat.setName(catName);
            wCat.setTitle(catName);
            wCat.setDescription(catName);
            wCat.setPath(catName);
            wCat.setImage(catName);
            wCats.add(wCat);
            //System.out.println("setCategory:"+catName);
            i++;
            if (i == cats.size())
                break;
        }
        //System.out.println("setCategory:out:"+wCats.get(1).getTitle());
        this.catSet = wCats;
        return wCats;
    }

    /**
     * Return collection of WeblogCategory objects of this entry.
     **/
    public List<WeblogCategory> getSetCategories() {
        return this.catSet;
    }

    /**
     * Return collection of setContent objects of this content
     * **/
    public void setContent(List cont) {
        int i = 0;
        List<String> wContent = new ArrayList<String>();
        for (Iterator wbcItr = cont.iterator(); wbcItr.hasNext(); ) {
            wContent.add(cont.get(i).toString());
            logger.log(BaseAPIHandler.LINFO, cont.get(i).toString());
            i++;
            if (i == cont.size())
                break;
        }
        this.content = wContent;
    }

    /**
     * Return collection of setContent objects of this entry.
     **/
    public List getContent() {
        return this.content;
    }

    /**
     * Return collection of WeblogCategory objects of this entry.
     * Added for symetry with PlanetEntryData object.
     * 
     * @roller.wrapPojoMethod type="pojo-collection" class="org.apache.roller.weblogger.pojos.WeblogCategory"
     */
    public List getCategories() {
        List cats = new ArrayList();
        cats.add(getCategory());
        return cats;
    }

    public void sCategories(List lCats) {
        this.cStr = lCats;
    }

    public List gCategories() {
        return this.cStr;
    }

    /**
     * @roller.wrapPojoMethod type="simple"
     * @ejb:persistent-field
     * @hibernate.property column="title" non-null="true" unique="false"
     */
    public String getTitle() {
        return this.title;
    }

    /** @ejb:persistent-field */
    public void setTitle(String title) {
        this.title = title;
    }

    public void setbTitle(byte[] title) {
        this.btitle = title;
    }

    public byte[] getbTitle() {
        return this.btitle;
    }

    public void setcTitle(char[] title) {
        this.ctitle = title;
    }

    public char[] getcTitle() {
        return this.ctitle;
    }

    public void setbText(byte[] title) {
        this.btext = title;
    }

    public byte[] getbText() {
        return this.btext;
    }
    public void setCustom ( List<LinkedHashMap> getSet) {
        this.customSet = new ArrayList<LinkedHashMap>(); 
        this.customSet = getSet;   
    };
    public List<LinkedHashMap> getCustom () {
        return this.customSet;   
    };
}
