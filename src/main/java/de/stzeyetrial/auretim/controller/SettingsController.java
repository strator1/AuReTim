package de.stzeyetrial.auretim.controller;

import de.stzeyetrial.auretim.audio.Tone;
import de.stzeyetrial.auretim.audio.ToneUtils;
import de.stzeyetrial.auretim.config.Config;
import de.stzeyetrial.auretim.input.Input;
import de.stzeyetrial.auretim.input.InputFactory;
import de.stzeyetrial.auretim.screens.Screens;
import de.stzeyetrial.auretim.util.EnterSubmitHandler;
import de.stzeyetrial.auretim.util.NullSafeNumberStringConverter;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.SwipeEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javax.sound.sampled.LineUnavailableException;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

/**
 * FXML Controller class
 *
 * @author strasser
 */
public class SettingsController extends AbstractBackSupportController {
	private static final int WAIT_TIME = 5;

	private final ValidationSupport _validation = new ValidationSupport();
	private final ExecutorService _executor = Executors.newSingleThreadExecutor( r -> {
		final Thread t = Executors.defaultThreadFactory().newThread(r);
		t.setDaemon(true);
		return t;
	});

	private ResourceBundle _rb;

	@FXML
	private CheckBox _useNoGoCheckbox;

	@FXML
	private ComboBox<Integer> _frequencyChoiceBox;

	@FXML
	private Circle _indicator;

	@FXML
	private TextField _directoryTextField;

	@FXML
	private TextField _volumeTextField;

	@FXML
	private CheckBox _useAutoCompletionCheckbox;

	@FXML
	private TextField _pulseDurationTextField;

	@FXML
	private TextField _timeoutTextField;

	@FXML
	private TextField _minimumDelayTextField;

	@FXML
	private TextField _maximumDelayTextField;

	@FXML
	private TextField _minimumResponseTimeTextField;

	@FXML
	private TextField _repetitionsTextField;

	@FXML
	private ComboBox<Input> _inputComboBox;

	@FXML
	private Accordion _accordion;

	@FXML
	private Button _inputTestButton;

	private Paint _indicatorDefaultColor;

	private boolean _positive = true;


