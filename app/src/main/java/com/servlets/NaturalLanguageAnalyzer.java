package com.servlets;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
* Input: user input from the webpage.
* Output: sentiment score, which is between -1 and 1.
* The sentiment score means how negative or positive the text is.
*/
@WebServlet("/sentiment")
public class NaturalLanguageAnalyzer extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String message = request.getParameter("message");

    // Create a new Document that contains the message entered by the user.
    Document doc =
        Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
    
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    // #1
    // String sessionId = (String) request.getSession().getId();
    // Credentials myCredentials = CredentialRetrival.GetCredentials(sessionId);
    // LanguageServiceSettings languageServiceSettings =
    //  LanguageServiceSettings.newBuilder()
    //      .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
    //      .build();
    // LanguageServiceClient languageServiceClient =
    // LanguageServiceClient.create(languageServiceSettings);

    // #2
    

    // Output the sentiment score as HTML.
    // A real project would probably store the score alongside the content.
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Sentiment Analysis</h1>");
    response.getWriter().println("<p>You entered: " + message + "</p>");
    response.getWriter().println("<p>Sentiment analysis score: " + score + "</p>");
    response.getWriter().println("<p><a href=\"/\">Back</a></p>");
  }

//   private GoogleCredentials LoadCredentials() {
//       // You can specify a credential file by providing a path to GoogleCredentials.
//   // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
//   GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
//         .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
//     return credentials;
//   }
}