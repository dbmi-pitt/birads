
/* First created by JCasGen Tue May 10 18:21:55 EDT 2016 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * @generated */
public class TimeMonth_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TimeMonth.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
 
  /** @generated */
  final Feature casFeat_Time;
  /** @generated */
  final int     casFeatCode_Time;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTime(int addr) {
        if (featOkTst && casFeat_Time == null)
      jcas.throwFeatMissing("Time", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Time);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTime(int addr, String v) {
        if (featOkTst && casFeat_Time == null)
      jcas.throwFeatMissing("Time", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    ll_cas.ll_setStringValue(addr, casFeatCode_Time, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Month;
  /** @generated */
  final int     casFeatCode_Month;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMonth(int addr) {
        if (featOkTst && casFeat_Month == null)
      jcas.throwFeatMissing("Month", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Month);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMonth(int addr, String v) {
        if (featOkTst && casFeat_Month == null)
      jcas.throwFeatMissing("Month", "edu.pitt.dbmi.birads.typesystem.type.TimeMonth");
    ll_cas.ll_setStringValue(addr, casFeatCode_Month, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TimeMonth_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Time = jcas.getRequiredFeatureDE(casType, "Time", "uima.cas.String", featOkTst);
    casFeatCode_Time  = (null == casFeat_Time) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Time).getCode();

 
    casFeat_Month = jcas.getRequiredFeatureDE(casType, "Month", "uima.cas.String", featOkTst);
    casFeatCode_Month  = (null == casFeat_Month) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Month).getCode();

  }
}



    