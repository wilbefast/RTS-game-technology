/*****************
 * @author wdyce
 * @date   Feb 14, 2012
 *****************/

package pathing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.FRect;
import math.FVect;
import view.IDrawable;
import view.ViewPort;

public class Graph implements IDrawable
{
    
    /// ATTRIBUTES
    protected Vertex[] vertices;
    protected Edge[] edges;
    protected FRect bounds;
    protected int start_i = -1, end_i = -1;

    /// METHODES

    // construction
    
    public Graph(String filename)
    {   
        try 
        {
            
            // open the file
            FileReader file = new FileReader(filename);
            BufferedReader reader = new BufferedReader(file);
            // load the graph from the file (overriden)
            load(reader);
            // remember to close the file!
            file.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void load(BufferedReader reader) throws IOException
    {
        // get the first line
        String file_line = reader.readLine();

        // read the size and number of vertices
        String[] tokens = file_line.split("\\s");
        vertices = new Vertex[Integer.parseInt(tokens[0])];
        edges = new Edge[Integer.parseInt(tokens[1])];
           
        // stops creating vertices when we reach the required quota
        int created_vertices = 0;
        int created_edges = 0;

        // create and connect vertices
        file_line = reader.readLine();
        
        while (file_line != null)
        {
            // tokenise
            tokens = file_line.split("\\s");
            if(created_vertices != -1 && tokens[0].length() < 3)
                created_vertices = -1; // stop parsing vertices

            // create vertices
            if(created_vertices != -1)
            {
               // parse attributes
                String name = tokens[0].subSequence(1,
                        tokens[0].length()-1).toString();
                FVect position = new FVect(Float.parseFloat(tokens[1]),
                                            Float.parseFloat(tokens[2]));
                // create and save vertex
                createVertex(created_vertices, name, position);
                created_vertices++;
            }

            // create edges
            else
            {
                // parse attributes
                int weight = Integer.parseInt(tokens[3]);
                // create and save edge
                createEdge(created_edges, getNameIndex(tokens[1]),
                                        getNameIndex(tokens[2]), weight);
                created_edges++;
            }
            
            // next line
            file_line = reader.readLine();
        }
        
        // set up start and end indices
        setStartIndex(0);
        setEndIndex(vertices.length-1);
        
        // correct (flip) coordinates
        for(Vertex v : vertices)
        {
            FVect v_pos = v.getPosition();
            v_pos.x = bounds.width - v_pos.x;
            v_pos.y = bounds.height - v_pos.y;
        }
    }
    

    // query
    
    public int getVertexNumber()
    {
        return vertices.length;
    }
    
    public List<Vertex> getVertices() 
    {
        return Arrays.asList(vertices);
    }
    
    public Vertex getStart()
    {
        return vertices[start_i];
    }
    
    public Vertex getEnd()
    {
        return vertices[end_i];
    }
    
    // modification
    
    public boolean setDestPos(FVect position)
    {
        // reset the start vertex
        int i = getIndexAt(position);
        if(i != end_i && i != -1)
        {
            // end becomes new start, new end is selected vertice
            setStartIndex(end_i);
            setEndIndex(i);
            
            return true;
        }
        
        // if no vertex is found, return fail
        else
            return false;
    }
    
    public boolean setStartPos(FVect position)
    {
        // reset the start vertex
        int i = getIndexAt(position);
        if(i != start_i && i != -1)
        {
            setStartIndex(i);
            return true;
        }
        
        // if no vertex is found, return fail
        else
            return false;
    }
    
    public boolean setEndPos(FVect position)
    {
        // reset the end vertex
        int i = getIndexAt(position);
        if(i != end_i && i != -1)
        {
            setEndIndex(i);
            return true;
        }
        
        // if no vertex is found, return fail
        else
            return false;
    }

    public void setSize(FVect new_size)
    {
        // local variables
        FVect size = bounds.getSize();
        FVect size_delta = new FVect(new_size.x/size.x, new_size.y/size.y);
        
        // update the boundary
        bounds.setPosition(((FVect)bounds.getPosition().clone()).scale(size_delta));
        bounds.setSize(new_size);

        // update vertex positions
        for(Vertex v : vertices)
            v.getPosition().scale(size_delta);

    }
    
    public void setCenter(FVect new_center)
    {
        // update vertex positions
        FVect delta = new FVect(new_center.x - bounds.width/2, 
                                new_center.y - bounds.height/2);
        for(Vertex v : vertices)
        {
            v.getPosition().subtract(bounds.getPosition());
            v.getPosition().add(delta);
        }
        
        // update the boundary
        bounds.x = bounds.y = 0;
        bounds.centerOn(new_center);
    }

    // overrides
    @Override
    public String toString()
    {
        String s = "Graph:\n";
        for(int i = 0; i<vertices.length; i++)
        {
            s += vertices[i] + "\n";
            for(int j = 0; j<vertices.length; j++)
            {
                int edge_weight = vertices[i].arcWeight(vertices[j]);
                if(edge_weight != -1)
                    s+= "\t->" + vertices[j].getName() + " " + edge_weight + "\n";
            }
        }
        
        return s;
    }
    
    // implement
    public void draw()
    {        
        // draw edges
        for(Edge e : edges)
            e.draw();
        
        // draw vertices
        for(Vertex v : vertices)
            v.draw();
    }
    
    
    public void draw(ViewPort view) 
    {
        // draw edges
        for(Edge e : edges)
            e.draw(view);
        
        // draw vertices
        for(Vertex v : vertices)
            v.draw(view);
    }
    
    
    /// SUBROUTINES
    
    // modification

    protected void setStartIndex(int v_i)
    {
        if(start_i != -1)
            vertices[start_i].setType(Vertex.Type.UNEXPLORED);
        start_i = v_i;
        vertices[v_i].setType(Vertex.Type.START);
        
        if(v_i == end_i)
            end_i = -1;
    }
    
    protected void setEndIndex(int v_i)
    {
        if(end_i != -1)
            vertices[end_i].setType(Vertex.Type.UNEXPLORED);
        end_i = v_i;
        vertices[v_i].setType(Vertex.Type.END);
        
        if(v_i == start_i)
            start_i = -1;
    }
    
    protected void createVertex(int v_i, String name, FVect position)
    {
        // create and save vertex
        Vertex new_vertex = new Vertex(name, position);
        addVertex(v_i, new_vertex);
    }
    
    protected void addVertex(int v_i, Vertex new_vertex)
    {
        // add the vertices
        vertices[v_i] = new_vertex;
        
        // reset boundaries of graph
        FVect position = new_vertex.getPosition();
        if(bounds == null)
            bounds = new FRect(position);
        else
        {
            // horizontal
            if(position.x < bounds.x)
            {
                bounds.width += bounds.x-position.x;
                bounds.x = position.x;
            }
            else if (position.x > bounds.x+bounds.width)
                bounds.width = position.x - bounds.x;
            
            // vertical
            if(position.y < bounds.y)
            {
                bounds.height += bounds.y-position.y;
                bounds.y = position.y;
            }
            else if (position.y > bounds.y+bounds.height)
                bounds.height = position.y - bounds.y;
        }
    }
    
    protected void createEdge(int e_i, int v1_i, int v2_i, int weight)
    {
        edges[e_i] = new Edge(vertices[v1_i], vertices[v2_i], weight);
        vertices[v1_i].addAdjacent(vertices[v2_i], weight);
        vertices[v2_i].addAdjacent(vertices[v1_i], weight);
    }

    // query
    private int getNameIndex(String name)
    {
        for(int i = 0; i<vertices.length; i++)
            if(vertices[i].getName().equals(name))
                return i;
        return -1;
    }
    
    private int getIndexAt(FVect position)
    {
        for(int i = 0; i<vertices.length; i++)
            if(vertices[i].contains(position))
                return i;
        return -1;
    }
}
