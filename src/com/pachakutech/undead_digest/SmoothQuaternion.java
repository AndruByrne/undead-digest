package com.pachakutech.undead_digest;

//This class takes in quaternions and spits out smoothed float elements
//that are re-ordered to transform the axes

public class SmoothQuaternion extends java.lang.Object implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int EVENT_TYPE = 11;
	final int QUAT_ARRAY_SIZE = 8;
	private float[] averageRotationVector = null;
	
	//manual creation
	public SmoothQuaternion()
	{
	}
	
	//ugly hack; don;t know if this is supposed to be in a canonical bean
	public void init()
	{
		averageRotationVector = new float[4];
	}
	
	//Properties
 	public int getType()
	{
		return EVENT_TYPE;
	}
	//transfer axes here

	public float[] getQuat()
	{
		return averageRotationVector;
	}
	
	public void newAverage()
	{
		//this.averageRotationVector = new float[4];
	}

	public void setQuat(float[] newRotationVector)
	{
		
		this.averageRotationVector[0] = (averageRotationVector[0] * (QUAT_ARRAY_SIZE - 1) + newRotationVector[3])/QUAT_ARRAY_SIZE;
		this.averageRotationVector[1] = (averageRotationVector[1] * (QUAT_ARRAY_SIZE - 1) + newRotationVector[1])/QUAT_ARRAY_SIZE;	
		this.averageRotationVector[2] = (averageRotationVector[2] * (QUAT_ARRAY_SIZE - 1) + newRotationVector[2])/QUAT_ARRAY_SIZE;
		this.averageRotationVector[3] = (averageRotationVector[3] * (QUAT_ARRAY_SIZE - 1) + newRotationVector[0])/QUAT_ARRAY_SIZE;
	}
};
