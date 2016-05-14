/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

import java.io.DataInputStream;
import java.io.EOFException;

import java.io.IOException;
import masterPanel.ReportException;


/**
 *
 * @author viky
 */
class StudentPane extends Component
{
    public BufferedImage BI;    
    private String ip="";
    private boolean isSelected;
    private Dimension DImg;
  
    Dimension PaneSize=new Dimension(250,250);
      
    public StudentPane()
    {
       this.DImg= new Dimension(PaneSize.width-10,PaneSize.height-20);
       this.BI= new BufferedImage(this.DImg.width,this.DImg.height, BufferedImage.TYPE_INT_RGB);
       this.isSelected= false;
       this.setPreferredSize(this.PaneSize);
      
    }
    
    public void setSelected( boolean s)
    {
        isSelected=s;
    }
    
    public boolean getSelect()
    {return this.isSelected;}
    
    @Override
    public void paint(Graphics g)
    { 
      
        if(this.isSelected)
            g.setColor(new Color (155,155,200));
        else
            g.setColor(new Color (105,105,180));
        
        g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(),5,5);   
        
        if(this.BI== null)return;
        Point start= new Point((this.getWidth()-this.BI.getWidth())/2, (this.getHeight()-this.BI.getHeight())/2-20 );
        g.drawImage(this.BI, start.x, start.y,this.BI.getWidth(),this.BI.getHeight(), this);
        
       
        g.setColor(Color.WHITE);
        Font F= new Font("Arial", Font.PLAIN,20);
        g.setFont(F);
        drawCenter(this.getLabelIp(),this.getWidth(),this.getHeight(),g);
        g.dispose();
    
    }
    
    public void drawCenter(String s, int w, int h, Graphics g)
    {
        FontMetrics fm= g.getFontMetrics();
        
        int x = (w-fm.stringWidth(s))/2;
       
        int y =h-fm.getAscent()/2;
        g.drawString(s, x, y);
    }
    
    public  void UnPackPreview( DataInputStream DIS ) 
    {
         
        try
        {
            int w=DIS.readInt();
            int h=DIS.readInt();
            synchronized(this.BI)
            {
            if(this.BI.getWidth()!=w || this.BI.getHeight()!=h)
            {
                this.BI= new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            }            
            WritableRaster WR_Small = this.BI.getRaster();      
            DataBuffer DB_small = WR_Small.getDataBuffer();
            for (int i = 0; i <this.BI.getHeight(); i++)
            { 
                for (int j = 0; j < this.BI.getWidth(); j=j+2)
                {
                    int value=DIS.readInt();
                    
                    int value1=value&0x00F0F0F0;
                    int value2=(value&0x000F0F0F)<<4;
                    DB_small.setElem(i*this.BI.getWidth()+j, value1) ;
                    DB_small.setElem(i*this.BI.getWidth()+j+1,value2) ;
                }
            }
             
            this.repaint();
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
            ReportException.write("StudentPane.UnPackPrewiew()"+ex.getMessage());
        }
        
   
    }
    
    /**
     * распаковка картинки
     * @param DIS 
     */
    public  void UnPackImage( DataInputStream DIS ) 
    {
     try{
        int blockPixelWidth = DIS.readByte()&0xFF;//System.out.println("blockPixelWidth "+blockPixelWidth);   
        int blockPixelHeight = DIS.readByte()&0xFF;// System.out.println("blockPixelHeight "+blockPixelHeight);
        int widthCountOfBlocks = DIS.readInt();// System.out.println("widthCountOfBlocks "+widthCountOfBlocks);
        int heightCountOfBlocks = DIS.readInt();//System.out.println("heightCountOfBlocks "+heightCountOfBlocks);

        int blocks_size = DIS.readInt(); // System.out.println("blocks_size "+blocks_size);

        int pixelInBlock = blockPixelHeight * blockPixelWidth;
        int pixelInLine = pixelInBlock * widthCountOfBlocks;
        int widthResolution= blockPixelWidth* widthCountOfBlocks;
        int heightResolution = blockPixelHeight * heightCountOfBlocks;
        this.DImg.setSize(new Dimension(widthResolution,heightResolution));
        
        synchronized(this.BI)
        {
         if(this.BI==null || this.BI.getWidth()!=widthResolution ||this.BI.getHeight()!=heightResolution)
         { 
             this.BI=new BufferedImage(widthResolution, heightResolution, BufferedImage.TYPE_INT_RGB); 
         }

         WritableRaster WR_Base = this.BI.getRaster();
         DataBuffer DB_Base = WR_Base.getDataBuffer(); 

            for (int b = 0; b < blocks_size; b++)
            {
                int indexBlock =DIS.readInt();       
                int blockFullLines = indexBlock / widthCountOfBlocks;
                int blockInNotFullLine = indexBlock % widthCountOfBlocks;
                int startByte = blockFullLines * pixelInLine + blockInNotFullLine * blockPixelWidth;
                int endByte = startByte + blockPixelHeight * widthResolution - 1;

                for (int i = startByte; i < endByte; i += widthResolution )
                {
                    for (int j = 0; j < blockPixelWidth-1; j=j+2)
                    {                                       
                        try
                        {   

                            int value=DIS.readInt();                              
                            int value1=value&0x00F0F0F0;
                            int value2=(value&0x000F0F0F)<<4;

                            DB_Base.setElem(i+j, value1) ;
                            DB_Base.setElem(i+j+1,value2) ;
                        }
                        catch  (EOFException ex)
                        {
                            System.out.println(ex.getMessage()+" DIS.read()" );
                            ReportException.write("StudentPane.UnPackImage() EOFException\t"+ex.getMessage());
                        }                                                
                    }
                }

            }
        }
    }
    catch(Exception ex)
    {
        ReportException.write("StudentPane.UnPackImage()  Exception\t"+ex.getMessage());
    }

       
    }

    /**
     * @return the ip
     */
    public String getLabelIp()
    {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setLabelIp(String ip)
    {
        this.ip = ip;
    }
    
}
