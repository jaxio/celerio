package repackaged.org.hibernate.search.annotations;

public enum TermVector {
    YES,
    NO,
    WITH_OFFSETS,
    WITH_POSITIONS,
    WITH_POSITION_OFFSETS;

    private TermVector() {
    }
}
