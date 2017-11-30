// CArtAgO artifact code for project melissa

package artifact;

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
	
	void init() {
		defineObsProperty("day", 0);
		Environment.getInstance().launchGraphicApplication(800, 600);
		execInternalOp("dayChange");
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
	void collect(String pollenFieldId) {
		try {
			Environment.getInstance().collect(pollenFieldId, getCurrentOpAgentId().getAgentName());
		} catch (PollenIsOverException | NoLongerPollenFieldException | CannotCollectOnThisPositionException e) {
//			e.printStackTrace();
			failed(e.getMessage());
		}
	}	
	
	@OPERATION
	void setPosition(int x, int y) {
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
}
