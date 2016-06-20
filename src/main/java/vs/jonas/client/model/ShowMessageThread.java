package vs.jonas.client.model;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ShowMessageThread extends Thread {

	private String message;
	private JLabel label;
	private boolean toggleLabel;
	
	public ShowMessageThread(String msg) {
		this.message = msg;
		this.toggleLabel = false;
	}
	
	public ShowMessageThread(JLabel label){
		this.label = label;
		this.toggleLabel = true;
	}
	
	@Override
	public void run(){
		if(toggleLabel){
			JOptionPane.showMessageDialog(null, label);
		} else{
			JOptionPane.showMessageDialog(null, message);
		}
	}
}
