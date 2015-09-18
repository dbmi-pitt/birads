package edu.pitt.dbmi.birads.crf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFOptimizableByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.SimpleTagger.SimpleTaggerSentence2FeatureVectorSequence;
import cc.mallet.fst.Transducer;
import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import cc.mallet.util.MalletLogger;

public class CrfEvaluator {

	private double gaussianVarianceOption = 10.0;
	private boolean trainOption = true;
	private String testOption = null; // label based
	private String modelOption = null; // file name of model on disk
	private double trainingFractionOption = 0.5;
	private double randomSeedOption = 0.0;
	private int[] ordersOption = new int[] { 1 };
	private String forbiddenOption = "\\s";
	private String allowedOption = ".*";
	private String defaultOption = "O";
	private int iterations = 500;
	private boolean viterbiOutputOption = false;
	private boolean connectedOption = true;
	private String weightsOption = "some-dense";
	private boolean continueTrainingOption = false;
	private int nBestOption = 1;
	private int cacheSizeOption = 100000;
	private boolean includeInputOption = false;
	private boolean featureInductionOption = false;
	private int numThreads = 1;

	private File[] trainingFileHandles;
	private File[] testingFileHandles;
	private ArrayList<Integer> testingIndexSubset;

	private InstanceList trainingData;
	private Pipe p;

	private CRF crf;
	private CRFTrainerByLabelLikelihood crft;
	
	private static final String[] targetClasses = { "LeftBirads", "RightBirads", "MultiLateralBirads", "NonSpecificBirads", "OverAllBirads", "NoBirads"};

	private List<NominalClassificationResultTabulator> tabulators = new ArrayList<NominalClassificationResultTabulator>();
	
