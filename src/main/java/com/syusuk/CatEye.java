package com.syusuk;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CatEye {
    public static String catEyeUrl = "http://piaofang.maoyan.com";
    public static String basePath = "d:\\";

    public static void main(String[] args) {
        String basePath = CatEye.basePath;
        if (args != null && args.length >= 1) {
            basePath = args[0];
        }
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
                SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
                Integer duration = CatEye.daysBetween(sdf.parse(releaseDate), new Date());
                movieInfo.setDuration(duration);

                movieInfo.setReleaseDate(releaseDate);
                //导演、主演
                Elements movieMainInfoDoc = movieDoc.getElementsByClass("m-info-crews").select(".h-content");
//                movieInfo.put(movieMainInfoDoc.get(0).select(".l-title").text(),movieMainInfoDoc.get(0).select(".r-content").text());
                movieInfo.setDirector(movieMainInfoDoc.first().select(".r-content").text());
//                movieInfo.put(movieMainInfoDoc.get(1).select(".l-title").text(),movieMainInfoDoc.get(1).select(".r-content").text());
                movieInfo.setLeadActors(movieMainInfoDoc.get(1).select(".r-content").text());
                //出品公司
                Elements productionDoc = movieDoc.getElementsByClass("production-companies").select(".content");
                movieInfo.setProductionCompany(productionDoc.text());
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
                if (duration > 1) {
                    //昨日相关信息
                    Elements dayDocs = movieDoc.select("#ticketContent .content ul");
                    for (int j = 0; j < dayDocs.size(); j++) {
                        if (sdf.format(new Date()).equals(dayDocs.get(j).select("b").text())) {
                            if (j == 0)
                                continue;
                            Elements dayInfoDoc = dayDocs.get(j-1).select("li");
                            movieInfo.setBoxOfficePercent(dayInfoDoc.get(2).text());
                            movieInfo.setEpisodePercent(dayInfoDoc.get(3).text());
                            movieInfo.setEpisodePersonTime(dayInfoDoc.get(4).text());
                        }
                    }
                }
                movieInfoList.add(movieInfo);
            } catch (Exception e) {
                System.out.println(movieInfo.getName());
                e.printStackTrace();
            }
        }
        exportExcelFile(movieInfoList, basePath);
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

    public static String exportExcelFile(List<MovieInfo> movieInfos, String baseFilePath) {
        List<Map> resultMapList = new LinkedList<Map>();
        Iterator<MovieInfo> movieInfoIteratorIt = movieInfos.iterator();
        while (movieInfoIteratorIt.hasNext()) {
            Map<String, String> dataRow = new LinkedHashMap<String,String>();
            MovieInfo movieInfo = movieInfoIteratorIt.next();
            dataRow.put("影片名称", movieInfo.getName());
            dataRow.put("主类型", movieInfo.getType());
            dataRow.put("导演", movieInfo.getDirector());
            dataRow.put("编剧", "");
            dataRow.put("演员",movieInfo.getLeadActors());
            dataRow.put("上映日期",movieInfo.getReleaseDate());
            dataRow.put("下线日期", "");
            dataRow.put("上映天数",String.valueOf(movieInfo.getDuration()));
            dataRow.put("累计票房",movieInfo.getTotalBoxOffice());
            dataRow.put("首周票房",movieInfo.getFirstWeekBoxOffice());
            dataRow.put("票房占比",movieInfo.getBoxOfficePercent());
            dataRow.put("场次占比",movieInfo.getEpisodePercent());
            dataRow.put("人次占比","");
            dataRow.put("场均人次",movieInfo.getEpisodePersonTime());
            dataRow.put("场均收入","");
            dataRow.put("制作公司",movieInfo.getProductionCompany());
            dataRow.put("发行公司",movieInfo.getReleaseCompany());
            resultMapList.add(dataRow);
        }
        if (resultMapList.size() == 0) {
            return null;
        }
        return generateExcelFile(resultMapList, baseFilePath);
    }

    private static String generateExcelFile(List<Map> exportDataList, String baseFilePath) {
        HSSFWorkbook wb = new HSSFWorkbook();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HSSFSheet sheet = wb.createSheet(sdf.format(new Date()));
        HSSFRow row = sheet.createRow(0);

        Iterator<Map.Entry<String, String>> it = exportDataList.get(0).entrySet().iterator();
        int columnCounter = 0;
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            HSSFCell cell = row.createCell((short) columnCounter);
            cell.setCellValue(entry.getKey());
            columnCounter++;
        }

        for (int i = 0; i < exportDataList.size(); i++) {
            row = sheet.createRow(i + 1);
            Iterator<Map.Entry<String, String>> playerIt = exportDataList.get(i).entrySet().iterator();
            columnCounter = 0;
            while (playerIt.hasNext()) {
                Map.Entry<String, String> entry = playerIt.next();
                HSSFCell cell = row.createCell((short) columnCounter);
                cell.setCellValue(entry.getValue());
                columnCounter++;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (IOException e) {
            System.err.println(e);
        }
        byte[] content = os.toByteArray();

        if (!baseFilePath.endsWith(File.separator)) {
            baseFilePath += File.separator;
        }
        File file = new File(baseFilePath + "movieInfo" + sdf.format(new Date()) + ".xls");
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content);
            os.close();
            fos.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return file.getAbsolutePath();
    }

}
