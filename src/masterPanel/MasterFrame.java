/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterPanel;

//import com.sun.awt.AWTUtilities;
import myjavadesc.events.IChModeType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import myjavadesc.MasterBoard;
import myjavadesc.events.IStart;

import teacher_teamviewer.MasterTeamViewer;
import teacher_teamviewer.event_package.IFullScreen;
import userControl.ModeChanger;
import screen_stream.Thread_SenderImage;
import userControl.ImageIconURL;
import userControl.ModeType;

/**
 *
 * @author viky
 *
 * private variables default variables protected variables public variables
 */
public class MasterFrame extends JFrame
{

    private MasterTeamViewer TV;
    private MasterBoard Desc;
    private ModeChanger Modes;
    private Thread_SenderImage SenderImage;
    private Dimension StartDim = new Dimension(805, 705);

    private RecordInfo recordHead;
    private SettingsConfig SC;

    private int PrevX, PrevY, PrevWidth, PrevHeight;

    private IFullScreen FS = new IFullScreen()
    {
        @Override
        public void setIsFullScreen(boolean isFullScreen)
        {
            if (isFullScreen == true)
            {
                MasterFrame.this.saveSize();

                MasterFrame.this.dispose(); //Destroys the whole JFrame but keeps organized every Component                               
                //Needed if you want to use Undecorated JFrame
                //dispose() is the reason that this trick doesn't work with videos
                MasterFrame.this.setUndecorated(true);
                MasterFrame.this.setBounds(0, 0, getToolkit().getScreenSize().width, getToolkit().getScreenSize().height);
                MasterFrame.this.setVisible(true);
            } else
            {

                MasterFrame.this.setVisible(true);
                setBounds(PrevX, PrevY, PrevWidth, PrevHeight);
                MasterFrame.this.setPreferredSize(new Dimension(PrevWidth, PrevHeight));
                MasterFrame.this.dispose();

                MasterFrame.this.setUndecorated(false);
                MasterFrame.this.setVisible(true);
                //  System.out.println("full screen  H= "+Frame_TeamViewer.this.getWidth() + "    H ="+ Frame_TeamViewer.this.getHeight()); 
            }

        }
    };

    /**
     * изменение режимов
     */
    IChModeType IMdType = new IChModeType()
    {

        @Override
        public void setModeType(int modeT)
        {
            switch (modeT)
            {
                case ModeType.viewer:
                    MasterFrame.this.setContentPane(TV);
                    MasterFrame.this.Desc.setVisibleCanvas(false);
                    MasterFrame.this.getContentPane().validate();
                    break;

                case ModeType.board:

                    if (MasterFrame.this.Desc.fileCurrentBoard != null)
                    {
                        MasterFrame.this.Desc.setVisibleCanvas(true);
                    }

                    MasterFrame.this.setContentPane(Desc);
                    MasterFrame.this.TV.unSelect();
                    MasterFrame.this.TV.TP.closeMode();

                    break;
            }

            MasterFrame.this.getContentPane().validate();
            MasterFrame.this.setSize(MasterFrame.this.getWidth() - 1, MasterFrame.this.getHeight() - 1);
            MasterFrame.this.setSize(MasterFrame.this.getWidth() + 1, MasterFrame.this.getHeight() + 1);
        }

    };
    
    /**
     * запуск потоков поиска студентов
     */
    IStart startConnection = new IStart()
    {
        @Override
        public void startConnection()
        {
            SC.setCongig();
            connectionStart();             
        }
    };

    class myComponentListener implements ComponentListener
    {

        @Override
        public void componentResized(ComponentEvent e)
        {
            MasterFrame.this.Modes.setNewLocation(new Dimension(MasterFrame.this.getWidth(), MasterFrame.this.getHeight()));
            MasterFrame.this.TV.TP.setNewLocation(new Dimension(MasterFrame.this.getWidth(), MasterFrame.this.getHeight()));
            JLayeredPane lp = getLayeredPane();
            lp.invalidate();
        }

        @Override
        public void componentMoved(ComponentEvent e)
        {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void componentShown(ComponentEvent e)
        {
            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void componentHidden(ComponentEvent e)
        {
            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public MasterFrame()
    {
        SC = new SettingsConfig();
        if (!SC.isValid)
        {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке файла конфигурации (Settings.xml)");
            System.exit(0);
        }

        if (!SC.isFirst)
        {
            JOptionPane.showMessageDialog(this, "Допускается запуск только одной копии");
            System.exit(0);
        }            

        this.setIconImage(ImageIconURL.get("resources/board45.png").getImage());
        this.TV = new MasterTeamViewer(this.StartDim);

        this.recordHead = new RecordInfo();

        this.Modes = new ModeChanger(this.StartDim);
        this.Desc = new MasterBoard(SC, this.recordHead, this.Modes.Record);
        this.Desc.ES.StartAdd(startConnection);

        if (SettingsConfig.isCanVideo)
        {
            this.SenderImage = new Thread_SenderImage(this.recordHead);
            this.Modes.setActionListenerSendScreen(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JToggleButton btn = (JToggleButton) e.getSource();
                    MasterFrame.this.Desc.setTimeOn(!btn.isSelected());
                    MasterFrame.this.SenderImage.setStatus(btn.isSelected());
                    MasterFrame.this.recordHead.setIsImageSender(btn.isSelected());
                }
            });
        } else
        {
            this.Modes.SendScreen.setEnabled(SC.isCanVideo);
            this.Modes.SendScreen.setToolTipText("Опция недоступна.");
        }

        this.Modes.setActionListenerRecord(
                new ActionListener()
                {

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        JToggleButton b = (JToggleButton) e.getSource();
                        MasterFrame.this.recordHead.setIsRecord(b.isSelected());
                    }
                }
        );

        this.addComponentListener(new myComponentListener());
        this.Modes.EMT.ModeTypeChangedAdd(IMdType);

        JLayeredPane lp = getLayeredPane();
        lp.add(this.Modes, JLayeredPane.POPUP_LAYER);
        lp.add(this.TV.TP, JLayeredPane.POPUP_LAYER);
        this.setSize(this.StartDim);
        this.setMinimumSize(new Dimension(300, 300));
        this.TV.TP.setVisible(false);
        this.TV.ESFS.addEventFullScreeen(FS);

        this.setContentPane(this.Desc);
        this.setTitle("Academical Board_Java(преподаватель) ");
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent we)
            {
                MasterFrame.this.Desc.saveCurrentBoard();
                System.exit(0);
            }
        });
        this.setVisible(true);

    }

    private void connectionStart()
    {
        // запуск потоков  прослушки маяков студентов и соединения 
        this.TV.connector_finder_Start();
        this.Modes.startConnection();
        this.Desc.setEnableAll();
        if (SettingsConfig.isCanVideo)
        {
            this.SenderImage.start();
        }

    }

    public void saveSize()
    {
        PrevX = MasterFrame.this.getX();
        PrevY = MasterFrame.this.getY();
        PrevWidth = MasterFrame.this.getWidth();
        PrevHeight = MasterFrame.this.getHeight();

    }

}
