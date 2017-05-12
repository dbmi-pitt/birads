
/* First created by JCasGen Fri May 20 11:31:32 EDT 2016 */
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

/** Annotation for each Line
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * @generated */
public class Line_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Line.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.birads.typesystem.type.Line");



  /** @generated */
  final Feature casFeat_lineNumber;
  /** @generated */
  final int     casFeatCode_lineNumber;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLineNumber(int addr) {
        if (featOkTst && casFeat_lineNumber == null)
      jcas.throwFeatMissing("lineNumber", "edu.pitt.dbmi.birads.typesystem.type.Line");
    return ll_cas.ll_getIntValue(addr, casFeatCode_lineNumber);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLineNumber(int addr, int v) {
        if (featOkTst && casFeat_lineNumber == null)
      jcas.throwFeatMissing("lineNumber", "edu.pitt.dbmi.birads.typesystem.type.Line");
    ll_cas.ll_setIntValue(addr, casFeatCode_lineNumber, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Line_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_lineNumber = jcas.getRequiredFeatureDE(casType, "lineNumber", "uima.cas.Integer", featOkTst);
    casFeatCode_lineNumber  = (null == casFeat_lineNumber) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lineNumber).getCode();

  }
}



    