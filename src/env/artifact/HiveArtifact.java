// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;
import model.Hive;

public class HiveArtifact extends Artifact {
	private static final long DELAY_TIME = 10000;

	void init() {
		defineObsProperty("day", 0);
		defineObsProperty("honey", 0);
		defineObsProperty("temperature", 0);
		defineObsProperty("countLarva", 0);
		
		execInternalOp("dayChange");
	}
	
	@OPERATION
	void honeyStart(int ammount) {
		Environment.getInstance().setHoneyStart(ammount);
		updateObsProperty("honey", ammount);
	}	
	
	@OPERATION
	void createLarva() {
		Environment.getInstance().createLarva();
		updateObsProperty("countLarva", Hive.getInstance().getLarvas().size());
	}	
	
	@OPERATION
	void registerBee(String type, int age) {
		Environment.getInstance().registerBee(getCurrentOpAgentId().getAgentName(), type, age);
	}
	
	@INTERNAL_OPERATION
	void dayChange() {
		while (true) {
			await_time(DELAY_TIME);
			ObsProperty day = getObsProperty("day");
			int newDay = day.intValue()+1;
			
			Environment.getInstance().changeDay(newDay);
			
			day.updateValue(newDay);
		}
	}	
	
	@OPERATION
	void delivery() {
		Environment.getInstance().delivery(getCurrentOpAgentId().getAgentName());
	}	
}