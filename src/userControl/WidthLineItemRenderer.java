/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author viky
 */
public class WidthLineItemRenderer extends JPanel implements ListCellRenderer
{

    private JLabel labelItem = new JLabel();
    private Color c;
    public String unit = "";

    public WidthLineItemRenderer()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(2, 2, 2, 2);
        c = this.getBackground();
        labelItem.setOpaque(true);
        labelItem.setHorizontalAlignment(JLabel.CENTER);

        add(labelItem, constraints);

        // setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public Component getListCellRendererComponent(JList jlist, Object e, int index, boolean isSelected, boolean cellHasFocus)
    {
        Object[] item = (Object[]) e;

        // set text
        if (this.unit != "")
        {
            int w = 0;
            w = (int) item[0];
            if (w < 10)
            {
                labelItem.setText(w + "  " + this.unit);
            } else
            {
                labelItem.setText(w + this.unit);
            }
        }

        
        if (isSelected)
        {
            labelItem.setIcon(ImageIconURL.get((String) item[2]));
            labelItem.setBackground(Color.GRAY);
            labelItem.setForeground(Color.WHITE);
        } else
        {
            labelItem.setIcon(ImageIconURL.get((String) item[1]));
            labelItem.setForeground(Color.BLACK);
            labelItem.setBackground(c);
        }

        return this;
    }

}