	public SettingsController() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> _executor.shutdown()));
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		_rb = resources;

		_indicatorDefaultColor = _indicator.getFill();

		final Config config = Config.getInstance();
		_directoryTextField.textProperty().bindBidirectional(config.directoryProperty());
		_validation.registerValidator(_directoryTextField, false, Validator.createEmptyValidator(""));
		_directoryTextField.setOnKeyPressed(new EnterSubmitHandler());
		_useAutoCompletionCheckbox.selectedProperty().bindBidirectional(config.useAutoCompletionProperty());
		_useNoGoCheckbox.selectedProperty().bindBidirectional(config.useNoGoProperty());
		_volumeTextField.textProperty().bindBidirectional(config.volumeProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_validation.registerValidator(_volumeTextField, false, Validator.createEmptyValidator(""));
		
		_pulseDurationTextField.getProperties().put("vkType", "numeric");
		_pulseDurationTextField.textProperty().bindBidirectional(config.pulseDurationProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_pulseDurationTextField.setOnKeyPressed(new EnterSubmitHandler());
		_validation.registerValidator(_pulseDurationTextField, false, Validator.createEmptyValidator(""));
		_timeoutTextField.textProperty().bindBidirectional(config.timeoutProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_timeoutTextField.getProperties().put("vkType", "numeric");
		_timeoutTextField.setOnKeyPressed(new EnterSubmitHandler());
		_validation.registerValidator(_timeoutTextField, false, Validator.createEmptyValidator(""));
		_minimumDelayTextField.textProperty().bindBidirectional(config.minimumDelayProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_minimumDelayTextField.getProperties().put("vkType", "numeric");
		_minimumDelayTextField.setOnKeyPressed(new EnterSubmitHandler());
		_validation.registerValidator(_minimumDelayTextField, false, Validator.createEmptyValidator(""));
		_maximumDelayTextField.textProperty().bindBidirectional(config.maximumDelayProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_maximumDelayTextField.getProperties().put("vkType", "numeric");
		_maximumDelayTextField.setOnKeyPressed(new EnterSubmitHandler());
		config.useNoGoProperty().addListener((final ObservableValue<? extends Boolean> observable, final Boolean oldValue, Boolean newValue) -> _maximumDelayTextField.setDisable(newValue));
		_maximumDelayTextField.setDisable(config.useNoGoProperty().get());

		_validation.registerValidator(_maximumDelayTextField, false, Validator.createEmptyValidator(""));
		_repetitionsTextField.textProperty().bindBidirectional(config.repetitionsProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_repetitionsTextField.getProperties().put("vkType", "numeric");
		_repetitionsTextField.setOnKeyPressed(new EnterSubmitHandler());
		_validation.registerValidator(_repetitionsTextField, false, Validator.createEmptyValidator(""));
		_minimumResponseTimeTextField.textProperty().bindBidirectional(config.minimumResponseTimeProperty(), new NullSafeNumberStringConverter(NumberFormat.getIntegerInstance()));
		_minimumResponseTimeTextField.getProperties().put("vkType", "numeric");
		_minimumResponseTimeTextField.setOnKeyPressed(new EnterSubmitHandler());
		_validation.registerValidator(_minimumResponseTimeTextField, false, Validator.createEmptyValidator(""));

		_frequencyChoiceBox.setConverter(new StringConverter<Integer>() {
			@Override
			public String toString(final Integer object) {
				return String.format("%d Hz", object);
			}

			@Override
			public Integer fromString(final String string) {
				return Integer.valueOf(string.split(" ")[0]);
			}
		});
		_frequencyChoiceBox.getSelectionModel().select(config.frequencyProperty().getValue());
		config.frequencyProperty().bind(_frequencyChoiceBox.getSelectionModel().selectedItemProperty());
		_frequencyChoiceBox.itemsProperty().bind(config.frequenciesProperty());

		_inputComboBox.getSelectionModel().select(config.inputProperty().getValue());
		config.inputProperty().bind(_inputComboBox.getSelectionModel().selectedItemProperty());
		_inputComboBox.itemsProperty().get().addAll(Input.values());
	}

	@Override
	protected void back() {
		getScreenManager().setScreen(Screens.MAIN);
	}

	@FXML
	private void save(final ActionEvent e) {
		if (!_validation.isInvalid()) {
			try {
				Config.getInstance().save();
				getScreenManager().showMessage(_rb.getString("settingsSaved.text"));
			} catch (IOException ex) {
				getScreenManager().showException(ex);
			}
		} else {
			_validation.initInitialDecoration();
		}
	}

	@FXML
	private void next(final SwipeEvent e) {
		final int index = _accordion.getPanes().indexOf(_accordion.getExpandedPane());
		if (index + 1 < _accordion.getPanes().size()) {
			_accordion.setExpandedPane(_accordion.getPanes().get(index + 1));
		}
		e.consume();
	}

	@FXML
	private void previous(final SwipeEvent e) {
		final int index = _accordion.getPanes().indexOf(_accordion.getExpandedPane());
		if (index - 1 >= 0) {
			_accordion.setExpandedPane(_accordion.getPanes().get(index - 1));
		}
		e.consume();
	}

	@FXML
	private void lowerVolume(final ActionEvent e) {
		final int volume = Config.getInstance().volumeProperty().get() - Config.VOLUME_DELTA;
		if (volume >= Config.MIN_VOLUME) {
			Config.getInstance().volumeProperty().set(volume);
		}
	}

	@FXML
	private void higherVolume(final ActionEvent e) {
		final int volume = Config.getInstance().volumeProperty().get() + Config.VOLUME_DELTA;
		if (volume <= Config.MAX_VOLUME) {
			Config.getInstance().volumeProperty().set(volume);
		}
	}

	@FXML
	private void chooser(final ActionEvent e) {
		getScreenManager().setScreen(Screens.DIRECTORY_CHOOSER);
		e.consume();
	}

	@FXML
	private void testTone(final ActionEvent e) {
		final int pulseDuration = Config.getInstance().pulseDurationProperty().get();
		int frequency = Config.getInstance().frequencyProperty().get() * ((_positive) ? 1 : 2);
		if (Config.getInstance().useNoGoProperty().get()) {
			frequency *= ((_positive) ? 1 : 2);
			_positive = !_positive;
		} else {
			_positive = true;
		}
		final int volume = Config.getInstance().volumeProperty().get();
		final ByteBuffer buffer = ToneUtils.createToneBuffer(pulseDuration, frequency);
		try (final Tone tone = Tone.createTone(buffer)) {
			tone.play(volume);
		} catch (LineUnavailableException | IOException ex) {
			getScreenManager().showException(ex);
		}
	}

	@FXML
	private void inputTest(final ActionEvent e) {
		e.consume();
		
		final AtomicInteger time = new AtomicInteger(WAIT_TIME);

		_inputTestButton.setDisable(true);
		_inputTestButton.setText(String.format(_rb.getString("inputTestButtonWait.text"), time.getAndDecrement()));
		_indicator.setFill(Color.web("#9f9f9f"));

		final Timeline timeline = new Timeline(
			new KeyFrame(
				Duration.seconds(1), 
				ae -> _inputTestButton.setText(String.format(_rb.getString("inputTestButtonWait.text"), time.getAndDecrement()))
			)
		);
		timeline.setCycleCount(WAIT_TIME);
		timeline.play();
		
		_executor.submit(() -> {
			final CountDownLatch latch = new CountDownLatch(1);
			InputFactory.getInstance().setInputCallback(() -> latch.countDown());

			try {
				if (latch.await(WAIT_TIME, TimeUnit.SECONDS)) {
					Platform.runLater(() -> _indicator.fillProperty().setValue(Color.GREEN));
				} else {
					Platform.runLater(() -> _indicator.fillProperty().setValue(Color.RED));
				}				
			} catch (InterruptedException ex) {
			}

			timeline.stop();
			Platform.runLater(() -> {
				_inputTestButton.setDisable(false);
				_inputTestButton.setText(_rb.getString("inputTestButton.text"));
			});
		});
	}
}