package com.pikachugo.RestManager.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ResizeImageUtil {
    private static Logger log = Logger.getLogger(ResizeImageUtil.class);
    
    public static File resizeFile(File temp, int maxSize, int width, int height, String toogle) throws IOException {
        File fileReturn = temp;
        String stringParam = "maxSize:1023,width:1400,height:1795,toogle:ON";
        log.debug("authString" + stringParam);
        String[] paramString = stringParam.split(",");
        log.debug("paramString" + paramString);
        String[] paramStringMaxSize = paramString[0].split(":");
        String[] paramStringWidth = paramString[1].split(":");
        String[] paramStringHeight = paramString[2].split(":");
        String[] paramStringToogle = paramString[3].split(":");
        log.debug("paramStringTemp 0" + paramStringMaxSize[1]);
        log.debug("paramStringTemp 1" + paramStringWidth[1]);
        log.debug("paramStringTemp 2" + paramStringHeight[1]);
        log.debug("paramStringTemp 3" + paramStringToogle[1]);
        if (maxSize == 0 || "".equals(maxSize)) {
            maxSize = Integer.parseInt(paramStringMaxSize[1]);
            log.debug("maxSize " + maxSize);
        }
        if (width == 0 || "".equals(width)) {
            width = Integer.parseInt(paramStringWidth[1]);
            log.debug("width " + width);
        }
        if (height == 0 || "".equals(height)) {
            height = Integer.parseInt(paramStringHeight[1]);
            log.debug("height" + height);
        }
        if (toogle == null || "".equals(toogle) || "null".equals(toogle)) {
            toogle = paramStringToogle[1];
            log.debug("toogle" + toogle);
        }
        
        long fileSizeInBytes = temp.length();
    	long fileSizeInKB = fileSizeInBytes / 1024L;
    	long fileSizeInMB = fileSizeInKB / 1024L;
    	
        log.debug("fileSizeInKB aco" + fileSizeInKB);
        log.debug("fileSizeInMB aco" + fileSizeInMB);
        log.debug("temp name" + temp.getAbsolutePath().toString());
        if (fileSizeInKB > maxSize && "ON".equalsIgnoreCase(toogle)) {
            log.debug("masuk resize");
            BufferedImage imgResize = ImageIO.read(temp);
            int type = (imgResize.getType() == 0) ? 2 : imgResize.getType();
            log.debug("type" + type);
            log.debug("INT_ARGB2");
            BufferedImage resizeImageJpg = resizeImage(imgResize, type, width, height);
            ImageIO.write(resizeImageJpg, "jpg", new File(temp.getAbsolutePath().toString()));
            return fileReturn;
        }
        return fileReturn;
    }
    
    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
}
