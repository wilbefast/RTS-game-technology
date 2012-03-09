/*****************
 * @author william
 * @date 16-Feb-2012
 *****************/


package pathing;

import java.util.*;


public abstract class AStarSearch 
{
    /// NESTED TYPES
    public static enum Heuristic
    {
        NONE, EUCLIDEAN, MANHATTAN
    }
    
    private static class State implements Comparable<State>
    {
        // attributes
        public final Vertex vertex;
        public State parent = null;
        public int currentCost = 0;
        public int totalCostEstimate = 0;
        public boolean closed = false;
        
        // methods
        public State(Vertex _vertex)
        {
            vertex = _vertex;
        }
        
        public void setParent(State _parent, Arc via)
        {
            parent = _parent;
            currentCost = parent.currentCost + via.getWeight();
            totalCostEstimate = currentCost + h(vertex);
            
        }
        
        public int compareTo(State other) 
        {
            return totalCostEstimate - other.totalCostEstimate;
        }
    }
    
    /// CLASS NAMESPACE VARIABLES
    
    // required
    private static Graph graph;
    private static HashMap<Vertex, State> states;
    private static Queue<State> open;
    private static State start;
    private static State end;
    private static Heuristic heuristic_f = Heuristic.NONE;

    /// CLASS NAMEPSPACE FUNCTIONS
    
    public static void setHeuristic(Heuristic _heuristic_f)
    {
        heuristic_f = _heuristic_f;
    }
    
    public static Heuristic getHeuristic()
    {
        return heuristic_f;
    }
    
    public static List<Vertex> bestPath(Graph _graph)
    {
        // set up class variables
        graph = _graph;
        init();
        
        // perform the search
        boolean success = search();
        
        // if successful, generate path by reading back through tree
        return (success) ? unfurl() : new LinkedList<Vertex>();
    }
    
    // subroutines
    private static void init()
    {
        // get start and end vertices
        Vertex v_start = graph.getStart(), v_end = graph.getEnd();
        
        // wrap graph vertices in exploration state objects
        states = new HashMap<Vertex, State>();
        for (Vertex v : graph.getVertices())
        {
            State s = new State(v);
            
            // cache start and end vertices
            if(v == v_start)
                start = s;
            if(v == v_end)
                end = s;
            
            // set other vertices as unexplorered
            if(v != v_start && v != v_end)
                v.setType(Vertex.Type.UNEXPLORED);
                
            // save the states
            states.put(v, s);
        }

        // add the start state to the open set
        open = new PriorityQueue<State>();
        open.add(start);
    }
    
    private static boolean search()
    {
        while(!open.isEmpty())
        {
            // expand from the open state that is currently cheapest
            State x = open.poll();
            if(x != start && x != end)
                x.vertex.setType(Vertex.Type.EXPLORED);

            // have we reached the end?
            if(x.equals(end))
                return true;
            
            // try to expand each neighbour
            for(Arc a : x.vertex.getAdjacency())
                expand(x, a);
            
            // remember to close x now that all connections have been expanded
            x.closed = true;
        }
        System.out.println("Couldn't find end state!");
        return false;
    }
    
    private static void expand(State x, Arc a)
    {
        
        State y = states.get(a.getDestination());
                
        // closed states are no longer under consideration
        if(y.closed)
            return;   
        
        // states not yet opened always link back to x
        if(!open.contains(y))
        {
            // set cost before adding to heap, or order will be wrong!
            y.setParent(x, a);
            open.add(y);
        }

        // states already open link back to x only if it's better
        else if(x.currentCost+a.getWeight() < y.currentCost)
        {
            // remove, reset cost and replace, or order will be wrong!
            open.remove(y);
            y.setParent(x, a);
            open.add(y);
        }
    }
    
    private static List<Vertex> unfurl()
    {
        List<Vertex> result = new LinkedList<Vertex>();
        
        // start at the end, trace backwards adding vertices
        State current = end;
        while(current != null)
        {
            // add element to front, in order for list to be in the right order
            result.add(0, current.vertex);
            current = current.parent;
        }
        return result;
    }
    
    private static int h(Vertex v)
    {
        switch(heuristic_f)
        {
            case EUCLIDEAN:
                return (int)v.distEuclidean(end.vertex);
                
            case MANHATTAN:
                return (int)v.distManhattan(end.vertex);
            
            case NONE:
            default:
                return 0;
            
        }
    }
}
