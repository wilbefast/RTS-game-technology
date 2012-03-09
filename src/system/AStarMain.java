/*****************
 * @author wdyce
 * @date   Feb 14, 2012
 *****************/

package system;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import pathing.Graph;
import pathing.Maze;

abstract class AStarMain
{
    /// CLASS NAMESPACE CONSTANTS
    public static final Logger LOGGER = Logger.getLogger(AStarMain.class.getName());
    public static final String WIN_NAME = "A* Test";
    public static final int WIN_W = 640;
    public static final int WIN_H = 480;
    
    /// NESTING
    private static class Mode 
    {
        public String filename;
        public Mode(String _filename) { filename = _filename; }
    }
    private static final Mode mode_graph = new Mode("graph2.txt");
    private static final Mode mode_maze = new Mode("maze.txt");
    

    /// CLASS INITIALISATION
    static
    {
        try
        {
            LOGGER.addHandler(new FileHandler("errors.log",true));
        }
        catch(IOException ex)
        {
            LOGGER.log(Level.WARNING,ex.toString(),ex);
        }
    }

    /// PROGRAM ENTRANCE POINT
    public static void main(String[] args)
    {
        // treat arguments
        Mode mode;
        switch(args.length)
        {
            case 0:
            default:
                mode = mode_graph;
                break;
            case 1:
                mode = (args[0].equals("-maze")) ? mode_maze : mode_graph;
                break;
            case 2:
                mode = (args[0].equals("-maze")) ? mode_maze : mode_graph;
                mode.filename = args[1];
                break;        
        }
        
        
        LWJGLWindow window = null;
        try
        {
            // create the graph
            Graph g = (mode == mode_maze) ? new Maze(mode.filename)
                                           : new Graph(mode.filename);
           
            // create the window
            window = new AStarWindow(WIN_NAME, WIN_W, WIN_H, g);
            window.create();
            window.run();
        }
        catch(Exception ex)
        {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
        finally
        {
            if(window != null)
                window.destroy();
        }
    }
}

