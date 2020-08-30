package com.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/my-data-url")
public class ForFunServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name-input");
    String color = request.getParameter("color-input");
    String description = request.getParameter("description-input");

    response.setContentType("text/html;");
    response.getWriter().println("<p>Name: " + name + "</p>");
    response.getWriter().println("<p>Color: " + color + "</p>");
    response.getWriter().println("<p>Description: " + description + "</p>");
  }
}