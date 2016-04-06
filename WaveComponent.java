import javax.swing.JComponent;
import java.util.Vector;
import java.awt.geom.Line2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.BorderFactory;

/**
 * Displays a graph of the waveform of the incoming audio signal.
 */
public class WaveComponent extends JComponent {
    private int y_shift;                                            		
    // Distance to shift the x-axis up or down

    Vector<Line2D> lines;
    
    WaveComponent(int width, int height, int y_sh) {
        super();
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(width,height));
        lines = new Vector<Line2D>();
        y_shift = y_sh;               
    }

    /**
     * Adds a line segment to the vector
     * @param x1 starting x value
     * @param x2 ending x value
     * @param y1 starting y value
     * @param y2 ending y value
     */
    public void addLine(double x1, double y1, double x2, double y2) {
        Line2D shape = new Line2D.Double(x1, y1 + y_shift, x2, y2 + y_shift);
        lines.addElement(shape);
        repaint();
    }   
    
    /**
     * Draws the waveform onto the component.
     */
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLUE);

        for (int i = 0; i < lines.size(); i++){                       
            g2.draw(lines.elementAt(i));                
        }
    }
    
    public void clear(){
        lines.clear();
        repaint();
    }
}
