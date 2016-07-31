package com.syusuk;

import java.util.Date;

public class MovieInfo implements java.io.Serializable {

    private String name;

    private String releaseDate;

    private Integer duration;

    private String totalBoxOffice;

    private String firstWeekBoxOffice;

    private String type;

    private String director;

    private String leadActors;

    private String productionCompany;

    private String releaseCompany;

    private String boxOfficePercent;

    private String episodePercent;

    private String personTimePercent;

    private String averagePrice;

    private String episodePersonTime;

    private String episodeIncome;

    private String shape;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTotalBoxOffice() {
        return totalBoxOffice;
    }

    public void setTotalBoxOffice(String totalBoxOffice) {
        this.totalBoxOffice = totalBoxOffice;
    }

    public String getFirstWeekBoxOffice() {
        return firstWeekBoxOffice;
    }

    public void setFirstWeekBoxOffice(String firstWeekBoxOffice) {
        this.firstWeekBoxOffice = firstWeekBoxOffice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getLeadActors() {
        return leadActors;
    }

    public void setLeadActors(String leadActors) {
        this.leadActors = leadActors;
    }

    public String getProductionCompany() {
        return productionCompany;
    }

    public void setProductionCompany(String productionCompany) {
        this.productionCompany = productionCompany;
    }

    public String getReleaseCompany() {
        return releaseCompany;
    }

    public void setReleaseCompany(String releaseCompany) {
        this.releaseCompany = releaseCompany;
    }

    public String getBoxOfficePercent() {
        return boxOfficePercent;
    }

    public void setBoxOfficePercent(String boxOfficePercent) {
        this.boxOfficePercent = boxOfficePercent;
    }

    public String getEpisodePercent() {
        return episodePercent;
    }

    public void setEpisodePercent(String episodePercent) {
        this.episodePercent = episodePercent;
    }

    public String getPersonTimePercent() {
        return personTimePercent;
    }

    public void setPersonTimePercent(String personTimePercent) {
        this.personTimePercent = personTimePercent;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getEpisodePersonTime() {
        return episodePersonTime;
    }

    public void setEpisodePersonTime(String episodePersonTime) {
        this.episodePersonTime = episodePersonTime;
    }

    public String getEpisodeIncome() {
        return episodeIncome;
    }

    public void setEpisodeIncome(String episodeIncome) {
        this.episodeIncome = episodeIncome;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
