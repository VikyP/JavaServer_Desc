/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.events;

import java.util.ArrayList;

/**
 *
 * @author Viky_Pa
 */
public class EventChangeBoard
{
    private  ArrayList listeners = new ArrayList();
    public EventChangeBoard(){}
    
    public void ChangeBoardAdd (IChBoard l)
    {
        if (!listeners.contains(l))
            listeners.add (l);
    }

    public void ChangeBoardRemove (IChBoard l)
    {
        if (listeners.contains(l))
            listeners.remove (l);
    }
    
    public Object  getListener()
    {
        return listeners.iterator().next();
    }
    
}
