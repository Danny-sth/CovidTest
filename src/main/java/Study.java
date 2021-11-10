public class Study {
    private String id;
    private String isHealthy;
    private String prob;
    private String status;
    private String statusText;

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
