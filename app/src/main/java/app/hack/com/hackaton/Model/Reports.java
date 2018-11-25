package app.hack.com.hackaton.Model;

public class Reports {
    private String idReport, title, description, picture, lat, lng, status, date, type;

    public Reports() {
    }

    public Reports(String idReport, String title, String description, String picture, String lat, String lng, String status, String date, String type) {
        this.idReport = idReport;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.date = date;
        this.type = type;
    }

    public String getIdReport() {
        return idReport;
    }

    public void setIdReport(String idReport) {
        this.idReport = idReport;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type;    }
}
