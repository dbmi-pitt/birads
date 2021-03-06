

/* First created by JCasGen Sun May 08 17:42:38 EDT 2016 */
package org.apache.ctakes.typesystem.type.refsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** A body part or area, corresponding to the UMLS semantic group of Anatomy.  An Entity based on generic Clinical Element Models (CEMs)
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * XML source: /home/tseytlin/Work/BiRADS_extractor/src/main/resources/edu/pitt/dbmi/birads/typesystem/type/TypeSystem.xml
 * @generated */
public class AnatomicalSite extends Entity {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnatomicalSite.class);
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
  protected AnatomicalSite() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AnatomicalSite(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnatomicalSite(JCas jcas) {
    super(jcas);
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
  //* Feature: bodyLaterality

  /** getter for bodyLaterality - gets 
   * @generated
   * @return value of the feature 
   */
  public BodyLaterality getBodyLaterality() {
    if (AnatomicalSite_Type.featOkTst && ((AnatomicalSite_Type)jcasType).casFeat_bodyLaterality == null)
      jcasType.jcas.throwFeatMissing("bodyLaterality", "org.apache.ctakes.typesystem.type.refsem.AnatomicalSite");
    return (BodyLaterality)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AnatomicalSite_Type)jcasType).casFeatCode_bodyLaterality)));}
    
  /** setter for bodyLaterality - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBodyLaterality(BodyLaterality v) {
    if (AnatomicalSite_Type.featOkTst && ((AnatomicalSite_Type)jcasType).casFeat_bodyLaterality == null)
      jcasType.jcas.throwFeatMissing("bodyLaterality", "org.apache.ctakes.typesystem.type.refsem.AnatomicalSite");
    jcasType.ll_cas.ll_setRefValue(addr, ((AnatomicalSite_Type)jcasType).casFeatCode_bodyLaterality, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: bodySide

  /** getter for bodySide - gets 
   * @generated
   * @return value of the feature 
   */
  public BodySide getBodySide() {
    if (AnatomicalSite_Type.featOkTst && ((AnatomicalSite_Type)jcasType).casFeat_bodySide == null)
      jcasType.jcas.throwFeatMissing("bodySide", "org.apache.ctakes.typesystem.type.refsem.AnatomicalSite");
    return (BodySide)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AnatomicalSite_Type)jcasType).casFeatCode_bodySide)));}
    
  /** setter for bodySide - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBodySide(BodySide v) {
    if (AnatomicalSite_Type.featOkTst && ((AnatomicalSite_Type)jcasType).casFeat_bodySide == null)
      jcasType.jcas.throwFeatMissing("bodySide", "org.apache.ctakes.typesystem.type.refsem.AnatomicalSite");
    jcasType.ll_cas.ll_setRefValue(addr, ((AnatomicalSite_Type)jcasType).casFeatCode_bodySide, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    