
/* First created by JCasGen Sun May 08 17:42:42 EDT 2016 */
package org.apache.ctakes.typesystem.type.textsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** Used to indicate that a procedure or assertion did not occur or does not exist. If indicated, negation is present and polarity should be -1.  If not indicated, negation is not present and polarity should be +1 by default.
 * Updated by JCasGen Mon Sep 19 10:07:55 EDT 2016
 * @generated */
public class PolarityModifier_Type extends Modifier_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PolarityModifier.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.textsem.PolarityModifier");
 
  /** @generated */
  final Feature casFeat_indicated;
  /** @generated */
  final int     casFeatCode_indicated;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIndicated(int addr) {
        if (featOkTst && casFeat_indicated == null)
      jcas.throwFeatMissing("indicated", "org.apache.ctakes.typesystem.type.textsem.PolarityModifier");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_indicated);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIndicated(int addr, boolean v) {
        if (featOkTst && casFeat_indicated == null)
      jcas.throwFeatMissing("indicated", "org.apache.ctakes.typesystem.type.textsem.PolarityModifier");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_indicated, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public PolarityModifier_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_indicated = jcas.getRequiredFeatureDE(casType, "indicated", "uima.cas.Boolean", featOkTst);
    casFeatCode_indicated  = (null == casFeat_indicated) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_indicated).getCode();

  }
}



    