package com.nnetworks.xmlrpc;

import com.nnetworks.jopa.JOPAException;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import redstone.xmlrpc.XmlRpcDispatcher;

public class FileManagerAPI extends BaseAPIHandler {

    private static Logger logger = 
        Logger.getLogger(XmlRpcDispatcher.class.getName());

    /**
     * @see org.apache.roller.weblogger.model.FileManager#saveFile(weblog, java.lang.String, java.lang.String, long, java.io.InputStream)
     */
    public static Hashtable saveFile(Weblog weblog, String path, String contentType, 
                                int size, 
                                InputStream is,String fileLink) throws FileNotFoundException, 
                                                       JOPAException {

        return saveFile(weblog, path, contentType, size, is, true,fileLink);
    }

    /**
     * @see org.apache.roller.weblogger.model.FileManager#saveFile(weblog, java.lang.String, java.lang.String, long, java.io.InputStream)
     */
    public static Hashtable saveFile(Weblog weblog, String path, String contentType, long size, 
                  InputStream is, 
                  boolean checkCanSave,String fileLink) throws FileNotFoundException, 
                                               JOPAException {
        Hashtable FileStruct = new Hashtable(1);
        FileStruct.put("link",fileLink);
        String savePath = path;
        if (path.startsWith("/")) {
            savePath = path.substring(1);
        }

       // System.out.println(size);
       // create File that we are about to save
        File saveFile = new File(dirPath + File.separator + savePath);

        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        OutputStream bos = null;
        try {
            bos = new FileOutputStream(saveFile);
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            
            FileStruct.put("path",saveFile.getAbsolutePath());
            FileStruct.put("size",size);
            int width  =0;
            int height =0;
            try{
            BufferedImage bimg = ImageIO.read(new File(saveFile.getAbsolutePath()));
              width          = bimg.getWidth();
              height         = bimg.getHeight();
             FileStruct.put("width",width);
             FileStruct.put("height",height);
              }
            catch(Exception i){}
            
               
            logger.log(LINFO, 
                       "The file has been written to [" + saveFile.getAbsolutePath() + 
                       "]" +
                       "\n width :" + width+
                       "\n height:" + height);

            
        } catch (Exception e) {
            throw new JOPAException("ERROR uploading file:" + e);
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (Exception ignored) {
            }
        }
       return FileStruct;
       

    }


}