	public static void main(String[] args) {
		Logger logger = MalletLogger
				.getLogger(CRFTrainerByLabelLikelihood.class.getName());
		logger.setLevel(Level.OFF);
		logger = MalletLogger.getLogger(CRF.class.getName());
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.OFF);
		logger = MalletLogger.getLogger(CRFOptimizableByLabelLikelihood.class
				.getName());
		logger.setLevel(Level.OFF);
		logger = MalletLogger.getLogger(LimitedMemoryBFGS.class.getName());
		logger.setLevel(Level.OFF);
		logger = MalletLogger
				.getLogger("edu.umass.cs.mallet.base.ml.maximize.LimitedMemoryBFGS");
		logger.setLevel(Level.OFF);
		CrfEvaluator evaluator = new CrfEvaluator();
		try {
			evaluator.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private CrfEvaluator() {
		;
	}

	public void execute() throws IOException {
		cacheFileHandles();
		buildResultTabulators();
		Partitioner partitioner = new Partitioner();
		partitioner.setNumberOfPartitions(10);
		partitioner.setNumberOfDataItems(trainingFileHandles.length);
		partitioner.initialize();
		int numberOfFold = 0;
		while (partitioner.hasMoreFolds()) {
			testingIndexSubset = partitioner.getTestIndices();
			System.out.println("Running fold " + numberOfFold + " with test indices " + testingIndexSubset);
			aggregateTrainingData();
			trainWithCombinedDeck();
			testAgainstCrfModel();
			partitioner.nextFold();
			crf = null;
			p = null;
			trainingData = null;
		}
		reportEvaluationResults();
	}
	
	private void reportEvaluationResults() {
		for (NominalClassificationResultTabulator tabulator : tabulators) {
			tabulator.calculateMicroStatistics();
			System.out.println(tabulator);
		}
		
	}

	private void buildResultTabulators() {
		for (String targetClass : targetClasses) {
			NominalClassificationResultTabulator tabulator = new NominalClassificationResultTabulator();
			tabulator.setTargetClass(targetClass);
			tabulators.add(tabulator);
		}
	}

	public void executeOld() throws IOException {
		cacheFileHandles();
		for (int idx = 0; idx < 10; idx++) {
			runFold();
			crf = null;
			p = null;
			trainingData = null;
		}
		reportEvaluationResults();
	}

	private void runFold() throws IOException {
		System.out.println("Running fold");
		partitionRandomly();
		iterativelyTrain();
		testAgainstCrfModel();
	}

	// private void runFoldWithAggregates() throws IOException {
	// partitionRandomly();
	// aggregateTrainingData();
	// trainWithCombinedDeck();
	// testAgainstCrfModel();
	// }

	private void partitionRandomly() {
		BentleyIndexShuffler shuffler = new BentleyIndexShuffler();
		testingIndexSubset = shuffler.generateShuffledArrayList(10,
				trainingFileHandles.length);
	}

	private void iterativelyTrain() throws FileNotFoundException {
		for (int idx = 0; idx < trainingFileHandles.length; idx++) {
			if (!testingIndexSubset.contains(idx)) {
				augmentTrainingWith(trainingFileHandles[idx].getAbsolutePath());
			}
		}
	}

	private void augmentTrainingWith(String trainingFilePath)
			throws FileNotFoundException {
		FileReader trainingFile = new FileReader(new File(trainingFilePath));
		if (crf == null) {
			startCrfTraining(trainingFile);
		} else {
			continueCrfTraining(trainingFile);
		}
		trainWithInstanceList();

	}

	private void startCrfTraining(FileReader trainingFile) {
		p = new SimpleTaggerSentence2FeatureVectorSequence();
		p.getTargetAlphabet().lookupIndex(defaultOption);
		p.setTargetProcessing(true);
		trainingData = new InstanceList(p);
		trainingData.addThruPipe(new LineGroupIterator(trainingFile, Pattern
				.compile("^\\s*$"), true));
		Pattern forbiddenPat = Pattern.compile(forbiddenOption);
		Pattern allowedPat = Pattern.compile(allowedOption);
		crf = new CRF(trainingData.getPipe(), (Pipe) null);
		String startName = crf.addOrderNStates(trainingData, ordersOption,
				null, defaultOption, forbiddenPat, allowedPat, connectedOption);
		for (int i = 0; i < crf.numStates(); i++) {
			crf.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
		}
		crf.getState(startName).setInitialWeight(0.0);
	}

	private void continueCrfTraining(FileReader trainingFile) {
		p = crf.getInputPipe();
		trainingData = new InstanceList(p);
		trainingData.addThruPipe(new LineGroupIterator(trainingFile, Pattern
				.compile("^\\s*$"), true));
	}

	private void trainWithInstanceList() {
		crft = new CRFTrainerByLabelLikelihood(crf);
		crft.setGaussianPriorVariance(10.0d);
		crft.setUseSparseWeights(true);
		crft.setUseSomeUnsupportedTrick(true);
		boolean converged;
		for (int i = 1; i <= iterations; i++) {
			converged = crft.train(trainingData, 1);
			if (converged) {
				break;
			}
		}
	}

	private void aggregateTrainingData() throws FileNotFoundException {
		for (int idx = 0; idx < trainingFileHandles.length; idx++) {
			if (!testingIndexSubset.contains(idx)) {
				gatherTrainingInstances(trainingFileHandles[idx]
						.getAbsolutePath());
			}
		}
	}

	private void gatherTrainingInstances(String trainingFilePath)
			throws FileNotFoundException {
		FileReader trainingFile = new FileReader(new File(trainingFilePath));
		if (p == null) {
			p = new SimpleTaggerSentence2FeatureVectorSequence();
			p.getTargetAlphabet().lookupIndex(defaultOption);
			p.setTargetProcessing(true);
			trainingData = new InstanceList(p);
		}
		trainingData.addThruPipe(new LineGroupIterator(trainingFile, Pattern
				.compile("^\\s*$"), true));
	}

	public void cacheFileHandles() {
		final String trainDirectoryPath = "C:\\Users\\kjm84\\Desktop\\snapshot091715\\ftr";
		final String testDirectoryPath = "C:\\Users\\kjm84\\Desktop\\snapshot091715\\ftr";
//		final String trainDirectoryPath = "C:\\Users\\kjm84\\Desktop\\crfs\\train";
//		final String testDirectoryPath = "C:\\Users\\kjm84\\Desktop\\crfs\\test";
		trainingFileHandles = listFilesInDirectory(trainDirectoryPath);
		testingFileHandles = listFilesInDirectory(testDirectoryPath);
	}

	private File[] listFilesInDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		return directory.listFiles();
	}

	private void testAgainstCrfModel() throws IOException {
		for (int idx = 0; idx < trainingFileHandles.length; idx++) {
			if (testingIndexSubset.contains(idx)) {
				String gold = FileUtils
						.readFileToString(trainingFileHandles[idx]);
				String mach = testModel(testingFileHandles[idx]
						.getAbsolutePath());
				for (NominalClassificationResultTabulator tabulator : tabulators) {
					tabulator.setCurrentFileName(testingFileHandles[idx].getName());
					tabulator.setCurrentToken(0);
				}
				List<String> goldLines = Arrays.asList(gold.split(("\n")));
				List<String> machLines = Arrays.asList(mach.split(("\n")));
				evaluateGoldVsMachLines(goldLines, machLines);
			}
		}
	}

