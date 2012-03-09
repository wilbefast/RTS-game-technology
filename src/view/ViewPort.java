/*****************
 * @author william
 * @date 05-Mar-2012
 *****************/


package view;

import math.FRect;
import math.FVect;


public class ViewPort 
{
    /// CONSTANTS
    private static final float ZOOM_MIN = 0.1f;
    private static final float ZOOM_MAX = 2.0f;
    
    /// ATTRIBUTES
    private FVect window_size;
    private FRect area;
    private float zoom;
    
    /// METHODS
    
    // creation
    public ViewPort(FVect size)
    {
        area = new FRect(FVect.ORIGIN, size);
        window_size = size;
        zoom = 1.0f;
    }
    
    public void reset()
    {
        area.setPosition(FVect.ORIGIN);
        area.setSize(window_size);
        zoom = 1.0f;
    }
    
    // query
    public float getZoom()
    {
        return zoom;
    }

    public FVect getPerspective(FVect p) 
    {
        return new FVect((p.x-area.x)*zoom, (p.y-area.y)*zoom);
    }
    
    public FVect getGlobal(FVect p)
    {
        return new FVect(p.x/zoom + area.x, p.y/zoom + area.y);
    }

    public boolean containsPoint(FVect position) 
    {
        return area.contains(position);
    }
    
    public boolean containsLine(FVect start, FVect end) 
    {
        // discard useless states
        if(Math.min(start.x, end.x) > area.x+area.width 
        || Math.max(start.x, end.x) < area.x)
            return false;
        if(Math.min(start.y, end.y) > area.y+area.height 
        || Math.max(start.y, end.y) < area.y)
            return false;
        
        // otherwise it's all good!
        return true;
    }
    
    // modification
    
    public void setWindowSize(FVect _window_size) 
    {
        window_size = _window_size;
        area.setSize(window_size.x/zoom, window_size.y/zoom);  
    }
    
    public void translate(FVect delta)
    {
        area.translate(delta.scale(1/zoom));
    }
    
    public void zoom(float delta, FVect mouse_pos)
    {
        
        FVect mouse_true = getGlobal(mouse_pos);
        FVect mouse_rel = new FVect(window_size.x/mouse_pos.x,
                                    window_size.y/mouse_pos.y);
        
        // reset zoom counter, don't zoom too much
        zoom += delta*zoom;
        if(zoom > ZOOM_MAX)
            zoom = ZOOM_MAX;
        else if (zoom < ZOOM_MIN)
            zoom = ZOOM_MIN;
        
        // perform the zoom
        area.setSize(window_size.x/zoom, window_size.y/zoom);  
        
        
        //area.x = (mouse_pos.x/zoom + area.x) - area.width/(window_size.x/mouse_pos.x);
        
        area.x = mouse_true.x - area.width/mouse_rel.x;
        area.y = mouse_true.y - area.height/mouse_rel.y;

    }
}
