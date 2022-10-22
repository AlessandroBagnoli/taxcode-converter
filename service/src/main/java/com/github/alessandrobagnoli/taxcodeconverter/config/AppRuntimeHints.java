package com.github.alessandrobagnoli.taxcodeconverter.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class AppRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    // Needed for picking up everything inside resources folder for native image
    hints.resources().registerPattern("*");
  }
}