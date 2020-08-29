package com.servlets;

import com.data.Cases;
import com.google.gson.Gson;
import java.util.LinkedHashMap;

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
 * This is a help class to query the database and save the data in a map.
 * Input: query.
 * Output: A map of <Key, num. cases>.
 */
public class QueryServletHelper {

  // Runs the query and saves the query result into a map.
  public LinkedHashMap<Long, Cases> getQueryData (String query) {
    LinkedHashMap<Long, Cases> keyedCases = new LinkedHashMap<>();
    // Run the query.
    Optional<TableResult> result = runQuery(query);
    if (result.isPresent()) {
      processQueryResults(result.get(), keyedCases);
    } 
    return keyedCases;
  }

  // Saves the query results to the keyedCases map.
  private void processQueryResults(TableResult result, LinkedHashMap<Long, Cases> keyedCases) {
    for (FieldValueList row : result.iterateAll()) {
        long temperature = 0;
        // If temperature = null, no need to save this data.
        if(row.get("temperature").isNull()) {
            continue;
        }
        else {
            temperature = row.get("temperature").getLongValue();
        }

        long confirmed = 0, deceased = 0, recovered = 0, tested = 0;
        // If new cases = null, treat them as 0.
        if(!row.get("new_confirmed").isNull()) {
            confirmed = row.get("new_confirmed").getLongValue();
        }
        if(!row.get("new_deceased").isNull()) {
            deceased = row.get("new_deceased").getLongValue();
        }
        if(!row.get("new_recovered").isNull()) {
            recovered = row.get("new_recovered").getLongValue();
        }
        if(!row.get("new_tested").isNull()) {
            tested = row.get("new_tested").getLongValue();
        }
        keyedCases.put(temperature, new Cases(confirmed, deceased, recovered, tested));
    }
  }

  /* Runs the query and returns the query results. If there are errors, nothing will be returned. */
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