/*****************
 * @author william
 * @date 22-Feb-2012
 *****************/


package pathing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import math.FVect;
import math.IVect;


public class Maze extends Graph
{
    /// CONSTANTS
    private static final float VERTEX_SPACING = 96;
    private static final int EDGE_WEIGHT = (int)VERTEX_SPACING;
    private static final char CHAR_WALL_H = '-';
    private static final char CHAR_WALL_V = '|';
    private static final char CHAR_START = 's';
    private static final char CHAR_END = 't';
    
    /// ATTRIBUTES
    private IVect size;
    
    /// METHODS

    // creation
    
    public Maze(String filename) throws FileNotFoundException, IOException
    {
        super(filename);
    }
    
    @Override
    protected void load(BufferedReader reader) throws IOException
    {
        // get the first line
        String file_line = reader.readLine();

        // read the size and number of vertices
        String[] tokens = file_line.split("\\s");
        size = new IVect(Integer.parseInt(tokens[0]), 
                            Integer.parseInt(tokens[1]));
        
        // vertices with no edges with be culled later
        Vertex temp_vertices[][] = new Vertex[size.y][size.x];
        int v_line = 0;
        for(v_line = 0; v_line < size.y; v_line++)
            for(int v_col = 0; v_col < size.x; v_col++)
                temp_vertices[v_line][v_col] = matrixVertex(v_line, v_col);
        
        // we don't know how many edges to create in the beginning
        List<Edge> temp_edges = new LinkedList<Edge>();
        
        // skip the top line, which is always a wall
        reader.readLine();  
        
        // create and connect vertices
        String file_lines[] = { reader.readLine(), reader.readLine() };
        
        // last line to be treated with care
        for(v_line = 0; v_line < size.y; v_line++)
        {
            // start and finish
            findEndpoints(file_lines[0], v_line);
            findEndpoints(file_lines[1], v_line);
             
            // first column to be treated with care
            for(int v_col = 0; v_col < size.x; v_col++)
            {
                // vertical walls or horizontal edges -- skip first column
                if(v_col != 0 
                        && file_lines[0].charAt(4*v_col) != CHAR_WALL_V)
                {
                    Vertex v1 = temp_vertices[v_line][v_col-1],
                            v2 = temp_vertices[v_line][v_col];
                    temp_edges.add(new Edge(v1, v2, EDGE_WEIGHT));
                    v1.addAdjacent(v2, EDGE_WEIGHT);
                    v2.addAdjacent(v1, EDGE_WEIGHT);
                }
                // horizontal walls or vertical edges -- skip last line
                if(v_line != size.y-1 
                        && file_lines[1].charAt(4*v_col+2) != CHAR_WALL_H)
                {
                    Vertex v1 = temp_vertices[v_line][v_col],
                            v2 = temp_vertices[v_line+1][v_col];
                    temp_edges.add(new Edge(v1, v2, EDGE_WEIGHT));
                    v1.addAdjacent(v2, EDGE_WEIGHT);
                    v2.addAdjacent(v1, EDGE_WEIGHT);
                }
            }
                
            // read the next two lines
            file_lines[0] = reader.readLine();
            file_lines[1] = reader.readLine();
        }
        
        // unroll temporary buffers into final arrays
        
        // edges - note: temp_edges.toArray() return a Object[] not a Edge[]
        edges = new Edge[temp_edges.size()];
        for(int i = 0; i < temp_edges.size(); i++)
            edges[i] = temp_edges.get(i);
        
        // vertices - discard those which have a degree of 0
        int vertex_count = 0;
        for(v_line = 0; v_line < size.y; v_line++)
            for(int v_col = 0; v_col < size.x; v_col++)
            {
                if(temp_vertices[v_line][v_col].getAdjacency().isEmpty())
                    temp_vertices[v_line][v_col] = null;
                else vertex_count++;
            }
        // now we know how many "useful" vertices there are
        vertices = new Vertex[vertex_count];
        vertex_count = 0;
        for(v_line = 0; v_line < size.y; v_line++)
            for(int v_col = 0; v_col < size.x; v_col++)
            {
                if(temp_vertices[v_line][v_col] != null)
                {
                    addVertex(vertex_count, temp_vertices[v_line][v_col]);
                    vertex_count++;
                }
            }
        
        // set up start and end indices (found during search)
        setStartIndex(start_i != -1 ? start_i : 0);
        setEndIndex(end_i != -1 ? end_i : vertices.length-1);
    }
    
    /// SUBROUTINES
    
    private Vertex matrixVertex(int v_col, int v_line)
    {
        String name = v_line+","+v_col;
        FVect position = new FVect(v_line, v_col).scale(VERTEX_SPACING);
        return new Vertex(name, position);
    }
    
    private void findEndpoints(String line_str, int line_i)
    {
        int start_col = line_str.indexOf(CHAR_START),
            end_col = line_str.indexOf(CHAR_END);
      
        if(start_col != -1)
            start_i = line_i*size.x + start_col/4;
        
        if(end_col != -1)
            end_i = line_i*size.x + end_col/4;
    }
}
