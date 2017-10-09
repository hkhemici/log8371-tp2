package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.TreesService;
import io.swagger.api.WekaUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Vector;

public class TreesImpl extends TreesService {



    @Override
    @Produces("text/plain")
    public Response algorithmJ48NetPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String estimator, BigDecimal estimatorParams, Integer useADTree, String searchAlgorithm, String searchParams, SecurityContext securityContext) throws NotFoundException, IOException {
        // do some magic!
        Object[] params = {estimator, estimatorParams, useADTree, searchAlgorithm, searchParams};

        for (int i = 0; i < params.length; i++) {
            System.out.println("param are: " + params[i]);
        }

        StringBuffer txtStr = new StringBuffer();
        int c;
        while ((c = fileInputStream.read()) != -1) {
            txtStr.append((char) c);
        }

        int nNrOfAtts = params.length;

        StringBuilder parameters = new StringBuilder();

        if (useADTree != 1) {
            parameters.append(" -D ");
        }

        // Set the parameter for the searchAlgo
        parameters.append(" -Q ");
        parameters.append("weka.classifiers.bayes.net.search." + searchAlgorithm);
        System.out.println("searchAlgorithm is: " + searchAlgorithm);
        // Set the search parameters
        if (searchParams != null) {
            parameters.append(" -- ");
            parameters.append(searchParams);
        }
        // Set estimator
        if (estimator != null) {
            parameters.append(" -E ");
            parameters.append("weka.classifiers.bayes.net.estimate." + estimator);
        }
        // Set the parameters for the estimator
        if (estimatorParams != null) {
            parameters.append(" -- ");
            parameters.append(" -A ");
            parameters.append(estimatorParams);
        }
        System.out.println("parameterstring for weka: " + parameters.toString());
        BayesNet net = new BayesNet();
        String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr.toString());

        try {
            options = weka.core.Utils.splitOptions(parameters.toString());
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            net.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + parameters.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }
        try {
            net.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + parameters.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        Vector v = new Vector();
        v.add(net);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n").build();
    }

}