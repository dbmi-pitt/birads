package edu.pitt.dbmi.birads;

//import edu.pitt.dbmi.birads.crf.XmiReader;
//import edu.pitt.dbmi.birads.crf.ctakes.ClinicalPipeline;
//import edu.pitt.dbmi.birads.naivebayes.BayesClinicalPipeline;
//import edu.pitt.dbmi.birads.naivebayes.BayesXmiReader;

public class BiRADS_Workflow {

	
	
	
	public static void main(String[] args) throws ClassNotFoundException {
		// run clinical pipeline that uses ctakes + expert annotations from Anafora
		// to generate CAS as .xmi files
		// sectioning, birads anchor regex, time mentions, tokenazation, roman numerals (context)
		// save expert annotations in CAS
		//ClinicalPipeline.main(args);
		
		// use .XMI reader to interpret CAS output as a MALET CRF input text file 
		//XmiReader.main(args);
		
		// now run CRF (this is done seperatly via command line
		// to save the model file.
		//java -cp "C:\mallet\class;C:\mallet\lib\mallet-deps.jar" cc.mallet.fst.SimpleTagger --train true --model-file "C:\Users\sec113\Downloads/birads/birads_test.model" "C:\Users\sec113\Downloads/birads/training_set.txt">output_f.txt

		// now I have trained model !!!!!
		// (hardcoded in CrrfEvaluator) 
		// just needs testing as Kevin's code requires to training a model that gets discarded because we use a model 
		// we got by running MALLET via command line in step before
		//CrfEvaluator.main(args);
		
		// produces token stream file with predicted label (on the right)
		// produces awesome F-Scores
		
		// create feature vector and dataset matrix in cTAKES 
		//BayesClinicalPipeline.main(args);
		// write out .XMI
		
		// read in .XMI and build a binary term occurance matrix for Baysean (also needs expert annotations from anafora)
		// it has all words from taining set as its columns, + some extra features s.a.
		// sequence counter, birads number, 
		//BayesXmiReader.main(args);
		
		
		// used R to do further processing s.a. add sequence numbers and remove lines that are outside of
		// 3 line BIRADS scope
		//nb_generator.R 
		// strip lines that are not part of BiRADS, remove tokens that are not from correct lines
		//save WEKA file from R
		
		// class (left, right, overall, bilateral, non-specific)
		// BiRads sequence counter (birads category mentions in document s.a. 1st mention, 2nd mention etc.)
		// BiRads sequence descretized same info, different spin (first, middle, last)
		// bag of words from 3 lines in BirRads corpus.
		
			
		//use  WEKA GUI to train the model
		
		
		
		
		
		
		
	
	}

}
