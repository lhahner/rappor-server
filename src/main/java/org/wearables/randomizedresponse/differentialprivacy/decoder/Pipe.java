/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.decoder;

import java.util.concurrent.ExecutionException;
import org.wearables.randomizedresponse.differentialprivacy.ReportEntity;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.Substance;

public interface Pipe<T extends ReportEntity> {
  Substance<T> process(Substance<T> substance) throws ExecutionException;
}
