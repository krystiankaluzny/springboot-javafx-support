package de.felixroske.jfxsupport.util;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;

/**
 * Created by Krystian Kałużny on 09.07.2017.
 */
@FXMLController
class FxPlainController {

	private OnFxInitialize onFxInitialize;

	@FXML
	public void initialize() {
		if (onFxInitialize != null) {
			onFxInitialize.onInitialize();
		}
	}

	@Autowired(required = false)
	public void setOnFxInitialize(OnFxInitialize onFxInitialize) {
		this.onFxInitialize = onFxInitialize;
	}
}
