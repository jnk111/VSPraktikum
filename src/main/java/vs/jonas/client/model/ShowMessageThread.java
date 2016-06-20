package vs.jonas.client.model;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ShowMessageThread extends Thread {

	private String message;
	private JLabel label;
	private MyCallback callback;
	private int toggleLabel;
	
	public ShowMessageThread(String msg) {
		this.message = msg;
		this.toggleLabel = 0;
	}
	
	public ShowMessageThread(String msg, MyCallback callback){
		this.message = msg;
		this.toggleLabel = -1;
		this.callback = callback;
	}
	
	public ShowMessageThread(JLabel label){
		this.label = label;
		this.toggleLabel = 1;
	}
	
	@Override
	public void run(){
		if(toggleLabel == 1){
			JOptionPane.showMessageDialog(null, label);
		} else if(toggleLabel == 0){
			JOptionPane.showMessageDialog(null, message);
		} else if(toggleLabel == -1){
			int userResponse = JOptionPane.showConfirmDialog(null,message);
			callback.onReceive(userResponse);
		}
	}
}
