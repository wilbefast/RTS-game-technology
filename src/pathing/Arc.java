/*****************
 * @author william
 * @date 17-Feb-2012
 *****************/


package pathing;


public class Arc
{
    /// ATTRIBUTES
    private final Vertex destination;
    private final int weight;

    // METHODS
    
    // creation
    public Arc(Vertex _destination, int _weight)
    {
        destination = _destination;
        weight = _weight;
    }

    // query
    public Vertex getDestination() 
    {
        return destination;
    }
    
    public int getWeight()
    {
        return weight;
    }
}
    
