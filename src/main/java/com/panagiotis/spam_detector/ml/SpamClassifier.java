package com.panagiotis.spam_detector.ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import weka.core.SerializationHelper;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 * ML Classifier με Weka random forest
 */
@Component
public class SpamClassifier {

    private Classifier classifier;
    private Instances dataset;
    private FeatureExtractor featureExtractor;

    private ArrayList<Attribute> attributes;

    public SpamClassifier() {
        this.featureExtractor = new FeatureExtractor();
        initializeAttributes();
        trainModel();
    }

    private void initializeAttributes() {
        attributes = new ArrayList<>();
        attributes.add(new Attribute("spam_keyword_count"));
        attributes.add(new Attribute("url_present"));
        attributes.add(new Attribute("excessive_caps"));
        attributes.add(new Attribute("multiple_exclamations"));
        attributes.add(new Attribute("phone_pattern"));
        attributes.add(new Attribute("urgency_words"));
        attributes.add(new Attribute("short_length"));
        attributes.add(new Attribute("free_mentions"));
        attributes.add(new Attribute("suspicious_patterns"));
        attributes.add(new Attribute("money_mentions"));
        attributes.add(new Attribute("spam_ratio"));

        List<String> classValues = new ArrayList<>();
        classValues.add("ham");
        classValues.add("spam");
        attributes.add(new Attribute("class", classValues));

        dataset = new Instances("spamDataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);
    }

    public void trainModel() {
        try {
            classifier = new RandomForest();
            ((RandomForest)classifier).setOptions(new String[]{"-I", "100", "-depth", "10"});

            for(TrainingData.SpamTrainingExample example : TrainingData.getTrainingData()) {
                double[] features = featureExtractor.extractFeaturesArray(example.getMessage());
                double[] instanceValues = new double[features.length + 1];

                System.arraycopy(features, 0, instanceValues, 0, features.length);
                instanceValues[features.length] = example.isSpam() ? 1.0 : 0.0;

                dataset.add(new DenseInstance(1.0, instanceValues));
            }
            
            classifier.buildClassifier(dataset);
            System.out.println("ML model trained successfully with " + dataset.size() + " examples");
        } catch (Exception e) {
            System.err.println("Error training ML model: " + e.getMessage());
        }
    }

    public SpamPrediction predict(String message) {
        try {
            if (classifier == null) {
                return fallbackPrediction(message);
            }
            System.out.println("ML Analysis for: " + message.substring(0, Math.min(50, message.length())));

            double[] features = featureExtractor.extractFeaturesArray(message);
            System.out.println("Features extracted: " + Arrays.toString(features));
            double[] instanceValues = new double[features.length + 1];
            System.arraycopy(features, 0, instanceValues, 0, features.length);
            instanceValues[features.length] = 0.0;

            DenseInstance instance = new DenseInstance(1.0, instanceValues);
            instance.setDataset(dataset);

            double[] distribution = classifier.distributionForInstance(instance);
            double spamProbability = distribution[1];
             System.out.println("ML Result Confidence: " + spamProbability);

            return new SpamPrediction(spamProbability > 0.5, spamProbability);
        } catch (Exception e) {
            System.err.println("ML prediction failed: " + e.getMessage());
            return fallbackPrediction(message);
        }
    }

    private SpamPrediction fallbackPrediction(String message) {
        Map<String, Object> features = featureExtractor.extractFeatures(message);
        double probability = featureExtractor.calculateRuleBasedProbability(features);
        return new SpamPrediction(probability > 0.5, probability);
    }

    public void saveModel(String filepath) throws Exception {
        SerializationHelper.write(filepath, classifier);
    }

    public void loadModel(String filepath) throws Exception {
        classifier = (Classifier) SerializationHelper.read(filepath);
    }

    public static class SpamPrediction {
        private final boolean isSpam;
        private final double probability;

        public SpamPrediction(boolean isSpam, double probability) {
            this.isSpam = isSpam;
            this.probability = probability;
        }

        public boolean isSpam() { return isSpam; }
        public double getProbability() { return probability; }
    }
    
}
