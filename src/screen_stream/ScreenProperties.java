/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screen_stream;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author viky
 */
public class ScreenProperties
{

    //ширина и высота блока в пикселях
    public int blockPixelWidth, blockPixelHeight;
    //количество блоков по ширине и высоте
    public int widthCountOfBlocks, heightCountOfBlocks;
    
    //максимальный объем пакета, который после сжатия можно переслать по UDP
    public static int DataMax=300000;
    public static int DataMaxUDP=40000;

    // максимальная  разница между истинной высотой экрана и
    //высотой кратной количеству строк
    byte precision = 4;

    // стартовая высота строки
    byte heightRow = 111;

    // стартовое количество строк
    public byte countRow = 10;

    // разрешение экрана
    //ширина
    public int Width;
    //высота
    public int Height;

    // снимок экрана для сранения
    public BufferedImage basePictureBuffer;
    // получение обновленного снимка экрана
    public BufferedImage newPictureBuffer;
    
    // получение быстрого снимка экрана
    public BufferedImage FastPictureBuffer;
    
    
    //истинное  разрешение
    private Dimension screenNow;
    
    public void setDimensionScreenNow(int w, int h)
    {
        this.screenNow= new Dimension(w,h);    
        setFastDimension();
    }

    /**
     * оптимальные для передачи размеры снимка экрана высота кратна количеству
     * строк ширина четная размер объема строки после сжатия не превышает 25kB
     * ограние UDP
     */
    public Dimension dOptimal;

    /**
     * новая картинка
     *
     * @return
     */
    public DataBuffer DataNew()
    {
        WritableRaster WR_New = this.newPictureBuffer.getRaster();
        return WR_New.getDataBuffer();
    }

    /**
     * картинка для сравнения
     *
     * @return
     */
    public DataBuffer DataBase()
    {
        WritableRaster WR_Base = this.basePictureBuffer.getRaster();
        return WR_Base.getDataBuffer();
    }
    
    /**
     * картинка для быстрой (первичной отправки)
     *
     * @return
     */
    public DataBuffer DataFast()
    {
        WritableRaster WR_Base = this.FastPictureBuffer.getRaster();
        return WR_Base.getDataBuffer();
    }

    // общее количество блоков
    public int getTotalCountOfBlocks()
    {       
        return widthCountOfBlocks * heightCountOfBlocks;
    }

    //количество pixels  в блоке
    public int getPixelsInBlock()
    {
        return blockPixelHeight * blockPixelWidth;
    }

    //количество pixel в строке блокa
    public int getPixelsInLine()
    {
        return blockPixelHeight * blockPixelWidth * widthCountOfBlocks;
    }

    //определение количества блоков по оси х  и по оси у 
    //в зависимости от размеров экрана
    private void getBlocksCount()
    {
        if (basePictureBuffer == null)
        {
            System.out.println("basePictureBuffer== null");
            return;
        }
        this.Width = this.basePictureBuffer.getWidth(); //System.out.println("this.widthResolution = "+this.widthResolution); 
        this.Height = this.basePictureBuffer.getHeight();// System.out.println(" this.heightResolution = "+ this.heightResolution); 

        int cnt = 48;
        do
        {
            if ((double) ((double) this.Width / (double) cnt) % 1 == 0)
            {
                widthCountOfBlocks = cnt;
                break;
            }
            cnt++;
        } while (true);
        cnt = 48;
        do
        {
            if ((double) ((double) this.Height / (double) cnt) % 1 == 0)
            {
                heightCountOfBlocks = cnt;
                break;
            }
            cnt++;
        } while (true);

        blockPixelWidth = this.Width / widthCountOfBlocks;
        // System.out.println("blockPixelWidth "+ blockPixelWidth); 
        //  System.out.println(" widthCountOfBlocks "+  widthCountOfBlocks);   
        blockPixelHeight = this.Height / heightCountOfBlocks;
        // System.out.println("blockPixelHeight"+ blockPixelHeight);   

    }

