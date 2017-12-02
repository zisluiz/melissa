// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;
import model.Position;
import model.enumeration.Direction;
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
		
		defineObsProperty("pollenField", 0,0,150,200);
		defineObsProperty("pollenField", 0, 300, 60, 170);
		defineObsProperty("pollenField", 400, 0, 40, 40);
		defineObsProperty("pollenField", 759, 230, 40, 40);
		
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
	void dayChange() {
		while (true) {
			await_time(DELAY_TIME);
			ObsProperty day = getObsProperty("day");
			int newDay = day.intValue()+1;
			
			Environment.getInstance().changeDay(newDay);
			day.updateValue(newDay);
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
}
