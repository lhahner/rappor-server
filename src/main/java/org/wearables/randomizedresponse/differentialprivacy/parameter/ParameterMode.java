/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.parameter;

public enum ParameterMode {
  /** If the user requests default mode, the default parameters are sent to the client. */
  DEFAULT,

  /** If the users request customer mode, the custom parameters are sent to the client. */
  PROFILE;
}
