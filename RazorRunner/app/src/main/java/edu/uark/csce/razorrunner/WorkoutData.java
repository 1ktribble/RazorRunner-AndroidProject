package edu.uark.csce.razorrunner;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkoutData implements Parcelable{
	String workoutName, workoutTime;
    double latitudeA, longitudeA, latitudeB, longitudeB;
    float distance;
	long createdDate;
	int steps;
	
	public String getWorkoutName() {
		return workoutName;
	}
	public String getWorkoutTime() {
		return workoutTime;
	}
	public int getSteps() {
		return steps;
	}
    public float getDistance() {return distance;}
	public long getDate() {return createdDate;}
    public double getLatitudeA() {return latitudeA;}
    public double getLatitudeB() {return latitudeB;}
    public double getLongitudeA() {return longitudeA;}
    public double getLongitudeB() {return longitudeB;}

	public void setWorkoutName(String workoutName) {
		this.workoutName = workoutName;
	}
	public void setWorkoutTime(String workoutTime) {
		this.workoutTime = workoutTime;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
    public void setLatitudeA(double latitudeA){this.latitudeA = latitudeA;}
    public void setLatitudeB(double latitudeB){this.latitudeB = latitudeB;}
    public void setLongitudeA(double longitudeA){this.longitudeA = longitudeA;}
    public void setLongitudeB(double longitudeB){this.longitudeB = longitudeB;}
    public void setDate(long l) {this.createdDate = l;}
	public void setDistance(float distance)
	{
		this.distance = distance;
	}
	public WorkoutData() {
		;
	}
	
	public WorkoutData(Parcel p){
		readFromParcel(p);
	}
	private void readFromParcel(Parcel p) {
		// TODO Auto-generated method stub
		workoutName = p.readString();
		workoutTime = p.readString();
        latitudeA = p.readDouble();
        longitudeA = p.readDouble();
		latitudeB = p.readDouble();
        longitudeB = p.readDouble();
        steps = p.readInt();
	}
	public WorkoutData(String workoutName, String time) {
		super();
		this.workoutName = workoutName;
		this.workoutTime = time;
		this.createdDate = System.currentTimeMillis();
	}

	public WorkoutData(String workoutName, String workoutTime, long c, int steps, float distance,
                       double latitudeA, double latitudeB, double longitudeA, double longitudeB)
	{
		super();
		this.workoutName = workoutName;
		this.workoutTime = workoutTime;
		this.steps = steps;
		this.createdDate = c;
        this.latitudeA = latitudeA;
        this.longitudeA = longitudeA;
        this.latitudeB= latitudeB;
        this.longitudeB = longitudeB;
		this.distance = distance;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(workoutName);
		dest.writeString(workoutTime);
        dest.writeDouble(latitudeA);
        dest.writeDouble(latitudeB);
        dest.writeDouble(longitudeA);
        dest.writeDouble(longitudeB);
        dest.writeFloat(distance);
		dest.writeInt(steps);
	}
	
	public static final Parcelable.Creator<WorkoutData> CREATOR = new Creator<WorkoutData>() {
	            public WorkoutData createFromParcel(Parcel in) {
	                WorkoutData wData = new WorkoutData();
	                wData.workoutName = in.readString();
	                wData.workoutTime = in.readString();
                    wData.latitudeA = in.readDouble();
                    wData.latitudeB = in.readDouble();
                    wData.longitudeA = in.readDouble();
                    wData.latitudeB = in.readDouble();
                    wData.distance = in.readFloat();
                    wData.steps = in.readInt();
	                return wData;
	            }
	 
	            public WorkoutData[] newArray(int size) {
	                return new WorkoutData[size];
	            }
	        };
}

