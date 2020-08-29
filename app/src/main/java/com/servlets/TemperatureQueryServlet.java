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
@WebServlet("/temperature-query")
public class TemperatureQueryServlet extends HttpServlet {
  // A map of <temperature, num. cases>.
  private LinkedHashMap<Long, Cases> casesByTemp;

  @Override
  public void init() {
    // Query cases vs temperature for the last one week.
    String tempQuery = 
      "SELECT "
      + "  CAST(ROUND(average_temperature_celsius) AS INT64) AS key, "
      + "  SUM(new_confirmed) AS new_confirmed, "
      + "  SUM(new_deceased) AS new_deceased, "
      + "  SUM(new_recovered) AS new_recovered, "
      + "  SUM(new_tested) AS new_tested "
      + "FROM "
      + "  `bigquery-public-data.covid19_open_data.covid19_open_data` "
      + "WHERE date BETWEEN DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY) AND DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY) "
      + "GROUP BY 1";
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