/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.wearables.randomizedresponse.*"})
public class RapporServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(RapporServerApplication.class, args);
  }
}
