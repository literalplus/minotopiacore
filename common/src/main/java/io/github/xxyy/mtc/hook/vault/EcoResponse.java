package io.github.xxyy.mtc.hook.vault;

/**
 * Wrapper for an economy response returned by the Vault API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-03
 */
public class EcoResponse {
    private final boolean successful;
    private final String errorMessage;

    public EcoResponse(boolean successful, String errorMessage) {
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    /**
     * @return whether the transaction was successful
     */
    public boolean wasSuccessful() {
        return successful;
    }

    /**
     * @return the error message, or null if the transaction was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
