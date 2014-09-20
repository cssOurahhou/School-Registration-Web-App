package com.orangeandbronze.schoolreg.domain;

import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;

public class EnrollmentTest {

	@Test
	public void enlistInFirstSection() {
		Enrollment enrollment = new Enrollment(123, new Student(456), new Term(new Date(0), new Date()));
		Section section = new Section("MTH123", new Subject("ENG101"));
		enrollment.enlist(section);
		assertTrue( enrollment.getSections().contains(section));
	}
	
	@Test(expected = EnlistmentConflictException.class)
	public void enlistInSectionWithScheduleConflict() {
		Enrollment enrollment = new Enrollment(123, new Student(456), new Term(new Date(0), new Date()));
		Section sec1 = new Section("MTH123", new Subject("ENG101"), new Schedule(Days.MTH, Period.AM10));
		enrollment.enlist(sec1);
		Section sec2 = new Section("TFX123", new Subject("BA101"), new Schedule(Days.MTH, Period.AM10));
		enrollment.enlist(sec2);
	}
	
	@Test(expected = MissingPrerequisitesException.class)
	public void enlistInSectionWithoutPrerequisite() {
		Subject math11 = new Subject("MATH 11");
		Subject math14 = new Subject("MATH 14");
		Set<Subject> prerequisites = new HashSet<Subject>() {{ add(math11); add(math14); }};
		Subject math53 = new Subject("MATH 53", prerequisites);
		Section section = new Section("ABC123", math53);
		Enrollment enrollment = new Enrollment(123, new Student(456), new Term(new Date(0), new Date()));
		enrollment.enlist(section);
	}
	
	@Test(expected = MissingPrerequisitesException.class)
	public void enlistInSectionWithOnlyPartialPrerequisite() {
		Subject math11 = new Subject("MATH 11");
		Subject math14 = new Subject("MATH 14");
		Set<Subject> prerequisitesToNewSection = new HashSet<Subject>() {{ add(math11); add(math14); }};
		Subject math53 = new Subject("MATH 53", prerequisitesToNewSection);
		Section newSection = new Section("ABC123", math53);
		
		Student student = new Student(456);
		
		Enrollment previousEnrollment = new Enrollment(122, student, new Term(new Date(0), new Date(1)));
		Section oldSection = new Section("XXX111", math11);
		
		Enrollment enrollment = new Enrollment(123, student, new Term(new Date(2), new Date(3)));		
		enrollment.enlist(newSection);
	}


}