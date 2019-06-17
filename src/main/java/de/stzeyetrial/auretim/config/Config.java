package de.stzeyetrial.auretim.config;

import de.stzeyetrial.auretim.input.Input;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author strasser
 */
public class Config {
	public static final int MIN_VOLUME									= -80;
	public static final int MAX_VOLUME									= 0;
	public static final int VOLUME_DELTA								= 10;

	private static final String CONFIG_FILENAME							= "config.properties";

	private static final String CONFIG_COMMENT							= "AuReTim configuration";

	private static final String PROPERTY_DIRECTORY						= "directory";
	private static final String PROPERTY_INPUT							= "input";
	private static final String PROPERTY_FREQUENCY						= "frequency";
	private static final String PROPERTY_VOLUME							= "volume";
	private static final String PROPERTY_USE_AUTO_COMPLETION			= "userAutoCompletion";
	private static final String PROPERTY_USE_NO_GO						= "useNoGo";
	private static final String PROPERTY_FREQUENCIES					= "frequencies";
	private static final String PROPERTY_PULSEDURATION					= "pulseDuration";
	private static final String PROPERTY_TIMEOUT						= "timeout";
	private static final String PROPERTY_MINIMUM_DELAY					= "minimumDelay";
	private static final String PROPERTY_MAXIMUM_DELAY					= "maximumDelay";
	private static final String PROPERTY_MINIMUM_RESPONSE_TIME			= "minimumResponseTime";
	private static final String PROPERTY_REPETITIONS					= "repetitions";

	private static final String PROPERTY_DIRECTORY_DEFAULT				= ".";
	private static final String PROPERTY_INPUT_DEFAULT					= Input.BUTTON.toString();
	private static final String PROPERTY_FREQUENCY_DEFAULT				= Integer.toString(440);
	private static final String PROPERTY_VOLUME_DEFAULT					= Double.toString(0.5);
	private static final String PROPERTY_USE_AUTO_COMPLETION_DEFAULT	= Boolean.toString(false);
	private static final String PROPERTY_USE_NO_GO_DEFAULT				= Boolean.toString(false);
	private static final String PROPERTY_FREQUENCIES_DEFAULT			= "220,440,880,1760,3520";
	private static final String PROPERTY_PULSEDURATION_DEFAULT			= Integer.toString(500);
	private static final String PROPERTY_TIMEOUT_DEFAULT				= Integer.toString(2);
	private static final String PROPERTY_MINIMUM_DELAY_DEFAULT			= Integer.toString(2);
	private static final String PROPERTY_MAXIMUM_DELAY_DEFAULT			= Integer.toString(8);
	private static final String PROPERTY_REPETITIONS_DEFAULT			= Integer.toString(10);
	private static final String PROPERTY_MINIMUM_RESPONSE_TIME_DEFAULT	= Integer.toString(100);

	private static Config _instance;

	private final StringProperty _directory				= new SimpleStringProperty();
	private final ObjectProperty<Input> _input			= new SimpleObjectProperty<>();
	private final IntegerProperty _frequency			= new SimpleIntegerProperty();
	private final IntegerProperty _volume				= new SimpleIntegerProperty();
	private final BooleanProperty _useAutoCompletion	= new SimpleBooleanProperty();
	private final BooleanProperty _useNoGo				= new SimpleBooleanProperty();
	private final IntegerProperty _pulseDuration		= new SimpleIntegerProperty();
	private final IntegerProperty _timeout				= new SimpleIntegerProperty();
	private final IntegerProperty _minimumDelay			= new SimpleIntegerProperty();
	private final IntegerProperty _maximumDelay			= new SimpleIntegerProperty();
	private final IntegerProperty _minimumResponseTime	= new SimpleIntegerProperty();
	private final IntegerProperty _repetitions			= new SimpleIntegerProperty();

	private final ObjectProperty<ObservableList<Integer>> _frequencies = new SimpleObjectProperty<>(FXCollections.observableArrayList());

	private Config() {
	}

	public static synchronized Config getInstance() {
		if (_instance == null) {
			_instance = new Config();
		}

		return _instance;
	}

