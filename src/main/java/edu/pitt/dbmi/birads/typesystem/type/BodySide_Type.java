
/* First created by JCasGen Mon Sep 19 10:07:54 EDT 2016 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Body side s.a. left,right, bilateral
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * @generated */
public class BodySide_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = BodySide.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.birads.typesystem.type.BodySide");
 
  /** @generated */
  final Feature casFeat_side;
  /** @generated */
  final int     casFeatCode_side;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSide(int addr) {
        if (featOkTst && casFeat_side == null)
      jcas.throwFeatMissing("side", "edu.pitt.dbmi.birads.typesystem.type.BodySide");
    return ll_cas.ll_getStringValue(addr, casFeatCode_side);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSide(int addr, String v) {
        if (featOkTst && casFeat_side == null)
      jcas.throwFeatMissing("side", "edu.pitt.dbmi.birads.typesystem.type.BodySide");
    ll_cas.ll_setStringValue(addr, casFeatCode_side, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public BodySide_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_side = jcas.getRequiredFeatureDE(casType, "side", "uima.cas.String", featOkTst);
    casFeatCode_side  = (null == casFeat_side) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_side).getCode();

  }
}



    