/*****************
 * @author wdyce
 * @date   Feb 15, 2012
 *****************/

package system;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.FVect;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import pathing.*;
import view.DrawGL;
import view.ViewPort;

public class AStarWindow extends LWJGLWindow
{
    /// CONSTANTS
    private static final int SCOLL_MOUSE_DISTANCE = 48;
    private static final int SCROLL_SPEED = 6;
    private static final float ZOOM_SPEED = 0.001f;
    
    /// ATTRIBUTES
    private Graph graph;
    private ViewPort view;
    private List<Edge> path_edges;

    /// METHODS
    
    // construction
    public AStarWindow(String _name, int _width, int _height, Graph _graph)
    {
        // save variables
        super(_name, _width, _height);
        
        // create view
        view = new ViewPort(new FVect(_width, _height));

        // start up
        reset(_graph);
    }
    
    private void reset(Graph _graph)
    {
        graph = _graph;	
        
        // create view
        view.reset();
        
        // calculate initial path
        recalculatePath();
    }

    // overrides
    @Override
    protected void render()
    {
        // standard stuff
        super.render();
        
        // draw the graph
        graph.draw(view);
        
        // draw the A* path
        for(Edge e : path_edges)
            e.draw(view);
        
        // draw the chosen heuristic
        String heuristic_name;
        FVect heuristic_pos = new FVect(16, 16);
        switch(AStarSearch.getHeuristic())
        {
            case MANHATTAN:
                heuristic_name = "Manhattan distance";
                break;
                
            case EUCLIDEAN:
                heuristic_name = "Euclidean distance";
                break;
                
            case NONE:
            default:
                heuristic_name = "None";
                break;
        }
        DrawGL.text("Heuristic: "+heuristic_name, heuristic_pos);
    }
    
    @Override
    protected void processKeyboard() 
    {
        // get arrow-key input
        FVect key_dir = new FVect(0, 0);
        if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            key_dir.y += 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            key_dir.y -= 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            key_dir.x += 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            key_dir.x -= 1;
        
        // change search heuristic
        if(Keyboard.isKeyDown(Keyboard.KEY_M))
            // M for Manhattan
            AStarSearch.setHeuristic(AStarSearch.Heuristic.MANHATTAN);
        if(Keyboard.isKeyDown(Keyboard.KEY_E))
            // E for Euclidean
            AStarSearch.setHeuristic(AStarSearch.Heuristic.EUCLIDEAN);
        if(Keyboard.isKeyDown(Keyboard.KEY_N))
            // N for None
            AStarSearch.setHeuristic(AStarSearch.Heuristic.NONE);
        
        // load a new graph
        try
        {
            if(Keyboard.isKeyDown(Keyboard.KEY_L))
                // L for Labyrinth
                reset(new Maze("maze.txt"));
            if(Keyboard.isKeyDown(Keyboard.KEY_G))
                // G for Graph
                reset(new Graph("graph2.txt"));
        }
        catch(Exception e)
        {
            Logger.getLogger(AStarWindow.class.getName()).log(Level.SEVERE, null, e);
        }
        // move the view
        view.translate(key_dir.scale(SCROLL_SPEED));
    }

    @Override
    protected void processMouse() 
    {
        // mouse position
        FVect mouse_pos = new FVect(Mouse.getX(), getHeight()-Mouse.getY());
        FVect mouse_true = view.getGlobal(mouse_pos);
        
        // mouse near edges = pan
        /*FVect scroll_dir = new FVect(0, 0);
        if(mouse_pos.x < SCOLL_MOUSE_DISTANCE) 
            scroll_dir.x = -1;
        else if(mouse_pos.x > getWidth()-SCOLL_MOUSE_DISTANCE) 
            scroll_dir.x = 1;
        if(mouse_pos.y < SCOLL_MOUSE_DISTANCE) 
            scroll_dir.y = -1;
        else if(mouse_pos.y > getHeight()-SCOLL_MOUSE_DISTANCE) 
            scroll_dir.y = 1;
        view.translate(scroll_dir.scale(SCROLL_SPEED));*/
        
        // mouse wheel = zoom
        int wheel = Mouse.getDWheel();
        if(wheel != 0)
            view.zoom(wheel*ZOOM_SPEED, mouse_pos);
        
        // mouse buttons
        if(Mouse.isButtonDown(0))
        {
            if(graph.setDestPos(mouse_true))
                recalculatePath();
        }
    }
    
    @Override
    protected void resizeGL()
    {
        // standard stuff
        super.resizeGL();
        
        // resize viewport too
        view.setWindowSize(new FVect(getWidth(), getHeight()));
    }
    
    /// SUBROUTINES
    
    private void recalculatePath()
    {
        // run the algorithm
        List<Vertex> path = AStarSearch.bestPath(graph);
        // reset the edge list
        path_edges = new LinkedList<Edge>();
        for(int i = 0; i < path.size()-1; i++)
        {
            Edge e = new Edge(path.get(i), path.get(i+1));
            path_edges.add(e);
        }
    }

}
