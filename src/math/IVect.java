/* @author william */

package math;

import java.awt.Point;

public class IVect extends Point
{
	/// CONSTANTS
	private static final long serialVersionUID = 1L;
	
    /// METHODS

    // creation
    public IVect(int init_x, int init_y)
    {
        x = init_x;
        y = init_y;
    }

    // cast
    public FVect FVect()
    {
        return new FVect((float)x, (float)y);
    }

}
