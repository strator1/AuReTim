package de.stzeyetrial.auretim.controller;

import de.stzeyetrial.auretim.config.Config;
import de.stzeyetrial.auretim.screens.Screens;
import de.stzeyetrial.auretim.session.Session;
import de.stzeyetrial.auretim.util.EnterSubmitHandler;
import de.stzeyetrial.auretim.util.PreferencesMap;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class MainController extends AbstractController {
	@FXML
	private TextField _subjectTextField;

	@FXML
	private TextField _testTextField;

	private final ValidationSupport _validation = new ValidationSupport();
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
		_testTextField.textProperty().set(Integer.toString(1));
		_testTextField.getProperties().put("vkType", "numeric");
		_testTextField.setOnKeyPressed(new EnterSubmitHandler());
		_subjectTextField.getProperties().put("vkType", "text");
		_subjectTextField.setOnKeyPressed(new EnterSubmitHandler());

		_validation.registerValidator(_testTextField, false, Validator.createEmptyValidator(rb.getString("emptyTestId.text")));
		_validation.registerValidator(_subjectTextField, false, Validator.createEmptyValidator(rb.getString("emptySubjectId.text")));
		
		final PreferencesMap p = new PreferencesMap(10, "subjects");
		TextFields.bindAutoCompletion(_subjectTextField, (AutoCompletionBinding.ISuggestionRequest param) -> {
			if (!param.isCancelled() && Config.getInstance().useAutoCompletionProperty().get()) {
				return p.keySet().stream().filter(text -> text.startsWith(param.getUserText())).collect(Collectors.toList());
			} else {
				return null;
			}
		}).setOnAutoCompleted((event) -> {
			final Integer value = p.get(event.getCompletion());
			try {
				_testTextField.textProperty().setValue(Integer.toString(value));
			} catch (NumberFormatException e) {
			}
		});
    }

	@FXML
	private void test(final ActionEvent e) {
		if (!_validation.isInvalid()) {
			Session.newSession(_subjectTextField.getText(), _testTextField.getText());
			getScreenManager().setScreen(Screens.TEST);
		} else {
			_validation.initInitialDecoration();
		}
	}

	@FXML
	private void settings(final ActionEvent e) {
		getScreenManager().setScreen(Screens.SETTINGS);
	}

	@FXML
	private void exit(final ActionEvent e) {
		getScreenManager().showExitConfirmationDialog();
	}

	@Override
	public void enter() {

	}
}