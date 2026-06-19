/**
 * Tests for configuration system and validation framework.
 */
class TestConfigAndValidation {

    public static void main(String[] args) {
        testConfigBuilder();
        testPresetConfigs();
        testValidation();
        System.out.println("\n✓ All configuration and validation tests passed");
    }

    static void testConfigBuilder() {
        System.out.println("=== Configuration Builder ===\n");

        // Test custom configuration
        System.out.println("Test 1: Custom configuration");
        LcsConfig config = new LcsConfig.Builder()
                .algorithm(LcsConfig.AlgorithmPreference.SPACE_OPTIMIZED)
                .cache(true)
                .normalization(false)
                .approximateThreshold(0.85)
                .build();
        assert config.algorithm == LcsConfig.AlgorithmPreference.SPACE_OPTIMIZED;
        assert config.cacheEnabled;
        System.out.println("✓ Custom config: " + config);

        // Test invalid threshold
        System.out.println("\nTest 2: Invalid threshold validation");
        try {
            new LcsConfig.Builder().approximateThreshold(1.5).build();
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught threshold validation: " + e.getMessage());
        }

        // Test cache size validation
        System.out.println("\nTest 3: Cache size validation");
        try {
            new LcsConfig.Builder().cacheSizeLimit(-1).build();
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught cache size validation: " + e.getMessage());
        }

        System.out.println();
    }

    static void testPresetConfigs() {
        System.out.println("=== Preset Configurations ===\n");

        // Test default
        System.out.println("Test 1: Default config");
        LcsConfig defaultCfg = LcsConfig.defaultConfig();
        assert defaultCfg.cacheEnabled;
        System.out.println("✓ Default: " + defaultCfg);

        // Test performance
        System.out.println("\nTest 2: Performance config");
        LcsConfig perfCfg = LcsConfig.performance();
        assert perfCfg.algorithm == LcsConfig.AlgorithmPreference.SPACE_OPTIMIZED;
        assert perfCfg.cacheEnabled;
        System.out.println("✓ Performance: " + perfCfg);

        // Test strict
        System.out.println("\nTest 3: Strict config");
        LcsConfig strictCfg = LcsConfig.strict();
        assert strictCfg.algorithm == LcsConfig.AlgorithmPreference.STANDARD;
        assert !strictCfg.normalizationEnabled;
        System.out.println("✓ Strict: " + strictCfg);

        // Test lenient
        System.out.println("\nTest 4: Lenient config");
        LcsConfig lenientCfg = LcsConfig.lenient();
        assert lenientCfg.normalizationEnabled;
        System.out.println("✓ Lenient: " + lenientCfg);

        // Test text matching
        System.out.println("\nTest 5: Text matching config");
        LcsConfig textCfg = LcsConfig.textMatching();
        assert textCfg.normalizationEnabled;
        System.out.println("✓ Text matching: " + textCfg);

        // Test large inputs
        System.out.println("\nTest 6: Large inputs config");
        LcsConfig largeCfg = LcsConfig.largeInputs();
        assert largeCfg.algorithm == LcsConfig.AlgorithmPreference.APPROXIMATE;
        System.out.println("✓ Large inputs: " + largeCfg);

        System.out.println();
    }

    static void testValidation() {
        System.out.println("=== Validation Framework ===\n");

        // Test standard validator - valid input
        System.out.println("Test 1: Standard validator - valid input");
        LcsValidator validator = LcsValidators.standard();
        ValidationResult result1 = validator.validate("AGGTAB", "GXTXAYB");
        assert result1.valid;
        System.out.println("✓ Valid input: " + result1);

        // Test null validation
        System.out.println("\nTest 2: Null input rejection");
        ValidationResult result2 = validator.validate(null, "ABC");
        assert !result2.valid;
        System.out.println("✓ Null rejected: " + result2);

        // Test empty string handling
        System.out.println("\nTest 3: Empty string handling");
        ValidationResult result3 = validator.validate("", "ABC");
        assert result3.valid; // Valid but informative
        System.out.println("✓ Empty string: " + result3);

        // Test large input warning
        System.out.println("\nTest 4: Large input warning");
        String largeString = "X".repeat(6000);
        ValidationResult result4 = validator.validate(largeString, "ABC");
        assert result4.valid;
        assert result4.level == ValidationResult.ValidationLevel.WARNING;
        System.out.println("✓ Large input warning: " + result4);

        // Test max length rejection
        System.out.println("\nTest 5: Max length rejection");
        String tooLarge = "X".repeat(15000);
        ValidationResult result5 = validator.validate(tooLarge, "ABC");
        assert !result5.valid;
        System.out.println("✓ Too large rejected: " + result5);

        // Test strict validator
        System.out.println("\nTest 6: Strict validator");
        LcsValidator strictValidator = LcsValidators.strict();
        ValidationResult result6 = strictValidator.validate("HELLO", "WORLD");
        assert result6.valid;
        System.out.println("✓ Strict validation: " + result6);

        // Test lenient validator
        System.out.println("\nTest 7: Lenient validator");
        LcsValidator lenientValidator = LcsValidators.lenient();
        ValidationResult result7 = lenientValidator.validate(null, "ABC");
        assert !result7.valid; // Still rejects nulls
        System.out.println("✓ Lenient validation: " + result7);

        // Test composite validator
        System.out.println("\nTest 8: Composite validator");
        LcsValidator compositeValidator = new CompositeValidator.Builder()
                .add(LcsValidators.standard())
                .add(LcsValidators.strict())
                .build();
        ValidationResult result8 = compositeValidator.validate("ABC", "DEF");
        assert result8.valid;
        System.out.println("✓ Composite validation: " + result8);

        System.out.println();
    }
}
