/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screen_stream;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;

import java.io.DataOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import masterPanel.RecordInfo;

/**
 *
 * @author viky
 */
// определяет параметры картинки
// определяет индексы блоков с отличиями
// фомирование массива байт необходимых блоков
public class ScreenTiles
{

    
    public RecordInfo recordHead;
    
    private ScreenProperties ImageToSend;
    //индексы блоков для отправки
    ArrayList<Integer> blocks = new ArrayList<>();
    private Robot robot;
    private int startRow=0;
    private int stopRow=0;
    private byte typeSend=TypeImageSend.Fast;
    public int time=500;
    private static int DataMax=300000;
    private int rowsTime=0;
    
    private int getDiffereceDataSize()
    {
        return blocks.size()*ImageToSend.blockPixelHeight*ImageToSend.blockPixelWidth*2;
    }
    
    

    public ScreenTiles(RecordInfo r)
    {
        try
        {
            this.robot = new Robot();
        } catch (AWTException ex)
        {
            Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        ImageToSend = new ScreenProperties();
        // первый снимок экрана
        ImageToSend.newPictureBuffer = MakePrintScreen();
        ImageToSend.setDimentionOptimal(); 
        // истинные размеры (разрешение) экрана
        ImageToSend.setDimensionScreenNow(ImageToSend.newPictureBuffer.getWidth(), ImageToSend.newPictureBuffer.getHeight());
        ImageToSend.NewSize();
        this.recordHead=r;
    }

    /**
     * подготовка картинки снимок экрана и огрубление цвета
     *
     * @return
     */
    private BufferedImage MakePrintScreen()
    {
        Dimension D = Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage BI = robot.createScreenCapture(new Rectangle(0, 0, D.width, D.height));
        
        //*** Огрубление цвета
        WritableRaster WR = BI.getRaster();
        DataBuffer DB = WR.getDataBuffer();

        for (int i = 0; i < BI.getHeight(); i++)
        {
            for (int j = 0; j < BI.getWidth(); j++)
            {
                int value = DB.getElem(i * BI.getWidth() + j);
                value = value & 0x00F0F0F0;
                DB.setElem(i * BI.getWidth() + j, value);
            }
        }
        
       
        return BI;
    }

    /**
     * Подготовка данных для отправки
     *
     * @return массив байт(картинка или часть)
     */
    public byte[] PrScrToBytes()
    {
        BufferedImage BI=MakePrintScreen();
        ImageToSend.getOptimalImage(BI);
        getChanges();
        
       
      //  byteCompressorTEST();
        byte[] body;     
        int diff=getDiffereceDataSize();
       
        if(diff>DataMax )
        {
           ImageToSend.getFastlImage(BI);
           typeSend=TypeImageSend.Fast;
           body= gzip(this.byteCompressorFastView());  
           this.startRow=0;
           this.stopRow=0;
           rowsTime=0;
           time=200;
           System.out.println("    Diff  FAST"+ diff );
        } 
        else
        {
           
           if(rowsTime==0 || this.stopRow<ImageToSend.newPictureBuffer.getHeight()) 
           {
            
            typeSend=TypeImageSend.Row;
            body= gzip(this.byteCompressorRow());
            time=100;
            // System.out.println("    ROW "+ this.startRow);
           }
           else
           {
               rowsTime--;               
               typeSend=TypeImageSend.Difference;
               body= gzip(this.byteCompressor());  
               time=200;
            //   System.out.println("    Difference "+body.length);
           }
        }
        
        return body;
    }

    /**
     * сжатие данных для отправки
     *
     * @param body исходный массив байт
     * @return сжатый массив байт
     */
    byte[] gzip(byte[] body)
    {
        try
        {
            ByteArrayInputStream BAIS = new ByteArrayInputStream(body);
            ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
            try
            {
                byte[] buffer = new byte[32768];

                GZIPOutputStream gzos = new GZIPOutputStream(BAOS);

                int length;
                while ((length = BAIS.read(buffer)) > 0)
                {
                    gzos.write(buffer, 0, length);
                }
                gzos.finish();
            } catch (FileNotFoundException ex)
            {
                return null;

            } catch (IOException ex)
            {
                return null;
            }
            //сжатый массив
            byte[] bodyZip = BAOS.toByteArray();
            // размер массива
            byte[] head = this.getHead(bodyZip.length + 5);//int(4 byte)+byte

            BAOS.reset();
            //запись заголовка
            BAOS.write(head);
            //запись сжатого массива
            BAOS.write(bodyZip, 0, bodyZip.length);

            return BAOS.toByteArray();

        } catch (IOException ex)
        {
            return null;
        }
    }

    private void getChanges()
    {
       
        this.ImageToSend.CheckDimension();
        DataBuffer DB_Base = this.ImageToSend.DataBase();
        DataBuffer DB_New = this.ImageToSend.DataNew();
        int W=this.ImageToSend.dOptimal.width;
        
        this.blocks.clear();
        for (int block = 0; block < this.ImageToSend.getTotalCountOfBlocks(); block++)
        {
            //опредеяет переход на новую строку блока
            int blockFullLines = block / this.ImageToSend.widthCountOfBlocks;
            
            int blockInNotFullLine = block % this.ImageToSend.widthCountOfBlocks;
            
            int startByte = blockFullLines * this.ImageToSend.getPixelsInLine() + blockInNotFullLine * this.ImageToSend.blockPixelWidth;
            int EndByte = startByte + this.ImageToSend.blockPixelHeight * W - 1;
            
            for (int i = startByte; i < EndByte; i += W)
            {
                for (int j = 0; j < this.ImageToSend.blockPixelWidth; j++)
                {
                    int value_Base = DB_Base.getElem(i + j);
                    int value_New = DB_New.getElem(i + j);
                    if ((value_Base ^ value_New) != 0)
                    {                        
                        this.blocks.add(block);
                        i = EndByte;
                        j = this.ImageToSend.blockPixelWidth;
                    }

                }
              
            } 
        }
        
        this.ImageToSend.NextImage();
       
    }

    public byte[] byteCompressorRow()
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);

