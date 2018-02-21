package me.ByteCoder.Core.Client.Event;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;


public class EventManager
{
    public HashSet<EventListener> events;
    
	public EventManager() {
    	events = new HashSet<EventListener>();
    }
    
    public void addEvent(final EventListener e) {
        this.events.add(e);
    }
    
    public void remEvent(final EventListener e) {
        this.events.remove(e);
    }
    
    public void callEvent(final Event e) {
        if (this.events == null || this.events.isEmpty()) {
            return;
        }
        for (final EventListener event : this.events) {
            final Class<? extends EventListener> s = event.getClass();
            Method[] declaredMethods;
            for (int length = (declaredMethods = s.getDeclaredMethods()).length, i = 0; i < length; ++i) {
                final Method m = declaredMethods[i];
                if (m.isAnnotationPresent(EventHandler.class) && m.getParameterCount() == 1 && e.getClass() == m.getParameters()[0].getType()) {
                    try {
                        m.invoke(event, e);
                    }
                    catch (IllegalAccessException e2) {
                        e2.printStackTrace();
                    }
                    catch (IllegalArgumentException e3) {
                        e3.printStackTrace();
                    }
                    catch (InvocationTargetException e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }
    }
}
