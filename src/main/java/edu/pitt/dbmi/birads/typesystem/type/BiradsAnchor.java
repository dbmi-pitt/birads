

/* First created by JCasGen Sun May 08 19:08:51 EDT 2016 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Birads Anchor Words
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * XML source: /home/tseytlin/Work/BiRADS_extractor/src/main/resources/edu/pitt/dbmi/birads/typesystem/type/TypeSystem.xml
 * @generated */
public class BiradsAnchor extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(BiradsAnchor.class);
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
  protected BiradsAnchor() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public BiradsAnchor(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public BiradsAnchor(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public BiradsAnchor(JCas jcas, int begin, int end) {
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
  //* Feature: Anchor

  /** getter for Anchor - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAnchor() {
    if (BiradsAnchor_Type.featOkTst && ((BiradsAnchor_Type)jcasType).casFeat_Anchor == null)
      jcasType.jcas.throwFeatMissing("Anchor", "edu.pitt.dbmi.birads.typesystem.type.BiradsAnchor");
    return jcasType.ll_cas.ll_getStringValue(addr, ((BiradsAnchor_Type)jcasType).casFeatCode_Anchor);}
    
  /** setter for Anchor - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnchor(String v) {
    if (BiradsAnchor_Type.featOkTst && ((BiradsAnchor_Type)jcasType).casFeat_Anchor == null)
      jcasType.jcas.throwFeatMissing("Anchor", "edu.pitt.dbmi.birads.typesystem.type.BiradsAnchor");
    jcasType.ll_cas.ll_setStringValue(addr, ((BiradsAnchor_Type)jcasType).casFeatCode_Anchor, v);}    
  }

    