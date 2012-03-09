/* @author william */

package math;

import java.awt.geom.Point2D;

public class FVect extends Point2D.Float
{
    /// CONSTANTS
    public static final FVect ORIGIN = new FVect();
    
    /// FUNCTIONS
    public static float det(FVect v1, FVect v2)
    {
        return v1.x*v2.y - v2.x*v1.y;
    }
	
    /// METHODS

	// creation
    public FVect(float init_x, float init_y)
    {
        x = init_x;
        y = init_y;
    }
    
    public FVect()
    {
        x = y = 0f;
    }

    // V2i2D.Double can't be translated!
    public FVect translate(float dx, float dy)
    {
        x += dx;
        y += dy;
        return this;
    }

    public FVect add(FVect v)
    {
        x += v.x;
        y += v.y;
        return this;
    }
    
    public FVect subtract(FVect v) 
    {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public FVect scale(float amount)
    {
        x *= amount;
        y *= amount;
        return this;
    }
    
    public FVect scale(FVect amount) 
    {
        x *= amount.x;
        y *= amount.y;
        return this;
    }
    
    public FVect normalise()
    {
    	double norm = getNorm();
    	x /= norm;
    	y /= norm;
    	return this;
    }
    
    
    // query
    public double getNorm()
    {
    	return Math.sqrt(x*x + y*y);
    }
    
    public float distance(FVect other)
    {
        return (float) Math.sqrt(sq_distance(other));
    }
    
    public float sq_distance(FVect other)
    {
        float dx = x-other.x, dy = y-other.y;
        return dx*dx + dy*dy;
    }

    // cast
    public IVect iVect()
    {
        return new IVect((int)x, (int)y);
    }



}
