// CArtAgO artifact code for project melissa

package artifact;

import java.util.List;

import com.sun.javafx.geom.Rectangle;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;
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
	
	private List<Rectangle> pollenFields;
	
	void init() {
		defineObsProperty("day", 0);
		defineObsProperty("extTemperature", 25);
		Environment.getInstance().launchGraphicApplication(800, 600);
		execInternalOp("createPolenFields");
		execInternalOp("dayChange");
		execInternalOp("temperatureChange");
	}

	@INTERNAL_OPERATION
	void createPolenFields() {
		pollenFields.add(new Rectangle(0, 0, 150, 200));
		pollenFields.add(new Rectangle(0, 300, 60, 170));
		pollenFields.add(new Rectangle(400, 0, 40, 40));
		pollenFields.add(new Rectangle(759, 230, 40, 40));
	}
	
	boolean posInField(int x0, int y0) {
		for(Rectangle rec : pollenFields) {
			if(rec.x < x0 && x0 < (rec.x + rec.width)) {
				if(rec.y < y0 && y0 < (rec.y + rec.height)) {
					return true;
				}
			}
		}
		return false;
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
