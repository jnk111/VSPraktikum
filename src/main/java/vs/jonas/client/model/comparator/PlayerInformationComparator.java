package vs.jonas.client.model.comparator;

import java.util.Comparator;

import vs.jonas.client.json.PlayerInformation;

public class PlayerInformationComparator implements Comparator<PlayerInformation> {

	@Override
	public int compare(PlayerInformation o1, PlayerInformation o2) {
		int result = 0;
		try{
			result = Integer.valueOf(o1.getAccount()).compareTo(Integer.valueOf(o2.getAccount()));
		}catch (Exception ex){
		}
		return result;
	}



}
