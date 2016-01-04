/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterPanel;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Viky
 */
public class RecordInfo 
{
    private boolean isRecord=false;
    public boolean isIsRecord() 
    {
        return isRecord;
    }

    public void setIsRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }
    private String nameGroup="";
    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }
    
    private boolean isImageSender=false;

    public boolean isIsImageSender()
    {
        return isImageSender;
    }

    public void setIsImageSender(boolean isImageSender)
    {
        this.isImageSender = isImageSender;
    }
    
    public byte[] getHeadDesc()
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        try 
        {
           //Доска записывается при включенной записи и выключенном видео
            DOS.writeByte(((isRecord && isImageSender)^isRecord) ? (byte) 1 : (byte) 0);
            DOS.writeByte(this.nameGroup.length());
            DOS.writeChars(this.nameGroup);
            try
            {  
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(RecordInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            
        } catch (IOException ex)
        {
            Logger.getLogger(RecordInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
     return BAOS.toByteArray();
    }
    
    public byte[] getHeadImagerSender()
    {
       ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        try 
        {
           //Видео записывается при включенной записи и включенном видео
            DOS.writeByte(((isRecord && isImageSender)) ? (byte) 1 : (byte) 0);
            DOS.writeByte(this.nameGroup.length());
            DOS.writeChars(this.nameGroup);
            try
            {  
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(RecordInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            
        } catch (IOException ex)
        {
            Logger.getLogger(RecordInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
     return BAOS.toByteArray();
    
    }
}
