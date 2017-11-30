// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.OPERATION;
import graphic.Environment;
import model.Hive;
import model.exception.CannotDepositOnThisPositionException;
import model.exception.NoLongerHiveException;
import model.exception.NoPollenCollectedException;

public class HiveArtifact extends Artifact {

	void init() {
		defineObsProperty("honey", 0);
		defineObsProperty("temperature", 0);
		defineObsProperty("countLarva", 0);
	}
	
	@OPERATION
	void honeyStart(int ammount) {
		Environment.getInstance().setHoneyStart(ammount);
		updateObsProperty("honey", ammount);
	}	
	
	@OPERATION
	void createLarva() {
		Environment.getInstance().createLarva();
		updateObsProperty("larvas", Hive.getInstance().getLarvas().size());
	}	
	
	@OPERATION
	void registerBee(String type, int age) {
		Environment.getInstance().registerBee(getCurrentOpAgentId().getAgentName(), type, age);
	}
	
	@OPERATION
	void delivery() {
		try {
			Environment.getInstance().delivery(getCurrentOpAgentId().getAgentName());
		} catch (CannotDepositOnThisPositionException | NoLongerHiveException | NoPollenCollectedException e) {
//			e.printStackTrace();
			failed(e.getMessage());
		}
	}	
}