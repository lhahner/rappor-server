/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wearables.randomizedresponse.utilities.MappingUtils;

/**
 * Controller that manages client communication for the RAPPOR algorithm. Clients retrieve RAPPOR
 * parameters from the server and use them to randomize sensitive values before sending them. This
 * ensures that the server cannot reconstruct private data from any single client, while still
 * allowing statistical analysis across many clients.
 *
 * <p>The design follows the RAPPOR algorithm as introduced in the referenced paper.
 *
 * @see "Erlingsson, Úlfar, Vasyl Pihur, and Aleksandra Korolova. RAPPOR: Randomized Aggregatable
 *     Privacy-Preserving Ordinal Response. Proceedings of the 2014 ACM SIGSAC Conference on
 *     Computer and Communications Security."
 */
@RestController
@RequestMapping("parameters")
public class ParameterController {

  private final ParameterService parameterService;

  private final MappingUtils mappingUtils;

  @Autowired
  public ParameterController(ParameterService parameterService, MappingUtils mappingUtils) {
    this.parameterService = parameterService;
    this.mappingUtils = mappingUtils;
  }

  /**
   * Retrieves parameters for the RAPPOR algorithm. Depending on the mode, returns either default
   * parameters or parameters associated with a specific profile.
   *
   * @param mode the mode specifying which parameters to return
   * @param name optional profile name if mode is PROFILE
   * @return ParameterEntity containing the RAPPOR parameters, or null if no profile is found
   */
  @GetMapping
  public ResponseEntity<Object> getParameters(
      @RequestParam(required = true) ParameterMode mode,
      @RequestParam(required = false) String name)
      throws ChangeSetPersister.NotFoundException {
    return switch (mode) {
      case DEFAULT -> ResponseEntity.ok().body(parameterService.getDefaultParameterEntity());
      case PROFILE ->
          name == null
              ? null
              : ResponseEntity.ok()
                  .body(
                      parameterService
                          .findParameterEntityByProfile(name)
                          .orElseThrow(ChangeSetPersister.NotFoundException::new));
    };
  }

  /**
   * Stores new RAPPOR parameters for a given profile. If the profile already exists, returns the
   * existing parameters. Otherwise, the provided parameters are parsed, mapped, and saved.
   *
   * @param profile the profile name to associate with the parameters
   * @param parameters JSON string containing the parameter data
   * @return ResponseEntity with HTTP 200 OK if saved successfully, HTTP 400 Bad Request if
   *     processing fails
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> postParameters(
      @RequestParam(required = true) String profile, @RequestBody String parameters)
      throws JsonProcessingException {
    if (parameterService.findParameterEntityByProfile(profile).isPresent()) {
      var found = parameterService.findParameterEntityByProfile(profile);
      if (found.isPresent()) {
        return ResponseEntity.ok(found.get());
      }
    }
    ParameterData parameterData =
        mappingUtils.objectMapper.readValue(parameters, new TypeReference<ParameterData>() {});
    parameterService.save(parameterService.mapParameterDataToEntity(parameterData));
    return ResponseEntity.ok().build();
  }
}
