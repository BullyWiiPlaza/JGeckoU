package wiiudev.gecko.client.codes;

import wiiudev.gecko.client.conversions.Validation;

import javax.xml.bind.DatatypeConverter;

public class CheatCodeFormatting
{
	private String code;
	private String exceptionMessage;
	private String lineSeparator;

	public CheatCodeFormatting(String code)
	{
		this.code = code;

		setLineSeparator();

		if (!isValid())
		{
			throw new InvalidCheatCodeException(exceptionMessage);
		}
	}

	/**
	 * Sets the line separator String depending on the input code for flexibility
	 */
	private void setLineSeparator()
	{
		String carriageReturn = "\r\n";

		if (code.contains(carriageReturn))
		{
			lineSeparator = carriageReturn;
		} else
		{
			lineSeparator = "\n";
		}
	}

	public String getCode()
	{
		return code;
	}

	/**
	 * Checks if the code length is valid. Empty codes will not be accepted.
	 *
	 * @param code The code to check
	 * @return True if the code has a valid length, false otherwise
	 */
	private boolean hasValidLength(String code)
	{
		return (code.length() - 17) % (17 + lineSeparator.length()) == 0;
	}

	/**
	 * Checks if the code has correct placement of spaces AND new lines
	 *
	 * @param code The code to check
	 * @return True if the code is correctly formatted false otherwise
	 */
	private boolean isCorrectlyFormatted(String code)
	{
		for (int characterIndex = 8; characterIndex < code.length(); )
		{
			char character = code.charAt(characterIndex);

			// Here should be a space
			if ((characterIndex - 8) % (17 + lineSeparator.length()) == 0)
			{
				if (character != ' ')
				{
					return false;
				}
			} else
			{
				// Here should be a line break
				int endIndex = characterIndex + lineSeparator.length();

				if (endIndex > code.length() - 1)
				{
					break;
				}

				String subString = code.substring(characterIndex, endIndex);

				if (!subString.equals(lineSeparator))
				{
					return false;
				}

				characterIndex += lineSeparator.length() - 1;
			}

			characterIndex += 9;
		}

		return true;
	}

	private boolean hasCorrectNumberOfSpaces()
	{
		int lineSeparators = count(lineSeparator);
		int spaces = count(" ");

		return lineSeparators == spaces - 1;
	}

	private int count(String text)
	{
		int occurrences = 0;

		while (code.contains(text))
		{
			code = code.replaceFirst(text, "");
			occurrences++;
		}

		return occurrences;
	}

	private boolean isValid()
	{
		if (code.equals(""))
		{
			exceptionMessage = "The code is empty!";
			return false;
		}

		boolean hasCorrectLength = hasValidLength(code);

		if (!hasCorrectLength)
		{
			exceptionMessage = "The code's length is invalid!";
		}

		boolean isCorrectlyFormatted = isCorrectlyFormatted(code);

		if (!isCorrectlyFormatted)
		{
			exceptionMessage = "The code is not correctly formatted!";
		}

		boolean hasCorrectSpaces = hasCorrectNumberOfSpaces();

		if (!hasCorrectSpaces)
		{
			exceptionMessage = "Incorrect number of spaces/line breaks!";
		}

		boolean isHexadecimal = Validation.isHexadecimal(trimCode(code));

		if (!isHexadecimal)
		{
			exceptionMessage = "The code contains non-hexadecimal characters!";
		}

		return isCorrectlyFormatted && hasCorrectLength && hasCorrectSpaces && isHexadecimal;
	}

	private String trimCode(String code)
	{
		code = code.replaceAll(" ", "");
		code = code.replaceAll(lineSeparator, "");

		return code;
	}

	/**
	 * Converts a cheat code into its byte array representation
	 *
	 * @return The created byte array
	 */
	byte[] getBytes()
	{
		String trimmedCode = trimCode(code);

		return DatatypeConverter.parseHexBinary(trimmedCode);
	}

	public static class InvalidCheatCodeException extends RuntimeException
	{
		public InvalidCheatCodeException(String message)
		{
			super(message);
		}
	}
}