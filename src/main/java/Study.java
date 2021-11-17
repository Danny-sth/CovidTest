public class Study {

    private String seriesInstanceUID;
    private String id;
    private String isHealthy;
    private String prob;
    private String status;
    private String statusText;

    public Study(String seriesInstanceUID, String id, String isHealthy, String prob, String status, String statusText) {
        this.seriesInstanceUID = seriesInstanceUID;
        this.id = id;
        this.isHealthy = isHealthy;
        this.prob = prob;
        this.status = status;
        this.statusText = statusText;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsHealthy(String isHealthy) {
        this.isHealthy = isHealthy;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getId() {
        return id;
    }

    public String getIsHealthy() {
        return isHealthy;
    }

    public String getProb() {
        return prob;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public void setSeriesInstanceUID(String seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    @Override
    public String toString() {
        return "Study{" +
                "id='" + id + '\'' +
                ", isHealthy='" + isHealthy + '\'' +
                ", prob='" + prob + '\'' +
                ", status='" + status + '\'' +
                ", statusText='" + statusText + '\'' +
                '}';
    }
}
