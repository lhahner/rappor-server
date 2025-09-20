/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.decoder;

import java.util.concurrent.ExecutionException;

public interface Pipe<T> {
  Substance<T> process(Substance<T> substance) throws ExecutionException;
}
