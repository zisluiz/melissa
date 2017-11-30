package graphic;

import java.util.HashMap;
import java.util.Map;

import graphic.model.PollenFieldGraphic;

public class PollenFieldResolver {
	private Map<String, PollenFieldGraphic> pollenFields = new HashMap<String, PollenFieldGraphic>();

	public PollenFieldGraphic createPollenField(String pollenFieldId, PollenFieldGraphic pollenField) {
		pollenFields.put(pollenFieldId, pollenField);
		return pollenField;
	}

	public PollenFieldGraphic getPollenField(String pollenFieldId) {
		return pollenFields.get(pollenFieldId);
	}
}
