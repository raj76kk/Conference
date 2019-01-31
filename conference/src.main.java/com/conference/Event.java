package com.conference;

public class Event
{
	private String name;

	private int duration;

	private boolean scheduled;

	public Event(String name, int duration)
	{
		this.name = name;
		this.duration = duration;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public boolean isScheduled()
	{
		return scheduled;
	}

	public void setScheduled(boolean scheduled)
	{
		this.scheduled = scheduled;
	}

	@Override
	public String toString()
	{
		return "Event [name=" + name + ", duration=" + duration + ", scheduled="
				+ scheduled + "]";
	}

}
