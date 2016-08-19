package wiiudev.gecko.client.search;

import java.math.BigInteger;

public enum SearchCondition
{
	EQUAL("Equal"),
	NOT_EQUAL ("Not Equal"),
	GREATER_THAN ("Greater Than"),
	LESS_THAN ("Less Than"),
	GREATER_OR_EQUAL ("Greater or Equal"),
	LESS_OR_EQUAL ("Less or Equal");

	private String name;

	SearchCondition(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public boolean isTrue(BigInteger targetValue, BigInteger retrievedValue)
	{
		int comparisonResult = retrievedValue.compareTo(targetValue);

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

			default:
				throw new IllegalArgumentException("Unhandled search condition");
		}
	}
}