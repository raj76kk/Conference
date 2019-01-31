package com.conference;

public class Slot
{
	private int max;

	private int min;

	public Slot(int max, int min)
	{
		this.max = max;
		this.min = min;
	}

	public Slot(int max)
	{
		this.max = max;
		this.min = 0;
	}

	public int getMax()
	{
		return max;
	}

	public void setMax(int max)
	{
		this.max = max;
	}

	public int getMin()
	{
		return min;
	}

	public void setMin(int min)
	{
		this.min = min;
	}

	public boolean isValidEvent(Event event, int totalTime)
	{
		if (event.getDuration() > max
				|| event.getDuration() + totalTime > max) {
			return false;
		}
		return true;
	}

	public boolean isValidSession(int totalTime)
	{
		boolean validSession = false;
		if (min != 0) {
			if (totalTime > 0 && totalTime <= max) {
				validSession = true;
			}
		} else {
			if (totalTime == max) {
				validSession = true;
			}
		}
		return validSession;
	}

	@Override
	public String toString()
	{
		return "Slot [max=" + max + ", min=" + min + "]";
	}

}
