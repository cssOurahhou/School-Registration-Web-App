package com.orangeandbronze.schoolreg.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.orangeandbronze.schoolreg.domain.Days;
import com.orangeandbronze.schoolreg.domain.Faculty;
import com.orangeandbronze.schoolreg.domain.Period;
import com.orangeandbronze.schoolreg.domain.Schedule;
import com.orangeandbronze.schoolreg.domain.Section;
import com.orangeandbronze.schoolreg.domain.Subject;

public class SectionDao extends Dao {

	/**
	 * Only retrieves one level of prerequisites. Does not recursively get
	 * prerequisites of prerequisites... that's beyond my powers of SQL. Anyone,
	 * none of the use cases require beyond first-level prerequisties.
	 **/
	public Section getById(String sectionNumber) {

		String sql = getSql("SectionDao.getById.sql");

		Section section = null;

		try (Connection conn = getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sectionNumber);
			ResultSet rs = pstmt.executeQuery();

			long pk = 0;
			long fkSubject = 0;
			String scheduleString = null;
			String subjectId = null;

			Faculty instructor = null;

			Set<Subject> prereqs = new HashSet<>();
			while (rs.next()) {
				if (rs.isFirst()) {
					pk = rs.getInt("pk");
					fkSubject = rs.getInt("fk_subject");
					long fkFaculty = rs.getInt("fk_faculty");
					scheduleString = rs.getString("schedule");
					subjectId = rs.getString("subject_id");
					int facultyNum = rs.getInt("faculty_number");

					instructor = newFaculty(fkFaculty, facultyNum);

				}

				// get prereqs
				int fkPrerequisite = rs.getInt("fk_prerequisite");
				Subject prereq = new Subject(rs.getString("prerequisites"));
				setPrimaryKey(prereq, fkPrerequisite);
				prereqs.add(prereq);

			}
			Subject subject = new Subject(subjectId, prereqs);
			setPrimaryKey(subject, fkSubject);

			Schedule schedule = newSchedule(scheduleString);
			section = new Section(sectionNumber, subject, schedule, instructor);
			setPrimaryKey(section, pk);

		} catch (SQLException e) {
			handleException(section, e);
		}

		return section;

	}

	/** Does not get prerequisites. **/
	public Set<Section> getAll() {

		String sql = getSql("SectionDao.getAll.sql");

		Set<Section> sections = new HashSet<>();
		Section currentSection = null;

		try (Connection conn = getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				long pk = rs.getInt("pk");
				String sectionNumber = rs.getString("section_number");
				long fkSubject = rs.getInt("fk_subject");
				long fkFaculty = rs.getInt("fk_faculty");
				String scheduleString = rs.getString("schedule");
				String subjectId = rs.getString("subject_id");
				int facultyNum = rs.getInt("faculty_number");
				Faculty instructor;

				instructor = newFaculty(fkFaculty, facultyNum);

				Subject subject = new Subject(subjectId);
				setPrimaryKey(subject, fkSubject);

				Schedule schedule = newSchedule(scheduleString);
				currentSection = new Section(sectionNumber, subject, schedule, instructor);
				setPrimaryKey(currentSection, pk);

				sections.add(new Section(sectionNumber, subject, schedule, instructor));
			}

		} catch (SQLException e) {
			handleException(currentSection, e);
		}

		return sections;
	}

	private Schedule newSchedule(String scheduleString) {
		String[] dayPeriod = scheduleString.split("\\s+");
		return dayPeriod.length > 1 ? new Schedule(Days.valueOf(dayPeriod[0]), Period.valueOf(dayPeriod[1])) : Schedule.TBA;
	}

	private Faculty newFaculty(long fkFaculty, int facultyNum) {
		Faculty instructor;
		if (fkFaculty != 0) { // not TBA
			instructor = new Faculty(facultyNum);
			setPrimaryKey(instructor, fkFaculty);
		} else { // TBA
			instructor = Faculty.TBA;
		}
		return instructor;
	}

}
