import Index1;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Container;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JPanel;

public class GUI extends JFrame implements MouseListener {
	public static void main(String args[]){
		GUI g = new GUI();
	}
	public static JTextArea editText = new JTextArea("Search .."); //Event listener for Enter
	boolean clicked = false;
	
	public GUI(){
		JFrame mainframe = new JFrame();
		mainframe.setSize(400,100);
		mainframe.setLocationRelativeTo(null);
		mainframe.setDefaultCloseOperation (JFrame. EXIT_ON_CLOSE );
		mainframe.setTitle("WOZA");
		mainframe.setBackground(new Color(0,0,0));
		
		Font font = new Font("Helvetica", Font.PLAIN, 32);
		editText.setBackground(Color.WHITE);
		editText.setBounds(0, 0, 400, 100);
		editText.setFont(font);
		editText.addMouseListener(this);
		mainframe.add(editText);
		mainframe.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(!clicked){
			editText.setText("");
			clicked = true;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}