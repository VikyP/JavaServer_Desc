/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer.event_package;

import java.util.ArrayList;


public class EventAddStudent
{
    private  ArrayList listeners = new ArrayList();
    public EventAddStudent(){}
     
    public void addStudentEventAdd (IEventAddStudent l)
    {
        
        if (!listeners.contains(l))
            listeners.add (l);
        
    }

    // метод удаляющий из очереди подписчиков объект-слушатель
    public void addStudentEventRemove (IEventAddStudent l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    public Object  getListener()
    {
        return listeners.iterator().next();
    
    }
}
    
    
    
    



///Interfaces






