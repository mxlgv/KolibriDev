/***************************************************************************
 *   Copyright © 2005-2011 by Gabriele Svelto                              *
 *   gabriele.svelto@gmail.com                                             *
 *                                                                         *
 *   This file is part of Jelatine.                                        *
 *                                                                         *
 *   Jelatine is free software: you can redistribute it and/or modify      *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   Jelatine is distributed in the hope that it will be useful,           *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with Jelatine.  If not, see <http://www.gnu.org/licenses/>.     *
 ***************************************************************************/

package jelatine;

import java.util.Calendar;
import java.util.Date;

/**
 * Internal implementation of the default Calendar class
 */
public final class VMCalendar extends Calendar
{
    /**
     * Constant representing the era BC (Before Christ).
     */
    public static final int BC = 0;

    /**
     * Constant representing the era AD (Anno Domini).
     */
    public static final int AD = 1;

    /**
     * The point at which the Gregorian calendar rules were used.
     * This may be changed by using setGregorianChange;
     * The default is midnight (UTC) on October 5, 1582 (Julian),
     * or October 15, 1582 (Gregorian).
     *
     * @serial the changeover point from the Julian calendar
     *         system to the Gregorian.
     */
    private long gregorianCutover = (new Date((24 * 60 * 60 * 1000L)
        * (((1582 * (365 * 4 + 1)) / 4 + (java.util.Calendar.OCTOBER
        * (31 + 30 + 31 + 30 + 31) - 9) / 5 + 5) - ((1970 * (365 * 4 + 1))
        / 4 + 1 - 13)))).getTime();

    /**
     * Days in the epoch. Relative Jan 1, year '0' which is not a leap year.
     * (although there is no year zero, this does not matter.)
     * This is consistent with the formula:
     * = (year-1)*365L + ((year-1) >> 2)
     *
     * Plus the gregorian correction:
     *  Math.floor((year-1) / 400.) - Math.floor((year-1) / 100.);
     * For a correct julian date, the correction is -2 instead.
     *
     * The gregorian cutover in 1582 was 10 days, so by calculating the
     * correction from year zero, we have 15 non-leap days (even centuries)
     * minus 3 leap days (year 400,800,1200) = 12. Subtracting two corrects
     * this to the correct number 10.
     */
    private static final int EPOCH_DAYS = 719162;

    /**
     * Constructs a new GregorianCalender representing the current
     * time, using the default time zone and the default locale.
     */
    public VMCalendar()
    {
        super();
        setTimeInMillis(System.currentTimeMillis());
        complete();
    }

    /**
     * Implements the abstract computeTime() method
     */
    protected void computeTime()
    {
        int millisInDay = 0;
        int year = fields[YEAR];
        int month = fields[MONTH];
        int day = fields[DAY_OF_MONTH];

        int minute = fields[MINUTE];
        int second = fields[SECOND];
        int millis = fields[MILLISECOND];
        int[] month_days = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int[] dayCount = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
        int hour = 0;

        // rest of code assumes day/month/year set
        // should negative BC years be AD?
        // get the hour (but no check for validity)
        if (isSet[HOUR])
        {
            hour = fields[HOUR];

            if (fields[AM_PM] == PM)
                hour += 12;
        }
        else
            hour = fields[HOUR_OF_DAY];

        // Read the era,year,month,day fields and convert as appropriate.
        // Calculate number of milliseconds into the day
        // This takes care of both h, m, s, ms over/underflows.
        long allMillis = (((hour * 60L) + minute) * 60L + second) * 1000L + millis;
        day += allMillis / (24 * 60 * 60 * 1000L);
        millisInDay = (int) (allMillis % (24 * 60 * 60 * 1000L));

        if (month < 0)
        {
            year += (int) month / 12;
            month = month % 12;
            if (month < 0)
            {
                month += 12;
                year--;
            }
        }
        if (month > 11)
        {
            year += (month / 12);
            month = month % 12;
        }

        month_days[1] = isLeapYear(year) ? 29 : 28;

        while (day <= 0)
        {
            if (month == 0)
            {
                year--;
                month_days[1] = isLeapYear(year) ? 29 : 28;
            }

            month = (month + 11) % 12;
            day += month_days[month];
        }
        while (day > month_days[month])
        {
            day -= (month_days[month]);
            month = (month + 1) % 12;

            if (month == 0)
            {
                year++;
                month_days[1] = isLeapYear(year) ? 29 : 28;
            }
        }

        // ok, by here we have valid day,month,year,era and millisinday
        int dayOfYear = dayCount[month] + day - 1; // (day starts on 1)
        if (isLeapYear(year) && month > 1)
            dayOfYear++;

        int relativeDay = (year - 1) * 365 + ((year - 1) >> 2) + dayOfYear
            - EPOCH_DAYS; // gregorian days from 1 to epoch.
        int gregFactor = (int) Math.floor((double) (year - 1) / 400.)
            - (int) Math.floor((double) (year - 1) / 100.);

        if ((relativeDay + gregFactor) * 60L * 60L * 24L * 1000L >= gregorianCutover)
            relativeDay += gregFactor;
        else
            relativeDay -= 2;

        time = relativeDay * (24 * 60 * 60 * 1000L) + millisInDay;

        // the epoch was a Thursday.
        int weekday = (int) (relativeDay + THURSDAY) % 7;

        if (weekday <= 0)
            weekday += 7;

        fields[DAY_OF_WEEK] = weekday;

        /* No Time zone corrections are required since the only time zone
         * which must be supported by the CLDC spec is GMT. */

        isTimeSet = true;
    }

