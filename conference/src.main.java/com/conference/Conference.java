package com.conference;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Conference
{
	// max session in a day
	private static final int PER_DAY = 7 * 60;

	private static final DateFormat formater = new SimpleDateFormat("hh:mm a");

	private Map<String, List<List<Event>>> schedule(List<Event> events)
			throws Exception
	{
		int totalTime = getTotalTime(events);
		int possibleDays = (int) (totalTime / PER_DAY) + 1;

		Collections.sort(events, new Comparator<Event>() {

			@Override
			public int compare(Event o1, Event o2)
			{
				return o1.getDuration() - o2.getDuration();
			}
		});

		// morning slots
		List<List<Event>> m = combinations(events, possibleDays,
				new Slot(3 * 60));

		// clearing scheduled talks
		clear(m, events);

		// evening slots
		List<List<Event>> e = combinations(events, possibleDays,
				new Slot(4 * 60, 3 * 60));
		// clearing scheduled talks
		clear(e, events);

		if (!events.isEmpty()) {
			throw new Exception("Unable to schedule");
		}

		Map<String, List<List<Event>>> schedules = new HashMap<>();
		schedules.put("morning", m);
		schedules.put("evening", e);
		return schedules;

	}

	private void clear(List<List<Event>> slotList, List<Event> events)
	{
		for (List<Event> eventList : slotList) {
			for (Event event : eventList) {
				events.remove(event);
			}
		}
	}

	private List<List<Event>> combinations(List<Event> events, int possibleDays,
			Slot slot)
	{
		int listSize = events.size();
		int count = 0;
		List<List<Event>> e = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			int start = i;
			int totalTime = 0;
			List<Event> comb = new ArrayList<>();

			while (start != listSize) {
				int curr = start;
				start++;
				Event event = events.get(curr);
				if (event.isScheduled()
						|| !slot.isValidEvent(event, totalTime)) {
					continue;
				}

				comb.add(event);
				totalTime += event.getDuration();

				if (totalTime >= slot.getMax()) {
					break;
				}
			}

			if (slot.isValidSession(totalTime)) {
				e.add(comb);
				for (Event talk : comb) {
					// marking events selected as scheduled
					talk.setScheduled(true);
				}
				count++;
				if (count == possibleDays) {
					break;
				}
			}
		}
		return e;
	}

	private List<Event> readInput(String filePath) throws FileNotFoundException
	{
		List<Event> events = new ArrayList<>();
		// pass the path to the file as a parameter

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(filePath).getFile());
		Scanner sc = new Scanner(file);

		while (sc.hasNextLine()) {
			String str = sc.nextLine();
			String str1 = str.substring(str.lastIndexOf(" ") + 1);
			str1 = str1.replaceAll("min", "");
			int time = Integer.parseInt(str1);
			String title = str.substring(0, str.lastIndexOf(" ") + 1);
			Event e = new Event(title, time);
			events.add(e);
		}
		return events;
	}

	private int getTotalTime(List<Event> events)
	{
		int totalTime = 0;
		for (Event event : events) {
			totalTime += event.getDuration();
		}
		return totalTime;
	}

	private void printResults(Map<String, List<List<Event>>> schedules)
	{
		String out = "";
		List<List<Event>> mornEvents = schedules.get("morning");
		List<List<Event>> evenEvents = schedules.get("evening");
		for (int day = 0; day < mornEvents.size(); day++) {
			Date date = new Date();
			date.setHours(9);
			date.setMinutes(0);
			LocalDateTime localDateTime = date.toInstant()
					.atZone(ZoneId.systemDefault()).toLocalDateTime();

			Date edate = new Date();
			edate.setHours(16);
			edate.setMinutes(0);
			LocalDateTime eveningTime = edate.toInstant()
					.atZone(ZoneId.systemDefault()).toLocalDateTime();
			out += "Track " + (day + 1) + ":" + "\n";
			for (Event event : mornEvents.get(day)) {
				out += formater
						.format(Date.from(localDateTime
								.atZone(ZoneId.systemDefault()).toInstant()))
						+ " " + event.getName() + "\n";
				localDateTime = localDateTime.plusMinutes(event.getDuration());
			}
			out += formater
					.format(Date.from(localDateTime
							.atZone(ZoneId.systemDefault()).toInstant()))
					+ " Lunch" + "\n";
			localDateTime = localDateTime.plusMinutes(60);

			// to handle cases where morning session has extra session and no
			// evening session
			try {
				for (Event event : evenEvents.get(day)) {
					out += formater
							.format(Date.from(
									localDateTime.atZone(ZoneId.systemDefault())
											.toInstant()))
							+ " " + event.getName() + "\n";
					localDateTime = localDateTime
							.plusMinutes(event.getDuration());
				}
			} catch (Exception e) {
			}
			// if evening event finishes before 4PM Network event at 4PM
			// else it is occured at 5PM if it evening slot finished between 4
			// and 5 PM
			if (localDateTime.isBefore(eveningTime)) {
				out += formater
						.format(Date.from(eveningTime
								.atZone(ZoneId.systemDefault()).toInstant()))
						+ " Network Event" + "\n" + "\n";
			} else {
				out += formater
						.format(Date.from(eveningTime.plusHours(1)
								.atZone(ZoneId.systemDefault()).toInstant()))
						+ " Network Event" + "\n" + "\n";
			}
		}

		System.out.println(out);
	}

	public static void main(String[] args)
	{
		try {
			Conference c = new Conference();
			List<Event> events = c.readInput("testFiles/test1.txt");
			if (events != null && !events.isEmpty()) {
				Map<String, List<List<Event>>> schedules = c.schedule(events);
				c.printResults(schedules);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
