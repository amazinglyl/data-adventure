package com.servlets;

import com.data.Landmark;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
* Input: landmarks defined in file "/WEB-INF/landmarks.csv".
* Output: JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}]
*/
@WebServlet("/landmarks")
public class DataServlet extends HttpServlet {

  // A list of Landmark objects.
  private Collection<Landmark> landmarks;

  @Override
  public void init() {
    landmarks = new ArrayList<>();

    // Create a scanner to read the input csv file.
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/landmarks.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] landmark = line.split(", ");

      landmarks.add(new Landmark(
        /*lat=*/Double.parseDouble(landmark[0]),
        /*lng=*/Double.parseDouble(landmark[1]),
        /*title=*/landmark[2],
        /*description=*/landmark[3]));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(landmarks);
    response.getWriter().println(json);
  }
}