// package com.servlets;

// import org.junit.Assert;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;

// import com.google.cloud.bigquery.FieldValueList;
// import com.google.cloud.bigquery.TableResult;

// public final class QueryServletTest {

//     @Test
//     public void testRunQuery(){
//         QueryServlet queryServlet;

//         String query = "SELECT "
//                     + "CONCAT('https://stackoverflow.com/questions/', CAST(id as STRING)) as url, "
//                     + "view_count "
//                     + "FROM `bigquery-public-data.stackoverflow.posts_questions` "
//                     + "WHERE tags like '%google-bigquery%' "
//                     + "ORDER BY favorite_count DESC LIMIT 10";
        
//         // Run the query.
//         Optional<TableResult> result = queryServlet.runQuery(query);
//         if (result.isPresent()) {
//             // Save the results to map.
//             Iterable<FieldValueList> values = result.get().iterateAll();
//             System.out.println("Is query result null: " + (values == null));
//             for (FieldValueList row : values) {
//                 String url = row.get("url").getStringValue();
//                 long viewCount = row.get("view_count").getLongValue();
//                 System.out.printf("url: %s views: %d%n", url, viewCount);
//             }
//         }
//     }
// }