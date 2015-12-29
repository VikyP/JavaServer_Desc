/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer.event_package;

import java.io.DataInputStream;
import java.util.ArrayList;

/**
 *
 * @author viky
 */


public class EventUnpack
{
    private  ArrayList listeners = new ArrayList();
    public EventUnpack()
    {}
    
    public void addEventUnpack (IUnpack l)
     {

        if (!listeners.contains(l))
            listeners.add (l);

     }

    // метод удаляющий из очереди подписчиков объект-слушатель
    public void removeEventUnpack (IUnpack l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    public Object  getListener()
    {
        return listeners.iterator().next();
    
    }
    
}
