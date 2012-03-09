/* @author william */

package math;

import java.awt.geom.Rectangle2D;

public class FRect extends Rectangle2D.Float
{
    /// CONSTANTS
    private static final long serialVersionUID = 1L;
	
    /// ATTRIBUTES

    /// METHODS
    
    // creation
    public FRect(float init_x, float init_y, float init_w, float init_h)
    {
        super(init_x, init_y, init_w, init_h);
    }
    
    public FRect(FVect position, FVect size)
    {
        super(position.x, position.y, size.x, size.y);
    }
    
    public FRect(FVect position, float uniform_size)
    {
        super(position.x, position.y, uniform_size, uniform_size);
    }
    
    public FRect(FVect position)
    {
        super(position.x, position.y, 0f, 0f);
    }
    
    public FRect()
    {
        super(0f, 0f, 0f, 0f);
    }

    // modification
    public FRect translate(FVect v)
    {
        x += v.x;
        y += v.y;
        return this;
    }

    public FRect centerOn(FVect v)
    {
        x = v.x - width/2;
        y = v.y - height/2;
        return this;
    }
    
    public void setSize(FVect new_size) 
    {
        width = new_size.x;
        height = new_size.y;
    }
    
    public void setSize(float _width, float _height) 
    {
        width = _width;
        height = _height;
    }
    
    public void setPosition(FVect new_position) 
    {
        x = new_position.x;
        y = new_position.y;
    }
    
    // query
    public FVect getPosition()
    {
        return new FVect(x, y);
    }
    
    public FVect getSize()
    {
        return new FVect(width, height);
    }




}
