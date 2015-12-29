/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionListener;


/**
 *
 * @author Viky_Pa
 * 
 * панель редактирования графических объектов
 * 
 * cобъект создается в MasterBoard
 * размещается на ToolsPanel
 * 
 */
public class EditGraphTools extends JPanel
{
    
    public JList shapesList;
    public DefaultListModel dlm;
    private Dimension btnSize30 = new Dimension(30, 30);
    private JButton btnClear;
    private JButton btnRemove;
    private JButton btnCopyPast;
    
    
    
    public EditGraphTools()
    {
        JPanel p= new JPanel();
        this.setLayout(new BorderLayout());
        this.add(p, BorderLayout.NORTH);
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.dlm =new DefaultListModel();
        this.shapesList = new JList(dlm);
        
        this.shapesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        this.btnCopyPast = new JButton(ImageIconURL.get("resources/copypaste24.png"));
        this.btnCopyPast.setPreferredSize(btnSize30);
        this.setButtonPaintOff(this.btnCopyPast);
        this.btnCopyPast.setToolTipText("Копия выбранного объекта");
        p.add(btnCopyPast);
       
        this.btnRemove = new JButton(ImageIconURL.get("resources/delete24.png"));
        this.btnRemove.setPreferredSize(btnSize30);
        this.btnRemove.setToolTipText("Удаление выбранного объекта");
        this.setButtonPaintOff(this.btnRemove);
        p.add(btnRemove);

        this.btnClear = new JButton(ImageIconURL.get("resources/cleaner24.png"));
        this.btnClear.setPreferredSize(btnSize30);
        this.btnClear.setToolTipText("Удаление всех объектов");
        this.setButtonPaintOff(this.btnClear);
        
        p.add(btnClear);

    }
    
    /**
     * Добавляем функционал нажания кнопки
     * удаление всех графических объектов
     * @param AL обработчик события ( создается в MyJavaCanvas)
     */
    public void ActionListenerClear(ActionListener AL)
    {
    this.btnClear.addActionListener(AL);
    }
    
     /**
     * Добавляем функционал нажания кнопки
     * удаление выбранного графического объекта
     * @param AL обработчик события ( создается в MyJavaCanvas)
     */
    public void ActionListenerButtonDelete(ActionListener AL)
    {
    this.btnRemove.addActionListener(AL);
    }
    
     /**
     * Добавляем функционал нажания кнопки
     * копирование и вставка выбранного графического объекта
     * @param AL обработчик события ( создается в MyJavaCanvas)
     */
    public void ActionListenerButtonAdd(ActionListener AL)
    {
    this.btnCopyPast.addActionListener(AL);
    }
    
    
    /**
     * Добавляем функционал выбора объекта в списке
     * удаление или редактирование  выбранного графического объекта
     * @param AL обработчик события ( создается в MyJavaCanvas)
     */
    public void ActionListenerListDelete(ListSelectionListener LSL)
    {
        this.shapesList.addListSelectionListener(LSL);
    }
    
    
    /**
     * задаем размеры панели и списка фигур
     * @param w ширина панели
     * @param h высота панели
     */
    public void setSizePanel( int w, int h)
    {
        this.setPreferredSize(new Dimension(w+10, h)); 
        
        JScrollPane scroll = new JScrollPane(shapesList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroll,BorderLayout.CENTER);
    }
    
    /**
     * Отключение прорисовки рамки, фона 
     * @param TB 
     */
    private void setButtonPaintOff(JButton B)
    {
        B.setFocusPainted(false);
        B.setBorderPainted(false);
        B.setContentAreaFilled(false);    
    }
     
    public void isSelectedOne(boolean flag)
    {
        this.btnCopyPast.setEnabled(flag);
        this.btnRemove.setEnabled(flag);    
    }
    
    public void isCount(boolean flag)
    {
    this.btnClear.setEnabled(flag);
    }
    
}
