package de.stzeyetrial.auretim.screens;

/**
 * @author strasser
 */
public enum Screens {
	MAIN("/fxml/Main.fxml"),
	SETTINGS("/fxml/Settings.fxml"),
	TEST("/fxml/Test.fxml"),
	RESULT("/fxml/Result.fxml"),
	DIRECTORY_CHOOSER("/fxml/DirectoryChooser.fxml");

	private final String _fxml;

	private Screens(final String fxml) {
		_fxml = fxml;
	}

	public String getFile() {
		return _fxml;
	}
}