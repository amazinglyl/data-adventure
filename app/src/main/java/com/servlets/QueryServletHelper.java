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
      int size = row.size();
      if(size == 0) {
        return;
      }
      long key = 0;
      // If key = null, no need to save this data.
      if(row.get("key").isNull()) {
          continue;
      }
      else {
          key = row.get("key").getLongValue();
      }

      long confirmed = 0, deceased = 0, recovered = 0, tested = 0;
      // If new cases = null, treat them as 0.
      if(size >= 2 && !row.get("confirmed").isNull()) {
          confirmed = row.get("confirmed").getLongValue();
      }
      if(size >= 3 && !row.get("deceased").isNull()) {
          deceased = row.get("deceased").getLongValue();
      }
      if(size >= 4 && !row.get("recovered").isNull()) {
          recovered = row.get("recovered").getLongValue();
      }
      if(size >= 5 && !row.get("tested").isNull()) {
          tested = row.get("tested").getLongValue();
      }
      keyedCases.put(key, new Cases(confirmed, deceased, recovered, tested));
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