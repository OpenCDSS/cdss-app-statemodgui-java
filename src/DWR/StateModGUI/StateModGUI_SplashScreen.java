// StateModGUI_SplashScreen - splash screen

/* NoticeStart

StateMod GUI
StateMod GUI is a part of Colorado's Decision Support Systems (CDSS)
Copyright (C) 1997-2019 Colorado Department of Natural Resources

StateMod GUI is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

StateMod GUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

You should have received a copy of the GNU General Public License
    along with StateMod GUI.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

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
