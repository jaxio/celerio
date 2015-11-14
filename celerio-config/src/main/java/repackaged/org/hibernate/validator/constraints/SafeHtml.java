package repackaged.org.hibernate.validator.constraints;

public class SafeHtml {
    public static enum WhiteListType {
        NONE,
        SIMPLE_TEXT,
        BASIC,
        BASIC_WITH_IMAGES,
        RELAXED;

        private WhiteListType() {
        }
    }
}
