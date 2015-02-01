package spec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.concordion.integration.junit4.ConcordionRunner;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.orangeandbronze.schoolreg.dao.EnrollmentDao;
import com.orangeandbronze.schoolreg.dao.SectionDao;
import com.orangeandbronze.schoolreg.dao.StudentDao;
import com.orangeandbronze.schoolreg.domain.Enrollment;
import com.orangeandbronze.schoolreg.domain.Section;
import com.orangeandbronze.schoolreg.domain.Student;
import com.orangeandbronze.schoolreg.domain.Subject;
import com.orangeandbronze.schoolreg.service.EnlistService;
import com.orangeandbronze.schoolreg.service.EnlistService.EnlistmentResult;
import com.orangeandbronze.test.IntegrationTest;

@Category(IntegrationTest.class)
@RunWith(ConcordionRunner.class)
public class StudentEnlistsFixture extends DBTestCase {

	private final String datasetFilename = "StudentEnlistsFixture.xml";

	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setDtdMetadata(false);
		return builder.build(getClass().getResourceAsStream(datasetFilename));
	}

	// TO DO: Remove mocks and have this call actual database
	public boolean enlistAndCheckIfSaved(int studentNumber, String sectionId) {
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.jdbc.Driver");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
				"jdbc:mysql://localhost:3306/school_registration?sessionVariables=FOREIGN_KEY_CHECKS=0");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");

		EnlistService service = new EnlistService();

		EnlistmentResult result = service.enlistSections(studentNumber, sectionId);

		Set<Section> resultSections = result.getSuccessfullyEnlisted();
		
		for (Section sec : resultSections) {
			if (sec.getSectionNumber().equals(sectionId)) {
				return true;
			}
		}

		return false;
	}
}
