/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author Viky
 */
public class StartEndLine_ComboBox extends JComboBox 
{
    private DefaultComboBoxModel model;
    private LineParam_ItemEditor WLIE;
    
    public  StartEndLine_ComboBox ( String toolTipText)
    {
        model = new DefaultComboBoxModel();
        this.setToolTipText(toolTipText);
        this.setPreferredSize(new Dimension(50,20));//SizeSketch.LABEL_WIDTH, SizeSketch.CONTROL_HEIGHT));
        setModel(model);
        setRenderer(new LineParam_ItemRenderer());        
        WLIE=new LineParam_ItemEditor();
        setEditor(WLIE);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
     
    /**
     * Add an array items to this combo box.
     * Each item is an array of two String elements:
     * - first element is quantity px.
     * - second element is path of an image file for icon.
     * @param items
     */
    public void addItems(Object[][] items) {
        for (Object[] anItem : items) {
            model.addElement(anItem);
        }
    }
    
    public int getSelectedValue()
    {
        return  (int) WLIE.getItem();
    }
    
}