	public void load() {
		final Properties p = new Properties();
		try {
			p.load(new FileInputStream(CONFIG_FILENAME));
		} catch (final IOException ex) {
			Logger.getLogger(Config.class.getName()).log(Level.WARNING, "Could not load config file.", ex);
		}

		directoryProperty().setValue(p.getProperty(PROPERTY_DIRECTORY, PROPERTY_DIRECTORY_DEFAULT));
		inputProperty().setValue(Input.valueOf(p.getProperty(PROPERTY_INPUT, PROPERTY_INPUT_DEFAULT)));
		frequencyProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_FREQUENCY, PROPERTY_FREQUENCY_DEFAULT)));
		volumeProperty().setValue(Double.valueOf(p.getProperty(PROPERTY_VOLUME, PROPERTY_VOLUME_DEFAULT)));
		useAutoCompletionProperty().setValue(Boolean.valueOf(p.getProperty(PROPERTY_USE_AUTO_COMPLETION, PROPERTY_USE_AUTO_COMPLETION_DEFAULT)));
		useNoGoProperty().setValue(Boolean.valueOf(p.getProperty(PROPERTY_USE_NO_GO, PROPERTY_USE_NO_GO_DEFAULT)));
		pulseDurationProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_PULSEDURATION, PROPERTY_PULSEDURATION_DEFAULT)));
		timeoutProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_TIMEOUT, PROPERTY_TIMEOUT_DEFAULT)));
		minimumDelayProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_MINIMUM_DELAY, PROPERTY_MINIMUM_DELAY_DEFAULT)));
		maximumDelayProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_MAXIMUM_DELAY, PROPERTY_MAXIMUM_DELAY_DEFAULT)));
		repetitionsProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_REPETITIONS, PROPERTY_REPETITIONS_DEFAULT)));
		minimumResponseTimeProperty().setValue(Integer.valueOf(p.getProperty(PROPERTY_MINIMUM_RESPONSE_TIME, PROPERTY_MINIMUM_RESPONSE_TIME_DEFAULT)));

		Arrays.stream(p.getProperty(PROPERTY_FREQUENCIES, PROPERTY_FREQUENCIES_DEFAULT)
			.split(","))
			.map(s -> s.trim())
			.mapToInt(s -> Integer.valueOf(s))
			.forEach(f -> frequenciesProperty().get().add(f));
	}

	public void save() throws IOException {
		final Properties p = new Properties();

		p.setProperty(PROPERTY_DIRECTORY,				directoryProperty().getValue());
		p.setProperty(PROPERTY_INPUT,					inputProperty().getValue().toString());
		p.setProperty(PROPERTY_FREQUENCY,				Integer.toString(frequencyProperty().getValue()));
		p.setProperty(PROPERTY_VOLUME,					Double.toString(volumeProperty().getValue()));
		p.setProperty(PROPERTY_USE_AUTO_COMPLETION,		Boolean.toString(useAutoCompletionProperty().getValue()));
		p.setProperty(PROPERTY_USE_NO_GO,				Boolean.toString(useNoGoProperty().getValue()));
		p.setProperty(PROPERTY_FREQUENCIES,				PROPERTY_FREQUENCIES_DEFAULT);
		p.setProperty(PROPERTY_PULSEDURATION,			Integer.toString(pulseDurationProperty().getValue()));
		p.setProperty(PROPERTY_TIMEOUT,					Integer.toString(timeoutProperty().getValue()));
		p.setProperty(PROPERTY_MINIMUM_DELAY,			Integer.toString(minimumDelayProperty().getValue()));
		p.setProperty(PROPERTY_MAXIMUM_DELAY,			Integer.toString(maximumDelayProperty().getValue()));
		p.setProperty(PROPERTY_REPETITIONS,				Integer.toString(repetitionsProperty().getValue()));
		p.setProperty(PROPERTY_MINIMUM_RESPONSE_TIME,	Integer.toString(minimumResponseTimeProperty().getValue()));

		p.store(new FileOutputStream(CONFIG_FILENAME), CONFIG_COMMENT);
	}

	public StringProperty directoryProperty() {
		return _directory;
	}

	public ObjectProperty<Input> inputProperty() {
		return _input;
	}

	public IntegerProperty frequencyProperty() {
		return _frequency;
	}

	public IntegerProperty volumeProperty() {
		return _volume;
	}

	public BooleanProperty useAutoCompletionProperty() {
		return _useAutoCompletion;
	}

	public BooleanProperty useNoGoProperty() {
		return _useNoGo;
	}

	public IntegerProperty pulseDurationProperty() {
		return _pulseDuration;
	}

	public IntegerProperty timeoutProperty() {
		return _timeout;
	}

	public IntegerProperty minimumDelayProperty() {
		return _minimumDelay;
	}

	public IntegerProperty maximumDelayProperty() {
		return _maximumDelay;
	}

	public IntegerProperty minimumResponseTimeProperty() {
		return _minimumResponseTime;
	}

	public IntegerProperty repetitionsProperty() {
		return _repetitions;
	}

	public ObjectProperty<ObservableList<Integer>> frequenciesProperty() {
		return _frequencies;
	}
}