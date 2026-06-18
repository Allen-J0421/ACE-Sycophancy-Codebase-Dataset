import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Service layer interfaces
interface TrieService {
    OperationResult<?> execute(TrieCommand command, Trie trie);
}

interface SearchService {
    boolean search(String word, Trie trie);
    List<String> findByPrefix(String prefix, Trie trie);
    List<String> findByFrequency(int minFreq, Trie trie);
}

interface AnalyticsService {
    TrieStatistics getStatistics(Trie trie);
    Map<String, Object> getAnalytics(Trie trie);
}

interface PersistenceService {
    void save(Trie trie, String path);
    Trie load(String path);
}

// Default command service
class DefaultCommandService implements TrieService {
    private final CommandExecutor executor;
    private final EventBus eventBus;

    DefaultCommandService(CommandExecutor executor, EventBus eventBus) {
        this.executor = executor;
        this.eventBus = eventBus;
    }

    @Override
    public OperationResult<?> execute(TrieCommand command, Trie trie) {
        OperationResult<?> result = executor.execute(command, trie);
        if (command instanceof InsertWordCommand) {
            eventBus.publish(new WordInsertedEvent(((InsertWordCommand) command).getWord()));
        } else if (command instanceof DeleteWordCommand) {
            eventBus.publish(new WordDeletedEvent(((DeleteWordCommand) command).getWord()));
        } else if (command instanceof SearchWordCommand) {
            eventBus.publish(new WordSearchedEvent(((SearchWordCommand) command).getWord(), (Boolean) result.getValue()));
        }
        return result;
    }
}

// Default search service
class DefaultSearchService implements SearchService {
    @Override
    public boolean search(String word, Trie trie) {
        return trie.search(word);
    }

    @Override
    public List<String> findByPrefix(String prefix, Trie trie) {
        return trie.findWordsWithPrefix(prefix);
    }

    @Override
    public List<String> findByFrequency(int minFreq, Trie trie) {
        return trie.findWordsByFrequency(minFreq);
    }
}

// Default analytics service
class DefaultAnalyticsService implements AnalyticsService {
    @Override
    public TrieStatistics getStatistics(Trie trie) {
        return trie.getStatistics();
    }

    @Override
    public Map<String, Object> getAnalytics(Trie trie) {
        Map<String, Object> analytics = new LinkedHashMap<>();
        TrieStatistics stats = trie.getStatistics();
        analytics.put("totalWords", stats.getTotalWords());
        analytics.put("totalNodes", stats.getTotalNodes());
        analytics.put("maxDepth", stats.getMaxDepth());
        analytics.put("size", trie.getSize());
        analytics.put("metrics", trie.getMetrics().toString());
        return analytics;
    }
}

// Service registry
class ServiceRegistry {
    private final Map<Class<?>, Object> services = new HashMap<>();

    void register(Class<?> serviceInterface, Object implementation) {
        services.put(serviceInterface, implementation);
    }

    @SuppressWarnings("unchecked")
    <T> T getService(Class<T> serviceInterface) {
        Object service = services.get(serviceInterface);
        if (service == null) {
            throw new TrieException("Service not registered: " + serviceInterface.getSimpleName());
        }
        return (T) service;
    }

    boolean isRegistered(Class<?> serviceInterface) {
        return services.containsKey(serviceInterface);
    }

    void unregister(Class<?> serviceInterface) {
        services.remove(serviceInterface);
    }

    void clear() {
        services.clear();
    }

    int getServiceCount() {
        return services.size();
    }
}

// Service container
class TrieServiceContainer {
    private final ServiceRegistry registry = new ServiceRegistry();
    private final CommandExecutor commandExecutor;
    private final EventBus eventBus;

    TrieServiceContainer(CommandExecutor commandExecutor, EventBus eventBus) {
        this.commandExecutor = commandExecutor;
        this.eventBus = eventBus;
        initializeDefaultServices();
    }

    private void initializeDefaultServices() {
        registry.register(TrieService.class, new DefaultCommandService(commandExecutor, eventBus));
        registry.register(SearchService.class, new DefaultSearchService());
        registry.register(AnalyticsService.class, new DefaultAnalyticsService());
    }

    ServiceRegistry getRegistry() {
        return registry;
    }

    CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    EventBus getEventBus() {
        return eventBus;
    }

    TrieService getCommandService() {
        return registry.getService(TrieService.class);
    }

    SearchService getSearchService() {
        return registry.getService(SearchService.class);
    }

    AnalyticsService getAnalyticsService() {
        return registry.getService(AnalyticsService.class);
    }
}

// Advanced Trie with service integration
class AdvancedTrie extends Trie {
    private final TrieServiceContainer serviceContainer;

    AdvancedTrie() {
        super();
        this.serviceContainer = createServiceContainer();
    }

    AdvancedTrie(CharacterSet charset) {
        super(charset);
        this.serviceContainer = createServiceContainer();
    }

    AdvancedTrie(CharacterSet charset, int maxWordLength, CachePolicy<String, TrieNode> cache, TrieMetrics metrics) {
        super(charset, maxWordLength, cache, metrics);
        this.serviceContainer = createServiceContainer();
    }

    private TrieServiceContainer createServiceContainer() {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.addInterceptor(new TimingInterceptor());
        commandExecutor.addInterceptor(new ValidationInterceptor());

        EventBus eventBus = new EventBus();
        return new TrieServiceContainer(commandExecutor, eventBus);
    }

    OperationResult<?> executeCommand(TrieCommand command) {
        return serviceContainer.getCommandService().execute(command, this);
    }

    boolean searchViaService(String word) {
        return serviceContainer.getSearchService().search(word, this);
    }

    List<String> findByPrefixViaService(String prefix) {
        return serviceContainer.getSearchService().findByPrefix(prefix, this);
    }

    List<String> findByFrequencyViaService(int minFreq) {
        return serviceContainer.getSearchService().findByFrequency(minFreq, this);
    }

    Map<String, Object> getAnalyticsViaService() {
        return serviceContainer.getAnalyticsService().getAnalytics(this);
    }

    CommandExecutor getCommandExecutor() {
        return serviceContainer.getCommandExecutor();
    }

    EventBus getEventBus() {
        return serviceContainer.getEventBus();
    }

    TrieServiceContainer getServiceContainer() {
        return serviceContainer;
    }
}
