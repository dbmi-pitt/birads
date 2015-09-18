package edu.pitt.dbmi.birads.crf;

import java.util.ArrayList;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;

public class SimpleTaggerSentence2FeatureVectorSequence extends Pipe {

	private static final long serialVersionUID = -2059308802200728625L;

	public static void main(String[] args) {

	}

	/**
	 * Creates a new <code>SimpleTaggerSentence2FeatureVectorSequence</code>
	 * instance.
	 */
	public SimpleTaggerSentence2FeatureVectorSequence() {
		super(new Alphabet(), new LabelAlphabet());
	}

	/**
	 * Parses a string representing a sequence of rows of tokens into an array
	 * of arrays of tokens.
	 * 
	 * @param sentence
	 *            a <code>String</code>
	 * @return the corresponding array of arrays of tokens.
	 */
	private String[][] parseSentence(String sentence) {
		String[] lines = sentence.split("\n");
		String[][] tokens = new String[lines.length][];
		for (int i = 0; i < lines.length; i++)
			tokens[i] = lines[i].split(" ");
		return tokens;
	}

	public Instance pipe(Instance carrier) {
		Object inputData = carrier.getData();
		Alphabet features = getDataAlphabet();
		LabelAlphabet labels;
		LabelSequence target = null;
		String[][] tokens;
		if (inputData instanceof String)
			tokens = parseSentence((String) inputData);
		else if (inputData instanceof String[][])
			tokens = (String[][]) inputData;
		else
			throw new IllegalArgumentException(
					"Not a String or String[][]; got " + inputData);
		FeatureVector[] fvs = new FeatureVector[tokens.length];
		if (isTargetProcessing()) {
			labels = (LabelAlphabet) getTargetAlphabet();
			target = new LabelSequence(labels, tokens.length);
		}
		for (int l = 0; l < tokens.length; l++) {
			int nFeatures;
			if (isTargetProcessing()) {
				if (tokens[l].length < 1)
					throw new IllegalStateException("Missing label at line "
							+ l + " instance " + carrier.getName());
				nFeatures = tokens[l].length - 1;
				target.add(tokens[l][nFeatures]);
			} else
				nFeatures = tokens[l].length;
			ArrayList<Integer> featureIndices = new ArrayList<Integer>();
			for (int f = 0; f < nFeatures; f++) {
				int featureIndex = features.lookupIndex(tokens[l][f]);
				// gdruck
				// If the data alphabet's growth is stopped, featureIndex
				// will be -1. Ignore these features.
				if (featureIndex >= 0) {
					featureIndices.add(featureIndex);
				}
			}
			int[] featureIndicesArr = new int[featureIndices.size()];
			for (int index = 0; index < featureIndices.size(); index++) {
				featureIndicesArr[index] = featureIndices.get(index);
			}
			fvs[l] = new FeatureVector(features, featureIndicesArr) ;
		}
		carrier.setData(new FeatureVectorSequence(fvs));
		if (isTargetProcessing())
			carrier.setTarget(target);
		else
			carrier.setTarget(new LabelSequence(getTargetAlphabet()));
		return carrier;
	}
	
	public boolean isTargetProcessing ()
	{
		return true;
	}

}
