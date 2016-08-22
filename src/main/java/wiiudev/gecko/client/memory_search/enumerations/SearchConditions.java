package wiiudev.gecko.client.memory_search.enumerations;

import java.math.BigInteger;

public enum SearchConditions
{
	EQUAL("Equal"),
	NOT_EQUAL ("Not Equal"),
	GREATER_THAN ("Greater Than"),
	LESS_THAN ("Less Than"),
	GREATER_OR_EQUAL ("Greater or Equal"),
	LESS_OR_EQUAL ("Less or Equal");

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
				throw new IllegalArgumentException("Unhandled memory_search condition");
		}
	}

	public static SearchConditions parse(String searchCondition)
	{
		for(SearchConditions condition : values())
		{
			if(condition.name.equals(searchCondition))
			{
				return condition;
			}
		}

		throw new IllegalArgumentException("Illegal memory_search condition");
	}
}