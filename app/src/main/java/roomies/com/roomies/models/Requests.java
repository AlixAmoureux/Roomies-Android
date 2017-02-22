package roomies.com.roomies.models;


public class Requests
{
    private String ruledBy;
    private String ruledDate;
    private String requestBy;
    private String status;
    private String user;
    private String requestDate;
    private String id;
    private String requesterLastName;
    private String requesterFirstName;

    public String getRuledBy() {
        return ruledBy;
    }

    public void setRuledBy(String ruledBy) {
        this.ruledBy = ruledBy;
    }

    public String getRuledDate() {
        return ruledDate;
    }

    public void setRuledDate(String ruledDate) {
        this.ruledDate = ruledDate;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequesterLastName() {
        return requesterLastName;
    }

    public void setRequesterLastName(String requesterLastName) {
        this.requesterLastName = requesterLastName;
    }

    public String getRequesterFirstName() {
        return requesterFirstName;
    }

    public void setRequesterFirstName(String requesterFirstName) {
        this.requesterFirstName = requesterFirstName;
    }
}
