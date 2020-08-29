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
import com.google.auth.oauth2.GoogleCredentials;

/** 
* Input: query.
* Output: a JSON object. E.g. [{"temperature": 22.3, "num. cumulative confirmed cases": 100}].
*/
@WebServlet("/query")
public class QueryServlet extends HttpServlet {

  // Query confirmed cases vs temperature.
  private final static String query = 
    "SELECT "
    +"  average_temperature_celsius AS temperature, "
    +"  SUM(cumulative_confirmed) AS cumulative_confirmed "
    +"FROM `bigquery-public-data.covid19_open_data.covid19_open_data` "
    +"WHERE date = '2020-08-24' "
    +"GROUP BY 1";

    // A map of <temperature, num. cumulative confirmed cases>.
  private LinkedHashMap<Integer, Integer> confirmedByTemp;

  @Override
  public void init() {
    confirmedByTemp = new LinkedHashMap<>();

    // Run the query.
    System.out.println("Run the query ... ");
    Optional<TableResult> result = runQuery(query);
    if (result.isPresent()) {
        // Save the results to map.
        Iterable<FieldValueList> values = result.get().iterateAll();
        System.out.println("Is query result null: " + (values == null));
        for (FieldValueList row : values) {
            double temperature = 0.0;
            // If temperature = null, no need to save this data.
            if(row.get("temperature").isNull()) {
                continue;
            }
            else {
                temperature = row.get("temperature").getDoubleValue();
            }

            long confirmed = 0;
            // If cumulative_confirmed = null, treat is as 0.
            if(!row.get("cumulative_confirmed").isNull()) {
                confirmed = row.get("cumulative_confirmed").getLongValue();
            }
            confirmedByTemp.put((int)Math.round(temperature), (int)confirmed);
        }
    } else {
        // Put some dummy data into the map.
        confirmedByTemp.put(0, 0);
        confirmedByTemp.put(1, 2);
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
    // Create a BigQuery instance.
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
        // Use standard SQL syntax for queries. See: https://cloud.google.com/bigquery/sql-reference/
        .setUseLegacySql(false)
        .build();
    
    // Create a job ID.
    JobId jobId = JobId.of(UUID.randomUUID().toString());
    Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

    TableResult queryResult;
    try {
        // Wait for the job to complete.
        queryJob = queryJob.waitFor();
        return Optional.of(queryJob.getQueryResults());
    } catch (Exception e) {
        e.printStackTrace();
        // Log errors.
        if (queryJob == null) {
            System.err.println("The query job no longer exists!");
        } else if (queryJob.getStatus().getError() != null) {
            // Log the the latest error. Use queryJob.getStatus().getExecutionErrors() to log all errors.
            System.err.println("The query job has error: " + queryJob.getStatus().getError().toString());
        }
    }
    return Optional.empty();
  }
}