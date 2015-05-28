package edu.pitt.dbmi.birads.brok.pojos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import edu.pitt.dbmi.birads.util.BrokLogger;

//
// This class represents the contents of a breast imaging report. There is a
// variable for each of these.
//
@Entity
@Table(name = "REPORT")
@BatchSize(size = 5)
public class Report {

	/**
	 * The id.
	 */
	@Id
	@Column(name = "ID")
	@GenericGenerator(name = "hibseq", strategy = "edu.upmc.opi.caBIG.caTIES.database.ExistingIDPreservingTableHiLoGenerator", parameters = {
			@org.hibernate.annotations.Parameter(name = "table", value = "HIBERNATE_UNIQUE_KEY"),
			@org.hibernate.annotations.Parameter(name = "column", value = "NEXT_HI") })
	@GeneratedValue(generator = "hibseq")
	protected java.lang.Long id;

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	// These variables are what we are trying to extract from the text. "-1"
	// represents that the variable has not yet been found. This was used
	// instead of
	// null because an integer cannot be set to null.

	@Index(name = "ACCESSION_IDX")
	@Column(name = "ACCESSION")
	private String accession;

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	@Index(name = "APPLICATION_STATUS_IDX")
	@Column(name = "APPLICATION_STATUS", length = 50)
	private String applicationStatus;

	public String getApplicationStatus() {
		return this.applicationStatus;
	}

	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	@Column(name = "LEFT_RESULT")
	private String leftResult;

	public String getLeftResult() {
		return leftResult;
	}

	public void setLeftResult(String leftResult) {
		this.leftResult = leftResult;
	}

	@Column(name = "RIGHT_RESULT")
	private String rightResult;

	public String getRightResult() {
		return rightResult;
	}

	public void setRightResult(String rightResult) {
		this.rightResult = rightResult;
	}

	@Column(name = "LEFT_BIRADS")
	private int leftBIRADS = -1;

	public int getLeftBIRADS() {
		return leftBIRADS;
	}

	public void setLeftBIRADS(int leftBIRADS) {
		this.leftBIRADS = leftBIRADS;
	}

	@Column(name = "RIGHT_BIRADS")
	private int rightBIRADS = -1;

	public int getRightBIRADS() {
		return rightBIRADS;
	}

	public void setRightBIRADS(int rightBIRADS) {
		this.rightBIRADS = rightBIRADS;
	}

	@Column(name = "BILATERAL_BIRADS")
	private int bilateralBIRADS = -1;

	public int getBilateralBIRADS() {
		return bilateralBIRADS;
	}

	public void setBilateralBIRADS(int bilateralBIRADS) {
		this.bilateralBIRADS = bilateralBIRADS;
	}

	@Column(name = "NONSPECIFIC_BIRADS")
	private int nonspecificBIRADS = -1;

	public int getNonspecificBIRADS() {
		return nonspecificBIRADS;
	}

	public void setNonspecificBIRADS(int nonspecificBIRADS) {
		this.nonspecificBIRADS = nonspecificBIRADS;
	}

	@Column(name = "LEFT_SUB_CATEGORY")
	private String leftSubCategory;

	public String getLeftSubCategory() {
		return leftSubCategory;
	}

	public void setLeftSubCategory(String leftSubCategory) {
		this.leftSubCategory = leftSubCategory;
	}

	@Column(name = "RIGHT_SUB_CATEGORY")
	private String rightSubCategory;

	public String getRightSubCategory() {
		return rightSubCategory;
	}

	public void setRightSubCategory(String rightSubCategory) {
		this.rightSubCategory = rightSubCategory;
	}

	@Column(name = "BILATERAL_SUB_CATEGORY")
	private String bilateralSubCategory;

	public String getBilateralSubCategory() {
		return bilateralSubCategory;
	}

	public void setBilateralSubCategory(String bilateralSubCategory) {
		this.bilateralSubCategory = bilateralSubCategory;
	}

	@Column(name = "NONSPECIFIC_SUB_CATEGORY")
	private String nonspecificSubCategory;

	public String getNonspecificSubCategory() {
		return nonspecificSubCategory;
	}

	public void setNonspecificSubCategory(String nonspecificSubCategory) {
		this.nonspecificSubCategory = nonspecificSubCategory;
	}

