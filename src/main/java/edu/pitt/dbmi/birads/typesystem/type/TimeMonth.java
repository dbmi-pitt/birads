

/* First created by JCasGen Tue May 10 18:21:55 EDT 2016 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * XML source: /home/tseytlin/Work/BiRADS_extractor/src/main/resources/edu/pitt/dbmi/birads/typesystem/type/TypeSystem.xml
 * @generated */
public class TimeMonth extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TimeMonth.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TimeMonth() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TimeMonth(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TimeMonth(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TimeMonth(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Time

  /** getter for Time - gets Time in 12 hours
   * @generated
   * @return value of the feature 
   */
  public String getTime() {
    if (TimeMonth_Type.featOkTst && ((TimeMonth_Type)jcasType).casFeat_Time == null)
      jcasType.jcas.throwFeatMissing("Time", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TimeMonth_Type)jcasType).casFeatCode_Time);}
    
  /** setter for Time - sets Time in 12 hours 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTime(String v) {
    if (TimeMonth_Type.featOkTst && ((TimeMonth_Type)jcasType).casFeat_Time == null)
      jcasType.jcas.throwFeatMissing("Time", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    jcasType.ll_cas.ll_setStringValue(addr, ((TimeMonth_Type)jcasType).casFeatCode_Time, v);}    
   
    
  //*--------------*
  //* Feature: Month

  /** getter for Month - gets Temporal Expressions
   * @generated
   * @return value of the feature 
   */
  public String getMonth() {
    if (TimeMonth_Type.featOkTst && ((TimeMonth_Type)jcasType).casFeat_Month == null)
      jcasType.jcas.throwFeatMissing("Month", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TimeMonth_Type)jcasType).casFeatCode_Month);}
    
  /** setter for Month - sets Temporal Expressions 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMonth(String v) {
    if (TimeMonth_Type.featOkTst && ((TimeMonth_Type)jcasType).casFeat_Month == null)
      jcasType.jcas.throwFeatMissing("Month", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    jcasType.ll_cas.ll_setStringValue(addr, ((TimeMonth_Type)jcasType).casFeatCode_Month, v);}    
  }

    