    /**
     * Implements the abstract computeFields() method
     */
    protected void computeFields()
    {
        boolean gregorian = (time >= gregorianCutover);


        long day = time / (24 * 60 * 60 * 1000L);
        int millisInDay = (int) (time % (24 * 60 * 60 * 1000L));

        if (millisInDay < 0)
        {
            millisInDay += (24 * 60 * 60 * 1000);
            day--;
        }

        calculateDay(fields, day, gregorian);

        if (millisInDay >= 24 * 60 * 60 * 1000)
        {
            millisInDay -= 24 * 60 * 60 * 1000;
            calculateDay(fields, ++day, gregorian);
        }

        int hourOfDay = millisInDay / (60 * 60 * 1000);
        fields[AM_PM] = (hourOfDay < 12) ? AM : PM;
        int hour = hourOfDay % 12;
        fields[HOUR] = hour;
        fields[HOUR_OF_DAY] = hourOfDay;
        millisInDay %= (60 * 60 * 1000);
        fields[MINUTE] = millisInDay / (60 * 1000);
        millisInDay %= (60 * 1000);
        fields[SECOND] = millisInDay / (1000);
        fields[MILLISECOND] = millisInDay % 1000;

        areFieldsSet = isSet[YEAR] = isSet[MONTH] = isSet[DAY_OF_MONTH]
            = isSet[DAY_OF_WEEK] = isSet[AM_PM] = isSet[HOUR]
            = isSet[HOUR_OF_DAY] = isSet[MINUTE] = isSet[SECOND]
            = isSet[MILLISECOND] = true;
    }

    private boolean isLeapYear(int year)
    {
        // Only years divisible by 4 can be leap years
        if ((year & 3) != 0)
            return false;

        // Is the leap-day a Julian date? Then it's a leap year
        if (! isGregorian(year, 31 + 29 - 1))
            return true;

        // Apply gregorian rules otherwise
        return ((year % 100) != 0 || (year % 400) == 0);
    }

    private boolean isGregorian(int year, int dayOfYear)
    {
        int relativeDay = (year - 1) * 365 + ((year - 1) >> 2) + dayOfYear
            - EPOCH_DAYS; // gregorian days from 1 to epoch.
        int gregFactor = (int) Math.floor((double) (year - 1) / 400.)
            - (int) Math.floor((double) (year - 1) / 100.);

        return ((relativeDay + gregFactor) * 60L * 60L * 24L * 1000L >= gregorianCutover);
    }

    /**
     * Converts the given linear day into era, year, month,
     * day_of_year, day_of_month, day_of_week, and writes the result
     * into the fields array.
     *
     * @param day the linear day.
     * @param gregorian true, if we should use Gregorian rules.
     */
    private void calculateDay(int[] fields, long day, boolean gregorian)
    {
        // the epoch was a Thursday.
        int weekday = (int) (day + THURSDAY) % 7;

        if (weekday <= 0)
            weekday += 7;

        fields[DAY_OF_WEEK] = weekday;

        // get a first approximation of the year.  This may be one
        // year too big.
        int year = 1970 + (int) (gregorian
            ? ((day - 100L) * 400L) / (365L * 400L + 100L - 4L
            + 1L) : ((day - 100L) * 4L) / (365L * 4L + 1L));

        if (day >= 0)
            year++;

        long firstDayOfYear = getLinearDay(year, 1, gregorian);

        // Now look in which year day really lies.
        if (day < firstDayOfYear)
        {
            year--;
            firstDayOfYear = getLinearDay(year, 1, gregorian);
        }

        day -= firstDayOfYear - 1; // day of year,  one based.

        if (year <= 0)
        {
            fields[YEAR] = 1 - year;
        }
        else
        {
            fields[YEAR] = year;
        }

        int leapday = isLeapYear(year) ? 1 : 0;

        if (day <= 31 + 28 + leapday)
        {
            fields[MONTH] = (int) day / 32; // 31->JANUARY, 32->FEBRUARY
            fields[DAY_OF_MONTH] = (int) day - 31 * fields[MONTH];
        }
        else
        {
            // A few more magic formulas
            int scaledDay = ((int) day - leapday) * 5 + 8;
            fields[MONTH] = scaledDay / (31 + 30 + 31 + 30 + 31);
            fields[DAY_OF_MONTH] = (scaledDay % (31 + 30 + 31 + 30 + 31)) / 5 + 1;
        }
    }

    /**
     * Get the linear day in days since the epoch, using the
     * Julian or Gregorian calendar as specified.  If you specify a
     * nonpositive year it is interpreted as BC as following: 0 is 1
     * BC, -1 is 2 BC and so on.
     *
     * @param year the year of the date.
     * @param dayOfYear the day of year of the date; 1 based.
     * @param gregorian <code>true</code>, if we should use the Gregorian rules.
     * @return the days since the epoch, may be negative.
     */
    private long getLinearDay(int year, int dayOfYear, boolean gregorian)
    {
        // The 13 is the number of days, that were omitted in the Gregorian
        // Calender until the epoch.
        // We shift right by 2 instead of dividing by 4, to get correct
        // results for negative years (and this is even more efficient).
        long julianDay = (year - 1) * 365L + ((year - 1) >> 2) + (dayOfYear - 1)
            - EPOCH_DAYS; // gregorian days from 1 to epoch.

        if (gregorian)
        {
            // subtract the days that are missing in gregorian calendar
            // with respect to julian calendar.
            //
            // Okay, here we rely on the fact that the gregorian
            // calendar was introduced in the AD era.  This doesn't work
            // with negative years.
            //
            // The additional leap year factor accounts for the fact that
            // a leap day is not seen on Jan 1 of the leap year.
            int gregOffset = (int) Math.floor((double) (year - 1) / 400.)
                - (int) Math.floor((double) (year - 1) / 100.);

            return julianDay + gregOffset;
        }
        else
            julianDay -= 2;
        return julianDay;
    }

}
