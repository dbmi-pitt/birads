

/* First created by JCasGen Tue Aug 18 16:04:37 EDT 2015 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Holds and numeric value for BIRADS between zero and six
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * XML source: /home/tseytlin/Work/BiRADS_extractor/src/main/resources/edu/pitt/dbmi/birads/typesystem/type/TypeSystem.xml
 * @generated */
public class Birads extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Birads.class);
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
  protected Birads() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Birads(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Birads(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Birads(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets should be a number between 0 and 6
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Birads_Type.featOkTst && ((Birads_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "edu.pitt.dbmi.birads.typesystem.type.Birads");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Birads_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets should be a number between 0 and 6 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Birads_Type.featOkTst && ((Birads_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "edu.pitt.dbmi.birads.typesystem.type.Birads");
    jcasType.ll_cas.ll_setStringValue(addr, ((Birads_Type)jcasType).casFeatCode_value, v);}    
  }

    