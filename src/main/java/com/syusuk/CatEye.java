package com.syusuk;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CatEye {
    public static String catEyeUrl = "http://piaofang.maoyan.com";

    public static void main(String[] args) {

        String html = "";
        String hostUrl = CatEye.catEyeUrl;
        try {
            html = CatEye.httpGet(hostUrl);
        } catch (Exception e) {
            System.err.println(e);
        }
        Document doc = Jsoup.parse(html);
        Elements ticketList = doc.getElementsByClass("ticketList");
        Elements movies = ticketList.select(".canTouch");
        List<MovieInfo> movieInfoList = new ArrayList<MovieInfo>();
        for(int i = 0; i < movies.size(); i++) {
            MovieInfo movieInfo = new MovieInfo();
            String movieHtml = "";
            String movie_href = movies.get(i).attr("data-com");
            String movieUrl = hostUrl + movie_href.split("'")[1];
            try {
                movieHtml  = CatEye.httpGet(movieUrl);
                Document movieDoc = Jsoup.parse(movieHtml);
                //片名
                Elements titleDoc = movieDoc.select(".navBarTitle");
                movieInfo.setName(titleDoc.get(0).text());
                //类型、制式、上映日期、上映时间
                Elements releaseDateDoc = movieDoc.getElementsByClass("infos").select("p");
                movieInfo.setType(releaseDateDoc.first().text());
                movieInfo.setShape(releaseDateDoc.get(2).text());
                String releaseDateStr = releaseDateDoc.last().text();
                String releaseDate = releaseDateStr.substring(releaseDateStr.indexOf("：")+1, releaseDateStr.indexOf("：") + 11);
                SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
                Integer duration = CatEye.daysBetween(sdf.parse(releaseDate), new Date());
                movieInfo.setDuration(duration);

                movieInfo.setReleaseDate(releaseDate);
                //导演、主演
                Elements movieMainInfoDoc = movieDoc.getElementsByClass("m-info-crews").select(".h-content");
//                movieInfo.put(movieMainInfoDoc.get(0).select(".l-title").text(),movieMainInfoDoc.get(0).select(".r-content").text());
                movieInfo.setDirector(movieMainInfoDoc.first().select(".r-content").text());
//                movieInfo.put(movieMainInfoDoc.get(1).select(".l-title").text(),movieMainInfoDoc.get(1).select(".r-content").text());
                movieInfo.setDirector(movieMainInfoDoc.get(1).select(".r-content").text());
                //出品公司
                Elements productionDoc = movieDoc.getElementsByClass("production-companies").select(".content");
                movieInfo.setProductionCompany(productionDoc.first().text());
                //发行公司
                Elements releaseDoc = movieDoc.getElementsByClass("distribution-firm").select(".content");
                movieInfo.setReleaseCompany(releaseDoc.text());
                //总票房
                Elements totalBoxOfficeDoc = movieDoc.getElementsByClass("tags").select("span");
                String totalBoxOfficeStr = totalBoxOfficeDoc.first().text();
                String totalBoxOffice = totalBoxOfficeStr.substring(totalBoxOfficeStr.indexOf(":"));
                movieInfo.setTotalBoxOffice(totalBoxOffice);
                //首周票房
                if (duration > 7) {
                    String firstWeekDoc = totalBoxOfficeDoc.get(1).text();
                    movieInfo.setFirstWeekBoxOffice(firstWeekDoc.substring(firstWeekDoc.indexOf(":")));
                }
                //昨日相关信息
                Elements dayDocs = movieDoc.select("#ticketContent .content ul");
                for (Element dayDoc : dayDocs) {
                    if (sdf.format(new Date()).equals(dayDoc.select("b").text())) {
                        Elements dayInfoDoc = dayDoc.select("li");
                        movieInfo.setdayInfoDoc.get(0).text()
                    }
                }

                movieInfoList.add(movieInfo);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        for(MovieInfo movieInfo : movieInfoList) {
            System.out.println(movieInfo);
        }

    }

    public static String httpGet(String url) throws Exception {
        StringBuilder resultString = new StringBuilder();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                        resultString.append(text);
                    }
                }
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return resultString.toString();
    }

    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }
}
