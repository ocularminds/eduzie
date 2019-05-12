package com.ocularminds.eduzie.common;

import java.util.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public class DateUtil{

  public static String todayOrYesterday(String day) {

       LocalDate today = LocalDate.now();
       if(day.equalsIgnoreCase("Yesterday")){
		   today = today.minusDays(1);
	   }
       return DateTimeFormatter.ofPattern("dd MMM yyyy").format(today);
  }

  public static String hourOrMinutePast(int past,String hourOrMinute,String pattern) {

       LocalDateTime today = LocalDateTime.now();
       if(hourOrMinute.equalsIgnoreCase("Hours")){
       	  today = today.minusHours(past);
	  }else{
		  today = today.minusMinutes(past);
	  }

	  return DateTimeFormatter.ofPattern(pattern).format(today);
  }

  public static String tommorow() {

       LocalDate today = LocalDate.now();
       return today.plusDays(1).getDayOfWeek().toString();
  }

  public static String timeOfDay(){

	   LocalDateTime dateTime = LocalDateTime.now();
       int hour = dateTime.getHour();
       String time = "";
       if(hour >= 5 && hour <= 11){
		  time = "Morning";
	   }else  if(hour >= 12 && hour <= 16){
		   time = "Afternoon";
	   }else if(hour >= 17 && hour <= 21){
		   time = "Evening";
	   }else{
		   time = "Night";
	   }

	   return time;
   }

  public static LocalDate nextWorkingDay() {
	  /*int dayOfMonth = dateTime.getDayOfMonth();//returns the day of the month.
System.out.println("day Of Month is :"+dayOfMonth);
DayOfWeek dow = dateTime.getDayOfWeek();//returns DayOfWeek enum.
System.out.println("dow:"+dow);
int dayOfYear = dateTime.getDayOfYear();//returns the day of the year.
System.out.println("day Of Year is :"+dayOfYear);
int hour = dateTime.getHour();//returns hour of the day.
System.out.println("hour:"+hour);
int monthValue = dateTime.getMonthValue();//returns the number of the month in the year.
System.out.println("month of the date in number is:"+monthValue);
Month month = dateTime.getMonth();// returns the month enum for the month of the year.
System.out.println("month of the date is :"+month);
int sec = dateTime.getSecond();// returns seconds field value for the date time.
System.out.println("Seconds of the date time is:"+sec);
int nano = dateTime.getNano();();// returns nano seconds field value for the date time.
System.out.println("nano seconds of the date time is:"+nano);
int year = dateTime.getYear();();// returns year field value for the date time.
System.out.println("year of the date is :"+year);

LocalDateTime ldtmddm = dateTime.withDayOfMonth(10); // sets the day of the month to 10.
System.out.println("date time after adding modifying day of the month is : "+ldtmddm.toString());
LocalDateTime ldtmddy = dateTime.withDayOfYear(12);//sets the day of year to 12.
System.out.println("date time after adding modifying day of the year is : "+ldtmddy.toString());
LocalDateTime ldtmdh = dateTime.withHour(12);//sets the hour of the day to 12. The other date time fields are not modified.
System.out.println("date time after adding modifying hour is : "+ldtmdh.toString());
LocalDateTime ldtmdmm = dateTime.withMinute(12);//sets the minute of the hour to 12. The other date time fields are not modified.
System.out.println("date time after adding modifying minutes is : "+ldtmdmm.toString());
LocalDateTime ldtmdm = dateTime.withMonth(4);//sets the month to 4. The other date time fields are not modified.
System.out.println("date time after adding modifying month is : "+ldtmdm.toString());
LocalDateTime ldtmdy = dateTime.withYear(2011);//sets the year to 2012. The other date time fields are not modified.
System.out.println("date time after adding modifying year  is : "+ldtmdy.toString());
LocalDateTime ldtmds = dateTime.withSecond(12);//sets the seconds of the minute to 12. The other date time fields are not modified.
System.out.println("date time after adding modifying seconds is : "+ldtmds.toString());
LocalDateTime ldtmdn = dateTime.withNano(120000);//sets the nano seconds of seconds to 120000. The other date time fields are not modified.
System.out.println("date time after adding modifying nanos is : "+ldtmdn.toString());
*/

	 LocalDate today = LocalDate.now();
	 TemporalAdjuster nextWorkingDayAdjuster = TemporalAdjusters.ofDateAdjuster(localDate -> {
	 DayOfWeek dayOfWeek = localDate.getDayOfWeek();

	 if (dayOfWeek == DayOfWeek.FRIDAY) {
	 	return localDate.plusDays(3);
	 } else if (dayOfWeek == DayOfWeek.SATURDAY) {
	 	return localDate.plusDays(2);
	 }
	 	return localDate.plusDays(1);
	 });

	 return today.with(nextWorkingDayAdjuster);
  }

  public static LocalDate parse(String date,String pattern) {
	  //return LocalDate.now();
      return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
  }

  //pattern like "dd MMM yyyy hh:mm a"
  public static LocalDateTime parseWithTime(String date,String pattern) {

	 Date ds = null;
	 try{
		 ds = new SimpleDateFormat(pattern).parse(date);
	 }catch(Exception e){

		 e.printStackTrace();
		 if(date.contains("Yeeay")){

			 //"Yeeay 04:34PM"
			 LocalDate d = LocalDate.now();
			 date = DateTimeFormatter.ofPattern("dd MMM yyyy").format(d)+ date.substring(date.lastIndexOf("Yeeay")+1,date.length());
			 parseWithTime(date,"dd MMM yyyy hh:mma");

		 }

	 }
     return (ds != null)?LocalDateTime.ofInstant(Instant.ofEpochMilli(ds.getTime()), ZoneId.systemDefault()):null;
  }
}