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

/**
 *
 * @author viky
 * 
 * основная панель отображение выбранного элемента
 */


public class WidthLineItemEditor extends BasicComboBoxEditor
{
    private JPanel panel = new JPanel();
    private JLabel labelItem = new JLabel();
    private int selectedValue;
     
    public WidthLineItemEditor() 
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
     
    public Component getEditorComponent()
    {
        return this.panel;
    }
     
    public Object getItem() 
    {
        return this.selectedValue;
    }
     
    public void setItem(Object obj)
    {
        
        if (obj == null) 
        {
            return;
        }
        try
        {
       
        Object[] item = (Object[]) obj;
        selectedValue =(int) item[0];
        labelItem.setIcon(ImageIconURL.get((String)item[1]));
        }
        catch(Exception ex)                
        { 
            System.out.println("  setItem  *********"+obj.toString());
        }
    }  
}

