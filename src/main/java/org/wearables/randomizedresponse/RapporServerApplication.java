/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.wearables.randomizedresponse.differentialprivacy.hyperparameter.HyperParameterConfiguration;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {"org.wearables.randomizedresponse.*"})
@EnableConfigurationProperties({HyperParameterConfiguration.class})
public class RapporServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(RapporServerApplication.class, args);
  }
}
