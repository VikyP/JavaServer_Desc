/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.events;

import java.util.ArrayList;

/**
 *
 * @author 06585
 */
public class EventShapeTypeChanged 
{
    private  ArrayList listeners = new ArrayList();
    public EventShapeTypeChanged(){}
     
    public void ShapeTypeChangedAdd (IChShapeType l)
    {
        
        if (!listeners.contains(l))
            listeners.add (l);
        
    }

    // метод удаляющий из очереди подписчиков объект-слушатель
    public void ShapeTypeChangedRemove (IChShapeType l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    
    public Object  getListener()
    {
        return listeners.iterator().next();
    
    }
    
}