	// The variables listed below are counters.

	@Column(name = "NUM_BREASTS_IMAGED")
	private int numberBreastsImaged;

	public int getNumberBreastsImaged() {
		return numberBreastsImaged;
	}

	public void setNumberBreastsImaged(int numberBreastsImaged) {
		this.numberBreastsImaged = numberBreastsImaged;
	}

	@Column(name = "FOUND_LEFT_BIRADS")
	private int foundLeftBIRADS = 0;

	public int getFoundLeftBIRADS() {
		return foundLeftBIRADS;
	}

	public void setFoundLeftBIRADS(int foundLeftBIRADS) {
		this.foundLeftBIRADS = foundLeftBIRADS;
	}

	@Column(name = "FOUND_RIGHT_BIRADS")
	private int foundRightBIRADS = 0;

	public int getFoundRightBIRADS() {
		return foundRightBIRADS;
	}

	public void setFoundRightBIRADS(int foundRightBIRADS) {
		this.foundRightBIRADS = foundRightBIRADS;
	}

	@Column(name = "FOUND_BILATERAL_BIRADS")
	private int foundBilateralBIRADS = 0;

	public int getFoundBilateralBIRADS() {
		return foundBilateralBIRADS;
	}

	public void setFoundBilateralBIRADS(int foundBilateralBIRADS) {
		this.foundBilateralBIRADS = foundBilateralBIRADS;
	}

	@Column(name = "FOUND_NONSPECIFIC_BIRADS")
	private int foundNonspecificBIRADS = 0;

	public int getFoundNonspecificBIRADS() {
		return foundNonspecificBIRADS;
	}

	public void setFoundNonspecificBIRADS(int foundNonspecificBIRADS) {
		this.foundNonspecificBIRADS = foundNonspecificBIRADS;
	}

	// This simply searches for whether there is an addendum.

	@Column(name = "HAS_ADDENDUM")
	private boolean hasAddendum = false;

	public boolean hasAddendum() {
		return hasAddendum;
	}

	public void setHasAddendum(boolean hasAddendum) {
		this.hasAddendum = hasAddendum;
	}

	// The body and impression are where segmented report text is placed. If
	// the
	// impression cannot be segmented, all the text is assigned to the body.
	@Column(name = "BODY")
	@Type(type = "org.hibernate.type.MaterializedClobType")
	private String body;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Column(name = "IMP")
	@Type(type = "org.hibernate.type.MaterializedClobType")
	private String impression;

	public String getImpression() {
		return impression;
	}

	public void setImpression(String impression) {
		this.impression = impression;
	}
	
	//
	// Store stack trace if report processing fails
	//
	@Column(name = "ERROR_MSG")
	@Type(type = "org.hibernate.type.MaterializedClobType")
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	// removes numbers from report text that might be confused for BIRADS
	// categories (4 o'clock, 5 cm, 2 cc, 3 mL, 1., 2.,etc.)

	// There is one for body and impression. This removes numbers that might
	// be
	// confused for BIRADS categories from the report text.

	public void removeBadNumbers() {

		if (getBody() != null) {

			setBody(getBody().replaceAll(
					"\\b([0-9]+(\\.[0-9]+)?\\s*x\\s*)+([0-9]+(\\.[0-9]+)?)?",
					"")); // dimensions ( 2x4x5 or 1 x 3 x 4

			setBody(getBody()
					.replaceAll(
							"\\b[0-9]\\s*(cm|mm|cc|ml|tesla|month|months|week|weeks|o'clock|:\\d+)\\b",
							"")); // measurements and o'clock positions

			setBody(getBody().replaceAll("\\n[\\t ]*\\(?[0-9]+[\\.:\\)]", "\n")); // numbers
																					// used
																					// for
																					// formatting/bullets

		}

		if (getImpression() != null) {

			setBody(getBody().replaceAll(
					"\\b([0-9]+(\\.[0-9]+)?\\s*x\\s*)+([0-9]+(\\.[0-9]+)?)?",
					"")); // dimensions ( 2x4x5 or 1 x 3 x 4

			setImpression(getImpression()
					.replaceAll(
							"\\b[0-9]\\s*(cm|mm|cc|ml|tesla|month|months|week|weeks|o'clock|:\\d+)\\b",
							""));

			setImpression(getImpression().replaceAll(
					"\\n[\\t ]*\\(?[0-9]+[\\.:\\)]", "\n"));

		}

	}

