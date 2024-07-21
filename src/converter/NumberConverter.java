package converter;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This program is used to convert numbers
 * into the most common formats.
 * 
 * @author Mark Hudock
 *         Copyright � 2013 All rights reserved
 *
 */
public class NumberConverter extends JFrame {

	/**
	 * Generated version UID.
	 */
	private static final long serialVersionUID = 3359047340148528784L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new NumberConverter();
	}

	public NumberConverter() {
		super("Number Converter");

		String[] convertOptions = {
				"Decimal to Binary",
				"Binary to Decimal",
				"Hex to Decimal",
				"Decimal to Hex"
		};

		final JComboBox<String> options = new JComboBox<String>(convertOptions);

		// The button to trigger the calculation
		JButton button = new JButton("Convert");

		// The label for displaying the results
		final JTextField resultsLabel = new JTextField("Enter a number to convert.", 35);
		resultsLabel.setEditable(false);
		resultsLabel.setBorder(null);
		resultsLabel.setOpaque(false);
		resultsLabel.setLayout(new BorderLayout());

		final JTextField textField = new JTextField(TEXT_FIELD_LENGTH);

		// The panel that holds the user interface components
		JPanel northPanel = new JPanel();
		northPanel.add(options, BorderLayout.WEST);
		northPanel.add(textField, BorderLayout.CENTER);
		northPanel.add(button, BorderLayout.EAST);

		JPanel southPanel = new JPanel();
		southPanel.add(resultsLabel, BorderLayout.CENTER);

		add(northPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);

		textField.addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				if (checkForMaxCharacters(convertingMode, textField.getText().length())) {
					e.consume();
				}
				char c = e.getKeyChar();
				if (c == KeyEvent.VK_ESCAPE) {
					textField.setText(null);
				}
				if (convertingMode == DECIMAL_TO_BINARY || convertingMode == DECIMAL_TO_HEX) {
					if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
						e.consume();
					}
				} else if (convertingMode == BINARY_TO_DECIMAL) {
					if (((c != '0') && (c != '1')) && (c != KeyEvent.VK_BACK_SPACE)) {
						e.consume();
					}
				} else if (convertingMode == HEX_TO_DECIMAL) {
					if (((c < '0') || (c > '9')) &&
							((c < 'a') || (c > 'f')) &&
							((c < 'A') || (c > 'F')) &&
							(c != KeyEvent.VK_BACK_SPACE)) {
						e.consume();
					}
				}
			}

		});

		class ModeListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent event) {
				convertingMode = options.getSelectedIndex();
				textField.setText(null);
			}

		}

		class ConvertButtonListener implements ActionListener {

			public void actionPerformed(ActionEvent event) {
				String input = textField.getText();
				if (!checkForErrors(input)) {
					return;
				}
				String convertedNumber = getConvertedNumber(input, convertingMode);
				resultsLabel.setText(getTextForMode(input, convertedNumber, convertingMode));
				textField.selectAll();
			}

		}

		ActionListener listener = new ConvertButtonListener();
		button.addActionListener(listener);
		textField.addActionListener(listener);

		options.addActionListener(new ModeListener());

		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\data\\icon.png"));
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);

	}

	protected boolean checkForMaxCharacters(int mode, int length) {
		switch (mode) {

			case DECIMAL_TO_BINARY:
				if (length > 10)
					return true;
				break;

			case BINARY_TO_DECIMAL:
				if (length >= 31)
					return true;
				break;

			case HEX_TO_DECIMAL:
				if (length > 9)
					return true;
				break;

			case DECIMAL_TO_HEX:
				if (length > 10)
					return true;
				break;

			default:
				return false;

		}
		return false;
	}

	/**
	 * Checks the given String for specific number format
	 * errors according to the current mode.
	 * 
	 * @param input The String to check for number format errors.
	 * @return True if a number is not formatted properly.
	 */
	public boolean checkForErrors(final String input) {
		if (input.length() == 0) {
			JOptionPane.showMessageDialog(null,
					"Enter a valid number.", "Invalid number", 0);
			return false;
		}
		switch (convertingMode) {

			case HEX_TO_DECIMAL:
				if (input.length() > 8) {
					JOptionPane.showMessageDialog(null,
							"That is not a hexadecimal number! (Example: FA0F34)", "Invalid number", 0);
					return false;
				}
				if (!input.matches("[0-9A-Fa-f]+$")) {
					JOptionPane.showMessageDialog(null,
							"That is not a hexadecimal number! (Example: FA0F34)", "Invalid number", 0);
					return false;
				}
				break;

			case DECIMAL_TO_HEX:
				try {
					@SuppressWarnings("unused")
					int inputAsNumber = Integer.parseInt(input);
				} catch (NumberFormatException nFE) {
					JOptionPane.showMessageDialog(null,
							"That is not a number!", "Invalid number", 0);
					return false;
				}
				break;

			case DECIMAL_TO_BINARY:
				try {
					@SuppressWarnings("unused")
					int inputAsNumber = Integer.parseInt(input);
				} catch (NumberFormatException nFE) {
					JOptionPane.showMessageDialog(null,
							"That is not a number!", "Invalid number", 0);
					return false;
				}
				if (input.length() > 10) {
					JOptionPane.showMessageDialog(null,
							"That number is too big! (Max = 2,147,483,647)", "Invalid number", 0);
					return false;
				}
				break;

			case BINARY_TO_DECIMAL:
				for (int i = 0; i < input.length(); i++) {
					if (input.charAt(i) != '0' && input.charAt(i) != '1') {
						JOptionPane.showMessageDialog(null,
								"That is not a binary number!", "Invalid number", 0);
						return false;
					}
				}
				if (input.length() > 31) {
					JOptionPane.showMessageDialog(null,
							"That number is too big! (Max is 31 bits)", "Invalid number", 0);
					return false;
				}
				break;

		}
		return true;
	}

	/**
	 * Gets the text to print out according to the type of
	 * number used to convert and the type of number that it's
	 * being converted to.<br>
	 * <b>(Returns a String because it can return a number
	 * in a hexadecimal format)</b>
	 * 
	 * @param numberToConvert The number being converted
	 * @param convertedNumber The converted number
	 * @param mode            The current converting mode
	 * @return The text to print out
	 */
	public String getTextForMode(final String numberToConvert, final String convertedNumber, final int mode) {
		switch (mode) {
			case DECIMAL_TO_BINARY:
				return "Decimal: " + NumberFormat.getInstance().format(Integer.parseInt(numberToConvert))
						+ " > Binary: " + convertedNumber + ".";

			case BINARY_TO_DECIMAL:
				String formattedBinary = numberToConvert;
				if (numberToConvert.startsWith("0") && !numberToConvert.equals("0")) {
					for (int i = 0; i <= numberToConvert.length(); i++) { // Loop through all numbers
						if (numberToConvert.charAt(i) == '1') { // Skipped all leading 0s
							formattedBinary = numberToConvert.substring(i, numberToConvert.length()); // Set the rest of
																										// the String
							break;
						}
					}
				}
				return "Binary: " + formattedBinary + " > Decimal: "
						+ NumberFormat.getInstance().format((Integer.parseInt(convertedNumber))) + ".";

			case HEX_TO_DECIMAL:
				return "Hex: " + numberToConvert.toUpperCase() + " > Decimal: "
						+ NumberFormat.getInstance().format(Integer.parseInt(convertedNumber)) + ".";

			case DECIMAL_TO_HEX:
				return "Decimal: " + NumberFormat.getInstance().format((Integer.parseInt(numberToConvert))) + " > Hex: "
						+ convertedNumber.toUpperCase() + ".";

			default:
				return "UNKNOWN";
		}
	}

	/**
	 * Gets the converted number based on the number given
	 * to convert and the current converting mode.
	 * 
	 * @param numberToConvert The number to convert.
	 * @param mode            The current converting mode.
	 * @return The converted number.
	 */
	public String getConvertedNumber(final String numberToConvert, final int mode) {
		switch (mode) {
			case DECIMAL_TO_BINARY:
				return Integer.toBinaryString(Integer.parseInt(numberToConvert));

			case BINARY_TO_DECIMAL:
				return String.valueOf(Integer.parseInt(numberToConvert, 2));

			case HEX_TO_DECIMAL:
				return String.valueOf(Integer.parseInt(numberToConvert, 16));

			case DECIMAL_TO_HEX:
				return Integer.toHexString(Integer.parseInt(numberToConvert));

			default:
				return "UNKNOWN";
		}
	}

	/**
	 * The current converting mode.
	 */
	private int convertingMode = DECIMAL_TO_BINARY;

	/**
	 * The mode used to convert a decimal number into a binary number.
	 */
	private static final int DECIMAL_TO_BINARY = 0;

	/**
	 * The mode used to convert a binary number into a decimal number.
	 */
	private static final int BINARY_TO_DECIMAL = 1;

	/**
	 * The mode used to convert a hexadecimal number into a decimal number.
	 */
	private static final int HEX_TO_DECIMAL = 2;

	/**
	 * The mode used to convert a decimal number into a hex number.
	 */
	private static final int DECIMAL_TO_HEX = 3;

	private static final byte TEXT_FIELD_LENGTH = 10;
	private static final int FRAME_WIDTH = 403;
	private static final int FRAME_HEIGHT = 100;

}