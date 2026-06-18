import java.util.HashMap;
import java.util.Map;

public class ContentNegotiation {
    public enum ContentType {
        JSON("application/json"),
        XML("application/xml"),
        YAML("application/yaml"),
        PLAIN_TEXT("text/plain");

        private final String mimeType;

        ContentType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public static ContentType fromMimeType(String mimeType) {
            for (ContentType type : values()) {
                if (type.mimeType.equals(mimeType)) {
                    return type;
                }
            }
            return JSON; // default
        }
    }

    public static String serialize(Object data, ContentType type) {
        switch (type) {
            case JSON:
                return serializeJson(data);
            case XML:
                return serializeXml(data);
            case YAML:
                return serializeYaml(data);
            case PLAIN_TEXT:
                return data.toString();
            default:
                return data.toString();
        }
    }

    private static String serializeJson(Object data) {
        return "{\"data\": \"" + data.toString() + "\"}";
    }

    private static String serializeXml(Object data) {
        return "<data>" + data.toString() + "</data>";
    }

    private static String serializeYaml(Object data) {
        return "data: " + data.toString();
    }

    public static ContentType negotiate(String acceptHeader) {
        if (acceptHeader == null || acceptHeader.isEmpty()) {
            return ContentType.JSON;
        }

        if (acceptHeader.contains("application/json")) {
            return ContentType.JSON;
        } else if (acceptHeader.contains("application/xml")) {
            return ContentType.XML;
        } else if (acceptHeader.contains("application/yaml")) {
            return ContentType.YAML;
        }

        return ContentType.JSON;
    }
}