	// parameter specifies which report text to search 1=whole report,
	// 2=impression only.
	// tries to figure out which breast was examined...
	// returns 0 for unknown, 1 for right, 2 for left, and 3 for bilateral
	// The goal is to determine true or false for if a birads is detected
	// bilaterally, on the right or on the left.

	public int getLaterality(int searchTextCode) {

		String textToSearch = null;

		// Whole report.

		if (searchTextCode == 1) {

			textToSearch = ((getBody() == null) ? "" : getBody())
					+ ((getImpression() == null) ? "" : getImpression());

			// Impression only.

		} else if (searchTextCode == 2) {

			textToSearch = (getImpression() == null) ? "" : getImpression();

		}

		BufferedReader r = new BufferedReader(new StringReader(textToSearch));

		String line;

		boolean foundRight = false;

		boolean foundLeft = false;

		// While loop that states that the method reads one line at a time,
		// until it
		// runs out of lines or gets a bilateral return (1 bilateral return
		// or the combo of a right and left
		// return).

		try {

			while ((line = r.readLine()) != null) {

				// log("getLaterality line:"+line);

				// This looks for any character, anywhere from 0-20 in
				// number, then a word boundary (word boundary = a-z,digit,
				// underscore followed by anything that
				// is not one of these characters), then one of the words in
				// parentheses, then another word
				// boundary, up to 10 characters of anything, then another
				// word boundary, then some laterality
				// description (\\s means a white space, these vary to allow
				// for one or both sides).

				Pattern p1 = Pattern
						.compile(
								".{0,20}\\b(ultrasound|us|sonogram|sonography|mri|magnetic resonance imaging|mammogram|mammography|radiograph|radiography)\\b.{0,10}?\\b(left|right|bilateral|right\\s+and\\s+left|left\\s+and\\s+right)\\b.*",
								Pattern.DOTALL);

				// Same as the above, except in the reverse order such that
				// the sidedness comes before the modality info.

				Pattern p2 = Pattern
						.compile(
								".{0,20}\\b(left|right|bilateral|right\\s+and\\s+left|left\\s+and\\s+right)\\b.{0,10}?\\b(ultrasound|us|sonogram|sonography|mri|magnetic resonance imaging|mammogram|mammography|radiograph|radiography)\\b.*",
								Pattern.DOTALL);

				Matcher m1 = p1.matcher(line);

				Matcher m2 = p2.matcher(line);

				String laterality = (m1.matches()) ? m1.group(2) : (m2
						.matches() ? m2.group(1) : null);

				if (laterality == null)
					continue;

				if (laterality.contains("bilateral")
						|| (laterality.contains("right") && (laterality
								.contains("left")))) {

					return 3;

				} else if (laterality.contains("right")) {

					foundRight = true;

				} else if (laterality.contains("left")) {

					foundLeft = true;

				}

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		// If bilateral birads found, this is indicated by a return of 3.

		if (foundRight && foundLeft)
			return 3;

		// If a bilateral BIRADS was not found, it goes to a less specific
		// method for determining birads below and indicates whether the
		// whole report or impression should be used.

		// if (foundRight) return 1; // changed logic to always use less
		// specific getLaterality below with data found here...

		// if (foundLeft) return 2;

		return getLateralityAllowNullExaminationDescriptor(foundRight,
				foundLeft, searchTextCode);

	} // tries to figure out which breast was examined...

	// returns 0 for unknown, 1 for right, 2 for left, and 3 for bilateral

	// This method looks for sidedness alone without a modality description.
	// Instead, words used to localize this are
	// "breast|breasts|reconstruction|reconstructions|side|findings|results").

	private int getLateralityAllowNullExaminationDescriptor(boolean foundRight,
			boolean foundLeft, int searchTextCode) {

		String textToSearch = null;

		if (searchTextCode == 1) {

			textToSearch = ((getBody() == null) ? "" : getBody())
					+ ((getImpression() == null) ? "" : getImpression());

		} else if (searchTextCode == 2) {

			textToSearch = (getImpression() == null) ? "" : getImpression();

		}

		BufferedReader r = new BufferedReader(new StringReader(textToSearch));

		String line;

		try {

			while ((line = r.readLine()) != null) {

				Pattern p1 = Pattern
						.compile(
								".{0,20}\\b(breast|breasts|reconstruction|reconstructions|side|findings|results)\\b.{0,10}?\\b(left|right|bilateral|right\\s+and\\s+left|left\\s+and\\s+right)\\b.*",
								Pattern.DOTALL);

				Pattern p2 = Pattern
						.compile(
								".{0,20}\\b(left|right|bilateral|right\\s+and\\s+left|left\\s+and\\s+right)\\b.{0,10}?\\b(breast|breasts|reconstruction|reconstructions|side|findings|results)\\b.*",
								Pattern.DOTALL);

				Matcher m1 = p1.matcher(line);

				Matcher m2 = p2.matcher(line);

				String laterality = (m1.matches()) ? m1.group(2) : (m2
						.matches() ? m2.group(1) : null);

				if (laterality == null)
					continue;

				if (laterality.contains("bilateral")
						|| (laterality.contains("right") && (laterality
								.contains("left")))) {

					return 3;

				} else if (laterality.contains("right")) {

					foundRight = true;

				} else if (laterality.contains("left")) {

					foundLeft = true;

				}

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		// Bilateral found.

		if (foundRight && foundLeft)
			return 3;

		// Right only found.

		if (foundRight)
			return 1;

		// Left only found.

		if (foundLeft)
			return 2;

		// Nothing found.

		return 0;

	}

	// This method assigns birads categories based on report text.

	public void assignBIRADS(int debug) throws IOException {

		// If the impression part of the report is blank, then use the body.

		String textToSearch = getImpression();

		if (textToSearch == null) {

			if (debug > 0)
				BrokLogger.getInstance().log(
						"Impression null, trying to use report body.");

			textToSearch = getBody();

		}

		// If the body and impression are blank then state "No report text
		// to search
		// for BI-RADS." into the log file and stop trying to search this
		// report.

		if (textToSearch == null) {

			if (debug > 0)
				BrokLogger.getInstance().log(
						"No report text to search for BI-RADS.");

			return;

		}

		BufferedReader r = new BufferedReader(new StringReader(textToSearch));

		String line;

		// While loop searches one line at a time through the report text.

		while ((line = r.readLine()) != null) {

			line = line.toLowerCase();

			// These regular expressions look for the triple match of
			// laterality,
			// some statement of the word birads/category and the actual
			// numerical value of the number in
			// arabic numbers or roman numerals. This is looking for the
			// "tiple match." There are multiple
			// patterns because it allows these 3 things in any order. The
			// number optionally includes a
			// subcategory a, b or c.

			if (debug > 0)
				BrokLogger.getInstance().log("in assignBIRADS line: " + line);

			Pattern pBIRADSa = Pattern
					.compile(
							".*\\b(left|right|overall|combined|bilateral)\\b.{0,30}?\\b(birads|bi-rads|category)\\b.{0,10}?\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.*",
							Pattern.DOTALL);

			Pattern pBIRADSb = Pattern
					.compile(
							".*\\b(left|right|overall|combined|bilateral)\\b.{0,30}?\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.{0,10}?\\b(birads|bi-rads|category)\\b.*",
							Pattern.DOTALL);

			Pattern pBIRADSc = Pattern
					.compile(
							".*\\b(birads|bi-rads|category)\\b.{0,10}?\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.{0,30}?\\b(left|right|overall|combined|bilateral)\\b.*",
							Pattern.DOTALL);

			Pattern pBIRADSd = Pattern
					.compile(
							".*\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.{0,10}?\\b(birads|bi-rads|category)\\b.{0,30}?\\b(left|right|overall|combined|bilateral)\\b.*",
							Pattern.DOTALL);

			Matcher mBIRADSa = pBIRADSa.matcher(line);

			Matcher mBIRADSb = pBIRADSb.matcher(line);

			Matcher mBIRADSc = pBIRADSc.matcher(line);

			Matcher mBIRADSd = pBIRADSd.matcher(line);

			// If a triple match was found, proceed.

			if (mBIRADSa.matches()) {

				int birads = -1;

				// This records the local birads variable and if it was a
				// roman numeral, converts it to an arabic number and
				// records it to the local birads variable in that
				// format, so that it is now an integer (not a string
				// representation).

				try {

					birads = parseStringToInteger(mBIRADSa.group(3));

				} catch (NumberFormatException e) {

					birads = convertRomanNumeralToArabic(mBIRADSa.group(3));

				}

				// This is the output for the triple match, setting the
				// variables
				// that will ultimately be used to report the birads. This
				// checks to see if a birads was found (left,
				// right or bilateral), increments the appropriate counter
				// and sets the appropriate birads report
				// variable(s).

				if (mBIRADSa.group(1).equals("left")) {

					setFoundLeftBIRADS(getFoundLeftBIRADS() + 1);

					setLeftBIRADS(birads);

					// Subcategories are set to a, b or c if they exist.
					// This is a one character string. The integer birads
					// category is a separate integer from the subcategory.

					if (mBIRADSa.group(4) != null)
						setLeftSubCategory(mBIRADSa.group(4));

				} else if (mBIRADSa.group(1).equals("right")) {

					setFoundRightBIRADS(getFoundRightBIRADS() + 1);

					setRightBIRADS(birads);

					// Subcategories are set to a, b or c if they exist.

					if (mBIRADSa.group(4) != null)
						setRightSubCategory(mBIRADSa.group(4));

				} else {

					setFoundBilateralBIRADS(getFoundBilateralBIRADS() + 1);

					setBilateralBIRADS(birads);

					if (mBIRADSa.group(4) != null) {

						String bilateralSubCategoryText = mBIRADSa.group(4);
						if (bilateralSubCategoryText.trim().length() > 0) {
							setBilateralSubCategory(bilateralSubCategoryText);
						}

					}

				}

				// b, c and d do the same, but with alternative order of the
				// 3 components of the triple match.

			} else if (mBIRADSb.matches()) {

				int birads = -1;

				try {

					birads = parseStringToInteger(mBIRADSb.group(2));

				} catch (NumberFormatException e) {

					birads = convertRomanNumeralToArabic(mBIRADSb.group(2));

				}

				if (mBIRADSb.group(1).equals("left")) {

					setFoundLeftBIRADS(getFoundLeftBIRADS() + 1);

					setLeftBIRADS(birads);

					if (mBIRADSb.group(3) != null)
						setLeftSubCategory(mBIRADSb.group(3));

				} else if (mBIRADSb.group(1).equals("right")) {

					setFoundRightBIRADS(getFoundRightBIRADS() + 1);

					setRightBIRADS(birads);

					if (mBIRADSb.group(3) != null)
						setRightSubCategory(mBIRADSb.group(3));

				} else {

					setFoundBilateralBIRADS(getFoundBilateralBIRADS() + 1);

					setBilateralBIRADS(birads);

					if (mBIRADSb.group(3) != null) {

						setBilateralSubCategory(mBIRADSb.group(3));

					}

				}

			} else if (mBIRADSc.matches()) {

				int birads = -1;

				try {

					birads = parseStringToInteger(mBIRADSc.group(2));

				} catch (NumberFormatException e) {

					birads = convertRomanNumeralToArabic(mBIRADSc.group(2));

				}

				if (mBIRADSc.group(4).equals("left")) {

					setFoundLeftBIRADS(getFoundLeftBIRADS() + 1);

					setLeftBIRADS(birads);

					if (mBIRADSc.group(3) != null)
						setLeftSubCategory(mBIRADSc.group(3));

				} else if (mBIRADSc.group(4).equals("right")) {

					setFoundRightBIRADS(getFoundRightBIRADS() + 1);

					setRightBIRADS(birads);

					if (mBIRADSc.group(3) != null)
						setRightSubCategory(mBIRADSc.group(3));

				} else {

					setFoundBilateralBIRADS(getFoundBilateralBIRADS() + 1);

					setBilateralBIRADS(birads);

					if (mBIRADSc.group(3) != null) {

						setBilateralSubCategory(mBIRADSc.group(3));

					}

				}

			} else if (mBIRADSd.matches()) {

				int birads = -1;

				try {

					birads = parseStringToInteger(mBIRADSd.group(1));

				} catch (NumberFormatException e) {

					birads = convertRomanNumeralToArabic(mBIRADSd.group(1));

				}

				if (mBIRADSd.group(4).equals("left")) {

					setFoundLeftBIRADS(getFoundLeftBIRADS() + 1);

					setLeftBIRADS(birads);

					if (mBIRADSd.group(2) != null)
						setLeftSubCategory(mBIRADSd.group(2));

				} else if (mBIRADSd.group(4).equals("right")) {

					setFoundRightBIRADS(getFoundRightBIRADS() + 1);

					setRightBIRADS(birads);

					if (mBIRADSd.group(2) != null)
						setRightSubCategory(mBIRADSd.group(2));

				} else {

					setFoundBilateralBIRADS(getFoundBilateralBIRADS() + 1);

					setBilateralBIRADS(birads);

					if (mBIRADSd.group(2) != null) {

						setBilateralSubCategory(mBIRADSd.group(2));

					}

				}

			} else {

				if (debug > 0)
					BrokLogger.getInstance().log("No match for: " + line);

			}

		}

		// This is a log for debugging purposes. However, it seems to be
		// missing some logging info (bilateral).

		if (debug > 0)
			BrokLogger.getInstance().log(
					"Ended assignBIRADS with: left BIRADS="
							+ getLeftBIRADS()
							+ (getLeftSubCategory() == null ? ""
									: getLeftSubCategory())
							+

							" and right BIRADS="
							+ getRightBIRADS()
							+ (getRightSubCategory() == null ? ""
									: getRightSubCategory()));

	} // This is intended to do the same as above, but does not require the
		// statement of
		// birads or category.

	public void assignBIRADSAllowNullBIRADSDescription(int debug)
			throws IOException {

		String textToSearch = impression;

		if (textToSearch == null) {

			if (debug > 0)
				BrokLogger.getInstance().log(
						"Impression null, trying to use report body.");

			textToSearch = getBody();

		}

		if (textToSearch == null) {

			if (debug > 0)
				BrokLogger.getInstance().log(
						"No report text to search for BI-RADS.");

			return;

		}

		BufferedReader r = new BufferedReader(new StringReader(textToSearch));

		String line;

		while ((line = r.readLine()) != null) {

			line = line.toLowerCase();

			// Only a laterality and a number are searched for.

			if (debug > 0)
				BrokLogger.getInstance().log("in assignBIRADS line: " + line);

			Pattern pBIRADSa = Pattern
					.compile(
							".*\\b(left|right|overall|combined|bilateral)\\b.{0,30}?\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.*",
							Pattern.DOTALL);

			Pattern pBIRADSc = Pattern
					.compile(
							".*\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.{0,30}?\\b(left|right|overall|combined|bilateral)\\b.*",
							Pattern.DOTALL);

			Matcher mBIRADSa = pBIRADSa.matcher(line);

			Matcher mBIRADSc = pBIRADSc.matcher(line);

			if (mBIRADSa.matches()) {

				int birads = -1;

				// The string is converted into an integer and roman
				// numerals are converted, as needed.

				try {

					birads = parseStringToInteger(mBIRADSa.group(2));

				} catch (NumberFormatException e) {

					birads = convertRomanNumeralToArabic(mBIRADSa.group(2));

				}

				if (mBIRADSa.group(1).equals("left")) {

					setFoundLeftBIRADS(getFoundLeftBIRADS() + 1);

					setLeftBIRADS(birads);

					if (mBIRADSa.group(3) != null)
						setLeftSubCategory(mBIRADSa.group(3));

				} else if (mBIRADSa.group(1).equals("right")) {

					setFoundRightBIRADS(getFoundRightBIRADS() + 1);

					setRightBIRADS(birads);

					if (mBIRADSa.group(3) != null)
						setRightSubCategory(mBIRADSa.group(3));

				} else {

					setFoundBilateralBIRADS(getFoundBilateralBIRADS() + 1);

					setBilateralBIRADS(birads);

					if (mBIRADSa.group(3) != null) {

						setBilateralSubCategory(mBIRADSa.group(3));

					}

				}

				// c does the same thing but reversing the order of number
				// and laterality.

			} else if (mBIRADSc.matches()) {

				int birads = -1;

				try {

					birads = parseStringToInteger(mBIRADSc.group(1));

				} catch (NumberFormatException e) {

					birads = convertRomanNumeralToArabic(mBIRADSc.group(1));

				}

				if (mBIRADSc.group(3).equals("left")) {

					setFoundLeftBIRADS(getFoundLeftBIRADS() + 1);

					setLeftBIRADS(birads);

					if (mBIRADSc.group(2) != null)
						setLeftSubCategory(mBIRADSc.group(2));

				} else if (mBIRADSc.group(3).equals("right")) {

					setFoundRightBIRADS(getFoundRightBIRADS() + 1);

					setRightBIRADS(birads);

					if (mBIRADSc.group(2) != null)
						setRightSubCategory(mBIRADSc.group(2));

				} else {

					setFoundBilateralBIRADS(getFoundBilateralBIRADS() + 1);

					setBilateralBIRADS(birads);

					if (mBIRADSc.group(2) != null) {

						setBilateralSubCategory(mBIRADSc.group(2));

					}

				}

			} else {

				if (debug > 0)
					BrokLogger.getInstance().log("No match for: " + line);

			}

		}

		// Debug logging.

		if (debug > 0)
			BrokLogger.getInstance().log(
					"Ended assignBIRADSAllowNullBIRADSDescription with: left BIRADS="
							+ getLeftBIRADS()
							+ (getLeftSubCategory() == null ? ""
									: getLeftSubCategory())
							+

							" and right BIRADS="
							+ getRightBIRADS()
							+ (getRightSubCategory() == null ? ""
									: getRightSubCategory()));

	} // This is the actual process for converting roman numerals to arabic
		// numbers. The
		// above method called on this method.

	private int convertRomanNumeralToArabic(String romanNumeral) {

		if (romanNumeral == null)
			return -1;

		if (romanNumeral.equals("i"))
			return 1;

		if (romanNumeral.equals("ii"))
			return 2;

		if (romanNumeral.equals("iii"))
			return 3;

		if (romanNumeral.equals("iv"))
			return 4;

		if (romanNumeral.equals("v"))
			return 5;

		if (romanNumeral.equals("vi"))
			return 6;

		return -1;

	}

	// This looks for a number and birads or category, without a laterality
	// descriptor.
	// When a nonspecific birads is found, it is labeled as such, as opposed
	// to above where the birads is
	// recorded with a laterality.

	public void assignBIRADSAllowNullLateralityDescription(int debug)
			throws IOException {

		String textToSearch = getImpression();

		if (textToSearch == null) {

			if (debug > 0)
				BrokLogger.getInstance().log(
						"Impression null, trying to use report body.");

			textToSearch = getBody();

		}

		if (textToSearch == null) {

			if (debug > 0)
				BrokLogger.getInstance().log(
						"No report text to search for BI-RADS.");

			return;

		}

		BufferedReader r = new BufferedReader(new StringReader(textToSearch));

		String line;

		while ((line = r.readLine()) != null) {

			line = line.toLowerCase();

			if (debug > 0)
				BrokLogger.getInstance().log("in assignBIRADS line: " + line);

			Pattern pBIRADSa = Pattern
					.compile(
							".*\\b(birads|bi-rads|category)\\b.{0,10}?\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.*",
							Pattern.DOTALL);

			Pattern pBIRADSc = Pattern
					.compile(
							".*\\b([0-6]{1}|i|ii|iii|iv|v|vi)\\s*([abc]?)\\b.{0,10}?\\b(birads|bi-rads|category)\\b.*",
							Pattern.DOTALL);

			Matcher mBIRADSa = pBIRADSa.matcher(line);

			Matcher mBIRADSc = pBIRADSc.matcher(line);

			if (mBIRADSa.matches()) {

				setFoundNonspecificBIRADS(getFoundNonspecificBIRADS() + 1);

				if (mBIRADSa.group(2).matches("\\d+")) {
					setNonspecificBIRADS(parseStringToInteger(mBIRADSa.group(2)));
				} else if (mBIRADSa.group(2).matches("i|ii|iii|iv|v|vi")) {
					setNonspecificBIRADS(convertRomanNumeralToArabic(mBIRADSa
							.group(2)));
				}

				if (mBIRADSa.group(3) != null)
					setNonspecificSubCategory(mBIRADSa.group(3));

				if (getLeftBIRADS() == -1) {

					setLeftBIRADS(parseStringToInteger(mBIRADSa.group(2)));

					if (mBIRADSa.group(3) != null)
						setLeftSubCategory(mBIRADSa.group(3));

				}

				if (getRightBIRADS() == -1) {

					setRightBIRADS(parseStringToInteger(mBIRADSa.group(2)));

					if (mBIRADSa.group(3) != null)
						setRightSubCategory(mBIRADSa.group(3));

				}

				// This was allowed in the opposite order.

			} else if (mBIRADSc.matches()) {

				setFoundNonspecificBIRADS(getFoundNonspecificBIRADS() + 1);

				setNonspecificBIRADS(parseStringToInteger(mBIRADSa.group(1)));

				if (mBIRADSa.group(2) != null)
					setNonspecificSubCategory(mBIRADSa.group(2));

				if (getLeftBIRADS() == -1) {

					setLeftBIRADS(parseStringToInteger(mBIRADSc.group(1)));

					if (mBIRADSa.group(2) != null)
						setLeftSubCategory(mBIRADSa.group(2));

				}

				if (getRightBIRADS() == -1) {

					setRightBIRADS(parseStringToInteger(mBIRADSc.group(1)));

					if (mBIRADSa.group(2) != null)
						setRightSubCategory(mBIRADSa.group(2));

				}

			} else {

				if (debug > 0)
					BrokLogger.getInstance().log("No match for: " + line);

			}

		}

		if (debug > 0)
			BrokLogger.getInstance().log(
					"Ended assignBIRADSAllowNullLateralityDescription with: left BIRADS="
							+ getLeftBIRADS()
							+ (getLeftSubCategory() == null ? ""
									: getLeftSubCategory())
							+

							" and right BIRADS="
							+ getRightBIRADS()
							+ (getRightSubCategory() == null ? ""
									: getRightSubCategory()));

	}

	private Integer parseStringToInteger(String valueAsString) {
		Integer valueAsInteger = new Integer(-1);
		if (valueAsString == null) {
			;
		} else if (valueAsString.matches("\\d+")) {
			valueAsInteger = Integer.parseInt(valueAsString);
		} else if (valueAsString.matches("i|ii|iii|iv|v|vi")) {
			valueAsInteger = convertRomanNumeralToArabic(valueAsString);
		}
		return valueAsInteger;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: " + String.valueOf(getId()) + "\n");
		sb.append("accession: " + getAccession() + "\n");
		sb.append("applicationStatus: " + getApplicationStatus() + "\n");
		sb.append("leftResult: " + getLeftResult() + "\n");
		sb.append("rightResult: " + getRightResult() + "\n");
		sb.append("foundLeftBIRADS: " + getFoundLeftBIRADS() + "\n");
		sb.append("foundRightBIRADS: " + getFoundRightBIRADS() + "\n");
		sb.append("foundBilateralBIRADS: " + getFoundBilateralBIRADS() + "\n");
		sb.append("foundNonspecificBIRADS: " + getFoundNonspecificBIRADS()
				+ "\n");
		sb.append("laterality: " + getLaterality(2) + "\n");
		sb.append("leftBIRADS: " + getLeftBIRADS() + "\n");
		sb.append("rightBIRADS: " + getRightBIRADS() + "\n");
		sb.append("bilateralBIRADS: " + getBilateralBIRADS() + "\n");
		sb.append("nonspecificBIRADS: " + getNonspecificBIRADS() + "\n");
		sb.append("leftSubCategory: " + getLeftSubCategory() + "\n");
		sb.append("rightSubCategory: " + getLeftSubCategory() + "\n");
		sb.append("bilateralSubCategory: " + getLeftSubCategory() + "\n");
		sb.append("nonspecificSubCategory: " + getLeftSubCategory() + "\n");
		sb.append("body: " + String.valueOf(getBody()) + "\n");
		sb.append("impression: " + String.valueOf(getImpression()) + "\n");

		return sb.toString();
	}

	public String toStringProduction() {
		return "report_" + getAccession() + ".txt" + "," + getLeftResult()
				+ "," + getRightResult() + ",found left: "
				+ getFoundLeftBIRADS() + ",found right: "
				+ getFoundRightBIRADS() + ",found bilateral: "
				+ getFoundBilateralBIRADS() + ",found nonspecific: "
				+ getFoundNonspecificBIRADS() + ",laterality detected: "
				+  getNumberBreastsImaged();
	}

}
