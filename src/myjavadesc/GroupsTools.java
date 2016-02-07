/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import masterPanel.SettingsConfig;
import userControl.ImageIconURL;
import userControl.SizeSketch;

/**
 *
 * @author Viky_Pa
 */
//создается при запуске приложения
//определяет перечень групп в папке приложения по ключу
//добавляет новую группу(если ее нет в списке)
//возвращет каталог выбранной группы
//
public class GroupsTools {

    File rootAppFile;
    public static String groupKey = "group_";
    
    DefaultComboBoxModel cbm;    
    private JComboBox Groups;
    private JTextField tb= new JTextField();
    private JTextField ip_Broadcast;
    private JTextField port_Broadcast;
    private final JButton addGroup;
    
    // панель всегда доступна
    public JPanel groupPanel;
    //панель может быть скрыта
    public JPanel groupPanelHide;
    
    public GroupsTools()
    {
        File F = new File("");
        rootAppFile = new File(F.getAbsolutePath());
        this.cbm = new DefaultComboBoxModel();        
        File[] listF = rootAppFile.listFiles();
        // заполнение списка групп
        if (listF.length != 0) {
            for (File f : listF) {
                if (f.getName().startsWith(groupKey))
                {
                    //выделяем имя файла без ключевого слова
                    this.cbm.addElement(f.getName().substring(groupKey.length()));
                }
            }

        }
        
        //выпадающий список групп
        this.Groups = new JComboBox(cbm);
        this.Groups.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH,SizeSketch.CONTROL_HEIGHT));
        this.Groups.setSelectedIndex(-1);
        
        // текстовое поле добавления новой группы
        this.tb= new JTextField();
        tb.setEditable(true);
        this.tb.setPreferredSize( new Dimension(SizeSketch.TEXTBOX_WIDTH,SizeSketch.CONTROL_HEIGHT));
        
        
        //кнопка добавления новой группы
        this.addGroup= new JButton(ImageIconURL.get("resources/New20.png"));        
        this.addGroup.setFocusPainted(false);
        this.addGroup.setBorderPainted(false);
        this.addGroup.setContentAreaFilled(false);
        this.addGroup.setPressedIcon(ImageIconURL.get("resources/New20_press.png"));
        this.addGroup.setPreferredSize(new Dimension(SizeSketch.BUTTON_WIDTH,SizeSketch.CONTROL_HEIGHT));
        
        this.addGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e)
            {  
                String nameNewGroup=GroupsTools.this.tb.getText();
                String dirName = GroupsTools.this.groupKey + nameNewGroup;
                File newGroup = new File(dirName);
               
              if(nameNewGroup.trim().length()!=0 && !newGroup.exists())  
              {
                    newGroup.mkdir();
                    GroupsTools.this.cbm.addElement(newGroup.getName().substring(groupKey.length()));
              }              
              GroupsTools.this.tb.setText("");
            }
        });
        this.addGroup.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
       
        groupPanel= new JPanel(new FlowLayout(FlowLayout.CENTER));           
        this.groupPanel.add(this.Groups);
        
        groupPanelHide= new JPanel();
        GroupLayout layout = new GroupLayout(groupPanelHide);
        groupPanelHide.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        
        JLabel labelGr = new JLabel("Новая группа");
        JLabel labelIP = new JLabel("Broadcast");
        JLabel labelPort = new JLabel("Порт");
        
        this.ip_Broadcast= new JTextField(SettingsConfig.IP_UDP.getHostName());
        this.ip_Broadcast.setPreferredSize( new Dimension(SizeSketch.TEXTBOX_WIDTH,SizeSketch.CONTROL_HEIGHT));
        this.ip_Broadcast.addFocusListener(new FocusListener()
        {

            @Override
            public void focusGained(FocusEvent e)
            {
               // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                JTextField tf=(JTextField)e.getSource();
                try
                {
                    InetAddress  ip_broadcast =InetAddress.getByName(tf.getText());                    
                    InetAddress[] ip_=InetAddress.getAllByName(InetAddress.getLocalHost().getHostAddress());
                    for(InetAddress i:ip_)
                    {
                        boolean f=true;
                        for (int j = 0; j < 4; j++)
                        {
                            f=((ip_broadcast.getAddress()[j]&0xFF)==(i.getAddress()[j]&0xFF) ||(ip_broadcast.getAddress()[j]&0xFF)==255 );
                            if(!f) break;
                        }
                        if(f)
                        {
                            SettingsConfig.IP_UDP= ip_broadcast;
                            return;
                        }
                    } 
                    JOptionPane.showMessageDialog(groupPanelHide, "IP указан неверно." + ip_broadcast.getHostAddress() );
                    tf.setText(""+SettingsConfig.IP_UDP.getHostAddress());
                } 
                catch (UnknownHostException ex)
                {
                    tf.setText(""+SettingsConfig.IP_UDP.getHostAddress());
                    JOptionPane.showMessageDialog(groupPanelHide, "IP указан неверно");
                }
               
            }
        });
        
        this.port_Broadcast= new JTextField(""+SettingsConfig.PORT_UDP);
        this.port_Broadcast.setPreferredSize( new Dimension(SizeSketch.BUTTON_WIDTH,SizeSketch.CONTROL_HEIGHT));
       
        this.port_Broadcast.addFocusListener( new  FocusListener()
        {

            @Override
            public void focusGained(FocusEvent e)
            {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                JTextField tf=(JTextField)e.getSource();
                try
                {
                    int  port =Integer.parseInt(tf.getText());
                    SettingsConfig.PORT_UDP= port<4000?SettingsConfig.PORT_UDP:port;
                }
                catch(NumberFormatException exc)
                {
                    tf.setText(""+SettingsConfig.PORT_UDP);
                    JOptionPane.showMessageDialog(groupPanelHide, "Порт указан неверно");
                }
                
            }
        });
        
        labelGr.setPreferredSize(new Dimension(SizeSketch.TEXTBOX_WIDTH, SizeSketch.CONTROL_HEIGHT));
        layout.setAutoCreateGaps(false);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING) 
            .addComponent(labelGr)
            .addGroup(layout.createSequentialGroup()
                    .addComponent(this.tb)
                    .addComponent(this.addGroup))  
                
            .addGroup(layout.createParallelGroup(LEADING) 
                .addGroup(layout.createSequentialGroup() 
                .addGroup(layout.createParallelGroup(LEADING) 
                        .addComponent(labelIP)
                        .addComponent(this.ip_Broadcast)) 
                .addGroup(layout.createParallelGroup(LEADING) 
                       .addComponent(labelPort)
                        .addComponent(port_Broadcast)
                ))) 
            );
        layout.linkSize(SwingConstants.HORIZONTAL, this.tb, this.ip_Broadcast);
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(labelGr)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING) 
                    .addComponent(this.tb)
                    .addComponent(this.addGroup))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE) 
                    .addComponent(labelIP)
                    .addComponent(labelPort))
                    
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE) 
                    .addComponent(ip_Broadcast) 
                    .addComponent(port_Broadcast))
                ))
                
        );
          
    }
   
    
    public void setActionGroups(ActionListener AL)
    {
        this.Groups.addActionListener(AL);   
       
    }
    
    public void setNotEditable()
    {
        if(this.ip_Broadcast.isEditable())
        {
        this.ip_Broadcast.setEditable(false);
        this.port_Broadcast.setEditable(false);
        }
    
    }
    
    
}
