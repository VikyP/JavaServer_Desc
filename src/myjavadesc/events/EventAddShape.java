/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.events;

import java.util.ArrayList;

/**
 *
 * @author viky
 */
public class EventAddShape
{
    private  ArrayList listeners = new ArrayList();
    public EventAddShape(){}
    
    public void AddShapeAdd (IAddShape l)
    {
        
        if (!listeners.contains(l))
            listeners.add (l);
        
    }

    public void AddShapeRemove (IAddShape l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    
    public Object  getListener()
    {
        return listeners.iterator().next();
    
    }
    
}
