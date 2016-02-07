/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.events;

import java.util.ArrayList;

/**
 *
 * @author Viky
 */
public class EventStart
{
   private  ArrayList listeners = new ArrayList();
     
    public void StartAdd (IStart l)
    {
        
        if (!listeners.contains(l))
            listeners.add (l);        
    }

    // метод удаляющий из очереди подписчиков объект-слушатель
    public void StartRemove (IStart l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    
    public Object  getListener()
    {
        return listeners.iterator().next();
    }
     
}
