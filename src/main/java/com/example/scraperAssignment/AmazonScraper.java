package com.example.scraperAssignment;

import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class AmazonScraper {
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 1000;

    public static void main(String[] args) {
        int totalPages = 20;
        String baseUrl = "https://www.amazon.in/s?k=bags&crid=2M096C61O4MLT&qid=1653308124&sprefix=ba%2Caps%2C283&ref=sr_pg_";
        String csvFilePath = "src\\main\\resources\\products.csv"; // Specify the desired file path

        try {
            FileWriter csvWriter = new FileWriter(csvFilePath);
            csvWriter.append("Product URL,Product Name,Product Price,Rating,Number of Reviews\n");

            for (int page = 1; page <= totalPages; page++) {
                String url = baseUrl + page;
                int retries = 0;
                boolean success = false;

                while (retries < MAX_RETRIES && !success) {
                    try {
                        Document document = Jsoup.connect(url).get();

                        Elements productElements = document.select(".sg-col-inner .s-result-item");
                        for (Element productElement : productElements) {
                            String productUrl = productElement.select(".a-link-normal.a-text-normal").attr("href");
                            String productName = productElement.select(".a-size-base-plus.a-color-base.a-text-normal").text();
                            String productPrice = productElement.select(".a-price-whole").text();
                            String rating = productElement.select(".a-icon-alt").text();
                            String numOfReviews = productElement.select(".a-size-base").last() != null ? productElement.select(".a-size-base").last().text() : "";

                            csvWriter.append(productUrl + "," + productName + "," + productPrice + "," + rating + "," + numOfReviews + "\n");
                        }

                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        retries++;
                        Thread.sleep(RETRY_DELAY_MS);
                    }
                }
            }

            csvWriter.flush();
            csvWriter.close();
            System.out.println("Scraping complete. Data saved to " + csvFilePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

