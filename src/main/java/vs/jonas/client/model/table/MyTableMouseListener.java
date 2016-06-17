package vs.jonas.client.model.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import vs.jonas.client.controller.GameController;

public abstract class MyTableMouseListener implements MouseListener {

	private GameController controller;
	
	public MyTableMouseListener(GameController gameController) {
		this.controller = gameController;
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {

	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	public GameController getController(){
		return controller;
	}
}