        DataBuffer DB_Row = this.ImageToSend.DataNew();
        try
        {

            int w = this.ImageToSend.newPictureBuffer.getWidth();
            int h = this.ImageToSend.newPictureBuffer.getHeight();
            
            
            DOS.writeInt(w);//4
            //System.out.println("    w=" + w);
            DOS.writeInt(h);//4
           // System.out.println("    h=" + h);
            
            
            this.stopRow=this.startRow+h/this.ImageToSend.countRow;
            DOS.writeInt(this.startRow);
           // System.out.println("  startRow   "+startRow);
            DOS.writeInt(this.stopRow);
            System.out.println("  stopRow   "+stopRow);
            for (int i = this.startRow; i < this.stopRow; i++)
            {
                for (int j = 0; j < w; j = j + 2)
                {
                    int value1 = DB_Row.getElem(i * w + j) & 0x00F0F0F0;                   
                    int value2 = DB_Row.getElem(i * w + j + 1) & 0x00F0F0F0;
                    int pixel = (value1 | ((value2 & 0x00FFFFFF) >> 4));
                    DOS.writeInt(pixel);
                }
            }
            
            this.ImageToSend.nextImage(startRow, stopRow);
            
            if(this.stopRow<h)
            {
                this.startRow=this.stopRow;
            }
            else
            {
                this.startRow=0;
                rowsTime=40;
            }

            DOS.flush();
            byte[] body= BAOS.toByteArray();
            BAOS.close();
            return body;
        } 
        catch (IOException ex)
        {
            return null;
        } 
        finally
        {
            try
            {
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    public byte[] byteCompressorFastView()
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        this.ImageToSend.testImage(this.ImageToSend.FastPictureBuffer,"Test_FAST"); 
        DataBuffer DB_small = this.ImageToSend.DataFast();
        try
        {

            int w = this.ImageToSend.dOptimal.width;
            int h = this.ImageToSend.dOptimal.height;
            
            
            DOS.writeInt(w);//4
           // System.out.println("    w=" + w);
            DOS.writeInt(h);//4
           // System.out.println("    h=" + h);
            
            
            int wFast=this.ImageToSend.FastPictureBuffer.getWidth();
            DOS.writeInt(wFast);
           // System.out.println("  wFast   "+wFast);
            int hFast=this.ImageToSend.FastPictureBuffer.getHeight();
            DOS.writeInt(hFast);
           // System.out.println("  hFast   "+hFast);
            for (int i = 0; i < hFast; i++)
            {
                for (int j = 0; j < wFast; j = j + 2)
                {
                    int value1 = DB_small.getElem(i * wFast + j) & 0x00F0F0F0;                   
                    int value2 = DB_small.getElem(i * wFast + j + 1) & 0x00F0F0F0;
                    int pixel = (value1 | ((value2 & 0x00FFFFFF) >> 4));
                    DOS.writeInt(pixel);
                }
            }
            
            DOS.flush();
            return BAOS.toByteArray();
        } catch (IOException ex)
        {
            return null;
        } 
        finally
        {
            try
            {
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public byte[] byteCompressorTEST()
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);

        DataBuffer DB_small = this.ImageToSend.DataNew();
        int rate=6;
        int w=this.ImageToSend.newPictureBuffer.getWidth();
        int h=this.ImageToSend.newPictureBuffer.getHeight();
        BufferedImage BI=new BufferedImage(w/rate,h/rate,BufferedImage.TYPE_INT_RGB);
        WritableRaster WR=BI.getRaster();
        DataBuffer DB=WR.getDataBuffer();
        
        try
        {
            for (int i = 0; i <BI.getHeight()*rate; i+=rate)
            {
                
                for (int j = 0; j < BI.getWidth()*rate; j+= rate)
                {
                  
                    { int [] p= new int [rate*rate];
                    for(int t=0; t<rate;t++)
                    {
                        for(int y=0; y<rate;y++)
                            p[t*rate + y]=DB_small.getElem((i+t) * w + (j+y)) & 0x00F0F0F0;
                    
                    }
                   
                    
                    int pixel=averagePixel(p, rate);
                    
                    if(i*w/(rate*rate)+ j/rate<DB.getSize())
                    {
                        DB.setElem(i*w/(rate*rate)+ j/rate,pixel);
                        DOS.writeInt(pixel);
                    }
                    }
                    
                }
            }
            
            DOS.flush();
            byte [] b=BAOS.toByteArray();
            this.ImageToSend.testImage(BI,"Test_BI"); 
            System.out.println("    ##########"+b.length);
            
            BufferedImage BI_2=new BufferedImage(w/rate,h/rate,BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = BI_2.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //Has worked best in my case
            graphics2D.drawImage(this.ImageToSend.newPictureBuffer, 0, 0, BI_2.getWidth(), BI_2.getHeight(), null);
            
            this.ImageToSend.testImage(BI,"Test_BI_222"); 
            graphics2D.dispose();
            
            
            return b;
        } catch (IOException ex)
        {
            return null;
        } 
        finally
        {
            try
            {
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private int averagePixel( int [] pixel, int rate)
    {
        int r=0;
        int g=0;
        int b=0;
        int count=rate*rate;
     for(int p: pixel)
        {            
            r+=((p & 0x00F00000)>>16);           
           
            g+=((p & 0x0000F000)>>8);            
           
            b+=(p & 0x000000F0);        
            
        }
        
       r=(r/count);
        
        g=(g/count);
       
        b=(b/count);
        return new Color(r,g,b).getRGB();
    
    }
    
    
    

    /**
     * Запись заголовка байт массива,не сжимается
     *
     * @param size размер сжатого массива
     * @return заголовок
     */
    public byte[] getHead(int size)
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        try
        {
            DOS.write(this.recordHead.getHeadImagerSender());
            DOS.writeInt(size);
            DOS.writeByte(this.typeSend);
            return BAOS.toByteArray();
        }
        catch (IOException ex)
        {
            return null;
        } finally
        {
            try
            {
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public byte[] byteCompressor() 
    {
        
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);

        try
        {

           /// getChanges();

            DOS.writeByte(this.ImageToSend.blockPixelWidth); //System.out.println("blockPixelWidth "+this.ImageToSend.blockPixelWidth);

            //высота блока в пикселях
            DOS.writeByte(this.ImageToSend.blockPixelHeight);//System.out.println("blockPixelHeight "+this.ImageToSend.blockPixelHeight);

            //количество блоков по ширине
            DOS.writeInt(this.ImageToSend.widthCountOfBlocks);//System.out.println("widthCountOfBlocks "+this.ImageToSend.widthCountOfBlocks);

            //количество блоков по высоте
            DOS.writeInt(this.ImageToSend.heightCountOfBlocks);//System.out.println("heightCountOfBlocks "+this.ImageToSend.heightCountOfBlocks);

            DOS.writeInt(blocks.size()); // System.out.println("blocks.size() "+blocks.size()); 

            DataBuffer DB_New = this.ImageToSend.DataNew();
            

            for (int b = 0; b < blocks.size(); b++)
            {
                DOS.writeInt(blocks.get(b));

                int blockFullLines = blocks.get(b) / this.ImageToSend.widthCountOfBlocks;
                int blockInNotFullLine = blocks.get(b) % this.ImageToSend.widthCountOfBlocks;
                int startByte = blockFullLines * this.ImageToSend.getPixelsInLine() + blockInNotFullLine * this.ImageToSend.blockPixelWidth;
                int endByte = startByte + this.ImageToSend.blockPixelHeight * this.ImageToSend.Width - 1;
                for (int i = startByte; i < endByte; i += this.ImageToSend.Width)
                {
                    for (int j = 0; j < this.ImageToSend.blockPixelWidth - 1; j = j + 2)
                    {
                        int value1 = DB_New.getElem(i + j) & 0x00F0F0F0;
                        int value2 = DB_New.getElem(i + j + 1) & 0x00F0F0F0;
                        int pixel = (value1 | ((value2 & 0x00FFFFFF) >> 4));
                        DOS.writeInt(pixel);

                    }
                }
            }
            return BAOS.toByteArray();
        } catch (IOException ex)
        {
            return null;
        } finally
        {
            try
            {
                DOS.close();
            } 
            catch (IOException ex)
            {
                Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
