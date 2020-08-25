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

/** 
* Input: landmark info in file "/WEB-INF/landmark-info.csv".
* Output: a JSON object. E.g. [{"year": 2001, "num. vistors": 50}].
*/
@WebServlet("/landmark-info")
public class ChartDataServlet extends HttpServlet {

  // A map of <year, num. vistors>.
  private LinkedHashMap<Integer, Integer> vistorsByYear;

  @Override
  public void init() {
    vistorsByYear = new LinkedHashMap<>();

    // Create a scanner to read the input csv file.
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/landmark-info.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] vistors = line.split(", ");

      vistorsByYear.put(/*year=*/Integer.valueOf(vistors[0]), /*description=*/Integer.valueOf(vistors[1]));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(vistorsByYear);
    response.getWriter().println(json);
  }
}