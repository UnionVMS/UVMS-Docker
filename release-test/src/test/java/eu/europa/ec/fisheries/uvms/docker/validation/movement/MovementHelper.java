package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MovementHelper {

	public static int YEAR = 2017;
	public static int MONTH = 9;
	public static int DAY = 17;

	public static int SEC = 30;
	public static int MILLIS = 42;

	public static int START_HOUR = 8;
	public static int MINUTE_DELTA = 15;

	public static List<LatLong> createRutt() {

		List<LatLong> rutt = new ArrayList<>();

		int hour = START_HOUR;
		int minute = 0;
		rutt.add(new LatLong(57.42920, 11.58259, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42905, 11.58192, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42897, 11.58149, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42882, 11.58116, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));

		hour += 1;
		minute = 0;

		rutt.add(new LatLong(57.42858, 11.58071, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42825, 11.57973, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42796, 11.57890, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42762, 11.57814, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.42707, 11.57713, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42624, 11.57576, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42550, 11.57458, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42462, 11.57373, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.42386, 11.57265, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42316, 11.57141, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42264, 11.56922, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42194, 11.56721, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.42148, 11.56490, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42111, 11.56212, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42091, 11.55908, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42073, 11.55707, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.42011, 11.55375, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41934, 11.55112, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41829, 11.54826, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41664, 11.54486, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.41529, 11.54237, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41438, 11.54038, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41312, 11.53614, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41239, 11.53068, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.41131, 11.52269, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41041, 11.51412, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.40870, 11.50024, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.40727, 11.48819, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.40563, 11.48224, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.40256, 11.47660, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.39744, 11.46579, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.39507, 11.46002, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.38956, 11.42624, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.37787, 11.40996, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.36099, 11.38318, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.34045, 11.25876, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.31126, 11.9727, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.27140, 10.46655, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.25455, 10.36438, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.28647, 10.35944, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.35723, 10.35944, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.41104, 10.36603, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42216, 10.36026, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42711, 10.36263, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		
		hour += 1;
		minute = 0;
		
		rutt.add(new LatLong(57.42794, 10.35769, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42862, 10.35563, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42945, 10.35521, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));
		minute += MINUTE_DELTA;
		rutt.add(new LatLong(57.42946, 10.35416, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));

		hour += 1;
		minute = 0;

		
		rutt.add(new LatLong(57.42928, 10.35400, getDate(YEAR, MONTH, DAY, hour, minute, SEC, MILLIS)));

		return rutt;

	}

	private static Date getDate(int year4, int month, int day, int hour, int minute, int sec, int millis) {

		Calendar myCalendar = Calendar.getInstance();
		myCalendar.set(Calendar.YEAR, year4);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);

		myCalendar.set(Calendar.HOUR, hour);
		myCalendar.set(Calendar.MINUTE, minute);
		myCalendar.set(Calendar.SECOND, sec);
		myCalendar.set(Calendar.MILLISECOND, millis);
		Date date = myCalendar.getTime();
		return date;

	}

}
