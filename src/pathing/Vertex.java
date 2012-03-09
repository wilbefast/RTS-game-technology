/*****************
 * @author wdyce
 * @date   Feb 14, 2012
 *****************/

package pathing;

import java.util.LinkedList;
import java.util.List;
import math.FVect;
import view.DrawGL;
import view.IDrawable;
import view.ViewPort;

public class Vertex implements IDrawable
{
    /// CONSTANTS
    
    private static final int RADIUS = 16;
    private static final float[] COLOUR_NORMAL = { 0.9f, 0.5f, 0.1f };
    private static final float[] COLOUR_START = { 0.1f, 0.3f, 0.9f };
    private static final float[] COLOUR_END = { 0.1f, 0.9f, 0.3f };
    private static final float[] COLOUR_EXPLORED = { 0.9f, 0.1f, 0.5f };
    
    /// NESTING
    
    public static enum Type
    {
        UNEXPLORED,
        EXPLORED,
        START,
        END
    }
    
    
    /// ATTRIBUTES
    
    private final String name;
    private FVect position;
    private List<Arc> adjacency;
    private Type type = Type.UNEXPLORED;

    
    /// METHODS
    
    // construction
    Vertex(String _name, FVect _position)
    {
        name = _name;
        position = _position;
        adjacency = new LinkedList<Arc>();
    }

    // query
    
    public boolean contains(FVect p)
    {
        return (position.sq_distance(p) <= RADIUS*RADIUS);
    }
    
    public float distEuclidean(Vertex other) 
    {
        return position.distance(other.position);
    }
    
    public float distManhattan(Vertex other) 
    {
        FVect other_p = other.getPosition();
        return Math.abs(position.x-other_p.x) + Math.abs(position.y-other_p.y);
    }
    
    public String getName()
    {
        return name;
    }
    
    public FVect getPosition()
    {
        return position;
    }
    
    public int arcWeight(Vertex target) 
    {
        for(Arc a : adjacency)
            if(a.getDestination().equals(target))
                return a.getWeight();
        return -1;      
    }
    
    public List<Arc> getAdjacency()
    {
        return adjacency;
    }
    
    // modifications
    
    void addAdjacent(Vertex neighbour, int weight) 
    {
        adjacency.add(new Arc(neighbour, weight));
    }
    
    void setType(Type _type)
    {
        type = _type;
    }

    // overrides
    @Override
    public String toString()
    {
        return "Vertex: " + name + " at " + position;
    }

    // implements
    public void draw() 
    {
        
        DrawGL.circle(position, RADIUS, getColour());
        DrawGL.text(name, position);
    }

    public void draw(ViewPort view) 
    {
        if(view.containsPoint(position))
        {
            FVect position_in_view = view.getPerspective(position);
            DrawGL.circle(position_in_view, RADIUS*view.getZoom(),
                    getColour());
            DrawGL.text(name, position_in_view);
        }
    }
    
    /// SUBROUTINES
    
    public float[] getColour()
    {
        switch(type)
        {
            case END: return COLOUR_END;
            case START: return COLOUR_START;
            case EXPLORED: return COLOUR_EXPLORED;
            case UNEXPLORED: default: return COLOUR_NORMAL;
                
        }
    }




}
