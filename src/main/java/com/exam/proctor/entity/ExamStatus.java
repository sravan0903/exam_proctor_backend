package com.exam.proctor.entity;

public enum ExamStatus {
	STARTED,
	SUBMITTED,
    AUTO_SUBMITTED;
	
	 @Override
	    public String toString() {
	        return name().replace("_", " ");
	    }
}
