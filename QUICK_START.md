# LCS Ecosystem - Quick Start Guide

## Installation & Setup

```bash
# All files are self-contained Java classes
# No external dependencies required
# Compile all:
javac *.java

# Run tests:
java TestOrchestrator
```

## Basic Usage (30 seconds)

```java
// Get LCS length
int length = LcsQueries.compare("AGGTAB", "GXTXAYB").length();
System.out.println(length);  // → 4

// Get LCS sequence
String sequence = LcsQueries.compare("AGGTAB", "GXTXAYB").sequence();
System.out.println(sequence);  // → GTAB

// Get similarity percentage
double sim = LcsQueries.compare("AGGTAB", "GXTXAYB").similarity();
System.out.println(sim);  // → 66.7%
```

## Common Use Cases

### Case 1: Case-Insensitive Comparison
```java
int result = LcsQueries.compare("HELLO", "hello")
    .caseInsensitive()
    .length();
// → 5
```

### Case 2: Batch Processing
```java
int[] lengths = LcsQueries.batch(
    "ABC", "BCD",
    "HELLO", "HALLO"
).lengths();
// → [2, 4]
```

### Case 3: Comprehensive Analysis
```java
var analysis = LcsQueries.analyze("ABC", "AXC");
System.out.println(analysis);
// Shows all metrics: LCS, similarity, Dice, Jaccard, edit distance
```

### Case 4: Large Input Optimization
```java
LcsSolver adaptive = new AdaptiveSolver();
int result = adaptive.solve(new LcsInput(huge1, huge2)).getLength();
// Automatically chooses optimal algorithm
```

### Case 5: Performance Profiling
```java
PerformanceProfiler.comparesolvers("ABC", "BCD", 5,
    new StandardLcsSolver(),
    new SpaceOptimizedLcsSolver(),
    LcsSolverFactory.cached()
);
// Shows timing for each solver
```

## Configuration Presets

```java
// Standard (default)
LcsSolver s1 = LcsSolverFactory.standard();

// Performance (space-optimized)
LcsSolver s2 = new SpaceOptimizedLcsSolver();

// Lenient (case-insensitive + cached)
LcsSolver s3 = new NormalizingLcsSolver(
    LcsSolverFactory.cached(),
    InputNormalizers.caseInsensitive()
);

// Text-only (remove punctuation + cached)
LcsSolver s4 = new NormalizingLcsSolver(
    LcsSolverFactory.cached(),
    InputNormalizers.textOnly()
);

// Large inputs (approximation)
LcsSolver s5 = new ApproximateLcsSolver();

// Adaptive (automatic selection)
LcsSolver s6 = new AdaptiveSolver();
```

## Input Validation

```java
// Check input quality
LcsValidator validator = LcsValidators.standard();
ValidationResult result = validator.validate(s1, s2);

if (result.valid) {
    // Process
} else {
    System.err.println("Error: " + result.message);
}
```

## Similarity Metrics

```java
// After getting LCS length, compute metrics
int lcs = 4;
int len1 = 6;
int len2 = 7;

double similarity = LcsAnalyzer.similarityRatio(lcs, len1, len2);      // 66.7%
double dice = LcsAnalyzer.diceCoefficient(lcs, len1, len2);            // 0.615
double jaccard = LcsAnalyzer.jaccardSimilarity(lcs, len1, len2);       // 0.571
int editDist = LcsAnalyzer.estimatedEditDistance(lcs, len1, len2);    // 5
```

## String Diffing

```java
String s1 = "AGGTAB";
String s2 = "GXTXAYB";
String lcs = "GTAB";

// Visual
System.out.println(LcsDiffer.visualDiff(s1, s2, lcs));

// ASCII
System.out.println(LcsDiffer.asciiDiff(s1, s2, lcs));

// Side-by-side
System.out.println(LcsDiffer.sideBySideDiff(s1, s2, lcs));

// Markdown
System.out.println(LcsDiffer.markdownDiff(s1, s2, lcs));
```

## CLI Tool

```bash
# Simple comparison
java LcsCliTool -s1 "HELLO" -s2 "HALLO"

# With analysis
java LcsCliTool -s1 "AGGTAB" -s2 "GXTXAYB" -a

# With diff
java LcsCliTool -s1 "ABC" -s2 "AXC" -d --diff-format ascii

# Performance profiling
java LcsCliTool -s1 "TEST" -s2 "BEST" --profile --runs 10

# Interactive batch mode
java LcsCliTool batch

# Benchmark all solvers
java LcsCliTool -s1 "ABC" -s2 "BCD" benchmark

# Help
java LcsCliTool --help
```

