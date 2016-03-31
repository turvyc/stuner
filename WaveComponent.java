import javax.swing.JComponent;
import java.util.Vector;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;

//=============================================================================
public class WaveComponent extends JComponent {  // implements Observer
    private int y_shift;                                            		
    Vector<Line2D> lines;
    
    //-------------------------------------------------------------------------
    //
    // params:	width - component width, height - component height, 
    //			y_sh - down shift Y from the 0,0 origin by this length (moves the x-axis down)
    WaveComponent(int width, int height, int y_sh) {
        super();
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(width,height));
        lines = new Vector<Line2D>();
        y_shift = y_sh;               
    }

    //-------------------------------------------------------------------------
    // params:	x1 - start point x, y1 - start point y
    //    		x2 - end point x,   y2 - end point y
    public void addLine(double x1, double y1, double x2, double y2) {

    	//System.out.print("\nx1: " + x1 + "  y1: " + y1 + ",  x2: " + x2 + "  y2: " + y2);
        Line2D shape = new Line2D.Double(x1, y1 + y_shift, x2, y2 + y_shift);
        lines.addElement(shape);
        repaint();
    }   
    
    //-------------------------------------------------------------------------
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLUE);

        for (int i = 0; i < lines.size(); i++){                       
            g2.draw(lines.elementAt(i));                
        }
    }
    
    //­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­­
    public void clear(){
        lines.clear();
        repaint();
    }
}
