package teacher_teamviewer;


import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import masterPanel.ReportException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author viky
 */
public class printScreen
{
    Robot R;

    public printScreen()
    {
        try
        {
            this.R = new Robot();
            Dimension D =Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage BI=R.createScreenCapture(new Rectangle(0,0,D.width,D.height));
            ImageIO.write(BI, "JPG", new File("newPictureBuffer.jpg"));
        }
        catch (AWTException ex)
        {
             ReportException.write("printScreen()  AWTException\t"+ex.getMessage());
            Logger.getLogger(printScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(printScreen.class.getName()).log(Level.SEVERE, null, ex);
            ReportException.write("printScreen()  IOException\t"+ex.getMessage());
        }
    }
    
}
