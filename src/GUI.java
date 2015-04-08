 
   import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Color;

import javax.swing.JPanel;

public class GUI extends JFrame implements MouseListener, KeyListener {
	
		public static JTextArea editText = new JTextArea("Search .."); //Event listener for Enter
		boolean clicked = false;
		Index1 i;
	
	public GUI(Index1 filename){
		i = filename;
		JFrame mainframe = new JFrame();
			mainframe.setSize(400,50);
			mainframe.setLocationRelativeTo(null);
			mainframe.setDefaultCloseOperation (JFrame. EXIT_ON_CLOSE );
			mainframe.setTitle("WOZA");
			mainframe.setBackground(new Color(0,0,0));
		
			Font font = new Font("Helvetica", Font.PLAIN, 32);
			editText.setBackground(Color.WHITE);
			editText.setBounds(0, 0, 400, 100);
			editText.setFont(font);
			editText.addMouseListener(this);
			editText.addKeyListener(this);
		
			mainframe.add(editText);
			mainframe.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(!clicked){
			editText.setText("");
			clicked = true;
			//TODO When enter is pressed, search for word through search bar (as in Index1)

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

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			//System.out.println(editText.getText());
			i.search(editText.getText().toLowerCase());
			editText.setText(null);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			editText.setText(null);
		}
	}
}