
/* First created by JCasGen Tue Aug 18 16:04:37 EDT 2015 */
package edu.pitt.dbmi.birads.typesystem.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** 
 * Updated by JCasGen Mon Sep 19 10:07:54 EDT 2016
 * @generated */
public class MultiLateralBirads_Type extends Birads_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = MultiLateralBirads.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.birads.typesystem.type.MultiLateralBirads");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public MultiLateralBirads_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    