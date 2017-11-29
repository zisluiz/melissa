// CArtAgO artifact code for project melissa

package artifact;

import cartago.Artifact;
import cartago.OPERATION;
import graphic.Environment;
import model.enumeration.Direction;
import model.exception.InvalidMovimentException;
import model.exception.MovimentOutOfBoundsException;
import model.exception.PollenIsOverException;

public class MapArtifact extends Artifact {
	void init() {
		Environment.getInstance().launchGraphicApplication(800, 600);
	}

	@OPERATION
	void move(String direction) {
		try {
			Environment.getInstance().moveBee(getCurrentOpAgentId().getAgentName(), Direction.valueOf(direction.toUpperCase()));
		} catch (MovimentOutOfBoundsException e) {
			failed(e.getMessage());
		} catch (InvalidMovimentException e) {
			failed(e.getMessage());
		}
		await_time(10);
	}
	
	@OPERATION
	void collect(String pollenFieldId) {
		try {
			Environment.getInstance().collect(pollenFieldId, getCurrentOpAgentId().getAgentName());
		} catch (PollenIsOverException e) {
			failed(e.getMessage());
		}
	}	
	
	@OPERATION
	void setPosition(int x, int y) {
		Environment.getInstance().setPosition(getCurrentOpAgentId().getAgentName(), x, y);
	}	
	
	
}
