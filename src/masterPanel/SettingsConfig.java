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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
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
    public static boolean isCanVideo;
    private boolean isDefine;
    
    //  максимально допустимый размер пакета для передаче по UDP 
    public static int DataMaxUDP=20000 ;
    // public static int DataMaxUDP=70000 ;
    // максимально допустимый размер пакета для передаче по UDP
    public static int DataMaxUDP_OK;
    
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
     * broadcast UDP
     */
    public static InetAddress IP_UDP;   
    private InetAddress ip;
  
    /**
     * порт прослушки маяков студентов
     */
    public static int  PORT_UDP; 
    private int port_Base;
    /**
     * порт вещания доски
     */
    public static int  PORT_UDP_BOARD;
    private final int DELTA_UDP_BOARD=1;
    /**
     * порт TCP соединения для получения картинки
     */
    public static int  PORT_TCP_IMG;
    private final int DELTA_TCP_IMG=2;
    /**
     * порт TCP соединения для отправки команды
     */
    public static int  PORT_TCP_COMMAND;
    private  final int DELTA_TCP_COMMAND=3;
    
    /**
     * порт для отправки экрана преподавателя по UDP
     */ 
    public static int  PORT_TCP_ScStr;
    private final int DELTA_TCP_ScStr=4;
    
    
    
    //</editor-fold>  
    
    InputStream IS;
    /**
     * адресс компьютера
     */
  //  public InetAddress IP;
    
    
    
    Document doc;
    
    public  SettingsConfig()
    {
        isValid=isLoadStyle();
        isCanVideo=isStreamScreen();
        isFirst=isFirst();
    }
    
    public  void setCongig ()
    {       
       setAllPorts(); 
       //если порт менялся, то изменения сохраняться
       if(this.port_Base!=PORT_UDP || this.ip!=IP_UDP)
       { 
            Element p_udp = (Element)doc.getElementsByTagName("PORT_UDP").item(0);
            p_udp.setTextContent(""+PORT_UDP);
            Element ip_udp = (Element)doc.getElementsByTagName("IP_UDP").item(0); 
            ip_udp.setTextContent(IP_UDP.getHostAddress());
            saveDoc();
       }
    }
    
    
    
    public boolean isFirst()
    {
        try 
        {
            if(!SingleInst.lock())
            {
                ReportException.write("The program is already running");
              //  System.exit(0);
                return false;
            }
        } 
        catch (IOException ex)
        {
            ReportException.write("  " + ex.getMessage());
        }
        return true;
    }
    
    public boolean isStreamScreen()
    {
        DatagramSocket DS = null;
        DatagramPacket DP;
        int size=0;
        try {
            
            DS = new DatagramSocket();
            // если установлена проверка, определяем размер пакета
            if(this.isDefine)
            {
                for (int i = 512; i < DataMaxUDP; i=i*2)
                {
                    size=i;
                    byte[] test = new byte[i];
                    DP = new DatagramPacket(
                        test, test.length, this.IP_UDP, PORT_TCP_ScStr);
                    DS.send(DP);
                    Thread.sleep(1);
                }
                
            }
            else
            {
                byte[] test = new byte[DataMaxUDP_OK];
                DP = new DatagramPacket(
                        test, test.length, this.IP_UDP, PORT_TCP_ScStr);
                DS.send(DP);
            }
        }
        catch (SocketException ex)
        {
            ReportException.write("DataMaxUDP_OK : error send "+DataMaxUDP_OK);
            if(this.isDefine)
            {
                DataMaxUDP_OK=size/2;
                Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex); 
                Element sizeUDP = (Element)doc.getElementsByTagName("SizeMaxUDP").item(0);            
                sizeUDP.setTextContent(String.valueOf(size));
                saveDoc();
            }
            
        } 
        catch (IOException ex)
        {
            ReportException.write("DataMaxUDP_OK : error IOException "+ex.getMessage());
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);            
        } 
        catch (InterruptedException ex) 
        {
            ReportException.write("DataMaxUDP_OK : error InterruptedException "+ex.getMessage());
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            DS.close();        
        }
        
        return ! (DataMaxUDP_OK<DataMaxUDP); 
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
            
            
            Element ip_udp = (Element)doc.getElementsByTagName("IP_UDP").item(0); 
            this.IP_UDP=InetAddress.getByName(ip_udp.getTextContent().trim());
            this.ip=this.IP_UDP;
            
            Element isDef=(Element)doc.getElementsByTagName("Define").item(0);
            this.isDefine=Boolean.parseBoolean(isDef.getTextContent());
            
            Element sizeUDP=(Element)doc.getElementsByTagName("SizeMaxUDP").item(0);
            this.DataMaxUDP_OK=Integer.parseInt(sizeUDP.getTextContent()); 
            
            Element p_udp = (Element)doc.getElementsByTagName("PORT_UDP").item(0); 
            this.PORT_UDP=Integer.parseInt(p_udp.getTextContent());
            port_Base=this.PORT_UDP;
            setAllPorts();
            return true;
        }
        catch (ParserConfigurationException ex)
        { 
            ReportException.write("ParserConfigurationException "+ex.getMessage());
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
            ReportException.write("SAXException "+ex.getMessage());
        }
        catch (IOException ex)
        {
            ReportException.write("IOException "+ex.getMessage());
            Logger.getLogger(SettingsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private void  setAllPorts()
    {
        PORT_UDP_BOARD=PORT_UDP+DELTA_UDP_BOARD;            
        PORT_TCP_IMG=PORT_UDP+DELTA_TCP_IMG;
        PORT_TCP_COMMAND=PORT_UDP+DELTA_TCP_COMMAND;
        PORT_TCP_ScStr=PORT_UDP+DELTA_TCP_ScStr;
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
