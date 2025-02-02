import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.net.URI;

/**
 * @version 1.0
 * @author: Abhinandan Mallick
 * @Project: The Flappy Bird
 * @since 2025-02-01
 */
public class App {
    public static void main(String[] args) {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JEditorPane editorPane = new JEditorPane("text/html",
                "<html><body>" +
                        "<h2>The Flappy Bird</h2>" +
                        "<p><b>Created by:</b> Abhinandan Mallick</p>" +
                        "<p><b>GitHub:</b> <a href='https://github.com/abhimk11'>https://github.com/abhimk11</a></p>" +
                        "<p><b>How to play:</b><br>- Press Space or left-click on mouse to jump</p>" +
                        "</body></html>"
        );

        // Make the text clickable and change the cursor to a hand
        editorPane.setEditable(false);
        editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add a hyperlink listener to open the link in the browser when clicked
        editorPane.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Show the content in a JOptionPane dialog
        JOptionPane.showMessageDialog(null, new JScrollPane(editorPane), "Credits", JOptionPane.INFORMATION_MESSAGE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}
