
/* First created by JCasGen Sun May 08 17:42:40 EDT 2016 */
package org.apache.ctakes.typesystem.type.syntax;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** An adverb phrase
Equivalent to cTAKES: edu.mayo.bmi.uima.chunker.type.ADVP
 * Updated by JCasGen Mon Sep 19 10:07:55 EDT 2016
 * @generated */
public class ADVP_Type extends Chunk_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ADVP.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.syntax.ADVP");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ADVP_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    