/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer.event_package;

import java.io.DataInputStream;

/**
 *
 * @author viky
 */
public interface IUnpack
{

    public void unpackImg(DataInputStream DIS, byte type);
    
}
