/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author viky
 */
public class WidthLineComboBox extends JComboBox 
{
    private DefaultComboBoxModel model;
    private LineParam_ItemRenderer WLIR;
    private LineParam_ItemEditor WLIE;
     
    public WidthLineComboBox() 
    {
        model = new DefaultComboBoxModel();
        this.setToolTipText("Толщина линии");
        this.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH, SizeSketch.CONTROL_HEIGHT));
        setModel(model);
        WLIR = new LineParam_ItemRenderer();
        WLIR.unit=" px";
        setRenderer(WLIR);
        WLIE=new LineParam_ItemEditor();
        setEditor(WLIE);
        
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