	public String testModel(String testFilePath) throws FileNotFoundException {
		StringBuffer buf = new StringBuffer();
		FileReader testFile = new FileReader(new File(testFilePath));
		Pipe p = crf.getInputPipe();
		p.setTargetProcessing(false);
		InstanceList testData = new InstanceList(p);
		testData.addThruPipe(new LineGroupIterator(testFile, Pattern
				.compile("^\\s*$"), true));
		for (int i = 0; i < testData.size(); i++) {
			Sequence input = (Sequence) testData.get(i).getData();
			Sequence[] outputs = apply(crf, input, nBestOption);
			int k = outputs.length;
			for (int j = 0; j < input.size(); j++) {
				for (int a = 0; a < k; a++) {
					buf.append(outputs[a].get(j).toString()).append(" ");
					FeatureVector fv = (FeatureVector) input.get(j);
					buf.append(fv.toString(true));
					buf.append("\n");
				}
			}
		}
		String response = buf.toString();
		if (response.endsWith("\n")) {
			response = response.substring(0, response.length() - 1);
		}
		return response;
	}

	private void trainWithCombinedDeck() {
		Pattern forbiddenPat = Pattern.compile(forbiddenOption);
		Pattern allowedPat = Pattern.compile(allowedOption);
		crf = new CRF(trainingData.getPipe(), (Pipe) null);
		String startName = crf.addOrderNStates(trainingData, ordersOption,
				null, defaultOption, forbiddenPat, allowedPat, connectedOption);
		for (int i = 0; i < crf.numStates(); i++) {
			crf.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
		}
		crf.getState(startName).setInitialWeight(0.0);
		CRFTrainerByLabelLikelihood crft = new CRFTrainerByLabelLikelihood(crf);
		crft.setGaussianPriorVariance(10.0d);
		crft.setUseSparseWeights(true);
		crft.setUseSomeUnsupportedTrick(true);
		boolean converged;
		for (int i = 1; i <= iterations; i++) {
			converged = crft.train(trainingData, 1);
			if (converged) {
				break;
			}
		}
	}

	public Sequence[] apply(Transducer model, Sequence input, int k) {
		Sequence[] answers;
		if (k == 1) {
			answers = new Sequence[1];
			answers[0] = model.transduce(input);
		} else {
			MaxLatticeDefault lattice = new MaxLatticeDefault(model, input,
					null, cacheSizeOption);
			answers = lattice.bestOutputSequences(k).toArray(new Sequence[0]);
		}
		return answers;
	}

	@SuppressWarnings("unused")
	private void displayDiagnostics(Pipe p) {
		Alphabet dataAlphabet = p.getDataAlphabet();
		int dataAlphabetSize = dataAlphabet.size();
		System.out.println("Number of features in training data: "
				+ dataAlphabetSize);
		dataAlphabet.dump(System.out);
		Alphabet targetAlphabet = p.getTargetAlphabet();
		targetAlphabet.dump(System.out);
	}

	private void evaluate() {
		try {
			tryGoldVsMachComparison(
					"C:\\Users\\kjm84\\Desktop\\crfs\\trainWithA.txt",
					"C:\\Users\\kjm84\\Desktop\\crfs\\AtestedWithB.txt");
			tryGoldVsMachComparison(
					"C:\\Users\\kjm84\\Desktop\\crfs\\trainWithB.txt",
					"C:\\Users\\kjm84\\Desktop\\crfs\\BtestedWithA.txt");
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private void tryGoldVsMachComparison(String goldFilePath,
			String machFilePath) throws IOException {
		File gold = new File(goldFilePath);
		File mach = new File(machFilePath);
		final List<String> goldLines = FileUtils.readLines(gold);
		final List<String> machLines = FileUtils.readLines(mach);

		evaluateGoldVsMachLines(goldLines, machLines);

		reportEvaluationResults();
	}

	

	private void evaluateGoldVsMachLines(List<String> goldLines,
			List<String> machLines) {
		Iterator<String> goldIterator = goldLines.iterator();
		Iterator<String> machIterator = machLines.iterator();
		while (goldIterator.hasNext() && machIterator.hasNext()) {
			String machLine = machIterator.next();
			if (machLine.startsWith("Number of predicates:")) {
				continue;
			}
			String goldLine = goldIterator.next();

			final String[] machTokens = machLine.split("\\s");
			final String[] goldTokens = goldLine.split("\\s");

			String goldToken = goldTokens[0];
			List<String> machTokenList = Arrays.asList(machTokens);
			if (!machTokenList.contains(goldToken)) {
				System.out.println("\nToken Mismatch:");
				System.out.println(machLine);
				System.out.println(goldLine);
			}
			String goldClassification = goldTokens[goldTokens.length-1];
			String machClassification = machTokens[0];
			
			for (NominalClassificationResultTabulator tabulator : tabulators) {
				tabulator.setCurrentToken(tabulator.getCurrentToken()+1);
				tabulator.setGoldClassification(goldClassification);
				tabulator.setMachClassification(machClassification);
				tabulator.tally();
			}
			
		
		}
	}

}
