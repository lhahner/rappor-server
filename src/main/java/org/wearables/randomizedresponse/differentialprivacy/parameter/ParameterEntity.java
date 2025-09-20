/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.parameter;

import jakarta.persistence.*;
import java.util.UUID;

/** The parameters that are needed to run the RAPPOR algorithm to randomize some values. */
@Entity
@Table(name = "Parameters")
public class ParameterEntity {
  /** Primary key here. */
  @Id
  @Column(name = "Parameter_id", nullable = false, unique = true)
  public UUID parameterId;

  @Column(name = "Profile_name", nullable = false, unique = true)
  public String profile;

  /** The length of the message in bits = paper's "k" */
  @Column(name = "Message_Size")
  public int messageBitSize;

  /** The number of hash function for the bloom filter = paper's "h" */
  @Column(name = "Number_Hash_Functions")
  public int numberOfHashFunctions;

  /** The probability for the permanent randomize response step = paper's "f" */
  @Column(name = "Permanent_probability")
  public double permanentProbability;

  /**
   * The probability for the instantaneous randomized response step when the permanent randomize
   * response is one = paper's "q"
   */
  @Column(name = "Instantanous_probability_one")
  public double instantaneousProbabilityForOne;

  /**
   * The probability for the instantaneous randomized response step when the permanent randomize
   * response is zero = paper's "p"
   */
  @Column(name = "Instaneous_probability_zero")
  public double instantaneousProbabilityForZero;

  public ParameterEntity(
      UUID parameterId,
      String profile,
      int messageBitSize,
      int numberOfHashFunctions,
      double permanentProbability,
      double instantaneousProbabilityForOne,
      double instantaneousProbabilityForZero) {
    this.parameterId = parameterId;
    this.profile = profile;
    this.messageBitSize = messageBitSize;
    this.numberOfHashFunctions = numberOfHashFunctions;
    this.permanentProbability = permanentProbability;
    this.instantaneousProbabilityForOne = instantaneousProbabilityForOne;
    this.instantaneousProbabilityForZero = instantaneousProbabilityForZero;
  }

  public ParameterEntity(
      int messageBitSize,
      int numberOfHashFunctions,
      double permanentProbability,
      double instantaneousProbabilityForOne,
      double instantaneousProbabilityForZero) {
    this.messageBitSize = messageBitSize;
    this.numberOfHashFunctions = numberOfHashFunctions;
    this.permanentProbability = permanentProbability;
    this.instantaneousProbabilityForOne = instantaneousProbabilityForOne;
    this.instantaneousProbabilityForZero = instantaneousProbabilityForZero;
  }

  public ParameterEntity() {}

  public UUID getParameterId() {
    return parameterId;
  }

  public void setParameterId(UUID parameterId) {
    this.parameterId = parameterId;
  }

  public int getMessageBitSize() {
    return messageBitSize;
  }

  public void setMessageBitSize(int messageBitSize) {
    this.messageBitSize = messageBitSize;
  }

  public int getNumberOfHashFunctions() {
    return numberOfHashFunctions;
  }

  public void setNumberOfHashFunctions(int numberOfHashFunctions) {
    this.numberOfHashFunctions = numberOfHashFunctions;
  }

  public double getPermanentProbability() {
    return permanentProbability;
  }

  public void setPermanentProbability(double permanentProbability) {
    this.permanentProbability = permanentProbability;
  }

  public double getInstantaneousProbabilityForOne() {
    return instantaneousProbabilityForOne;
  }

  public void setInstantaneousProbabilityForOne(double instantaneousProbabilityForOne) {
    this.instantaneousProbabilityForOne = instantaneousProbabilityForOne;
  }

  public double getInstantaneousProbabilityForZero() {
    return instantaneousProbabilityForZero;
  }

  public void setInstantaneousProbabilityForZero(double instantaneousProbabilityForZero) {
    this.instantaneousProbabilityForZero = instantaneousProbabilityForZero;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }
}
