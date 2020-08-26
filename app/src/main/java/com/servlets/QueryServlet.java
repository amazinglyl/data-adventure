package com.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import java.util.UUID;

import java.lang.Exception;
import java.util.Optional;

/** 
* Input: query.
* Output: a JSON object. E.g. [{"temperature": 22.3, "num. cumulative confirmed cases": 100}].
*/
@WebServlet("/query")
public class QueryServlet extends HttpServlet {

  // A query.
  private final static String query = 
    "SELECT"
    +"  average_temperature_celsius AS temperature,"
    +"  SUM(cumulative_confirmed) AS cumulative_confirmed"
    +" FROM `bigquery-public-data.covid19_open_data.covid19_open_data`"
    +" WHERE date = '2020-08-24'"
    +" GROUP BY 1";

    // A map of <temperature, num. cumulative confirmed cases>.
  private LinkedHashMap<Double, Long> confirmedByTemp;

  @Override
  public void init() {
    confirmedByTemp = new LinkedHashMap<>();

    // Run the query.
    Optional<TableResult> result = runQuery(query);
    if (result.isPresent()) {
        // Save the results to map.
        for (FieldValueList row : result.get().getValues()) {
        double temperature = row.get("temperature").getDoubleValue();
        long confirmed = row.get("cumulative_confirmed").getLongValue();
        confirmedByTemp.put(temperature, confirmed);
        }
    } else {
        // Put a dummy entry into the map.
        confirmedByTemp.put(Double.valueOf(0.0), Long.valueOf(0));
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(confirmedByTemp);
    response.getWriter().println(json);
  }

  /* Returns the query results. */
  private Optional<TableResult> runQuery(String query) {
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
        // Use standard SQL syntax for queries.
        // See: https://cloud.google.com/bigquery/sql-reference/
        .setUseLegacySql(false)
        .build();
    
    // Create a job ID so that we can safely retry.
    JobId jobId = JobId.of(UUID.randomUUID().toString());
    Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

    // Wait for the query job to complete.
    TableResult queryResult;
    try {
        queryJob = queryJob.waitFor();
        return Optional.of(queryJob.getQueryResults());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Check for errors
    if (queryJob == null) {
        System.err.println("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
        // Use queryJob.getStatus().getExecutionErrors() for all
        // errors, not just the latest one.
        System.err.println(queryJob.getStatus().getError().toString());
    }
    return Optional.empty();
  }
}