package vs.jonas.client.model;

import javax.swing.JOptionPane;

public class ShowMessageThread extends Thread {

	private String message;
	
	public ShowMessageThread(String msg) {
		this.message = msg;
	}
	
	@Override
	public void run(){
		JOptionPane.showMessageDialog(null, message);
	}
}
