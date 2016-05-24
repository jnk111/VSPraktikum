package vs.jonas.client.model.comparator;

import java.util.Comparator;

import vs.jonas.client.model.Player;

public class PlayerInformationComparator implements Comparator<Player> {

	@Override
	public int compare(Player o1, Player o2) {
		int result = 0;
		try{
			result = Integer.valueOf(o1.getAccount()).compareTo(Integer.valueOf(o2.getAccount()));
		}catch (Exception ex){
		}
		return result;
	}



}
