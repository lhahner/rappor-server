/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.exceptions;

/**
 * Global Error Response Message Type, which reflects the Body of the send message whenever an
 * exception happens.
 *
 * @param message
 * @param timestamp
 */
public record ErrorResponse(String message, String timestamp) {}
