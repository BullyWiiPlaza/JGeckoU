package wiiudev.gecko.client.memory_search.enumerations;

import wiiudev.gecko.client.memory_search.SearchResult;

import java.math.BigInteger;

public enum SearchConditions
{
	EQUAL("Equal"),
	NOT_EQUAL("Not Equal"),
	GREATER_THAN("Greater Than"),
	LESS_THAN("Less Than"),
	GREATER_OR_EQUAL("Greater or Equal"),
	LESS_OR_EQUAL("Less or Equal"),
	FLAG_SET("Flag Set"),
	FLAG_UNSET("Flag Unset"),
	DIFFERENT_BY("Different By"),
	DIFFERENT_BY_GREATER("Different By Greater"),
	DIFFERENT_BY_LESS("Different By Less");

	private String name;

	SearchConditions(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public boolean isTrue(BigInteger searchValue, SearchResult searchResult)
	{
		BigInteger currentValue = searchResult.getCurrentValue();
		int comparisonResult = currentValue.compareTo(searchValue);
		BigInteger previousValue = searchResult.getPreviousValue();
		BigInteger valueDifference = currentValue.subtract(previousValue);

		switch (this)
		{
			case EQUAL:
				return comparisonResult == 0;

			case NOT_EQUAL:
				return comparisonResult != 0;

			case GREATER_THAN:
				return comparisonResult > 0;

			case LESS_THAN:
				return comparisonResult < 0;

			case GREATER_OR_EQUAL:
				return comparisonResult >= 0;

			case LESS_OR_EQUAL:
				return comparisonResult <= 0;

			case FLAG_SET:
				return isFlagSet(currentValue, searchValue);

			case FLAG_UNSET:
				return !isFlagSet(currentValue, searchValue);

			case DIFFERENT_BY:
				return valueDifference.abs().equals(searchValue);

			case DIFFERENT_BY_GREATER:
				return valueDifference.equals(searchValue);

			case DIFFERENT_BY_LESS:
				return valueDifference.equals(searchValue.negate());

			default:
				throw new IllegalArgumentException("Unhandled memory search condition");
		}
	}

	private boolean isFlagSet(BigInteger existingFlags, BigInteger flagToCheck)
	{
		return (existingFlags.and(flagToCheck)).equals(flagToCheck);
	}

	public static SearchConditions parse(String searchCondition)
	{
		for (SearchConditions condition : values())
		{
			if (condition.name.equals(searchCondition))
			{
				return condition;
			}
		}

		throw new IllegalArgumentException("Illegal memory search condition");
	}
}