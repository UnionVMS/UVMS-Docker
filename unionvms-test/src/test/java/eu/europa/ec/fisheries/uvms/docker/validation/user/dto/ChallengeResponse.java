package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

/**
 * A challenge/response based authentication request.
 */
public class ChallengeResponse {

    private String userName;
    private String challenge;
    private String response;

    /**
     * Creates a new instance.
     */
    public ChallengeResponse() {}

    public ChallengeResponse(String userName, String challenge, String response) {
        this.userName = userName;
        this.challenge = challenge;
        this.response = response;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the value of challenge
     *
     * @return the value of challenge
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     * Set the value of challenge
     *
     * @param challenge new value of challenge
     */
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    /**
     * Get the value of response
     *
     * @return the value of response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Set the value of response
     *
     * @param response new value of response
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Formats a human-readable view of this instance.
     * 
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "ChallengeResponse{" + "userName=" + getUserName() + "challenge=" + challenge + ", response="
                + (response == null ? null : "******") + '}';
    }

}
