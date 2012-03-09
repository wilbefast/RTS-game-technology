/*****************
 * @author wdyce
 * @date   Feb 14, 2012
 *****************/

package pathing;

import math.FVect;
import view.DrawGL;
import view.IDrawable;
import view.ViewPort;

public class Edge implements IDrawable
{
    
    /// ATTRIBUTES
    
    private Vertex start;
    private Vertex end;
    private int weight;

    /// METHODS
    
    // construction
    public Edge(Vertex _start, Vertex _end)
    {
        this(_start, _end, -1);
    }
    
    public Edge(Vertex _start, Vertex _end, int _weight)
    {
        // start should always be left of end!
        boolean correct_order = _start.getPosition().x < _end.getPosition().x;
        start = (correct_order) ? _start : _end;
        end = (correct_order) ? _end : _start;
        weight = _weight;
    }

    int getWeight()
    {
        return weight;
    }
    
    // update
    
    public void draw()
    {
        FVect pos_s = start.getPosition(), pos_e = end.getPosition();
        
        // draw edge between vertices
        DrawGL.line(pos_s, pos_e);
        // draw weight in the middle
        FVect middle = ((FVect)(pos_s.clone())).add(pos_e).scale(0.5f);
        DrawGL.text(weight+"", middle);
    }
    
    
    public void draw(ViewPort view) 
    {
        if(!view.containsLine(start.getPosition(), end.getPosition()))
            return;
        
        // translate based on view position
        FVect s = view.getPerspective(start.getPosition()),
              e = view.getPerspective(end.getPosition());

        // normal edge
        if(weight >= 0)
        {
            // draw edge between vertices
            DrawGL.line(s, e);
            // draw weight in the middle
            FVect middle = (s.add(e).scale(0.5f));
            DrawGL.text(weight+"", middle);
        }
        // highlit edge
        else
        {
            // draw a very fat yellow edge between vertices
            DrawGL.line(s, e, DrawGL.YELLOW, 3.0f);
        }
    }
    
    // overrides
    @Override
    public String toString()
    {
        return "Edge: " + start + " to " + end + " with weight " + weight;
    }

}
