package masterPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Viky_Pa
 */
public class SettingsConfig
{
    public boolean isValid;
    public boolean isFirst;
    public Color Background;
    public Color Foreground;
    public Color LineColor;
    public Color FillColor;
    
    public float thicknessLine;
    public byte typeLine=0;
    public int fontSize;
    private final int MIN_FONTSIZE=16;
    
    
    //<editor-fold defaultstate="collapsed" desc=" IP, ports ">
  
    /**
     * порт прослушки маяков студентов
     */
    public int  PORT_UDP; 
    /**
     * порт вещания доски
     */
    public int  PORT_UDP_BOARD;
    private final int DELTA_UDP_BOARD=1;
    /**
     * порт TCP соединения для получения картинки
     */
    public int  PORT_TCP_IMG;
    private final int DELTA_TCP_IMG=2;
    /**
     * порт TCP соединения для отправки команды
     */
    public int  PORT_TCP_COMMAND;
    private final int DELTA_TCP_COMMAND=3;
    /**
     * порт для отправки экрана преподавателя по UDP
     */
    public int  PORT_TCP_ScStr;
    private final int DELTA_TCP_ScStr=4;
    //</editor-fold>    
    InputStream IS;
    /**
     * адресс компьютера
     */
    public InetAddress IP;
    /**
     * broadcast UDP
     */
    public InetAddress IP_UDP;  
  
    
    Document doc;
    
    public  SettingsConfig()
    {
        isValid=isLoadStyle();
        isFirst=isFirst();
    }
    
    public boolean isFirst()
    {
        boolean flag=false;
         try 
         { 
             ServerSocket s=null;
             try
             {
                 s= new ServerSocket(PORT_TCP_ScStr);
                 flag=true;    
             } 
             catch (Exception se)
             {
                 ReportException.write("Sender_UDP.Send(..)!!!!!!!!!!!!" + se.getMessage());
             }
            if(flag)
                 System.out.println("   " +flag);  // s.close();
             
         } 
         catch (Exception ex)//(IOException ex)
        {
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);  
        }
        return flag;
    }
    
    /**
     * считывание данных из файла
     * @return 
     */
    private boolean isLoadStyle()
    { 
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.doc = builder.parse(new File("Settings.xml"));
             
            Element back = (Element)doc.getElementsByTagName("Background").item(0); 
            this.Background=new Color(Integer.parseInt(back.getTextContent(),16));
            Element fore = (Element)doc.getElementsByTagName("Foreground").item(0);
            this.Foreground=new Color(Integer.parseInt(fore.getTextContent(),16));
            
            Element line = (Element)doc.getElementsByTagName("LineColor").item(0); 
            this.LineColor=new Color(Integer.parseInt(line.getTextContent(),16));
            Element fill = (Element)doc.getElementsByTagName("FillColor").item(0);
            this.FillColor=new Color(Integer.parseInt(fill.getTextContent(),16));
            
            Element thickness = (Element)doc.getElementsByTagName("Thickness").item(0);
            this.thicknessLine=Float.parseFloat(thickness.getTextContent());
            
            Element dash = (Element)doc.getElementsByTagName("Dash").item(0);
            this.typeLine=Byte.parseByte(dash.getTextContent());
            
            Element fSize = (Element)doc.getElementsByTagName("FontSize").item(0);
            this.fontSize=Integer.parseInt(fSize.getTextContent());    
            this.fontSize=(this.fontSize==0)?this.fontSize=MIN_FONTSIZE:this.fontSize;
            
            Element ip = (Element)doc.getElementsByTagName("IP").item(0); 
            this.IP=InetAddress.getByName(ip.getTextContent().trim());
            
            
            Element ip_udp = (Element)doc.getElementsByTagName("IP_UDP").item(0); 
            this.IP_UDP=InetAddress.getByName(ip_udp.getTextContent().trim());
          
           
            //System.out.println("        IP "+ InetAddress.);
             // уточняем IP
            if(!this.IP.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress()))
            { 
                this.IP=InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
                
                byte[] mask=this.IP_UDP.getAddress();
                byte [] newUDP=this.IP.getAddress();
                for(int i=0; i<mask.length;i++)
                { 
                   // System.out.println(" " +(mask[i]&0x000000FF));
                    if (((mask[i]&0x000000FF)^0xFF)==0)
                    {
                       
                        newUDP[i]=(byte) 0xFF;
                    }                
                }
               this.IP_UDP=InetAddress.getByAddress(newUDP);
              // System.out.println(" this.IP_UDP   "+this.IP_UDP.getHostName());
               ip.setTextContent(this.IP.getHostAddress());
               ip_udp.setTextContent(this.IP_UDP.getHostAddress());
               saveDoc();
            }
            
            
            
            Element p_udp = (Element)doc.getElementsByTagName("PORT_UDP").item(0); 
            this.PORT_UDP=Integer.parseInt(p_udp.getTextContent());
            
            this.PORT_UDP_BOARD=this.PORT_UDP+DELTA_UDP_BOARD;            
            this.PORT_TCP_IMG=this.PORT_UDP+DELTA_TCP_IMG;
            this.PORT_TCP_COMMAND=this.PORT_UDP+DELTA_TCP_COMMAND;
            this.PORT_TCP_ScStr=this.PORT_UDP+DELTA_TCP_ScStr;
           
            return true;
        }
        catch (ParserConfigurationException ex)
        {
            
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public void saveIP()
    {
    
    }
    
    public void saveSettingsThemes(Color f, Color b, int fSize)
    {
        Element back = (Element)doc.getElementsByTagName("Background").item(0);            
        back.setTextContent(String.format( "%02X%02X%02X", b.getRed(), b.getGreen(), b.getBlue() ));
        Element fore = (Element)doc.getElementsByTagName("Foreground").item(0); 
        fore.setTextContent(String.format( "%02X%02X%02X", f.getRed(), f.getGreen(), f.getBlue() ));

        Element fS = (Element)doc.getElementsByTagName("FontSize").item(0); 
        fS.setTextContent(String.valueOf(fSize));            
        saveDoc();
        
    }
    
    public void saveColorsDraw(Color l, Color f)
    {
       
        Element back = (Element)doc.getElementsByTagName("LineColor").item(0);            
        back.setTextContent(String.format( "%02X%02X%02X", l.getRed(), l.getGreen(), l.getBlue() ));
        Element fore = (Element)doc.getElementsByTagName("FillColor").item(0); 
        fore.setTextContent(String.format( "%02X%02X%02X", f.getRed(), f.getGreen(), f.getBlue() ));
        saveDoc();
    }
    
    public void saveLineSetting(float w, int type)
    {
       
        Element thickness = (Element)doc.getElementsByTagName("Thickness").item(0);            
        thickness.setTextContent(String.valueOf(w));
        Element dash = (Element)doc.getElementsByTagName("Dash").item(0);
        dash.setTextContent(String.valueOf(type));
        saveDoc();
        
    }
    
    
    public void saveDoc()
    {
        try {
            Source domSource = new DOMSource(this.doc);
            Result fileResult = new StreamResult(new File("Settings.xml"));
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(domSource, fileResult);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
}
