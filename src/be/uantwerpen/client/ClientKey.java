package be.uantwerpen.client;


/**
 * Data class to ease searching in a HashSet
 *
 * Created by Dries on 27/11/2015.
 */
public class ClientKey {
    private final String username;
    private final int hashCode;

    public ClientKey() {
        this("");
    }

    public ClientKey(String username) {
        this.username = username;
        this.hashCode = username.toUpperCase().hashCode();
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof ClientKey && this.username.equalsIgnoreCase(((ClientKey) obj).username);
    }
}
