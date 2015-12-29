/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer.event_package;

import java.util.ArrayList;

/**
 *
 * @author Студент
 */
public class EventRemoveStudent
{
     private  ArrayList listeners = new ArrayList();
     
     public EventRemoveStudent()
     {}
    
     public void removeStudentEventAdd (IEventRemoveStudent l)
    {
        if (!listeners.contains(l))
            listeners.add (l);
    }

    // метод удаляющий из очереди подписчиков объект-слушатель
    public void removeStudentEventRemove (IEventRemoveStudent l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    
    public Object  getListener()
    {
        return listeners.iterator().next();    
    }
   
}
