package com.servlets;

import com.data.Landmark;
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

/** 
* Input: landmark info in file "/WEB-INF/landmark-info.csv".
* Output: a JSON object. E.g. [{"year": 2001, "num. vistors": 50}].
*/
@WebServlet("/query")
public class QueryDataServlet extends HttpServlet {

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
  public void init() throws Exception {
    confirmedByTemp = new LinkedHashMap<>();

    // Run the query.
    TableResult result = runQuery(query);

    // Save the results to map.
    for (FieldValueList row : result.getValues()) {
      String temperature = row.get("temperature").geDoubleValue();
      long confirmed = row.get("cumulative_confirmed").getLongValue();
      confirmedByTemp.put(temperature, confirmed);
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(vistorsByYear);
    response.getWriter().println(json);
  }

  private TableResult runQuery(String query) {
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
        // Use standard SQL syntax for queries.
        // See: https://cloud.google.com/bigquery/sql-reference/
        .setUseLegacySql(false)
        .build();
    
    // Create a job ID so that we can safely retry.
    JobId jobId = JobId.of(UUID.randomUUID().toString());
    Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

    // Wait for the query to complete.
    queryJob = queryJob.waitFor();

    // Check for errors
    if (queryJob == null) {
    throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
    // You can also look at queryJob.getStatus().getExecutionErrors() for all
    // errors, not just the latest one.
    throw new RuntimeException(queryJob.getStatus().getError().toString());
    }
    return queryJob.getQueryResults();
  }
}