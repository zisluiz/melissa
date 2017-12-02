// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;
import model.Hive;
import model.exception.CannotDepositOnThisPositionException;
import model.exception.InsufficientHoneyException;
import model.exception.InsufficientPolenException;
import model.exception.NoLongerHiveException;
import model.exception.NoPollenCollectedException;

public class HiveArtifact extends Artifact {
	private static final long DELAY_TIME = 10000;
	
	void init() {
		defineObsProperty("polen", 0);
		defineObsProperty("honey", 0);
		defineObsProperty("intTemperature", 0);
		defineObsProperty("larvas", 0);
		defineObsProperty("heaters", 0);
		defineObsProperty("coolers", 0);
		defineObsProperty("x", 0);
		defineObsProperty("y", 0);
		defineObsProperty("width", 0);
		defineObsProperty("heigth", 0);
		
		execInternalOp("temperatureChange");
	}
	
	@OPERATION
	void hiveStart() {
		honeyStart(100);
		polenStart(100);
		tempStart(25);
		dimStart(649, 449, 150, 150);
	}
	
	void dimStart(int x, int y, int width, int heigth) {
		updateObsProperty("x", x);
		updateObsProperty("y", y);
		updateObsProperty("width", width);
		updateObsProperty("heigth", heigth);
	}

	
	@OPERATION
	void aquecer() {
		ObsProperty h = getObsProperty("heaters");
		h.updateValue(h.intValue()+1);
		updateTemp();
	}
	
	@OPERATION
	void resfriar() {
		ObsProperty c = getObsProperty("coolers");
		c.updateValue(c.intValue()+1);
		updateTemp();
	}
	
	@OPERATION
	void stop_aquecer() {
		ObsProperty h = getObsProperty("heaters");
		h.updateValue(h.intValue()-1);
		updateTemp();
	}
	
	@OPERATION
	void stop_resfriar() {
		ObsProperty c = getObsProperty("coolers");
		c.updateValue(c.intValue()-1);
		updateTemp();
	}
	
	@OPERATION
	void processPolen() {
		try {
			Environment.getInstance().processPolen(1);
		} catch (InsufficientPolenException e) {
			failed(e.getMessage());
		}
	}
	
	@OPERATION
	void polenStart(int ammount) {
		Environment.getInstance().setPolenStart(ammount);
		updateObsProperty("polen", ammount);
	}	
	
	@OPERATION
	void honeyStart(int ammount) {
		Environment.getInstance().setHoneyStart(ammount);
		updateObsProperty("honey", ammount);
		polenStart(0);
	}	
	
	@OPERATION
	void comer(int ammount) {
		try {
			Environment.getInstance().eat(ammount);
		} catch (InsufficientHoneyException e) {
			failed(e.getMessage());
		}
	}	
	
	@OPERATION
	void createLarva() {
		Environment.getInstance().createLarva();
		updateObsProperty("larvas", Hive.getInstance().getLarvas().size());
	}	
	
	@OPERATION
	void registerBee(String type) {
		Environment.getInstance().registerBee(getCurrentOpAgentId().getAgentName(), type);
	}
	
	@OPERATION
	void delivery() {
		try {
			Environment.getInstance().delivery(getCurrentOpAgentId().getAgentName());
		} catch (CannotDepositOnThisPositionException | NoLongerHiveException | NoPollenCollectedException e) {
			failed(e.getMessage());
		}
	}
	
	@OPERATION
	void tempStart(int ammount) {
		Environment.getInstance().setIntTemp(ammount);
		updateObsProperty("intTemperature", ammount);
	}	
	
	@OPERATION
	void updateTemp() {
		int extTemp = Environment.getInstance().getExtTemperature();
		ObsProperty intTemp = getObsProperty("intTemperature");
		ObsProperty heaters = getObsProperty("heaters");
		ObsProperty coolers = getObsProperty("coolers");
		int heat = heaters.intValue();
		int cool = coolers.intValue();
		
		int ammount = extTemp + heat - cool;
		
		if (ammount != intTemp.intValue()) {
			intTemp.updateValue(ammount);
			Environment.getInstance().setIntTemp(ammount);
		}		
	}
	
	@INTERNAL_OPERATION
	void temperatureChange() {
		while(true){
			await_time((int)(DELAY_TIME/10));
			
			updateTemp();
		}
	}
}