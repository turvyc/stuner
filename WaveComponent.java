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
/*
import java.awt.GridBagLayout;

// SET IT UP IN THE CLASS WHERE MAIN IS

    public static int x_scale = 500;                                            // Set it to the Wavelength Component width 
    public static int x_jump = (bufferSize) / x_scale;
    public static int y_scale = 100;                                            // Set it to the 1/2 of the Component height
    public static int y_divisor = Short.MAX_VALUE / y_scale; 

// IN MAIN,

        WaveComponent waveform = new WaveComponent(350, 200);
        waveform.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JPanel p1 = new JPanel(new GridBagLayout());
        p1.setSize(380, 380);
        p1.setBackground(Color.WHITE);
        p1.add(waveform);  


// IN THE MAIN, WHILE LOOP

            for ( int i = 1; i < data.length; i+= x_jump ){    		
    		waveform.addLine((i/x_jump)-1, -(int)data[i-1]/y_divisor, i/x_jump, -(int)data[i]/y_divisor);    		
            }
            
            try {
                Thread.sleep(250); //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            waveform.clear();
*/

//=============================================================================
public class WaveComponent extends JComponent implements Observer {
    public static int y_shift;                                            		
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

    public void update(Observable o, Object arg) {
        PitchDetector detector = (PitchDetector) o;
        double[] samples = (double[]) arg;
        // Do stuff
    }

    //-------------------------------------------------------------------------
    // params:	x1 - start point x, y1 - start point y
    //    		x2 - end point x,   y2 - end point y
    public void addLine(double x1, double y1, double x2, double y2) {

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
