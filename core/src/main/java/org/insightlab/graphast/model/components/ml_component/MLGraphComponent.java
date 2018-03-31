package org.insightlab.graphast.model.components.ml_component;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import org.insightlab.graphast.model.components.GraphComponent;

import java.util.HashMap;

public class MLGraphComponent extends GraphComponent {

    private Booster booster = null;
    private boolean trained = false;
    private HashMap<String, Object> params = new HashMap<>();

    public MLGraphComponent() {
        params.put("eta", 1.0);
        params.put("max_depth", 2);
        params.put("silent", 1);
        params.put("objective", "binary:logistic");
    }

    public void train(float[] data, int nRows, int nCols) {
        try {
            DMatrix train = new DMatrix(data, nRows, nCols);
            booster = XGBoost.train(train, params, 2, null, null, null);
            trained = true;
        } catch (XGBoostError xgBoostError) {
            xgBoostError.printStackTrace();
        }
    }

    public float[][] predict(float[] data, int nRows, int nCols) {
        float[][] result = null;
        try {
            DMatrix predData = new DMatrix(data, nRows, nCols);
            result = booster.predict(predData);
        } catch (XGBoostError xgBoostError) {
            xgBoostError.printStackTrace();
        }
        return result;
    }

}
