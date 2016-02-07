/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import teacher_teamviewer.event_package.IEventAddStudent;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import masterPanel.ReportException;
import teacher_teamviewer.event_package.EventSetIsFullScreen;
import teacher_teamviewer.event_package.IEventRemoveStudent;
import teacher_teamviewer.event_package.IFullScreen;
import teacher_teamviewer.event_package.IRepaint;
import userControl.ImageIconURL;

/**
 *
 * @author viky
 */

public class MasterTeamViewer extends JPanel {

    private ArrayList<Student> students = new ArrayList<Student>();

    private ConnectionManagerLeader connector;
    private final UDP_Listener finder;
    private JScrollPane scrollPane;
    private JPanel PreviewPane = new JPanel();
    private Student MaxSizeStudent;
    
    public byte regimeCurrent;
    public boolean isPreview;
    public boolean haveControl;

    public EventSetIsFullScreen ESFS = new EventSetIsFullScreen();
    
    public TV_ToolsPanel TP;
    

    private Timer TimerCheck = new Timer();
    private TimerTask TaskCheck = new TimerTask() {

        @Override
        public void run() {

            /*
            for (Student S : MasterTeamViewer.this.students)
            {
                //  if(!S.isConnect)
                System.out.println(i + " " + S.toString());
            }*/

           // System.out.println("Applicants.size()***" + MasterTeamViewer.this.connector.Applicants.size());
            while (!MasterTeamViewer.this.connector.Applicants.isEmpty()) {
            //    System.out.println("TimerTask");
                MasterTeamViewer.this.addStudent(MasterTeamViewer.this.connector.Applicants.remove(0));

            }

        }
    };

    IEventAddStudent addSt = new IEventAddStudent() {

        public void addNewStudent(final Student sNew) {
            //Если студента нет в основном списке
            if (!students.contains(sNew)) 
            {
              //  System.out.println("try add");
                MasterTeamViewer.this.connector.addItem(sNew);
               // sNew.startSenderMessage();
            } 
            else
            {
                System.out.println("-----------" + sNew.getIP() + " exists");
            }
        }

    };

    IEventRemoveStudent removeSt = new IEventRemoveStudent() {
        public void removeStudent(String ip) 
        {
           // System.out.println("removeStudent");
            Student s = MasterTeamViewer.this.getStudentByIP(ip);

            if (s == null) {
              //  System.out.println(ip + " not found");
                return;
            }
           // System.out.println("Find :" + s);

            MasterTeamViewer.this.PreviewPane.remove(s.SP);
            if(s.equals(MaxSizeStudent))
            {
                unSelect();
            }
            students.remove(s);
            
            MasterTeamViewer.this.PreviewPane.validate();
            MasterTeamViewer.this.PreviewPane.repaint();

            if (!MasterTeamViewer.this.isPreview && MasterTeamViewer.this.MaxSizeStudent.RecieverPrScr.messageTypeView == Student.FULL) {
                MasterTeamViewer.this.MaxSizeStudent.CS.regimeCurrent = TV_ToolsPanel.EXIT;
            }

        }
    };

    IRepaint repaintSelected = new IRepaint() {
        @Override
        public void repaintImg() {
            MasterTeamViewer.this.scrollPane.validate();
            MasterTeamViewer.this.scrollPane.repaint();
        }

    };

