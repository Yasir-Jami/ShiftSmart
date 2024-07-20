package com.example.ShiftSmart;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private final Context context;
    private static final int DATABASE_VERSION = 12; // v12; added nickname column
    // Employee Table and Columns
    private static final String DATABASE_NAME = "Shift-schedule.db";
    private static final String TABLE_EMPLOYEE = "employee";
    private static final String EMPLOYEE_ID = "employeeID";
    private static final String EMPLOYEE_FIRST_NAME = "firstName";
    private static final String EMPLOYEE_LAST_NAME = "lastName";
    private static final String EMPLOYEE_EMAIL = "email";
    private static final String EMPLOYEE_PHONE_NUMBER = "phoneNumber";
    private static final String EMPLOYEE_TRAINED_OPENING = "trainedOpening";
    private static final String EMPLOYEE_TRAINED_CLOSING = "trainedClosing";
    private static final String MONDAY_AVAILABILITY = "mondayAvailability";
    private static final String TUESDAY_AVAILABILITY = "tuesdayAvailability";
    private static final String WEDNESDAY_AVAILABILITY = "wednesdayAvailability";
    private static final String THURSDAY_AVAILABILITY = "thursdayAvailability";
    private static final String FRIDAY_AVAILABILITY = "fridayAvailability";
    private static final String SATURDAY_AVAILABILITY = "saturdayAvailability";
    private static final String SUNDAY_AVAILABILITY = "sundayAvailability";
    private static final String IS_ACTIVE = "isActive";
    private static final String EMPLOYEE_NICKNAME = "nickname";

    // Availabilities Table - uses day availability columns
    private static final String TABLE_AVAILABILITIES = "availabilities";

    // Shift Table and Columns
    private static final String TABLE_SHIFT = "shift";
    private static final String SHIFT_ID = "shiftID";
    private static final String SHIFT_DAY_OF_WEEK = "dayOfWeek";
    private static final String SHIFT_DATE = "date";
    private static final String SHIFT_TYPE = "shiftType";
    private static final String SHIFT_EMPLOYEE_1 = "employee1";
    private static final String SHIFT_EMPLOYEE_2 = "employee2";
    private static final String SHIFT_EMPLOYEE_3 = "employee3";
    private static final String SHIFT_BUSY_DAY = "isBusy";

    // Temp Shift Table - uses same columns as Shift table and is used to hold info on a single day's shift
    private static final String TABLE_TEMP_SHIFT = "temp_shift";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Employee Table and Columns
        String employeeQuery =
                    // Employee Table and Columns
                    "CREATE TABLE " + TABLE_EMPLOYEE +
                    " (" + EMPLOYEE_ID + " INTEGER PRIMARY KEY NOT NULL, "
                    + EMPLOYEE_FIRST_NAME + " TEXT NOT NULL, " + EMPLOYEE_LAST_NAME + " TEXT NOT NULL, "
                    + EMPLOYEE_EMAIL + " TEXT NOT NULL, " + EMPLOYEE_PHONE_NUMBER + " TEXT NOT NULL, "
                    + EMPLOYEE_TRAINED_OPENING + " BOOLEAN NOT NULL CHECK ("+EMPLOYEE_TRAINED_OPENING+" IN (0, 1)), "
                    + EMPLOYEE_TRAINED_CLOSING + " BOOLEAN NOT NULL CHECK ("+EMPLOYEE_TRAINED_CLOSING+" IN (0, 1)), "
                    + MONDAY_AVAILABILITY + " INTEGER NOT NULL, "
                    + TUESDAY_AVAILABILITY + " INTEGER NOT NULL, "
                    + WEDNESDAY_AVAILABILITY + " INTEGER NOT NULL, "
                    + THURSDAY_AVAILABILITY + " INTEGER NOT NULL, "
                    + FRIDAY_AVAILABILITY + " INTEGER NOT NULL, "
                    + SATURDAY_AVAILABILITY + " INTEGER NOT NULL, "
                    + SUNDAY_AVAILABILITY + " INTEGER NOT NULL, "
                            + IS_ACTIVE + " BOOLEAN NOT NULL CHECK (" + IS_ACTIVE + " IN (0, 1)),"
                            + EMPLOYEE_NICKNAME + " TEXT NOT NULL)";


        // Availabilities Table and Columns
        String availabilitiesQuery = "CREATE TABLE " + TABLE_AVAILABILITIES +
                " (" + MONDAY_AVAILABILITY + " INTEGER NOT NULL,"
                + TUESDAY_AVAILABILITY + " INTEGER NOT NULL,"
                + WEDNESDAY_AVAILABILITY + " INTEGER NOT NULL,"
                + THURSDAY_AVAILABILITY + " INTEGER NOT NULL,"
                + FRIDAY_AVAILABILITY + " INTEGER NOT NULL,"
                + SATURDAY_AVAILABILITY + " INTEGER NOT NULL,"
                + SUNDAY_AVAILABILITY + " INTEGER NOT NULL" + ")";

        // Shift Table and Columns
        String shiftQuery =
                "CREATE TABLE " + TABLE_SHIFT + " (" + SHIFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + SHIFT_DATE + " REAL NOT NULL, "
                        + SHIFT_DAY_OF_WEEK + " INTEGER,"
                        + SHIFT_TYPE + " INTEGER NOT NULL,"
                        + SHIFT_EMPLOYEE_1 + " INTEGER REFERENCES "+TABLE_EMPLOYEE+"(employeeID),"
                        + SHIFT_EMPLOYEE_2 + " INTEGER REFERENCES "+TABLE_EMPLOYEE+"(employeeID),"
                        + SHIFT_EMPLOYEE_3 + " INTEGER REFERENCES "+TABLE_EMPLOYEE+"(employeeID),"
                        + SHIFT_BUSY_DAY + " BOOLEAN NOT NULL CHECK (" + SHIFT_BUSY_DAY + " IN (0, 1)));";

        String tempShiftQuery =
                "CREATE TABLE " + TABLE_TEMP_SHIFT
                        + " (" + SHIFT_DATE + " REAL NOT NULL, "
                        + SHIFT_DAY_OF_WEEK + " INTEGER,"
                        + SHIFT_TYPE + " INTEGER NOT NULL,"
                        + SHIFT_EMPLOYEE_1 + " INTEGER REFERENCES "+TABLE_EMPLOYEE+"(employeeID),"
                        + SHIFT_EMPLOYEE_2 + " INTEGER REFERENCES "+TABLE_EMPLOYEE+"(employeeID),"
                        + SHIFT_EMPLOYEE_3 + " INTEGER REFERENCES "+TABLE_EMPLOYEE+"(employeeID),"
                        + SHIFT_BUSY_DAY + " BOOLEAN NOT NULL CHECK (" + SHIFT_BUSY_DAY + " IN (0, 1)));";

        // Execute all queries
        db.execSQL(employeeQuery);
        db.execSQL(availabilitiesQuery);
        db.execSQL(shiftQuery);
        db.execSQL(tempShiftQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVAILABILITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIFT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_SHIFT);
        onCreate(db);
    }

    /**
     * Adds a a new employee record to the database
     * @param id Employee ID
     * @param firstName Employee First Name
     * @param lastName Employee Last Name
     * @param email Employee Email
     * @param phoneNumber Employee Phone Number
     * @param trainedOpening Employee trained for opening
     * @param trainedClosed Employee trained for closing
     */
    public void addEmployee(int id,
                            String firstName, String lastName, String nickname,
                            String email, String phoneNumber,
                            int trainedOpening, int trainedClosed,
                            int[] availabilities){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(EMPLOYEE_ID, id);
        cv.put(EMPLOYEE_FIRST_NAME, firstName);
        cv.put(EMPLOYEE_LAST_NAME, lastName);
        cv.put(EMPLOYEE_NICKNAME, nickname);
        cv.put(EMPLOYEE_EMAIL, email);
        cv.put(EMPLOYEE_PHONE_NUMBER, phoneNumber);
        cv.put(EMPLOYEE_TRAINED_OPENING, trainedOpening);
        cv.put(EMPLOYEE_TRAINED_CLOSING, trainedClosed);
        cv.put(MONDAY_AVAILABILITY, availabilities[0]);
        cv.put(TUESDAY_AVAILABILITY, availabilities[1]);
        cv.put(WEDNESDAY_AVAILABILITY, availabilities[2]);
        cv.put(THURSDAY_AVAILABILITY, availabilities[3]);
        cv.put(FRIDAY_AVAILABILITY, availabilities[4]);
        cv.put(SATURDAY_AVAILABILITY, availabilities[5]);
        cv.put(SUNDAY_AVAILABILITY, availabilities[6]);
        cv.put(IS_ACTIVE, 1);

        long result = db.insert(TABLE_EMPLOYEE, null, cv);

        if (result == -1){
            Toast.makeText(context, "Failed to add employee", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added employee", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Edits an employee's profile given an ID
     * @param oldId Original Employee ID
     * @param newId New Employee ID to change to
     * @param firstName Employee Name
     * @param lastName Employee Last Name
     * @param email Employee Email
     * @param phoneNumber Employee Phone Number
     * @param trainedOpening Whether employee is trained for opening shift
     * @param trainedClosed Whether employee is trained for closing shift
     */
    public void updateEmployee(int oldId, int newId, String firstName, String lastName, String nickname,
                            String email, String phoneNumber,
                               int trainedOpening, int trainedClosed,
                               int[] availabilities){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(EMPLOYEE_ID, newId);
        cv.put(EMPLOYEE_FIRST_NAME, firstName);
        cv.put(EMPLOYEE_LAST_NAME, lastName);
        cv.put(EMPLOYEE_NICKNAME, nickname);
        cv.put(EMPLOYEE_EMAIL, email);
        cv.put(EMPLOYEE_PHONE_NUMBER, phoneNumber);
        cv.put(EMPLOYEE_TRAINED_OPENING, trainedOpening);
        cv.put(EMPLOYEE_TRAINED_CLOSING, trainedClosed);
        cv.put(MONDAY_AVAILABILITY, availabilities[0]);
        cv.put(TUESDAY_AVAILABILITY, availabilities[1]);
        cv.put(WEDNESDAY_AVAILABILITY, availabilities[2]);
        cv.put(THURSDAY_AVAILABILITY, availabilities[3]);
        cv.put(FRIDAY_AVAILABILITY, availabilities[4]);
        cv.put(SATURDAY_AVAILABILITY, availabilities[5]);
        cv.put(SUNDAY_AVAILABILITY, availabilities[6]);

        long result = db.update(TABLE_EMPLOYEE, cv, EMPLOYEE_ID+"=?",
                new String[]{String.valueOf(oldId)});

        if (result == -1){
            Toast.makeText(context, "Failed to change employee profile", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Employee profile updated", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets availabilities from the availability table
     * @return Values currently in availability table as an int array
     */
    public int[] getAvailabilities(){
        SQLiteDatabase db = this.getReadableDatabase();
        int[] availabilities = new int[7];

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_AVAILABILITIES, null);

        if (cursor.getCount() == 0){
            resetAvailabilityTable();
        }
        else {
            cursor.moveToFirst();
            for (int i = 0; i < 7; i++) {
                availabilities[i] = cursor.getInt(i);
            }
        }
        cursor.close();
        return availabilities;
    }

    /**
     * Update availability table in database given a list.
     * @param availabilities int array containing employee availabilities
     */
    public void updateAvailabilities(int[] availabilities){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        resetAvailabilityTable(); // Set to 0 first in case rows are empty

        cv.put(MONDAY_AVAILABILITY, availabilities[0]);
        cv.put(TUESDAY_AVAILABILITY, availabilities[1]);
        cv.put(WEDNESDAY_AVAILABILITY, availabilities[2]);
        cv.put(THURSDAY_AVAILABILITY, availabilities[3]);
        cv.put(FRIDAY_AVAILABILITY, availabilities[4]);
        cv.put(SATURDAY_AVAILABILITY, availabilities[5]);
        cv.put(SUNDAY_AVAILABILITY, availabilities[6]);

        long result = db.update(TABLE_AVAILABILITIES, cv, null,null);
        if (result == -1){
            Toast.makeText(context, "Failed to save employee availabilities", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Saved employee availabilities", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reset all availability values to 0. Used for the addProfileFragment
     * and to ensure a record exists to update.
     */
    public void resetAvailabilityTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_AVAILABILITIES, null);
        cv.put(MONDAY_AVAILABILITY, 0);
        cv.put(TUESDAY_AVAILABILITY, 0);
        cv.put(WEDNESDAY_AVAILABILITY, 0);
        cv.put(THURSDAY_AVAILABILITY, 0);
        cv.put(FRIDAY_AVAILABILITY, 0);
        cv.put(SATURDAY_AVAILABILITY, 0);
        cv.put(SUNDAY_AVAILABILITY, 0);

        if (cursor.getCount() == 0){
            db.insert(TABLE_AVAILABILITIES, null, cv);
        }
        else{
            db.update(TABLE_AVAILABILITIES, cv, null,null);
        }
        cursor.close();
    }

    /**
     * Loads current employee's availabilities into the availability table
     * @param id Employee's ID
     */
    public void loadEmployeeAvailabilities(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_EMPLOYEE + " WHERE " + EMPLOYEE_ID + "=" + id,
                null);

        cursor.moveToFirst();
        int monday = cursor.getInt(7);
        int tuesday = cursor.getInt(8);
        int wednesday = cursor.getInt(9);
        int thursday = cursor.getInt(10);
        int friday = cursor.getInt(11);
        int saturday = cursor.getInt(12);
        int sunday = cursor.getInt(13);
        cursor.close();

        Cursor availabilityCursor = db.rawQuery(
                "SELECT * FROM " + TABLE_AVAILABILITIES,
                null);

        availabilityCursor.moveToFirst();
        cv.put(MONDAY_AVAILABILITY, monday);
        cv.put(TUESDAY_AVAILABILITY, tuesday);
        cv.put(WEDNESDAY_AVAILABILITY, wednesday);
        cv.put(THURSDAY_AVAILABILITY, thursday);
        cv.put(FRIDAY_AVAILABILITY, friday);
        cv.put(SATURDAY_AVAILABILITY, saturday);
        cv.put(SUNDAY_AVAILABILITY, sunday);
        db.update(TABLE_AVAILABILITIES, cv, null,null);
        availabilityCursor.close();
    }

    /**
     * Stores all employee records found in the database in a list.
     * @return list containing all records from the employee table.
     */
    public ArrayList<EmployeeProfile> getEmployeeDataFromTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor employeeCursor = db.rawQuery(
                "SELECT * FROM " + TABLE_EMPLOYEE + " ORDER BY " + EMPLOYEE_FIRST_NAME,
                null);
        ArrayList<EmployeeProfile> employeeProfileArrayList = getEmployeeQueryResults(employeeCursor);
        employeeCursor.close();
        return employeeProfileArrayList;
    }

    /**
     * Retrieves all information about an employee in a list given an ID
     * @param id employee ID
     * @return String[] containing employee information
     */
    public String[] getEmployeeProfile(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] profileText = new String[8];
        // Raw query might be unsafe if user input is somehow allowed
        Cursor employeeCursor = db.rawQuery(
                "SELECT * FROM " + TABLE_EMPLOYEE + " WHERE " + EMPLOYEE_ID + "=" + id,
                null);

        // Search for target row through table linearly
        if (employeeCursor.moveToFirst()){
            while (employeeCursor.getInt(0) != id) {
                    employeeCursor.moveToNext();
            }
                profileText[0] = employeeCursor.getString(0);
                profileText[1] = employeeCursor.getString(1);
                profileText[2] = employeeCursor.getString(2);
                profileText[3] = employeeCursor.getString(3);
                profileText[4] = employeeCursor.getString(4);
                profileText[5] = employeeCursor.getString(5);
                profileText[6] = employeeCursor.getString(6);
                profileText[7] = employeeCursor.getString(15);
                employeeCursor.close();
            }
        return profileText;
    }

    /**
     * Deletes an employee from the database given an ID.
     * Removes any references of them in the shift table.
     * @param id Employee ID
     */
    public void removeEmployee(int id){
        SQLiteDatabase wdb = this.getWritableDatabase();
        SQLiteDatabase rdb = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        String[] shiftEmployees = new String[]
                {SHIFT_EMPLOYEE_1, SHIFT_EMPLOYEE_2, SHIFT_EMPLOYEE_3};
        Cursor shiftCursor;
        // Remove any reference of the employee in the shift table
        String shiftQuery;

        for (int i = 0; i < 3; i++) {
            shiftQuery = "SELECT " + shiftEmployees[i] + " FROM " + TABLE_SHIFT +
                    " WHERE " + shiftEmployees[i] + "=" + id;
            // Start from employee1 up to employee 3
            shiftCursor = rdb.rawQuery(shiftQuery, null);
            if (shiftCursor.moveToFirst() && shiftCursor.getCount() > 0) {
                String employeeValue = shiftCursor.getString(0);

                if (employeeValue != null){
                    if (String.valueOf(id).equals(employeeValue)){
                        cv.putNull(shiftEmployees[i]);
                        wdb.update(TABLE_SHIFT, cv, shiftEmployees[i]
                                + "=" + id, null);
                        cv.clear();
                    }
                }
            }
        }

        long result = wdb.delete(TABLE_EMPLOYEE,  EMPLOYEE_ID + "=" + id, null);

        if (result == -1){
            Toast.makeText(context, "Failed to remove employee profile", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Employee profile removed", Toast.LENGTH_SHORT).show();
        }

        rdb.close();
        wdb.close();
    }

    /**
     * Adds a new shift entry to the database. If the record exists, update it with the new info.
     * @param date Date in the format: 2024-03-08
     * @param shiftType 1 - Opening, 2 - Closing, 3 - Full Day
     * @param employee1 Opening/Closing Employee 1
     * @param employee2 Opening/Closing Employee 2
     * @param employee3 Opening/Closing Employee 3 - only necessary when they are
     * @param isBusy 0 - not a busy day, 1 - is a busy day, requires 3 employees
     */
    public void addOrUpdateShift(int tableType, String date, String shiftType,
                                 int employee1, int employee2,
                                 int employee3, int isBusy){

        // Add or update the table based on tableType
        String table = TABLE_TEMP_SHIFT;
        if (tableType == 1){
            table = TABLE_SHIFT;
        }

        ContentValues cv = new ContentValues();
        SQLiteDatabase rdb = this.getReadableDatabase();
        SQLiteDatabase wdb = this.getWritableDatabase();
        // Variables for swapping an employee already on the shift
        String swapEmployee = "employee1";
        Cursor swapCursor;
        String swapQuery = "SELECT " + swapEmployee + " FROM " + table +
                " WHERE " + SHIFT_DATE + "=" + "'" + date + "'"
                + " AND " + SHIFT_TYPE + "=" + shiftType;
        String[] shiftEmployees = new String[]
                {SHIFT_EMPLOYEE_1, SHIFT_EMPLOYEE_2, SHIFT_EMPLOYEE_3};

        // Put the shift's values in the table
        cv.put(SHIFT_DATE, date);
        cv.put(SHIFT_DAY_OF_WEEK, getDayOfWeek(date));
        cv.put(SHIFT_TYPE, shiftType);
        // -1 indicates to not update that employee, -2 indicates to put a null in that field
        if (employee1 != -1 && employee1 != -2){cv.put(SHIFT_EMPLOYEE_1, employee1);}
        if (employee2 != -1 && employee2 != -2){cv.put(SHIFT_EMPLOYEE_2, employee2);}
        if (employee3 != -1 && employee3 != -2){cv.put(SHIFT_EMPLOYEE_3, employee3);}
        if (employee1 == -2){
            cv.putNull(SHIFT_EMPLOYEE_1);
        }
        if (employee2 == -2){
            cv.putNull(SHIFT_EMPLOYEE_2);
        }
        if (employee3 == -2){
            cv.putNull(SHIFT_EMPLOYEE_3);
        }
        cv.put(SHIFT_BUSY_DAY, isBusy);

        System.out.println("Employee1 ID: " + employee1);
        System.out.println("Employee2 ID: " + employee2);
        System.out.println("Employee3 ID: " + employee3);


        // First, look if there is already an entry matching the date and shift type
        Cursor shiftCursor = rdb.rawQuery("SELECT * FROM " + table +
                " WHERE " + SHIFT_DATE + "=" + "'" + date + "'" + " AND "
                + SHIFT_TYPE + "=" + shiftType, null);

        // Check if an employee's already on the same shift, in a different slot
        swapCursor = rdb.rawQuery(swapQuery, null);

        for (int i = 0; i < 3; i++) {
            swapQuery = "SELECT " + swapEmployee + " FROM " + table +
                    " WHERE " + SHIFT_DATE + "=" + "'" + date + "'"
                    + " AND " + SHIFT_TYPE + "=" + shiftType;

            // Start from employee1 up to employee 3
            swapCursor = rdb.rawQuery(swapQuery, null);
            if (table.equals(TABLE_TEMP_SHIFT) && swapCursor.moveToFirst() && swapCursor.getCount() > 0) {
                String employeeValue = swapCursor.getString(0);

                if (employeeValue != null){
                    if (String.valueOf(employee1).equals(employeeValue)){
                        cv.putNull(shiftEmployees[i]);
                    }
                    else if (String.valueOf(employee2).equals(employeeValue)){
                        cv.putNull(shiftEmployees[i]);
                    }
                    else if (String.valueOf(employee3).equals(employeeValue)){
                        cv.putNull(shiftEmployees[i]);
                    }
                }
                int num = i+2;
                swapEmployee = "employee"+num;
            }
        }

        // Update the record if there is an already an existing entry for that date and shift type
        if (tableType == 0){
            System.out.println("Updating temp shift table...");
            long result = wdb.update(TABLE_TEMP_SHIFT, cv, SHIFT_TYPE+"=?",
                    new String[]{shiftType});
            // Make a toast here if necessary
        }

        else if (shiftCursor.moveToFirst()){
            System.out.println("Updating...");
            long result = wdb.update(TABLE_SHIFT, cv, SHIFT_DATE+"=? AND "+SHIFT_TYPE+"=?",
                    new String[]{date, shiftType});
        }

        // Otherwise, add the new record to the shift table
        else if (shiftCursor.getCount() == 0){
            System.out.println("Inserting...");
            long result = wdb.insert(table, null, cv);
        }
        shiftCursor.close();
        swapCursor.close();
        rdb.close();
        wdb.close();
    }

    /**
     * Gets all the shift data for a given day;
     * used to display relevant data when clicking a day on the calendar.
     * @param date Date to query for in the shift table
     */
    public ArrayList<EmployeeProfile> getShifts(String date, String shiftType) {
        ArrayList<EmployeeProfile> employees = new ArrayList<>();
        ArrayList<EmployeeProfile> temp;
        Cursor employeeCursor;
        SQLiteDatabase db = this.getReadableDatabase();
        String employeeId = "-1";

        Cursor shiftEmployee1Cursor = db.rawQuery("SELECT * FROM " + TABLE_SHIFT
                + " WHERE NOT "+ SHIFT_EMPLOYEE_1 +" IS NULL "
                + " AND "+ SHIFT_DATE +"="+ "'"+date+"'"
                + " AND "+ SHIFT_TYPE +"="+ shiftType, null);
        Cursor shiftEmployee2Cursor = db.rawQuery("SELECT * FROM " + TABLE_SHIFT
                + " WHERE NOT "+ SHIFT_EMPLOYEE_2 +" IS NULL "
                + " AND "+ SHIFT_DATE +"="+ "'"+date+"'"
                + " AND "+ SHIFT_TYPE +"="+ shiftType, null);
        Cursor shiftEmployee3Cursor = db.rawQuery("SELECT * FROM " + TABLE_SHIFT
                + " WHERE NOT "+ SHIFT_EMPLOYEE_3 +" IS NULL "
                + " AND "+ SHIFT_DATE +"="+ "'"+date+"'"
                + " AND "+ SHIFT_TYPE +"="+ shiftType, null);

        ArrayList<Cursor> shiftCursorArrayList = new ArrayList<>();
        shiftCursorArrayList.add(shiftEmployee1Cursor);
        shiftCursorArrayList.add(shiftEmployee2Cursor);
        shiftCursorArrayList.add(shiftEmployee3Cursor);
        // Find the employees matching the IDs stored in the above list in the employee table
        int i = 0;
        while (i < shiftCursorArrayList.size()) {
            if (shiftCursorArrayList.get(i).getCount() != 0) {
                if (shiftCursorArrayList.get(i).moveToFirst()) {
                    employeeId = shiftCursorArrayList.get(i).getString(4+i);
                    employeeCursor = db.rawQuery("SELECT * FROM " + TABLE_EMPLOYEE
                            + " WHERE " + EMPLOYEE_ID + " = "+ employeeId , null);
                    temp = getEmployeeQueryResults(employeeCursor);
                    employees.add(temp.get(0));
                }
            }
            shiftCursorArrayList.get(i).close();
            i++;
        }
            return employees;
    }

    /**
     * Gets the profile of an employee working a specified shift
     * @param date Date of the shift the employee is working
     * @param shiftType Type of shift employee is working (opening, closing, fullday)
     * @param position Which employee they are (employee 1, 2, or 3)
     * @return the employee's profile
     */
    public EmployeeProfile getEmployeeOnShift(String date, String shiftType, int position) {
        EmployeeProfile employee = null;
        ArrayList<EmployeeProfile> temp;
        Cursor employeeCursor;
        SQLiteDatabase db = this.getReadableDatabase();
        String employeeId = "-1";
        String employeePosition = "";

        if (position == 1){
            employeePosition = SHIFT_EMPLOYEE_1;
        }
        else if (position == 2){
            employeePosition = SHIFT_EMPLOYEE_2;
        }
        else if (position == 3){
            employeePosition = SHIFT_EMPLOYEE_3;
        }

        Cursor shiftEmployeeCursor = db.rawQuery("SELECT * FROM " + TABLE_TEMP_SHIFT
                + " WHERE NOT "+ employeePosition +" IS NULL "
                + " AND "+ SHIFT_DATE +"="+ "'"+date+"'"
                + " AND "+ SHIFT_TYPE +"="+ shiftType, null);

        // Find the employees matching the IDs stored in the above list in the employee table
            if (shiftEmployeeCursor.getCount() != 0) {
                if (shiftEmployeeCursor.moveToFirst()) {
                    employeeId = shiftEmployeeCursor.getString(2+position);
                    employeeCursor = db.rawQuery("SELECT * FROM " + TABLE_EMPLOYEE
                            + " WHERE " + EMPLOYEE_ID + " = "+ employeeId , null);
                    temp = getEmployeeQueryResults(employeeCursor);
                    employee = temp.get(0);
                }
            }
        shiftEmployeeCursor.close();
        return employee;
    }

    /**
     * Gets all the employees available for a given day and type to display in the add shift menu.
     * @param date current date in the format "2024-03-08"
     * @param shiftType shift type (opening(1) / closing(2) / fullday(3))
     */
    public ArrayList<EmployeeProfile> getAvailableEmployees(String date, String shiftType){
        int day = getDayOfWeek(date);
        int available = 0;
        String column = null; // Which column to choose
        SQLiteDatabase db = this.getReadableDatabase();

        // Opening/Fullday shift
        if (shiftType.equals("1") || shiftType.equals("3")){
            available = 1;
        }
        // Closing shift
        else if (shiftType.equals("2")){
            available = 2;
        }

        switch(day){
            case 1:
                column = MONDAY_AVAILABILITY;
                break;
            case 2:
                column = TUESDAY_AVAILABILITY;
                break;
            case 3:
                column = WEDNESDAY_AVAILABILITY;
                break;
            case 4:
                column = THURSDAY_AVAILABILITY;
                break;
            case 5:
                column = FRIDAY_AVAILABILITY;
                break;
            case 6:
                column = SATURDAY_AVAILABILITY;
                break;
            case 7:
                column = SUNDAY_AVAILABILITY;
                break;
        }

        // Get all the employees based on what shift they are available for
        Cursor employeeCursor = db.rawQuery("SELECT * FROM " + TABLE_EMPLOYEE
                + " WHERE "+ column +" = "+ available +" OR "+ column +" = 3"
                + " ORDER BY "+ EMPLOYEE_FIRST_NAME, null);

        ArrayList<EmployeeProfile> employees = getEmployeeQueryResults(employeeCursor);
        employeeCursor.close();
        db.close();
        return employees;
    }

    /**
     * Resets shift table in the database. For debugging purposes only.
     */
    public void resetAllShifts(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHIFT, "1", null);
        db.close();
    }

    /**
     * Gets all the employee profiles from the employee table that match the given cursor's query
     * @param employeeCursor cursor representing a table row, starts from the first row until the last
     * @return an ArrayList of the employee profiles that fit the query's criteria
     */
    private ArrayList<EmployeeProfile> getEmployeeQueryResults(Cursor employeeCursor){
        ArrayList<EmployeeProfile> employees = new ArrayList<>();
        // Start at first row
        if (employeeCursor.moveToFirst()){
            do {
                int[] availabilities = new int[7];
                // Get availabilities
                availabilities[0] = employeeCursor.getInt(7);
                availabilities[1] = employeeCursor.getInt(8);
                availabilities[2] = employeeCursor.getInt(9);
                availabilities[3] = employeeCursor.getInt(10);
                availabilities[4] = employeeCursor.getInt(11);
                availabilities[5] = employeeCursor.getInt(12);
                availabilities[6] = employeeCursor.getInt(13);

                // Add all data as a class object
                employees.add(
                        new EmployeeProfile(
                                employeeCursor.getString(0),
                                employeeCursor.getString(1),
                                employeeCursor.getString(2),
                                employeeCursor.getString(15),
                                employeeCursor.getString(3),
                                employeeCursor.getString(4),
                                employeeCursor.getInt(5),
                                employeeCursor.getInt(6),
                                availabilities,
                                employeeCursor.getInt(14)
                        )
                );
            }
            while (employeeCursor.moveToNext()); // Move to next row until reaching the last record
        }
        return employees;
    }

    /**
     * Returns the day of the week given a calendar date (such as 2024-03-08).
     * @param date String representing a calendar date
     * @return The day of the week as an integer (1 - Monday, 7 - Sunday)
     */
    public int getDayOfWeek(String date){
        LocalDate selectedDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = selectedDate.getDayOfWeek(); // 1 is Monday, 7 is Sunday
        return dayOfWeek.getValue();
    }

    /**
     * Resets temp shift tables. Used to ensure a valid record exists.
     */
    public void resetTempShiftTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEMP_SHIFT, null);
        cv.put(SHIFT_DATE, "2000-01-01");
        cv.put(SHIFT_DAY_OF_WEEK, -1);
        cv.putNull(SHIFT_EMPLOYEE_1);
        cv.putNull(SHIFT_EMPLOYEE_2);
        cv.putNull(SHIFT_EMPLOYEE_3);
        cv.put(SHIFT_TYPE, 1);
        cv.put(SHIFT_BUSY_DAY, 0);

        if (cursor.getCount() == 0){
            db.insert(TABLE_TEMP_SHIFT, null, cv);
            cv.put(SHIFT_TYPE, 2);
            db.insert(TABLE_TEMP_SHIFT, null, cv);
            cv.put(SHIFT_TYPE, 3);
            db.insert(TABLE_TEMP_SHIFT, null, cv);
        }
        else{
            db.update(TABLE_TEMP_SHIFT, cv, SHIFT_TYPE + "=1",null);
            cv.put(SHIFT_TYPE, 2);
            db.update(TABLE_TEMP_SHIFT, cv, SHIFT_TYPE + "=2",null);
            cv.put(SHIFT_TYPE, 3);
            db.update(TABLE_TEMP_SHIFT, cv, SHIFT_TYPE + "=3",null);
        }
        cursor.close();
    }

    /**
     * Loads the shift info for a selected day into the temp table from the shift table.
     * @param date the date the shifts have been scheduled for
     */
    public void loadShiftsIntoTemp(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String shiftType;

        // First check if temp table is empty
        Cursor tempShiftCursor = db.rawQuery("SELECT * FROM " + TABLE_TEMP_SHIFT, null);

        if (tempShiftCursor.getCount() < 3){
            resetTempShiftTable();
        }
        tempShiftCursor.close();

        Cursor shiftCursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SHIFT + " WHERE " + SHIFT_DATE + "=\"" + date + "\"",
                null);

        if (shiftCursor.moveToFirst()){
            shiftType = shiftCursor.getString(3);
            cv.put(SHIFT_DATE, shiftCursor.getString(1));
            cv.put(SHIFT_DAY_OF_WEEK, shiftCursor.getString(2));
            cv.put(SHIFT_TYPE, shiftType);
            cv.put(SHIFT_EMPLOYEE_1, shiftCursor.getString(4));
            cv.put(SHIFT_EMPLOYEE_2, shiftCursor.getString(5));
            cv.put(SHIFT_EMPLOYEE_3, shiftCursor.getString(6));
            cv.put(SHIFT_BUSY_DAY, shiftCursor.getString(7));
            db.update(TABLE_TEMP_SHIFT, cv,
                    SHIFT_TYPE + "=\"" + shiftType + "\"",null);

            // If there is a second shift
            if (shiftCursor.moveToNext()){
                shiftType = shiftCursor.getString(3);
                cv.put(SHIFT_DATE, shiftCursor.getString(1));
                cv.put(SHIFT_DAY_OF_WEEK, shiftCursor.getString(2));
                cv.put(SHIFT_TYPE, shiftType);
                cv.put(SHIFT_EMPLOYEE_1, shiftCursor.getString(4));
                cv.put(SHIFT_EMPLOYEE_2, shiftCursor.getString(5));
                cv.put(SHIFT_EMPLOYEE_3, shiftCursor.getString(6));
                cv.put(SHIFT_BUSY_DAY, shiftCursor.getString(7));
                db.update(TABLE_TEMP_SHIFT, cv,
                        SHIFT_TYPE + "=\"" + shiftType + "\"",null);
            }
        }
        else {
            shiftCursor.close();
            return; // No shifts found for the date
        }
        shiftCursor.close();
    }

    /**
     * Resets all shifts of a given date.
     * @param date Current day in YYYY-DD-MM format
     */
    public void resetShifts(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor shiftCursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SHIFT + " WHERE " + SHIFT_DATE + "=\"" + date + "\"",//date,
                null);

        // Check if there is any record matching the date
        if (shiftCursor.moveToFirst()){
            cv.putNull(SHIFT_EMPLOYEE_1);
            cv.putNull(SHIFT_EMPLOYEE_2);
            cv.putNull(SHIFT_EMPLOYEE_3);
            db.update(TABLE_SHIFT, cv, SHIFT_DATE+"=\"" + date + "\"",null);
        }
        shiftCursor.close();
    }

    /**
     * Load the temp shift table's data.
     * @return string array carrying temp shift table's data
     */
    public String[] getTempShiftData(String dayType){
        String[] tableData = new String[6];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (dayType.equals("weekday")){
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_TEMP_SHIFT + " ORDER BY " + SHIFT_TYPE,
                    null);
        }
        else{
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_TEMP_SHIFT + " WHERE " + SHIFT_TYPE + "="+"3",
                    null);
        }

        if (cursor.moveToFirst()){
            tableData[0] = cursor.getString(3);
            tableData[1] = cursor.getString(4);
            tableData[2] = cursor.getString(5);

            // If there is a second shift
            if (cursor.moveToNext()){
                tableData[3] = cursor.getString(3);
                tableData[4] = cursor.getString(4);
                tableData[5] = cursor.getString(5);
            }
        }
        cursor.close();
        return tableData;
    }

    public void resetAllTempShifts(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMP_SHIFT, "1", null);
        db.close();
    }
}