    // размер изображения 
    public void NewSize()
    {
        Dimension D = this.dOptimal;
        this.basePictureBuffer = new BufferedImage(D.width, D.height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster WR_Base = this.basePictureBuffer.getRaster();
        DataBuffer DB_Base = WR_Base.getDataBuffer();
        for (int i = 0; i < this.basePictureBuffer.getHeight(); i++)
        {
            for (int j = 0; j < this.basePictureBuffer.getWidth(); j++)
            {
                DB_Base.setElem(i * this.basePictureBuffer.getWidth() + j, 0x00FF00FF);
            }
        }
        getBlocksCount();
        System.out.println(" Exit new size  W=" + D.width + "  H =" + D.height);

    }

    // новый кадр 
    public void NextImage()
    {
        
        this.basePictureBuffer = this.newPictureBuffer;
    }

    public void nextImage(int start, int stop)
    {
        DataBuffer DB_Base = this.DataBase();
        DataBuffer DB_New= this.DataNew();
        int index = 0;
        for (int i = start; i < stop; i++)
        {
            for (int j = 0; j < this.basePictureBuffer.getWidth(); j++)
            {
                index = i * this.basePictureBuffer.getWidth() + j;
                DB_Base.setElem(index, DB_New.getElem(index));
            }
        }
       
    }

    //определение размеров панели предпросмотра
    //заданы в файле конфигурации
    public void setDimentionOptimal()
    {
        int h = setOptimalValue(this.newPictureBuffer.getHeight());
        int w = this.newPictureBuffer.getWidth() * h / this.newPictureBuffer.getHeight();
        if ((w % 2) != 0)
        {
            w = w + 1;
        }
        this.dOptimal = new Dimension(w, h);
        this.newPictureBuffer = new BufferedImage(this.dOptimal.width, this.dOptimal.height, BufferedImage.TYPE_INT_ARGB);

    }
    
    

    /**
     * опредиеление оптимальных параметров изображения
     *
     */
    private int setOptimalValue( int h)
    {
       
        byte countRow_tmp = 0;
        while (
                (Math.abs(h - (heightRow * countRow_tmp)) > precision)
               ||               
                (heightRow*this.newPictureBuffer.getWidth()*Integer.BYTES/2>DataMax)
                )
        { 
            heightRow--;
            countRow_tmp = (byte) (h / heightRow);        
        }
        this.countRow = countRow_tmp;
        /*
        System.out.println("   *************"+(heightRow*this.newPictureBuffer.getWidth()*Integer.BYTES/2>DataMax));
        System.out.println("    row  "+ this.countRow);
        System.out.println("   DataMax " +heightRow*this.newPictureBuffer.getWidth()*Integer.BYTES/2);
        System.out.println("    H  " +( h + (heightRow * countRow_tmp - h)));*/
        
        //оптимальная для передачи высота снимка экрана
        return h + (heightRow * countRow_tmp - h);
    }
    
    private void setFastDimension()
    {
        int w=this.newPictureBuffer.getWidth();
      // System.out.println("    w==="+w);
        int h=this.newPictureBuffer.getHeight();
       // System.out.println("    h==="+h);
        float ratio=(float) w/h;
        int data=w*h*2;
       // System.out.println("    ratio==="+ratio);
        for(int i=1;data>DataMax;i++)
        {
            h=this.screenNow.height/i;
            w=(int)(h*ratio); 
            data=w*h*2;
        }
       
        if ((w % 2) != 0)
        {
            w = w + 1;
        }      
        this.FastPictureBuffer= new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }
    
    //масштабирование экрана для предпросмота
    public void getFastlImage(BufferedImage BI)
    {
        Graphics2D graphics2D = this.FastPictureBuffer.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //Has worked best in my case
        graphics2D.drawImage(BI, 0, 0, this.FastPictureBuffer.getWidth(), this.FastPictureBuffer.getHeight(), null);
        graphics2D.dispose();
    }

    //масштабирование экрана для предпросмота
    public void getOptimalImage(BufferedImage BI)
    {
        this.newPictureBuffer= new BufferedImage(this.newPictureBuffer.getWidth(), this.newPictureBuffer.getHeight(),BufferedImage.TYPE_INT_RGB);        
        Graphics2D graphics2D = this.newPictureBuffer.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //Has worked best in my case
        graphics2D.drawImage(BI, 0, 0, this.newPictureBuffer.getWidth(), this.newPictureBuffer.getHeight(), null);
        graphics2D.dispose();

    }

    //проверка на необходимость нового разбиения экрана на блоки
    //при изменении расширения
    public void CheckDimension()
    {  
        //изменилось разрешение экрана
        if (this.basePictureBuffer.getHeight() != this.newPictureBuffer.getHeight()
                || this.basePictureBuffer.getWidth() != this.newPictureBuffer.getWidth())
        {
            System.out.println("Error  getChanges()");
            NewSize();
            getBlocksCount();
        }
    }

    public void testImage(BufferedImage BI, String name)
    {
        try
        {
            File F = new File(name + ".png");
            ImageIO.write(BI, "PNG", F);

        } catch (IOException ex)
        {
            Logger.getLogger(ScreenTiles.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
