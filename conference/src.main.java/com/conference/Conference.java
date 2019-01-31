package com.conference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Conference
{
	// max session in a day
	private static final int PER_DAY = 7 * 60;

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
		if (schedules.get("morning") != null
				&& !schedules.get("morning").isEmpty()) {
			System.out.println("Morning: ");
			for (List<Event> schedule : schedules.get("morning")) {
				for (Event e : schedule) {
					System.out.println(
							e.getName() + "for " + e.getDuration() + " mins.");
				}
			}
		}
		System.out.println();

		if (schedules.get("evening") != null
				&& !schedules.get("evening").isEmpty()) {
			System.out.println("Evening: ");
			for (List<Event> schedule : schedules.get("evening")) {
				for (Event e : schedule) {
					System.out.println(
							e.getName() + "for " + e.getDuration() + " mins.");
				}
			}
		}

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
