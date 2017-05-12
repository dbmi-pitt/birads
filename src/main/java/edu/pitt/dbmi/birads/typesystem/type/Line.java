

/* First created by JCasGen Fri May 20 11:31:32 EDT 2016 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Annotation for each Line
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * XML source: /home/tseytlin/Work/BiRADS_extractor/src/main/resources/edu/pitt/dbmi/birads/typesystem/type/TypeSystem.xml
 * @generated */
public class Line extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Line.class);
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
  protected Line() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Line(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Line(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Line(JCas jcas, int begin, int end) {
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
  //* Feature: lineNumber

  /** getter for lineNumber - gets LineNumber
   * @generated
   * @return value of the feature 
   */
  public int getLineNumber() {
    if (Line_Type.featOkTst && ((Line_Type)jcasType).casFeat_lineNumber == null)
      jcasType.jcas.throwFeatMissing("lineNumber", "edu.pitt.dbmi.birads.typesystem.type.Line");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Line_Type)jcasType).casFeatCode_lineNumber);}
    
  /** setter for lineNumber - sets LineNumber 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLineNumber(int v) {
    if (Line_Type.featOkTst && ((Line_Type)jcasType).casFeat_lineNumber == null)
      jcasType.jcas.throwFeatMissing("lineNumber", "edu.pitt.dbmi.birads.typesystem.type.Line");
    jcasType.ll_cas.ll_setIntValue(addr, ((Line_Type)jcasType).casFeatCode_lineNumber, v);}    
  }

    