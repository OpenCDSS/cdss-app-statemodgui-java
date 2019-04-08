package DWR.StateModGUI;

/**
 *
 * Created on June 6, 2007, 2:14 PM
 *
 */

import java.awt.BorderLayout;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 *
 * @author iws
 */
public class StateModGUI_SplashScreen {

    private JWindow window;
    private JLabel label;

    public StateModGUI_SplashScreen(URL image) {
        window = new JWindow();
        window.setLayout(new BorderLayout());
        window.add(new JLabel(new ImageIcon(image)),BorderLayout.CENTER);
        window.add(label = new JLabel(" "),BorderLayout.SOUTH);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        window.pack();
        window.setLocationRelativeTo(null);
    }

    public void show() {
        window.setVisible(true);
        window.toFront();
    }

    public void setMessage(String message) {
        if (message == null || message.length() == 0) {
            message = " ";
        }
        label.setText(message);
    }

    public void hide() {
        window.dispose();
    }

}