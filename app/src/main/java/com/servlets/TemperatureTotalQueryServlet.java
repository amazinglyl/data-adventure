package com.servlets;

import com.data.Cases;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
* Input: query.
* Output: a JSON object. E.g. [{"temperature": 25, "num. cumulative confirmed cases": 100}].
*/
@WebServlet("/temperature-total-query")
public class TemperatureTotalQueryServlet extends HttpServlet {
  // A map of <temperature, num. cases>.
  private LinkedHashMap<Long, Cases> casesByTemp;

  @Override
  public void init() {
    // Query cases vs temperature for 2 days ago.
    String tempQuery = 
      "SELECT "
      + "  CAST(ROUND(average_temperature_celsius) AS INT64) AS key, "
      + "  SUM(cumulative_confirmed) AS confirmed "
      + "FROM "
      + "  `bigquery-public-data.covid19_open_data.covid19_open_data` "
      + "WHERE date = DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY) "
      + "GROUP BY 1 "
      + "ORDER BY 1"; // Need to order the data so that they are connected in order in the line charts.
    QueryServletHelper helper = new QueryServletHelper();
    casesByTemp = helper.getQueryData(tempQuery);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(casesByTemp);
    response.getWriter().println(json);
  }
}