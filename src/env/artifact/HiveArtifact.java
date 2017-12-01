// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;
import model.Hive;
import model.exception.CannotDepositOnThisPositionException;
import model.exception.NoLongerHiveException;
import model.exception.NoPollenCollectedException;

public class HiveArtifact extends Artifact {
	private static final long DELAY_TIME = 10000;
	void init() {
		defineObsProperty("honey", 0);
		defineObsProperty("intTemperature", 0);
		defineObsProperty("larvas", 0);
		defineObsProperty("heaters", 0);
		defineObsProperty("coolers", 0);
		
		execInternalOp("temperatureChange");
	}
	
	@OPERATION
	void aquecer() {
		
	}
	
	void resfriar() {
		
	}
	
	@OPERATION
	void honeyStart(int ammount) {
		Environment.getInstance().setHoneyStart(ammount);
		updateObsProperty("honey", ammount);
	}	
	
	@OPERATION
	void tempStart(int ammount) {
		Environment.getInstance().setIntTemp(ammount);
		updateObsProperty("intTemperature", ammount);
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
	
	@INTERNAL_OPERATION
	void temperatureChange() {
		while(true){
			await_time(DELAY_TIME);
			int extTemp = Integer.parseInt(MapArtifact.getOpKey("extTemperature",0));
			ObsProperty intTemp = getObsProperty("intTemperature");
			ObsProperty heaters = getObsProperty("heaters");
			ObsProperty coolers = getObsProperty("coolers");
			int heat = heaters.intValue();
			int cool = coolers.intValue();
			
			int ammount = extTemp + heat - cool;
			
			intTemp.updateValue(ammount);
			Environment.getInstance().setIntTemp(ammount);
		}
	}	
}