## Documentation

- **FINAL_SUMMARY.md** - Complete refactoring overview
- **LcsDocumentation.java** - 10 usage examples with code
- **REFACTORING_COMPLETE.md** - Phase summary

## Test Everything

```bash
# Run all tests
java TestOrchestrator

# Run specific test suite
java TestFluentApiAndDiff
java TestConfigAndValidation
java PropertyBasedTests
java TestAdvancedCaching
java TestAdaptiveSolver
java TestSpecializedSolvers
java IntegrationSuite

# Run CLI tool examples
java LcsDocumentation
```

## Performance Tips

1. **For small inputs (<100 chars)**: Use standard solver
2. **For medium inputs (100-5000 chars)**: Use adaptive solver
3. **For large inputs (5000+ chars)**: Use approximate solver
4. **For repeated queries**: Use caching
5. **For text comparison**: Use normalization with caching

## Architecture At-a-Glance

```
User Code
   ↓
Fluent API (LcsQueries)
   ↓
Configuration & Validation
   ↓
Solver Selection (Adaptive/Self-Optimizing)
   ↓
Core Solvers + Decorators (Caching, Normalization)
   ↓
DP Computation / Approximation
   ↓
Result + Analysis
```

## Files Quick Reference

### Core Algorithm
- `longest_common_subsequence.java` - Original interface
- `StandardLcsSolver.java` - Standard DP
- `SpaceOptimizedLcsSolver.java` - O(min(m,n)) space
- `LcsSequenceReconstructor.java` - Sequence recovery

### Optimization
- `ApproximateLcsSolver.java` - Band narrowing
- `SubstringLcsSolver.java` - Pattern matching
- `CachedLcsSolver.java` - Result caching
- `LruCache.java` - LRU caching
- `LcsBatchProcessor.java` - Batch operations

### User Interface
- `LcsQueries.java` - Fluent API
- `LcsDiffer.java` - String diffing
- `ResultFormatter.java` - Output formatting
- `LcsCliTool.java` - Command-line tool

### Analysis
- `LcsAnalyzer.java` - Similarity metrics
- `PerformanceProfiler.java` - Performance analysis
- `BenchmarkUtils.java` - Benchmarking

### Configuration
- `LcsConfig.java` - Configuration system
- `LcsValidator.java` - Input validation
- `InputNormalizer.java` - Normalization strategies

### Intelligence
- `AdaptiveSolver.java` - Automatic selection
- (Self-optimizing in AdaptiveSolver)

### Testing
- `TestOrchestrator.java` - Test runner
- `Test*.java` (13 files) - Comprehensive tests

---

## Common Patterns

### Pattern 1: Simple Comparison
```java
int result = LcsQueries.compare(s1, s2).length();
```

### Pattern 2: With Configuration
```java
LcsSolver solver = selectSolver(config);
LcsResult result = solver.solve(new LcsInput(s1, s2));
```

### Pattern 3: Full Analysis
```java
var analysis = LcsQueries.analyze(s1, s2);
System.out.println(analysis);
```

### Pattern 4: Batch Processing
```java
int[] results = LcsQueries.batch(s1, s2, s3, s4).lengths();
```

### Pattern 5: Adaptive
```java
new AdaptiveSolver().solve(new LcsInput(s1, s2))
```

---

## Troubleshooting

### Issue: Slow performance on large inputs
**Solution**: Use `AdaptiveSolver` or `ApproximateLcsSolver`

### Issue: Case-sensitivity causing issues
**Solution**: Use `LcsQueries.compare().caseInsensitive()`

### Issue: Punctuation/whitespace affecting results
**Solution**: Use `NormalizingLcsSolver` with `InputNormalizers.textOnly()`

### Issue: Memory constraints
**Solution**: Use `SpaceOptimizedLcsSolver`

### Issue: Repeated queries slow
**Solution**: Use `LcsSolverFactory.cached()` or `LruCachedLcsSolver`

---

## Next Steps

1. **Try it out**: Run `java TestOrchestrator` to verify all tests pass
2. **Explore**: Read `LcsDocumentation.java` for 10 complete examples
3. **Configure**: Use `LcsConfig` presets for your use case
4. **Optimize**: Run `PerformanceProfiler` to benchmark solvers
5. **Integrate**: Embed LCS solver in your application

**All code is production-ready with zero external dependencies!**
