/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import masterPanel.ReportException;

/**
 *
 * @author viky
 * 
 * основная панель отображение выбранного элемента
 */


public class LineParam_ItemEditor extends BasicComboBoxEditor
{
    private JPanel panel = new JPanel();
    private JLabel labelItem = new JLabel();
    private int selectedValue;
     
    public LineParam_ItemEditor() 
    {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(2, 5, 2, 2);
         
        labelItem.setOpaque(false);
        labelItem.setHorizontalAlignment(JLabel.LEFT);         
        panel.add(labelItem, constraints);    
    }
     
    @Override
    public Component getEditorComponent()
    {
        return this.panel;
    }
     
    @Override
    public Object getItem() 
    {
        return this.selectedValue;
    }
     
    @Override
    public void setItem(Object obj)
    {
        
        if (obj == null) 
        {
            return;
        }
        try
        {
       
        Object[] item = (Object[]) obj;
       
        selectedValue = Integer.parseInt(item[0].toString());
        labelItem.setIcon(ImageIconURL.get((String)item[1]));
        }
        catch(Exception ex)                
        { 
            ReportException.write(this.toString()+"\t"+ex.getMessage() ); 
        }
    }  
}

