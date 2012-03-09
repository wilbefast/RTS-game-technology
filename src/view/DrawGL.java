/*****************
 * @author william
 * @date 16-Feb-2012
 *****************/


package view;

import java.awt.Font;
import math.FRect;
import math.FVect;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public abstract class DrawGL 
{
    /// CLASS NAMESPACE CONSTANTS
    private static final int CIRCLE_SEGMENTS = 20;
    private static TrueTypeFont font = null;
    
    public static final float[] BLACK = { 0.0f, 0.0f, 0.0f };
    public static final float[] YELLOW = { 1.0f, 1.0f, 0.0f };
    
    
    /// CLASS NAMESPACE FUNCTIONS
    
    // initialisation
    
    public static void init()
    {
        // background colour and depth
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);  
        glClearDepth(1);  
        
        // 2d initialisation
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D); 
        
        // we need blending (alpha) for drawing strings
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // load a default java font
	font = new TrueTypeFont(new Font("Arial", Font.PLAIN, 12), false);
    }
    
    // drawing functions
    
    public static void circle(FVect center, float radius)
    {
        glBegin(GL_TRIANGLE_FAN);
            glVertex2f(center.x, center.y);
            for(int deg = 0; deg <= 360; deg += 360/CIRCLE_SEGMENTS)
            {
                double rad = deg * Math.PI/180;
                glVertex2f((float)(center.x + Math.cos(rad)*radius),
                            (float)(center.y + Math.sin(rad)*radius));
            }
        glEnd();
    }
    
    public static void circle(FVect center, float radius, float[] colour)
    {
        glColor3f(colour[0], colour[1], colour[2]);
        circle(center, radius);
    }
    
    public static void line(FVect start, FVect end)
    {
        line(start, end, BLACK, 1.0f);
    }
    
    public static void line(FVect start, FVect end, float[] colour, float width)
    {
        glLineWidth(width);
        glColor3f(colour[0], colour[1], colour[2]);
        glBegin(GL_LINES);
            glVertex2f(start.x, start.y);
            glVertex2f(end.x, end.y);
        glEnd();
    }
    
    public static void box(FRect rect)
    {
        box(rect, BLACK);
    }
    
    public static void box(FRect rect, float[] colour)
    {
        glColor3f(colour[0], colour[1], colour[2]);
        glBegin(GL_QUADS);
            glVertex2f(rect.x, rect.y);
            glVertex2f(rect.x+rect.width, rect.y);
            glVertex2f(rect.x, rect.y+rect.height);
            glVertex2f(rect.x+rect.width, rect.y+rect.height);
        glEnd();
    }
    
    public static void text(String string, FVect position)
    {
        glEnable(GL_BLEND);
        font.drawString(position.x, position.y, string, Color.black);
        glDisable(GL_BLEND);    
    }
    
    public static void text(String string, FVect position, float[] colour)
    {
        glEnable(GL_BLEND);
        font.drawString(position.x, position.y, string, 
                new Color(colour[0], colour[1], colour[2]));
        glDisable(GL_BLEND);
    }
}