    private class MyDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == 27) {
                   
                    IFullScreen IFS = (IFullScreen) ESFS.getListener();
                    IFS.setIsFullScreen(false);
                }
            }

            return false;
        }
    }

    public MasterTeamViewer(Dimension d)
    {
        

        this.TP = new TV_ToolsPanel(d);
        
        this.TP.FullScreen.addActionListener(new ActionListener()
        {
            private  ImageIcon maximizeOn= ImageIconURL.get("resources/fullscreeen_on_45.png") ;
            private  ImageIcon maximizeOff= ImageIconURL.get("resources/fullscreeen_off_45.png") ;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!MasterTeamViewer.this.TP.FullScreen.isSelected())
                {
                
                    System.out.println("11");
                     //IFullScreen IFS = (IFullScreen) ESFS.getListener();
                    // IFS.setIsFullScreen(false);
                }
                else
                {
                    System.out.println("22");
                //IFullScreen IFS = (IFullScreen) ESFS.getListener();
               // IFS.setIsFullScreen(true);
              // MasterTeamViewer.this.higthTools = MasterTeamViewer.this.TP.Tools.getHeight();
              // MasterTeamViewer.this.TP.Tools.setSize(MasterTeamViewer.this.TP.Tools.getWidth(), 0);
             //   MasterTeamViewer.this.MaxSizeStudent.setRegimeView(RegimeView.FULLSCREEN);
                
                
                }

               // IFullScreen IFS = (IFullScreen) ESFS.getListener();
              //  IFS.setIsFullScreen(true);
              //  MasterTeamViewer.this.higthTools = MasterTeamViewer.this.TP.Tools.getHeight();
              //  MasterTeamViewer.this.TP.Tools.setSize(MasterTeamViewer.this.TP.Tools.getWidth(), 0);
                // Master.this.MaxSizeStudent.setRegimeView(RegimeView.FULLSCREEN);
                
                
                MasterTeamViewer.this.MaxSizeStudent.CS.requestFocus();
            }
            
            
        });
        
        this.TP.b_exit.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae)
            {  
                MasterTeamViewer.this.unSelect();
            }
        });

         this.TP.b_scale.addActionListener
        ( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if(!MasterTeamViewer.this.TP.b_scale.isSelected())
                {
                MasterTeamViewer.this.TP.b_scale.setToolTipText("Экран вписан в окно приложения");
                    
                MasterTeamViewer.this.MaxSizeStudent.setRegimeView(TV_ToolsPanel.INSCRIBED);

                MasterTeamViewer.this.MaxSizeStudent.CS.FrameSize();

                MasterTeamViewer.this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                }
                else
                {
                    MasterTeamViewer.this.TP.b_scale.setToolTipText("Экран в исходном размере");
                MasterTeamViewer.this.MaxSizeStudent.setRegimeView(TV_ToolsPanel.USERSIZE);
                MasterTeamViewer.this.MaxSizeStudent.CS.UserSize();
                MasterTeamViewer.this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
               
                
                }
                MasterTeamViewer.this.MaxSizeStudent.CS.requestFocus();
            }
        });
        
        this.TP.b_control.addActionListener( new ActionListener(){
             
            @Override
            public void actionPerformed(ActionEvent ae)
            {
               MasterTeamViewer.this.MaxSizeStudent.CS.isControl=!MasterTeamViewer.this.MaxSizeStudent.CS.isControl;
               if(MasterTeamViewer.this.MaxSizeStudent.CS.isControl)
                { 
                    MasterTeamViewer.this.TP.b_control.setToolTipText(" Отключить управление ");
                }
               else
                {
                   MasterTeamViewer.this.TP.b_control.setToolTipText(" Включить управление ");
                }
                MasterTeamViewer.this.MaxSizeStudent.CS.requestFocus();
            }
                });
        this.setSize(d);
        this.setLayout(new BorderLayout());

        this.scrollPane = new JScrollPane(this.PreviewPane);

        this.add(scrollPane, BorderLayout.CENTER);
        this.PreviewPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.PreviewPane.setPreferredSize(StudentPane.PaneSize);
        this.PreviewPane.setBackground(Color.LIGHT_GRAY);

        this.finder = new UDP_Listener();
        this.finder.EL.addStudentEventAdd(addSt);
        this.isPreview = true;

        this.TP.setVisible(!this.isPreview);

        this.TimerCheck.schedule(TaskCheck, 1000, 500);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MasterTeamViewer.MyDispatcher());

        // диспетчер подключений (первый в очереди соединяется с сервером)
        this.connector = new ConnectionManagerLeader();

    }
    
    public void connector_finder_Start()
    {
        this.finder.start();
        this.connector.start();
    }

    private Student getStudentByIP(String ip) {
        for (Student s : students) {
            if (s.getStudentByIP(ip) != null) 
            { 
                return s;
            }
        }
        return null;

    }

    public void Invalidate() {
        this.PreviewPane.validate();
        this.PreviewPane.repaint();

    }

    private void setSelectedStudent(Student s) {
        try {

            this.isPreview = false;
            this.TP.setStudentName(s.getIP());
            this.TP.setVisible(!this.isPreview);
            this.regimeCurrent = TV_ToolsPanel.USERSIZE;
            setTimeForeach(12000, false);
            this.MaxSizeStudent = s;            
            this.MaxSizeStudent.RecieverPrScr.setTime(150);
            this.MaxSizeStudent.RecieverPrScr.setStatus(true);
            this.MaxSizeStudent.SP.setSelected(!this.isPreview);
            this.MaxSizeStudent.RecieverPrScr.messageTypeView = Student.FULL;
            Thread.sleep(1);
            this.scrollPane.setViewportView(s.CS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MasterTeamViewer.class.getName()).log(Level.SEVERE, null, ex);
            ReportException.write(ex.getMessage());
        }
    }
    
    // задаем время запроса экрана для всех студентов
    private void setTimeForeach(int mls, boolean status)
    {
        for (Student s : students)
        {
          //  s.RecieverPrScr.setTime(mls);
            s.RecieverPrScr.setStatus(status);
        }
    
    }

    public void unSelect()
    {
        try {
            
            this.isPreview = true;
             this.TP.setVisible(!this.isPreview);
            if(this.MaxSizeStudent!=null)
            {
                this.MaxSizeStudent.SP.setSelected(!isPreview);
                this.MaxSizeStudent.CS.isControl=false;           
                this.MaxSizeStudent.RecieverPrScr.setTime(1000);
                setTimeForeach(1000, true);
                this.MaxSizeStudent.RecieverPrScr.messageTypeView = Student.PREVIEW;
                this.TP.b_control.setSelected(false);
            }
            this.scrollPane.setViewportView(this.PreviewPane);
            Thread.sleep(1);
            this.Invalidate();
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(MasterTeamViewer.class.getName()).log(Level.SEVERE, null, ex);
            ReportException.write(ex.getMessage());
        }
    }

    private void addStudent(final Student s) 
    {

        s.SP.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {

                MasterTeamViewer.this.setSelectedStudent(s);
                for (Student st : students) {
                    if (!s.equals(st)) {
                        st.SP.setSelected(false);
                    }
                }

                MasterTeamViewer.this.Invalidate();

            }

        }
        );
        s.RecieverPrScr.ERS.removeStudentEventAdd(removeSt);
        s.RecieverPrScr.start();
        s.RecieverPrScr.setStatus(true);

        boolean isAdd = false;
        for (int i = 0; i < students.size(); i++) {

            if (s.compareTo(students.get(i)) < 0) {
                this.students.add(i, s);
                isAdd = true;
                break;
            }
        }

        if (isAdd) {
            this.PreviewPane.removeAll();
            for (Student tmp : students) {
                this.PreviewPane.add(tmp.SP);
            }

        } else {
            students.add(s);
            this.PreviewPane.add(s.SP);

        }

        this.PreviewPane.validate();
        this.PreviewPane.repaint();
    }
/*
    private void removeStudent(String ip)
    {
      //  System.out.println("removeStudent");

        Student s = this.getStudentByIP(ip);

        if (s == null) {
            System.out.println(ip + " not found");
            return;
        }
        System.out.println("Find :" + s);

        this.PreviewPane.remove(s.SP);
        students.remove(s);
        this.PreviewPane.validate();
        this.PreviewPane.repaint();

        if (!this.isPreview && this.MaxSizeStudent.RecieverPrScr.msg == Student.FULL) {
            this.MaxSizeStudent.CS.regimeCurrent = RegimeView.EXIT;
        }

    }
*/
    public ImageIcon getImageIcon(String resImgName) {

        java.net.URL imgUrl = MasterTeamViewer.class.getResource(resImgName);
        if (imgUrl == null) {
            return null;
        }
        return new ImageIcon(imgUrl);

    }


}
