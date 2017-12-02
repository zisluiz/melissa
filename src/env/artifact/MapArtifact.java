// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;
import model.Position;
import model.enumeration.Direction;
import model.enumeration.PollenSupply;
import model.exception.CannotCollectOnThisPositionException;
import model.exception.InvalidMovimentException;
import model.exception.MovimentOutOfBoundsException;
import model.exception.NoLongerPollenFieldException;
import model.exception.PollenIsOverException;

public class MapArtifact extends Artifact {
	private static final long DELAY_TIME = 10000;
	private static final int AVERAGE_TEMPERATURE = 25;
	private static final int TERMIC_AMPLITUDE = 10;
	
	void init() {
		Environment.getInstance().launchGraphicApplication(800, 600);
		
		defineObsProperty("day", 0);
		defineObsProperty("extTemperature", 25);
		
		// x, y, width, height
		defineObsProperty("hive", 649,449,150,150);
		
		defineObsProperty("pollenField", "HIGH", 0,0,150,200);
		defineObsProperty("pollenField", "HIGH", 0, 300, 60, 170);
		defineObsProperty("pollenField", "HIGH", 400, 0, 40, 40);
		defineObsProperty("pollenField", "HIGH", 759, 230, 40, 40);
		
	//	defineObsProperty("pollenField", "(pollenField1,0,0,150,200)", "(pollenField2, 0, 300, 60, 170)", "(pollenField3, 400, 0, 40, 40)", "(pollenField4, 759, 230, 40, 40)");
		
		execInternalOp("dayChange");
		execInternalOp("temperatureChange");
	}

	@OPERATION
	void move(String direction) {
		try {
			Environment.getInstance().moveBee(getCurrentOpAgentId().getAgentName(), Direction.valueOf(direction.toUpperCase()));
		} catch (MovimentOutOfBoundsException | InvalidMovimentException e) {
//			e.printStackTrace();
			failed(e.getMessage());
		}
		await_time(20);
	}
	
	@OPERATION
	void move(int x, int y) {
		try {
			Environment.getInstance().moveBee(getCurrentOpAgentId().getAgentName(), x, y);
		} catch (MovimentOutOfBoundsException | InvalidMovimentException e) {
//			e.printStackTrace();
			failed(e.getMessage());
		}
		await_time(20);
	}	

	@OPERATION
	void flyTo(int x, int y) {
		Position beePos = Environment.getInstance().getBeePos(getCurrentOpAgentId().getAgentName());
		int i = beePos.getX();
		int j = beePos.getY();
		if( !(x==i & y==j)) {
			if(Math.abs(y-j) > Math.abs(x-i))
				if(y-j>0)
					move(i,j+1);
				else
					move(i,j-1);
			else
				if(x-i>0)
					move(i+1,j);
				else
					move(i-1,j);
			flyTo(x,y);
		}
	}
	
	@OPERATION
	void collect(String pollenFieldId) {
		try {
			Environment.getInstance().collect(pollenFieldId, getCurrentOpAgentId().getAgentName());
			//updatePollenFields();
		} catch (PollenIsOverException | NoLongerPollenFieldException | CannotCollectOnThisPositionException e) {
//			e.printStackTrace();
			failed(e.getMessage());
		}
	}	
	
	@OPERATION
	void collect() {
		try {
			String beeId = getCurrentOpAgentId().getAgentName();
			Position beePos = getPosition(beeId);
			String pollenFieldId = Environment.getInstance().getMatchingPollenFieldId(beePos);
			Environment.getInstance().collect(pollenFieldId, beeId);
		} catch (PollenIsOverException | NoLongerPollenFieldException | CannotCollectOnThisPositionException e) {
//			e.printStackTrace();
			failed(e.getMessage());
		}
	}	
	
	@OPERATION
	public void setPosition(int x, int y) {
		Environment.getInstance().setPosition(getCurrentOpAgentId().getAgentName(), x, y);
	}
	
	@INTERNAL_OPERATION
	public Position getPosition(String beeId) {
		return Environment.getInstance().getBeePos(beeId);
	}
	
	@INTERNAL_OPERATION
	void dayChange() {
		while (true) {
			await_time(DELAY_TIME);
			ObsProperty day = getObsProperty("day");
			int newDay = day.intValue()+1;
			
			Environment.getInstance().changeDay(newDay);
			day.updateValue(newDay);
			//updatePollenFields();
		}
	}	
	
	@INTERNAL_OPERATION
	void temperatureChange() {
		while (true) {
			await_time(DELAY_TIME);
			ObsProperty day = getObsProperty("day");
			int today = day.intValue();
			
			int month = today % 12; // para estacoes do ano
			int newTemperature = (int)(AVERAGE_TEMPERATURE + TERMIC_AMPLITUDE * Math.sin(2*Math.PI*(month/12.)));
			
			Environment.getInstance().changeExtTemp(newTemperature);

			ObsProperty temperature = getObsProperty("extTemperature");
			temperature.updateValue(newTemperature);
		}
	}
	/*
	@INTERNAL_OPERATION
	void updatePollenFields() {
		for (int i =1; i <= Environment.getInstance().getPollenFieldResolver().getNumberPollenFields(); i++) {
			String name = "pollenField"+i;
			PollenSupply level = Environment.getInstance().getPollenFieldResolver().getPollenField(name).getPollenField().getStatus();
			
			ObsProperty pf = getObsProperty("pollenField");
			System.out.println("PollenFieldObs: "+pf.getValue(0));
			
			/*if(!(level == )){
				pf.updateValue(0, level);
			}
		}
	}*/
}
