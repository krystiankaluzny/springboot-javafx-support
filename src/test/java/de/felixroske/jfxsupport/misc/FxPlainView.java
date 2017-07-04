package de.felixroske.jfxsupport.misc;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

@FXMLView
class FxPlainView extends AbstractFxmlView {

	@Override
	public Parent getView() {
		return new Region();
	}
}
