package edu.pitt.dbmi.birads.brokprime.pojos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "STATISTICS")
@BatchSize(size = 5)
public class Statistics {

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

	@Column(name = "AT_LEAST_ONE")
	private java.lang.Integer examsWithAtLeastOneBIRADS = 0;
	public java.lang.Integer getExamsWithAtLeastOneBIRADS() {
		return examsWithAtLeastOneBIRADS;
	}
	public void setExamsWithAtLeastOneBIRADS(java.lang.Integer examsWithAtLeastOneBIRADS) {
		this.examsWithAtLeastOneBIRADS = examsWithAtLeastOneBIRADS;
	}
	
	@Column(name = "TOTAL_0")
	private java.lang.Integer totalBIRADS0 = 0;
	public java.lang.Integer getTotalBIRADS0() {
		return totalBIRADS0;
	}
	public void setTotalBIRADS0(java.lang.Integer totalBIRADS0) {
		this.totalBIRADS0 = totalBIRADS0;
	}
	
	
	@Column(name = "TOTAL_1")
	private java.lang.Integer totalBIRADS1 = 0;
	public java.lang.Integer getTotalBIRADS1() {
		return totalBIRADS1;
	}
	public void setTotalBIRADS1(java.lang.Integer totalBIRADS1) {
		this.totalBIRADS1 = totalBIRADS1;
	}
	
	@Column(name = "TOTAL_2")
	private java.lang.Integer totalBIRADS2 = 0;
	public java.lang.Integer getTotalBIRADS2() {
		return totalBIRADS2;
	}
	public void setTotalBIRADS2(java.lang.Integer totalBIRADS2) {
		this.totalBIRADS2 = totalBIRADS2;
	}

	@Column(name = "TOTAL_3")
	private java.lang.Integer totalBIRADS3 = 0;
	public java.lang.Integer getTotalBIRADS3() {
		return totalBIRADS3;
	}
	public void setTotalBIRADS3(java.lang.Integer totalBIRADS3) {
		this.totalBIRADS3 = totalBIRADS3;
	}

	@Column(name = "TOTAL_4")
	private java.lang.Integer totalBIRADS4 = 0;
	public java.lang.Integer getTotalBIRADS4() {
		return totalBIRADS4;
	}
	public void setTotalBIRADS4(java.lang.Integer totalBIRADS4) {
		this.totalBIRADS4 = totalBIRADS4;
	}
	
	@Column(name = "TOTAL_5")
	private java.lang.Integer totalBIRADS5 = 0;
	public java.lang.Integer getTotalBIRADS5() {
		return totalBIRADS5;
	}
	public void setTotalBIRADS5(java.lang.Integer totalBIRADS5) {
		this.totalBIRADS5 = totalBIRADS5;
	}
	
	@Column(name = "TOTAL_6")
	private java.lang.Integer totalBIRADS6 = 0;
	public java.lang.Integer getTotalBIRADS6() {
		return totalBIRADS6;
	}
	public void setTotalBIRADS6(java.lang.Integer totalBIRADS6) {
		this.totalBIRADS6 = totalBIRADS6;
	}
	
	@Column(name = "TOTAL_BOGUS")
	private java.lang.Integer totalBIRADSBogus = 0;
	public java.lang.Integer getTotalBIRADSBogus() {
		return totalBIRADSBogus;
	}
	public void setTotalBIRADSBogus(java.lang.Integer totalBIRADSBogus) {
		this.totalBIRADSBogus = totalBIRADSBogus;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("At least one birads: " + getExamsWithAtLeastOneBIRADS() + "\n");
		sb.append("Total 0 birads: " + getTotalBIRADS0() + "\n");
		sb.append("Total 1 birads: " + getTotalBIRADS1() + "\n");
		sb.append("Total 2 birads: " + getTotalBIRADS2() + "\n");
		sb.append("Total 3 birads: " + getTotalBIRADS3() + "\n");
		sb.append("Total 4 birads: " + getTotalBIRADS4() + "\n");
		sb.append("Total 5 birads: " + getTotalBIRADS5() + "\n");
		sb.append("Total 6 birads: " + getTotalBIRADS6() + "\n");
		sb.append("Total bogus birads: " + getTotalBIRADSBogus() + "\n");
		return sb.toString();
	}


	
}
