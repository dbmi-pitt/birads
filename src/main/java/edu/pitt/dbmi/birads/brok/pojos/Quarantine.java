package edu.pitt.dbmi.birads.brok.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

//
// This class represents the contents of a breast imaging report. There is a
// variable for each of these.
//
@Entity
@Table(name = "QUARANTINE")
@BatchSize(size = 5)
public class Quarantine {

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
	private Long accession;

	public Long getAccession() {
		return accession;
	}

	public void setAccession(Long accession) {
		this.accession = accession;
	}
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: " + String.valueOf(getId()) + "\n");
		sb.append("accession: " + getAccession() + "\n");
		return sb.toString();
	}

}
